package yitgogo.consumer.local.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.local.model.ModelLocalService;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.ui.OrderConfirmPartAddressFragment;
import yitgogo.consumer.order.ui.OrderConfirmPartDeliverFragment;
import yitgogo.consumer.order.ui.OrderConfirmPartPaymentFragment;
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
import android.widget.Button;
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
public class LocalServiceOrderConfirmFragment extends BaseNotifyFragment {

	String productId = "";
	int productCount = 1;
	double totalPrice = 0;
	ModelLocalService service = new ModelLocalService();

	ImageView imageView;
	TextView nameTextView, priceTextView, countTextView, countAddButton,
			countDeleteButton;

	TextView totalMoneyTextView;
	Button confirmButton;

	OrderConfirmPartAddressFragment addressFragment;
	OrderConfirmPartDeliverFragment deliverFragment;
	OrderConfirmPartPaymentFragment paymentFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_buy_local_service);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(LocalServiceOrderConfirmFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(LocalServiceOrderConfirmFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		new GetServiceDetail().execute();
	}

	private void init() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("productId")) {
				productId = bundle.getString("productId");
			}
		}
		addressFragment = new OrderConfirmPartAddressFragment();
		deliverFragment = new OrderConfirmPartDeliverFragment();
		paymentFragment = new OrderConfirmPartPaymentFragment(false);
	}

	@Override
	protected void findViews() {
		imageView = (ImageView) contentView
				.findViewById(R.id.local_goods_order_goods_image);
		nameTextView = (TextView) contentView
				.findViewById(R.id.local_goods_order_goods_name);
		priceTextView = (TextView) contentView
				.findViewById(R.id.local_goods_order_goods_price);
		countTextView = (TextView) contentView
				.findViewById(R.id.local_goods_order_goods_count);
		countDeleteButton = (TextView) contentView
				.findViewById(R.id.local_goods_order_goods_count_delete);
		countAddButton = (TextView) contentView
				.findViewById(R.id.local_goods_order_goods_count_add);
		totalMoneyTextView = (TextView) contentView
				.findViewById(R.id.local_goods_order_total_money);
		confirmButton = (Button) contentView
				.findViewById(R.id.local_goods_order_confirm);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		getFragmentManager().beginTransaction()
				.replace(R.id.local_goods_order_part_address, addressFragment)
				.replace(R.id.local_goods_order_part_deliver, deliverFragment)
				.replace(R.id.local_goods_order_part_payment, paymentFragment)
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
		deliverFragment.setBuyCount(productCount);
		totalPrice = productCount * service.getProductPrice();
		totalMoneyTextView.setText(Parameters.CONSTANT_RMB
				+ decimalFormat.format(totalPrice));
	}

	private void confirmOrder() {
		if (totalPrice <= 0) {
			Notify.show("商品信息有误");
		} else if (addressFragment.getAddress() == null) {
			Notify.show("收货人地址有误");
		} else {
			new AddLocalServiceOrder().execute();
		}
	}

	private void showServiceInfo() {
		ImageLoader.getInstance().displayImage(service.getImg(), imageView);
		nameTextView.setText(service.getProductName());
		priceTextView.setText(Parameters.CONSTANT_RMB
				+ decimalFormat.format(service.getProductPrice()));
		deliverFragment.initDeliverType(service.isDeliverYN(),
				service.getDeliverNum());
		paymentFragment.setCanPaySend(service.isDeliveredToPaidYN());
		countTotalPrice();
	}

	class GetServiceDetail extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
			valuePairs.add(new BasicNameValuePair("productId", productId));
			return netUtil.postWithoutCookie(
					API.API_LOCAL_BUSINESS_SERVICE_DETAIL, valuePairs, false,
					false);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						JSONObject object2 = object.optJSONObject("object");
						if (object2 != null) {
							service = new ModelLocalService(object2);
							showServiceInfo();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 添加本地服服务订单
	 * 
	 * @author Tiger
	 * 
	 * @result {"message":"ok","state"
	 *         :"SUCCESS","cacheKey":null,"dataList":[],"totalCount"
	 *         :1,"dataMap":{"mapinfo":{"zongjine":"2500.0","productNum":"500",
	 *         "productPrice"
	 *         :"5.0","productName":"萝卜5元一斤"},"orderType":"产品","fuwuZuoji"
	 *         :"028-32562356"
	 *         ,"zongjine":"5.0","ordernumber":"YT5587710343","fuwushang"
	 *         :"测试运营中心一"
	 *         ,"shijian":"2015-08-04","fuwuPhone":"15821346521"},"object":null}
	 */
	class AddLocalServiceOrder extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
			valuePairs.add(new BasicNameValuePair("customerName",
					addressFragment.getAddress().getPersonName()));
			valuePairs.add(new BasicNameValuePair("customerPhone",
					addressFragment.getAddress().getPhone()));
			valuePairs.add(new BasicNameValuePair("deliveryType",
					deliverFragment.getDeliverTypeName()));
			switch (deliverFragment.getDeliverType()) {
			case OrderConfirmPartDeliverFragment.DELIVER_TYPE_SELF:
				valuePairs.add(new BasicNameValuePair("mustAddress", Store
						.getStore().getStoreAddess()));
				break;

			default:
				valuePairs.add(new BasicNameValuePair("deliveryAddress",
						addressFragment.getAddress().getAreaAddress()
								+ addressFragment.getAddress()
										.getDetailedAddress()));
				break;
			}
			valuePairs.add(new BasicNameValuePair("paymentType",
					paymentFragment.getPaymentName()));
			valuePairs.add(new BasicNameValuePair("orderType", service
					.getProductType()));
			valuePairs
					.add(new BasicNameValuePair("orderPrice", totalPrice + ""));
			valuePairs
					.add(new BasicNameValuePair("productId", service.getId()));
			valuePairs.add(new BasicNameValuePair("productNum", productCount
					+ ""));
			valuePairs.add(new BasicNameValuePair("memberNumber", User
					.getUser().getUseraccount()));
			valuePairs.add(new BasicNameValuePair("providerId", Store
					.getStore().getStoreId()));
			return netUtil.postWithoutCookie(
					API.API_LOCAL_BUSINESS_SERVICE_ORDER_ADD, valuePairs,
					false, false);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						Toast.makeText(getActivity(), "下单成功",
								Toast.LENGTH_SHORT).show();
						// 如果选择了在线支付
						if (paymentFragment.getPaymentType() == OrderConfirmPartPaymentFragment.PAY_TYPE_CODE_ONLINE) {
							JSONObject orderObject = object
									.optJSONObject("dataMap");
							if (orderObject != null) {
								double payPrice = orderObject
										.optDouble("zongjine");
								ArrayList<String> orderNumbers = new ArrayList<String>();
								orderNumbers.add(orderObject
										.optString("ordernumber"));
								if (orderNumbers.size() > 0) {
									if (payPrice > 0) {
										payMoney(orderNumbers, payPrice,
												PayFragment.ORDER_TYPE_LS);
										getActivity().finish();
										return;
									}
								}
							}
						}
						showOrder(PayFragment.ORDER_TYPE_LS);
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
