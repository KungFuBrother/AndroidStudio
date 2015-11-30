package yitgogo.consumer;

import java.text.DecimalFormat;

import yitgogo.consumer.product.ui.ProductDetailFragment;
import yitgogo.consumer.tools.NetUtil;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;

public class BaseActivity extends FragmentActivity {

	public LayoutInflater layoutInflater;
	public NetUtil netUtil;
	public int screenWidth = 0, screenHeight = 0;
	public int pagenum = 0, pagesize = 10;
	public DecimalFormat decimalFormat;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		init();
	}

	private void init() {
		// if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
		// getWindow().addFlags(
		// WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// getWindow().addFlags(
		// WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		// setTranslucentStatus(true);
		// SystemBarTintManager tintManager = new SystemBarTintManager(this);
		// tintManager.setStatusBarTintEnabled(true);
		// tintManager.setStatusBarTintResource(R.color.actionbar_bg);
		// SystemBarConfig config = tintManager.getConfig();
		// listViewDrawer.setPadding(0, config.getPixelInsetTop(true), 0,
		// config.getPixelInsetBottom());
		// }
		layoutInflater = LayoutInflater.from(this);
		netUtil = NetUtil.getInstance();
		decimalFormat = new DecimalFormat("0.00");
	}

	protected void findViews() {

	}

	protected void initViews() {

	}

	protected void registerViews() {

	}

	protected void measureScreen() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenHeight = metrics.heightPixels;
		screenWidth = metrics.widthPixels;
	}

	protected void jump(String fragmentName, String fragmentTitle) {
		Intent intent = new Intent(BaseActivity.this, ContainerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("fragmentName", fragmentName);
		bundle.putString("fragmentTitle", fragmentTitle);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 带参数的fragment跳转
	 * 
	 * @param fragmentName
	 * @param fragmentTitle
	 * @param bundle
	 */
	protected void jump(String fragmentName, String fragmentTitle,
			Bundle parameters) {
		Intent intent = new Intent(BaseActivity.this, ContainerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("fragmentName", fragmentName);
		bundle.putString("fragmentTitle", fragmentTitle);
		bundle.putBundle("parameters", parameters);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	protected void showProductDetail(String productId) {
		Intent intent = new Intent(BaseActivity.this,
				ProductDetailFragment.class);
		intent.putExtra("productId", productId);
		startActivity(intent);
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
		if (originalUrl.length() > 0) {
			if (originalUrl.contains(".")) {

				String formation = originalUrl.substring(
						originalUrl.lastIndexOf("."), originalUrl.length());
				StringBuilder imgBuilder = new StringBuilder(originalUrl);
				return imgBuilder.replace(originalUrl.lastIndexOf("."),
						originalUrl.length(), "_350" + formation).toString();
			}
		}
		return originalUrl;
	}

	/**
	 * 判断是否连接网络
	 * 
	 * @return
	 */
	protected boolean isConnected() {
		// TODO Auto-generated method stub
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

}
