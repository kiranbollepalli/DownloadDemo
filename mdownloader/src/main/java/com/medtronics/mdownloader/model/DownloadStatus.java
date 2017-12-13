package com.medtronics.mdownloader.model;

/**
 * Created by kiran on 12/14/17.
 */

public class DownloadStatus {

  private int percent;
  private int downloadStatus;

  public int getPercent() {
    return percent;
  }

  public void setPercent(int percent) {
    this.percent = percent;
  }

  public int getDownloadStatus() {
    return downloadStatus;
  }

  public void setDownloadStatus(int downloadStatus) {
    this.downloadStatus = downloadStatus;
  }
}
