package com.medtronics.downloaddemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.medtronics.mdownloader.DownloadCallback;
import com.medtronics.mdownloader.MDownloadManager;
import com.medtronics.mdownloader.model.DownloadableItem;
import com.medtronics.mdownloader.util.Constants;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.text_download_Url) EditText mUrlInputView;
  @BindView(R.id.progress_download) ProgressBar mProgressView;
  @BindView(R.id.text_download_update) TextView mDownloadUpdateView;
  private boolean isDownloadInProgress = false;
  private static final int RC_STORAGE_PERMISSION = 1000;
  private static final String TAG = MainActivity.class.getName();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.button_download) public void onClick(View view) {
    String downloadUrl = mUrlInputView.getText().toString();
    if (validate(downloadUrl)) startDownload(downloadUrl);
  }

  private boolean validate(String downloadUrl) {
    if (downloadUrl.trim().length() == 0) {
      mUrlInputView.setError(getString(R.string.error_mandatory_field));
      return false;
    }
    if (isDownloadInProgress) {
      showToast(getString(R.string.error_download_inprogress));
      return false;
    }
    return isStoragePermissionGranted();
  }

  private void startDownload(String downloadUrl) {
    DownloadableItem item = new DownloadableItem(downloadUrl);
    long downloadId =
        MDownloadManager.getInstance(getApplicationContext()).enqueueDownload(item,mCallback);

    if (downloadId == Constants.INVALID_DOWNLOAD_ID) {
      showToast(getString(R.string.error_invalid_url));
    }
  }

  private void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  private void updateProgressUI(int progress) {
    mProgressView.setProgress(progress);
    mDownloadUpdateView.setText(getString(R.string.formatted_download_update, progress));
  }

  public boolean isStoragePermissionGranted() {

    if (Build.VERSION.SDK_INT < 23) {
      return true;
    }

    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED) {
      return true;
    } else {
      ActivityCompat.requestPermissions(this,
          new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, RC_STORAGE_PERMISSION);
      return false;
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode){
      case RC_STORAGE_PERMISSION:
        if (grantResults[0] == PackageManager.PERMISSION_DENIED){
          showToast(getString(R.string.error_storrage_permission_denied));
        }
        break;
    }
    if (requestCode == RC_STORAGE_PERMISSION
        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "Storage permission granted");
    }
  }

  DownloadCallback mCallback = new DownloadCallback() {
    @Override public void onDownloadStarted() {
      isDownloadInProgress = true;
    }

    @Override public void onDownloadCompleted(DownloadableItem item) {
      updateProgressUI(100);
      isDownloadInProgress = false;
    }

    @Override public void onDownloadFailed() {
      updateProgressUI(0);
      isDownloadInProgress = false;
    }

    @Override public void onDownloadProgress(DownloadableItem item) {
      updateProgressUI(item.getDownloadedPercentage());
    }
  };
}
