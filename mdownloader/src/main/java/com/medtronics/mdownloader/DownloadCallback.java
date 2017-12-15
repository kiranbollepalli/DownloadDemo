package com.medtronics.mdownloader;

import com.medtronics.mdownloader.model.DownloadableItem;

/**
 * This is Callback interface, provides information about download status and progress.
 */

public interface DownloadCallback {

  void onDownloadStarted();

  void onDownloadCompleted(DownloadableItem item);

  void onDownloadFailed();

  void onDownloadProgress(DownloadableItem item);
}
