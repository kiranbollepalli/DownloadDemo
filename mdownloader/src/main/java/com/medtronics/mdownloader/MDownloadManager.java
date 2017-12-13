package com.medtronics.mdownloader;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import com.medtronics.mdownloader.model.DownloadStatus;
import com.medtronics.mdownloader.model.DownloadableItem;

/**
 * Created by kiran on 12/14/17.
 */

public class MDownloadManager {

  private DownloadManager mManager;
  private static volatile MDownloadManager sDownloadManager;

  public static final long INVALID_DOWNLOAD_ID = -1;
  public static final int DOWNLOAD_COMPLETE_PERCENTAGE = 100;
  public static final long PROGRESS_CHECK_FREQUENCY = 100; // ONE MINUTE

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

  public long enqueueDownload(String downloadUrl) {

    if (downloadUrl == null) {
      return INVALID_DOWNLOAD_ID;
    }
    Uri uri = Uri.parse(downloadUrl);
    DownloadManager.Request downloadRequest = new DownloadManager.Request(uri);
    return mManager.enqueue(downloadRequest);
  }

  public void getDownloadPercents(final DownloadableItem item, final DownloadCallback callback) {
    DownloadStatus status = getDownloadResult(item.getDownloadId());
    if (status == null) {
      return;
    }

    item.setDownloadedPercentage(status.getPercent());
    item.setDownloadStatus(status.getDownloadStatus());

    switch (item.getDownloadStatus()) {

      case DownloadManager.STATUS_FAILED:
        if (callback!=null) callback.onDownloadFailed();
        break;

      case DownloadManager.STATUS_PAUSED:
      case DownloadManager.STATUS_PENDING:
      case DownloadManager.STATUS_RUNNING:
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override public void run() {
            if (callback!=null) callback.onDownloadProgress(item);
            getDownloadPercents(item, callback);
            Log.i( "MANAGER", "download progress : " + item.getDownloadedPercentage());
          }
        }, PROGRESS_CHECK_FREQUENCY);
        break;

      case DownloadManager.STATUS_SUCCESSFUL:
        if (callback!=null) callback.onDownloadCompleted(item);

        break;
    }
  }

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
      int percentage = (int) ((downloadedBytes / totalBytes) * DOWNLOAD_COMPLETE_PERCENTAGE);
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
