package yitgogo.consumer.order.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.home.model.ModelScoreProductDetail;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.model.ModelOrderResult;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.Notify;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author Tiger
 * 
 * @description 本地服务确认订单
 */
public class ScoreProductOrderConfirmFragment extends BaseNotifyFragment {

	String productId = "";
	int productCount = 1;
	double totalPrice = 0;
	ModelScoreProductDetail productDetail = new ModelScoreProductDetail();

	ImageView imageView;
	TextView nameTextView, priceTextView, countTextView, countAddButton,
			countDeleteButton, additionTextView;

	FrameLayout addressLayout, paymentLayout;
	TextView totalPriceTextView, confirmButton;

	OrderConfirmPartAddressFragment addressFragment;
	OrderConfirmPartPaymentFragment paymentFragment;

	List<ModelOrderResult> orderResults;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_confirm_order_sale);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(ScoreProductOrderConfirmFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(ScoreProductOrderConfirmFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		new GetProductDetail().execute();
	}

	private void init() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("productId")) {
				productId = bundle.getString("productId");
			}
		}
		addressFragment = new OrderConfirmPartAddressFragment();
		paymentFragment = new OrderConfirmPartPaymentFragment(true, false);
		orderResults = new ArrayList<ModelOrderResult>();
	}

	@Override
	protected void findViews() {
		imageView = (ImageView) contentView
				.findViewById(R.id.order_confirm_sale_image);
		nameTextView = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_name);
		priceTextView = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_price);
		countTextView = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_count);
		countDeleteButton = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_count_delete);
		countAddButton = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_count_add);
		additionTextView = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_addition);
		addressLayout = (FrameLayout) contentView
				.findViewById(R.id.order_confirm_sale_address);
		paymentLayout = (FrameLayout) contentView
				.findViewById(R.id.order_confirm_sale_payment);
		totalPriceTextView = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_total_money);
		confirmButton = (TextView) contentView
				.findViewById(R.id.order_confirm_sale_confirm);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		getFragmentManager().beginTransaction()
				.replace(R.id.order_confirm_sale_address, addressFragment)
				.replace(R.id.order_confirm_sale_payment, paymentFragment)
				.commit();
		countTextView.setText(productCount + "");
		countTotalPrice();
	}

	@Override
	protected void registerViews() {
		countDeleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteCount();
			}
		});
		countAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addCount();
			}
		});
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmOrder();
			}
		});
	}

	private void deleteCount() {
		if (productCount > 1) {
			productCount--;
		}
		if (productCount == 1) {
			countDeleteButton.setClickable(false);
		}
		countAddButton.setClickable(true);
		countTextView.setText(productCount + "");
		countTotalPrice();
	}

	private void addCount() {
		if (productCount < 100) {
			productCount++;
		}
		if (productCount == 100) {
			countAddButton.setClickable(false);
		}
		countDeleteButton.setClickable(true);
		countTextView.setText(productCount + "");
		countTotalPrice();
	}

	private void countTotalPrice() {
		totalPrice = productCount * productDetail.getJifenjia();
		totalPriceTextView.setText(Parameters.CONSTANT_RMB
				+ decimalFormat.format(totalPrice));
		additionTextView.setText("将扣除"
				+ (productCount * productDetail.getJifen() + "积分"));
	}

	private void confirmOrder() {
		if (totalPrice > 0) {
			if (addressFragment.getAddress() == null) {
				Notify.show("收货人信息有误");
			} else {
				new AddOrder().execute();
			}
		}
	}

	private void showDetail() {
		if (!productDetail.getImgs().isEmpty()) {
			ImageLoader.getInstance().displayImage(
					productDetail.getImgs().get(0), imageView);
		}
		nameTextView.setText(productDetail.getName());
		priceTextView.setText(Parameters.CONSTANT_RMB
				+ decimalFormat.format(productDetail.getJifenjia()));
		countTotalPrice();
	}

	/**
	 * 获取积分商品详情
	 * 
	 * @author Tiger
	 * 
	 */
	class GetProductDetail extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id", productId));
			return netUtil.postWithoutCookie(API.API_SCORE_PRODUCT_DETAIL,
					nameValuePairs, false, false);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
						productDetail = new ModelScoreProductDetail(
								object.optJSONObject("dataMap"));
						showDetail();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class AddOrder extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("userNumber", User
					.getUser().getUseraccount()));
			nameValuePairs.add(new BasicNameValuePair("customerName",
					addressFragment.getAddress().getPersonName()));
			nameValuePairs.add(new BasicNameValuePair("phone", addressFragment
					.getAddress().getPhone()));
			nameValuePairs
					.add(new BasicNameValuePair("shippingaddress",
							addressFragment.getAddress().getAreaAddress()
									+ addressFragment.getAddress()
											.getDetailedAddress()));
			nameValuePairs.add(new BasicNameValuePair("totalMoney", totalPrice
					+ ""));
			nameValuePairs.add(new BasicNameValuePair("sex", User.getUser()
					.getSex()));
			nameValuePairs.add(new BasicNameValuePair("age", User.getUser()
					.getAge()));
			nameValuePairs.add(new BasicNameValuePair("address", Store
					.getStore().getStoreArea()));
			nameValuePairs.add(new BasicNameValuePair("jmdId", Store.getStore()
					.getStoreId()));
			nameValuePairs.add(new BasicNameValuePair("orderType", "0"));
			JSONArray orderArray = new JSONArray();
			JSONObject object = new JSONObject();
			try {
				object.put("productIds", productDetail.getProductId());
				object.put("shopNum", productCount + "");
				object.put("price", productDetail.getJifenjia());
				object.put("isIntegralMall", "1");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			orderArray.put(object);
			nameValuePairs.add(new BasicNameValuePair("data", orderArray
					.toString()));
			return netUtil.postWithoutCookie(API.API_ORDER_ADD_CENTER,
					nameValuePairs, false, false);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
						Toast.makeText(getActivity(), "下单成功",
								Toast.LENGTH_SHORT).show();
						if (paymentFragment.getPaymentType() == OrderConfirmPartPaymentFragment.PAY_TYPE_CODE_ONLINE) {
							payMoney(object.optJSONArray("object"));
							getActivity().finish();
							return;
						}
						showOrder(PayFragment.ORDER_TYPE_YY);
						getActivity().finish();
						return;
					} else {
						hideLoading();
						Notify.show(object.optString("message"));
						return;
					}
				} catch (JSONException e) {
					hideLoading();
					Notify.show("下单失败");
					e.printStackTrace();
					return;
				}
			}
			hideLoading();
			Notify.show("下单失败");
		}
	}

}
