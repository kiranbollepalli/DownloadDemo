package com.medtronics.mdownloader.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kiran on 12/15/17.
 */

public class Utils {
  public static String getFilenameFromURL(URL url) {
    return new File(url.getPath()).getName();
  }

  public static String getFilenameFromURL(String urlString) {
    URL url = null;
    try {
      url = new URL(urlString);
      return new File(url.getPath()).getName();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String prepareSubPath(String urlString) {
    StringBuilder builder = new StringBuilder();
    builder.append("/")
        .append(Constants.DOWNLOAD_FOLDER)
        .append("/")
        .append(getFilenameFromURL(urlString));
        return builder.toString();
  }
}
