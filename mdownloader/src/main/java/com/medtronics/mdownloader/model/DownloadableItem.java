package com.medtronics.mdownloader.model;

/**
 * Created by kiran on 12/14/17.
 */

public class DownloadableItem {

  static int counter = 1;
  private int id;
  private long downloadId;
  private String title;
  private  String url;
  private int downloadedPercentage;
  private int downloadStatus;

  public DownloadableItem(){

  }

  public DownloadableItem(String url){
    id = counter++;
    this.url = url;
    this.title = url; //TODO: Need to change later.

  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public long getDownloadId() {
    return downloadId;
  }

  public void setDownloadId(long downloadId) {
    this.downloadId = downloadId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getDownloadedPercentage() {
    return downloadedPercentage;
  }

  public void setDownloadedPercentage(int downloadedPercentage) {
    this.downloadedPercentage = downloadedPercentage;
  }

  public int getDownloadStatus() {
    return downloadStatus;
  }

  public void setDownloadStatus(int downloadStatus) {
    this.downloadStatus = downloadStatus;
  }
}
