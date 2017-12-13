package com.medtronics.mdownloader;

import com.medtronics.mdownloader.model.DownloadableItem;

/**
 * Created by kiran on 12/14/17.
 */

public interface DownloadCallback {

  void onDownloadStarted();

  void onDownloadCompleted(DownloadableItem item);

  void onDownloadFailed();

  void onDownloadProgress(DownloadableItem item);
}
