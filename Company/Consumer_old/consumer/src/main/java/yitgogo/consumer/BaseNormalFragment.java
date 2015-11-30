package yitgogo.consumer;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;

import yitgogo.consumer.bianmin.phoneCharge.ui.PhoneChargeFragment;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.model.ModelOrderResult;
import yitgogo.consumer.product.ui.ProductDetailFragment;
import yitgogo.consumer.product.ui.ProductListFragment;
import yitgogo.consumer.tools.MD5;
import yitgogo.consumer.tools.NetUtil;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class BaseNormalFragment extends Fragment {

	public LayoutInflater layoutInflater;
	public NetUtil netUtil;
	public int screenWidth = 0, screenHeight = 0;
	public int pagenum = 0, pagesize = 10;
	public DecimalFormat decimalFormat;
	public SimpleDateFormat simpleDateFormat;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		layoutInflater = LayoutInflater.from(getActivity());
		netUtil = NetUtil.getInstance();
		decimalFormat = new DecimalFormat("0.00");
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	}

	protected void findViews(View view) {

	}

	protected void initViews() {

	}

	protected void registerViews() {

	}

	/**
	 * 带参数的fragment跳转
	 * 
	 * @param fragmentName
	 * @param fragmentTitle
	 * @param bundle
	 */
	protected void jumpFull(String fragmentName, String fragmentTitle,
			Bundle parameters) {
		Intent intent = new Intent(getActivity(), ContainerFullActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("fragmentName", fragmentName);
		bundle.putString("fragmentTitle", fragmentTitle);
		bundle.putBundle("parameters", parameters);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	protected void jump(String fragmentName, String fragmentTitle) {
		if (fragmentName.equals(PhoneChargeFragment.class.getName())) {
			jump(fragmentName, fragmentTitle, true);
			return;
		}
		Intent intent = new Intent(getActivity(), ContainerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("fragmentName", fragmentName);
		bundle.putString("fragmentTitle", fragmentTitle);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 可隐藏container标题栏的跳转
	 * 
	 * @param fragmentName
	 * @param fragmentTitle
	 * @param hideTitle
	 */
	protected void jump(String fragmentName, String fragmentTitle,
			boolean hideTitle) {
		Intent intent = new Intent(getActivity(), ContainerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("fragmentName", fragmentName);
		bundle.putString("fragmentTitle", fragmentTitle);
		bundle.putBoolean("hideTitle", hideTitle);
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
		Intent intent = new Intent(getActivity(), ContainerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("fragmentName", fragmentName);
		bundle.putString("fragmentTitle", fragmentTitle);
		bundle.putBundle("parameters", parameters);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 带参数的fragment跳转
	 * 
	 * @param fragmentName
	 * @param fragmentTitle
	 * @param bundle
	 * @param hideTitle
	 */
	protected void jump(String fragmentName, String fragmentTitle,
			Bundle parameters, boolean hideTitle) {
		Intent intent = new Intent(getActivity(), ContainerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("fragmentName", fragmentName);
		bundle.putString("fragmentTitle", fragmentTitle);
		bundle.putBundle("parameters", parameters);
		bundle.putBoolean("hideTitle", hideTitle);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 显示商品列表
	 * 
	 * @param title
	 *            标题
	 * @param value
	 *            参数值
	 * @param type
	 *            参数类型/产品类型
	 */
	protected void jumpProductList(String fragmentTitle, String value, int type) {
		Bundle bundle = new Bundle();
		bundle.putString("value", value);
		bundle.putInt("type", type);
		jump(ProductListFragment.class.getName(), fragmentTitle, bundle);
	}

	protected void showProductDetail(String productId, String productName,
			int saleType) {
		Bundle bundle = new Bundle();
		bundle.putString("productId", productId);
		bundle.putInt("saleType", saleType);
		jump(ProductDetailFragment.class.getName(), productName, bundle);
	}

	protected void measureScreen() {
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		screenHeight = metrics.heightPixels;
		screenWidth = metrics.widthPixels;
	}

	protected void addImageButton(int imageResId, String tag,
			OnClickListener onClickListener) {
		getContainerActivity().addImageButton(imageResId, tag, onClickListener);
	}

	/**
	 * fragment设置返回按钮点击事件
	 * 
	 * @param onClickListener
	 */
	protected void onBackButtonClick(OnClickListener onClickListener) {
		getContainerActivity().onBackButtonClick(onClickListener);
	}

	private ContainerActivity getContainerActivity() {
		ContainerActivity containerActivity = (ContainerActivity) getActivity();
		return containerActivity;
	}

	/**
	 * 获取圆角位图的方法
	 * 
	 * @param bitmap
	 *            需要转化成圆角的位图
	 * @param pixels
	 *            圆角的度数，数值越大，圆角越大
	 * @return 处理后的圆角位图
	 */
	protected Bitmap getRoundCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		int color = 0xff424242;
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
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

	/**
	 * 通过接口地址和参数组成唯一字符串，作为用于缓存数据的键
	 * 
	 * @param api_url
	 *            接口地址
	 * @param parameters
	 *            网络请求参数
	 * @return 缓存数据的键
	 */
	protected String getCacheKey(String api_url, List<NameValuePair> parameters) {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
		builder.append(api_url);
		if (parameters != null) {
			for (int i = 0; i < parameters.size(); i++) {
				if (i == 0) {
					builder.append("?");
				} else {
					builder.append("&");
				}
				builder.append(parameters.get(i).getName());
				builder.append("=");
				builder.append(parameters.get(i).getValue());
			}
		}
		return builder.toString();
	}

	/**
	 * 验证手机格式
	 */
	protected boolean isPhoneNumber(String number) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		// String telRegex = "[1][3578]\\d{9}";
		// if (TextUtils.isEmpty(number))
		// return false;
		// else
		// return number.matches(telRegex);
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
		// TODO Auto-generated method stub
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

	protected String getEncodedPassWord(String password) {
		return MD5.GetMD5Code(password + "{xiaozongqi}");
	}

	protected void payMoney(String orderNumber, double totalMoney, int orderType) {
		ArrayList<String> orderNumbers = new ArrayList<String>();
		orderNumbers.add(orderNumber);
		payMoney(orderNumbers, totalMoney, orderType);
	}

	protected void payMoney(ArrayList<String> orderNumbers, double totalMoney,
			int orderType) {
		Bundle bundle = new Bundle();
		bundle.putStringArrayList("orderNumbers", orderNumbers);
		bundle.putDouble("totalMoney", totalMoney);
		bundle.putInt("orderType", orderType);
		// bundle.putInt("productCount", productCount);
		jump(PayFragment.class.getName(), "订单支付", bundle);
	}

	/**
	 * 易田商城下单成功后支付
	 * 
	 * @param platformOrderResult
	 *            下单返回订单的结果
	 */
	protected void payMoney(JSONArray platformOrderResult) {
		if (platformOrderResult != null) {
			if (platformOrderResult != null) {
				double payPrice = 0;
				int productCount = 0;
				ArrayList<String> orderNumbers = new ArrayList<String>();
				for (int i = 0; i < platformOrderResult.length(); i++) {
					ModelOrderResult orderResult = new ModelOrderResult(
							platformOrderResult.optJSONObject(i));
					orderNumbers.add(orderResult.getOrdernumber());
					payPrice += orderResult.getZhekouhou();
					// productCount+= orderResult.get
				}
				if (orderNumbers.size() > 0) {
					if (payPrice > 0) {
						payMoney(orderNumbers, payPrice,
								PayFragment.ORDER_TYPE_YY);
					}
				}
			}
		}
	}

}
