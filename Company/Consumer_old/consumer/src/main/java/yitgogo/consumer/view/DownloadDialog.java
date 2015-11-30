package yitgogo.consumer.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import yitgogo.consumer.tools.PackageTool;
import yitgogo.consumer.tools.ScreenUtil;
import yitgogo.consumer.user.model.VersionInfo;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;

public class DownloadDialog extends DialogFragment {

	LayoutInflater layoutInflater;
	View dialogView;

	FrameLayout bottomLayout;
	LinearLayout topLayout;
	TextView progressTextView, stateTextView;
	CircleProgressView circleProgressView;

	VersionInfo versionInfo;
	File downloadFile, directory;

	boolean firstTime = true;

	public DownloadDialog() {
	}

	public DownloadDialog(VersionInfo versionInfo) {
		this.versionInfo = versionInfo;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (versionInfo.getGrade() == 2) {
			setCancelable(false);
		} else {
			setCancelable(true);
		}
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		checkLocalApk();
	}

	private void init() {
		layoutInflater = LayoutInflater.from(getActivity());
		directory = new File(Environment.getExternalStorageDirectory()
				+ "/Yitgogo/Consumer");
		if (!directory.exists()) {
			directory.mkdirs();
		}
		downloadFile = new File(directory.getPath(), "Consumer.apk");
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity());
		dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialogView,
				new LayoutParams((ScreenUtil.getScreenWidth() / 5 * 4),
						(ScreenUtil.getScreenWidth() / 5 * 4)));
		return dialog;
	}

	private void findViews() {
		dialogView = layoutInflater.inflate(R.layout.dialog_apk_download, null);
		bottomLayout = (FrameLayout) dialogView
				.findViewById(R.id.download_circle_bottom);
		topLayout = (LinearLayout) dialogView
				.findViewById(R.id.download_circle_top);
		progressTextView = (TextView) dialogView
				.findViewById(R.id.download_progress_text);
		stateTextView = (TextView) dialogView
				.findViewById(R.id.download_progress_state);
		circleProgressView = (CircleProgressView) dialogView
				.findViewById(R.id.download_progress_view);
		initViews();
	}

	private void initViews() {
		FrameLayout.LayoutParams bottomLayoutParams = new FrameLayout.LayoutParams(
				ScreenUtil.getScreenWidth() / 5 * 3,
				ScreenUtil.getScreenWidth() / 5 * 3);
		bottomLayoutParams.gravity = Gravity.CENTER;
		FrameLayout.LayoutParams topLayoutParams = new FrameLayout.LayoutParams(
				ScreenUtil.getScreenWidth() / 40 * 23,
				ScreenUtil.getScreenWidth() / 40 * 23);
		topLayoutParams.gravity = Gravity.CENTER;
		bottomLayout.setLayoutParams(bottomLayoutParams);
		topLayout.setLayoutParams(topLayoutParams);
	}

	public void install() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(downloadFile),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	private void checkLocalApk() {
		boolean downloaded = false;
		if (downloadFile.exists()) {
			PackageInfo info = getActivity().getPackageManager().getPackageArchiveInfo(
					downloadFile.getPath(), PackageManager.GET_ACTIVITIES);
			if (info != null) {
				ApplicationInfo appInfo = info.applicationInfo;
				String apkPackageName = appInfo.packageName; // 得到安装包名称
				int apkVerCode = info.versionCode; // 得到版本信息
				if (apkPackageName.equals(PackageTool.getPackageName()) && apkVerCode >= versionInfo.getVerCode()) {
					downloaded = true;
				}
			}
		}
		if (downloaded) {
			circleProgressView.setProgress(1);
			progressTextView.setText("已下载");
			stateTextView.setText("点击安装");
			topLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					install();
				}
			});
			if (versionInfo.getGrade() == 2) {
				if (firstTime) {
					install();
				}
			}
		} else {
			circleProgressView.setProgress(0);
			progressTextView.setText("0%");
			stateTextView.setText("点击下载");
			topLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new DownLoad().execute();
				}
			});
			if (versionInfo.getGrade() == 2) {
				if (firstTime) {
					new DownLoad().execute();
				}
			}
		}
		firstTime = false;
	}

	class DownLoad extends AsyncTask<Void, Float, Boolean> {

		@Override
		protected void onPreExecute() {
			stateTextView.setText("下载中...");
			topLayout.setEnabled(false);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(
					"http://updatePhone.yitos.net/android/ytgogo_consumer.apk");
			HttpResponse response;
			try {
				response = client.execute(get);
				HttpEntity entity = response.getEntity();
				long length = entity.getContentLength();
				InputStream is = entity.getContent();
				FileOutputStream fileOutputStream = null;
				if (is != null) {
					fileOutputStream = new FileOutputStream(downloadFile);
					byte[] b = new byte[1024];
					int charb = -1;
					int count = 0;
					while ((charb = is.read(b)) != -1) {
						count += charb;
						publishProgress((float) count / (float) length);
						fileOutputStream.write(b, 0, charb);
					}
				}
				fileOutputStream.flush();
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onProgressUpdate(Float... values) {
			progressTextView.setText((int) (values[0] * 100) + "%");
			circleProgressView.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			topLayout.setEnabled(true);
			if (result) {
				circleProgressView.setProgress(1);
				progressTextView.setText("已下载");
				stateTextView.setText("点击安装");
				install();
			} else {
				circleProgressView.setProgress(0);
				progressTextView.setText("下载失败");
				stateTextView.setText("点击重新下载");
				Notify.show("下载失败");
			}
		}
	}

}
