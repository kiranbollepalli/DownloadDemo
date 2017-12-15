package com.medtronics.mdownloader;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import com.medtronics.mdownloader.model.DownloadStatus;
import com.medtronics.mdownloader.model.DownloadableItem;
import com.medtronics.mdownloader.util.Constants;
import com.medtronics.mdownloader.util.Utils;

/**
 * Created by kiran on 12/14/17.
 */

public class MDownloadManager {

  private DownloadManager mManager;
  private static volatile MDownloadManager sDownloadManager;

  private MDownloadManager() {

  }

  private MDownloadManager(Context context) {
    initialize(context);
  }

  public static synchronized MDownloadManager getInstance(@NonNull Context context) {
    if (sDownloadManager == null) {
      sDownloadManager = new MDownloadManager(context);
    }
    return sDownloadManager;
  }

  private void initialize(Context context) {
    mManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
  }

  /**
   * Method enqueues Downloadable item.
   *
   * @param item {@link DownloadableItem} Downloadable item info.
   * @param callback {@link DownloadCallback} for updates on download progress.
   * @return downloadId. If url is null it will return -1;
   */
  public long enqueueDownload(final DownloadableItem item, final DownloadCallback callback) {

    if (item.getUrl() == null) {
      return Constants.INVALID_DOWNLOAD_ID;
    }
    Uri uri = Uri.parse(item.getUrl());
    DownloadManager.Request downloadRequest = new DownloadManager.Request(uri);
    downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
        Utils.getFilenameFromURL(item.getUrl()));

    long downloadId = mManager.enqueue(downloadRequest);
    item.setDownloadId(downloadId);
    item.setDownloadStatus(DownloadManager.STATUS_PENDING);
    if (callback != null) callback.onDownloadStarted();
    getDownloadPercents(item, callback);
    return downloadId;
  }

  /**
   * Method checks download progress.
   *
   * @param item {@link DownloadableItem} Downloadable item info.
   * @param callback {@link DownloadCallback} for updates on download progress.
   */

  private void getDownloadPercents(final DownloadableItem item, final DownloadCallback callback) {
    DownloadStatus status = getDownloadResult(item.getDownloadId());

    if (status == null) return;

    item.setDownloadedPercentage(status.getPercent());
    item.setDownloadStatus(status.getDownloadStatus());

    switch (item.getDownloadStatus()) {

      case DownloadManager.STATUS_FAILED:
        if (callback != null) callback.onDownloadFailed();
        break;

      case DownloadManager.STATUS_PAUSED:
      case DownloadManager.STATUS_PENDING:
      case DownloadManager.STATUS_RUNNING:
        new Handler().postDelayed(new Runnable() {
          @Override public void run() {
            if (callback != null) callback.onDownloadProgress(item);
            getDownloadPercents(item, callback);
            Log.i("MANAGER", "download progress : " + item.getDownloadedPercentage());
          }
        }, Constants.PROGRESS_CHECK_FREQUENCY);
        break;

      case DownloadManager.STATUS_SUCCESSFUL:
        if (callback != null) callback.onDownloadCompleted(item);

        break;
    }
  }

  /**
   * Get progress and status of downloadable item by id.
   *
   * @return {@link DownloadStatus}
   */

  private DownloadStatus getDownloadResult(long downloadId) {

    DownloadManager.Query query = new DownloadManager.Query();
    query.setFilterById(downloadId);

    Cursor cursor = null;
    DownloadStatus status = new DownloadStatus();

    try {
      cursor = mManager.query(query);
      if (cursor == null || !cursor.moveToFirst()) {
        return null;
      }

      //COMMENT: Get percentage of file download
      float downloadedBytes =
          cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
      float totalBytes =
          cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
      int percentage =
          (int) ((downloadedBytes / totalBytes) * Constants.DOWNLOAD_COMPLETE_PERCENTAGE);
      status.setPercent(percentage);

      //COMMENT: Get the download status
      int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
      int downloadStatus = cursor.getInt(columnIndex);
      status.setDownloadStatus(downloadStatus);
    } catch (Exception exception) {
      exception.printStackTrace();
    } finally {
      if (cursor != null) cursor.close();
    }
    return status;
  }
}
