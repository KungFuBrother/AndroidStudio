package yitgogo.smart;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import yitgogo.smart.product.ui.ProductDetailFragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;

public class BaseNormalFragment extends Fragment {

	public LayoutInflater layoutInflater;
	public int screenWidth = 0, screenHeight = 0;
	public DecimalFormat decimalFormat;
	public SimpleDateFormat simpleDateFormat;
	public ImageLoader imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	public void onPause() {
		super.onPause();
		imageLoader.clearMemoryCache();
		System.gc();
	}

	private void init() {
		layoutInflater = LayoutInflater.from(getActivity());
		imageLoader = ImageLoader.getInstance();
		decimalFormat = new DecimalFormat("0.00");
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	}

	protected void findViews(View view) {

	}

	protected void initViews() {

	}

	protected void registerViews() {

	}

	protected void openWindow(String fragmentName, String title) {
		Intent intent = new Intent(getActivity(), DialogActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("fragmentName", fragmentName);
		bundle.putString("title", title);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	protected void openWindow(String fragmentName, String title,
			Bundle parameters) {
		Intent intent = new Intent(getActivity(), DialogActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("fragmentName", fragmentName);
		bundle.putString("title", title);
		bundle.putBundle("parameters", parameters);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	protected void showProductDetail(String productId, int saleType) {
		Bundle bundle = new Bundle();
		bundle.putString("productId", productId);
		bundle.putInt("saleType", saleType);
		openWindow(ProductDetailFragment.class.getName(), "商品详情", bundle);
	}

	protected void measureScreen() {
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		screenHeight = metrics.heightPixels;
		screenWidth = metrics.widthPixels;
	}

	/**
	 * 验证手机格式
	 */
	protected boolean isPhoneNumber(String number) {
		if (TextUtils.isEmpty(number)) {
			return false;
		} else {
			return number.length() == 11;
		}
	}

	/**
	 * 判断是否连接网络
	 * 
	 * @return
	 */
	protected boolean isConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager.getActiveNetworkInfo() != null) {
			if (connectivityManager.getActiveNetworkInfo().isAvailable()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	protected String getHtmlFormated(String baseHtml) {
		String head = "<head>"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
				+ "<style>img{max-width: 100%; width:auto; height:auto;}</style>"
				+ "</head>";
		return "<html>" + head + "<body>" + baseHtml + "</body></html>";
	}

	/**
	 * @author Tiger
	 * 
	 * @param originalUrl
	 *            json得到的图片链接
	 * 
	 * @return formatedUrl 切图链接
	 */
	protected String getSmallImageUrl(String originalUrl) {
		String formatedUrl = "";
		if (!TextUtils.isEmpty(originalUrl)) {
			formatedUrl = originalUrl;
			if (originalUrl.contains("images.")) {
				formatedUrl = originalUrl.replace("images.", "imageprocess.")
						+ "@!350";
			}
		}
		return formatedUrl;
	}

	/**
	 * @author Tiger
	 * 
	 * @param originalUrl
	 *            json得到的图片链接
	 * 
	 * @return formatedUrl 切图链接
	 */
	protected String getBigImageUrl(String originalUrl) {
		String formatedUrl = "";
		if (!TextUtils.isEmpty(originalUrl)) {
			formatedUrl = originalUrl;
			if (originalUrl.contains("images.")) {
				formatedUrl = originalUrl.replace("images.", "imageprocess.")
						+ "@!600";
			}
		}
		return formatedUrl;
	}

}
