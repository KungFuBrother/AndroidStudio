package yitgogo.consumer.product.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import yitgogo.consumer.BaseNotifyFragment;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dtr.zxing.activity.CaptureActivity;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class WebFragment extends BaseNotifyFragment {

	WebView webView;
	ProgressBar progressBar;
	ImageView goTopButton;
	String html = "", url = "http://www.baidu.com";
	int type = -1;

	public final static int TYPE_HTML = 0;
	public final static int TYPE_URL = 1;

	File htmlFile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_web);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(WebFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(WebFragment.class.getName());
	}

	private void init() {
		htmlFile = new File(getActivity().getCacheDir().getPath(), "index.html");
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("html")) {
				html = bundle.getString("html");
			}
			if (bundle.containsKey("url")) {
				if (bundle.getString("url").length() > 0) {
					url = bundle.getString("url");
					System.out.println(url);
				}
			}
			if (bundle.containsKey("type")) {
				type = bundle.getInt("type");
			}
		}
	}

	@Override
	protected void findViews() {
		webView = (WebView) contentView.findViewById(R.id.web_webview);
		progressBar = (ProgressBar) contentView.findViewById(R.id.web_progress);
		goTopButton = (ImageView) contentView.findViewById(R.id.web_go_top);
		initViews();
		registerViews();
	}

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void initViews() {
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					progressBar.setVisibility(View.GONE);
				} else {
					if (progressBar.getVisibility() == View.GONE)
						progressBar.setVisibility(View.VISIBLE);
					progressBar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}
		});
		webView.addJavascriptInterface(new JsInterface(), "yitgogo");
		WebSettings settings = webView.getSettings();
		settings.setDefaultTextEncodingName("utf-8");
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		settings.setUseWideViewPort(true);
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setLoadWithOverviewMode(true);

		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		settings.setDomStorageEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setAppCachePath(getActivity().getCacheDir().getPath());
		settings.setAppCacheEnabled(true);

		showWebData();
	}

	@Override
	protected void registerViews() {
		addImageButton(R.drawable.ic_clear_black_24dp, "close",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						getActivity().finish();
					}
				});
		onBackButtonClick(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (webView.canGoBack()) {
					webView.goBack();
				} else {
					getActivity().finish();
				}
			}
		});
		goTopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				webView.scrollTo(0, 0);
			}
		});
	}

	class DownLoad extends AsyncTask<Void, Float, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpResponse response;
			try {
				response = client.execute(get);
				HttpEntity entity = response.getEntity();
				long length = entity.getContentLength();
				InputStream is = entity.getContent();
				FileOutputStream fileOutputStream = null;
				if (is != null) {
					fileOutputStream = new FileOutputStream(htmlFile);
					byte[] b = new byte[512];
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
		protected void onPostExecute(Boolean result) {
			if (result) {
				webView.loadUrl("file://" + htmlFile.getPath());
			} else {
				getActivity().finish();
			}
		}
	}

	private void showWebData() {
		switch (type) {
		case TYPE_HTML:
			webView.loadData(html, "text/html; charset=utf-8", "utf-8");
			break;

		case TYPE_URL:
			new DownLoad().execute();
			break;
		default:
			webView.loadUrl(url);
			break;
		}
	}

	class JsInterface {

		@JavascriptInterface
		public boolean showProductInfo(String productId) {
			showProductDetail(productId, "商品详情", CaptureActivity.SALE_TYPE_NONE);
			return true;
		}
	}

}
