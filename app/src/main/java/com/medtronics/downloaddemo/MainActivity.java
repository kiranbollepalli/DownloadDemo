package com.medtronics.downloaddemo;

import android.app.DownloadManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.medtronics.mdownloader.DownloadCallback;
import com.medtronics.mdownloader.MDownloadManager;
import com.medtronics.mdownloader.model.DownloadableItem;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.text_download_Url) TextInputEditText textDownloadUrl;
  @BindView(R.id.progress_download) ProgressBar progressBar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.button_download) public void onClick(View view) {
    String downloadUrl = textDownloadUrl.getText().toString();
    if (downloadUrl.trim().length() == 0) {
      showToast("Invalid url");
      return;
    }
    startDownload(downloadUrl);

  }

  private void startDownload(String downloadUrl){
    DownloadableItem item = new DownloadableItem(downloadUrl);
    long downloadId =
        MDownloadManager.getInstance(getApplicationContext()).enqueueDownload(downloadUrl);

    if (downloadId != MDownloadManager.INVALID_DOWNLOAD_ID) {
      item.setDownloadId(downloadId);
      item.setDownloadStatus(DownloadManager.STATUS_PENDING);
      MDownloadManager.getInstance(getApplicationContext()).getDownloadPercents(item, mCallback);
    } else {
      showToast("Invalid url");
    }
  }

  private void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  DownloadCallback mCallback = new DownloadCallback() {
    @Override public void onDownloadStarted() {

    }

    @Override public void onDownloadCompleted(DownloadableItem item) {
      progressBar.setProgress(item.getDownloadedPercentage());
    }

    @Override public void onDownloadFailed() {
      progressBar.setProgress(0);

    }

    @Override public void onDownloadProgress(DownloadableItem item) {
      progressBar.setProgress(item.getDownloadedPercentage());
    }
  };
}
