package yitgogo.consumer.local.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.local.model.ModelLocalGoodsOrder;
import yitgogo.consumer.local.model.ModelLocalGoodsOrderGoods;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.InnerListView;
import yitgogo.consumer.view.NormalAskDialog;
import yitgogo.consumer.view.Notify;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class LocalGoodsOrderDetailFragment extends BaseNotifyFragment {

	SwipeRefreshLayout refreshLayout;
	TextView orderNumberText, orderStateText, orderDateText, senderTextView,
			senderNameTextView, senderPhoneTextView, userNameText,
			userPhoneText, userAddressText, moneyText, paymentText,
			deliverText;
	TextView payButton, receiveButton;
	InnerListView productList;
	ModelLocalGoodsOrder localGoodsOrder;
	List<ModelLocalGoodsOrderGoods> orderGoods;
	OrderProductAdapter orderProductAdapter;
	String localGoodsOrderNumber = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_order_service_detail);
		init();
		findViews();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(LocalGoodsOrderDetailFragment.class.getName());
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(LocalGoodsOrderDetailFragment.class.getName());
		new GetLocalGoodsOrderDetail().execute();
	}

	private void init() {
		orderGoods = new ArrayList<ModelLocalGoodsOrderGoods>();
		orderProductAdapter = new OrderProductAdapter();
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("localGoodsOrderNumber")) {
				localGoodsOrderNumber = bundle
						.getString("localGoodsOrderNumber");
			}
		}
	}

	@Override
	protected void findViews() {
		refreshLayout = (SwipeRefreshLayout) contentView
				.findViewById(R.id.order_detail_refresh);
		orderNumberText = (TextView) contentView
				.findViewById(R.id.order_detail_number);
		orderStateText = (TextView) contentView
				.findViewById(R.id.order_detail_state);
		orderDateText = (TextView) contentView
				.findViewById(R.id.order_detail_date);
		senderTextView = (TextView) contentView
				.findViewById(R.id.order_detail_sender);
		senderNameTextView = (TextView) contentView
				.findViewById(R.id.order_detail_sender_name);
		senderPhoneTextView = (TextView) contentView
				.findViewById(R.id.order_detail_sender_phone);
		userNameText = (TextView) contentView
				.findViewById(R.id.order_detail_user_name);
		userPhoneText = (TextView) contentView
				.findViewById(R.id.order_detail_user_phone);
		userAddressText = (TextView) contentView
				.findViewById(R.id.order_detail_user_address);
		moneyText = (TextView) contentView
				.findViewById(R.id.order_detail_total_money);
		paymentText = (TextView) contentView
				.findViewById(R.id.order_detail_payment);
		deliverText = (TextView) contentView
				.findViewById(R.id.order_detail_delivery);
		productList = (InnerListView) contentView
				.findViewById(R.id.order_detail_product);
		payButton = (TextView) contentView
				.findViewById(R.id.order_detail_action_pay);
		receiveButton = (TextView) contentView
				.findViewById(R.id.order_detail_action_receive);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		productList.setAdapter(orderProductAdapter);
	}

	@Override
	protected void registerViews() {
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new GetLocalGoodsOrderDetail().execute();
			}
		});
		payButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				payMoney(localGoodsOrder.getRetailOrderNumber(),
						localGoodsOrder.getRetailOrderPrice(),
						PayFragment.ORDER_TYPE_LP);
			}
		});
		receiveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NormalAskDialog askDialog = new NormalAskDialog(
						"确认已经收到此订单中的货物了吗？", "收到了", "没收到") {
					@Override
					public void onDismiss(DialogInterface dialog) {
						if (makeSure) {
							new Received().execute();
						}
						super.onDismiss(dialog);
					}
				};
				askDialog.show(getFragmentManager(), null);
			}
		});
	}

	private void showOrderDetail() {
		orderGoods = localGoodsOrder.getOrderGoods();
		orderProductAdapter.notifyDataSetChanged();
		orderNumberText
				.setText("订单号：" + localGoodsOrder.getRetailOrderNumber());
		orderStateText.setText(localGoodsOrder.getRetailOrderStatus());
		orderDateText.setText(localGoodsOrder.getOrderDate());
		senderTextView.setText(localGoodsOrder.getSourceProviderBean()
				.getServicename());
		senderNameTextView.setText(localGoodsOrder.getSourceProviderBean()
				.getContacts());
		senderPhoneTextView
				.setText(localGoodsOrder.getSourceProviderBean()
						.getContactphone()
						+ " / "
						+ localGoodsOrder.getSourceProviderBean()
								.getContacttelephone());
		userNameText.setText(localGoodsOrder.getCustomerName());
		userPhoneText
				.setText(getSecretPhone(localGoodsOrder.getCustomerPhone()));
		userAddressText.setText(localGoodsOrder.getDeliveryAddress()
				+ localGoodsOrder.getMustAddress());
		moneyText.setText(Parameters.CONSTANT_RMB
				+ decimalFormat.format(localGoodsOrder.getRetailOrderPrice()));
		if (localGoodsOrder.getPaymentType().equals("2")) {
			paymentText.setText("在线支付");
		} else {
			paymentText.setText("货到付款");
		}
		deliverText.setText(localGoodsOrder.getDeliveryType());
		initAciotnBar();
	}

	private void initAciotnBar() {
		if (localGoodsOrder.getRetailOrderStatus().equalsIgnoreCase("新订单")) {
			payButton.setVisibility(View.VISIBLE);
			return;
		}
		if (localGoodsOrder.getRetailOrderStatus().equalsIgnoreCase("已发货")) {
			receiveButton.setVisibility(View.VISIBLE);
			return;
		}
		payButton.setVisibility(View.GONE);
		receiveButton.setVisibility(View.GONE);
	}

	class OrderProductAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return orderGoods.size();
		}

		@Override
		public Object getItem(int position) {
			return orderGoods.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(
						R.layout.list_order_product, null);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.list_product_img);
				holder.productNameText = (TextView) convertView
						.findViewById(R.id.list_product_name);
				holder.productAttrText = (TextView) convertView
						.findViewById(R.id.list_product_attr);
				holder.productPriceText = (TextView) convertView
						.findViewById(R.id.list_product_price);
				holder.productCountText = (TextView) convertView
						.findViewById(R.id.list_product_count);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ModelLocalGoodsOrderGoods localGoodsOrderGoods = orderGoods
					.get(position);
			holder.productNameText.setText(localGoodsOrderGoods
					.getRetailProductName());
			holder.productPriceText.setText("¥"
					+ decimalFormat.format(localGoodsOrderGoods
							.getRetailProductUnitPrice()));
			holder.productCountText.setText(" × "
					+ localGoodsOrderGoods.getProductQuantity());
			ImageLoader.getInstance().displayImage(
					localGoodsOrderGoods.getImg(), holder.image);
			return convertView;
		}

		class ViewHolder {
			ImageView image;
			TextView productNameText, productAttrText, productPriceText,
					productCountText;
		}
	}

	class GetLocalGoodsOrderDetail extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("memberAccount", User
					.getUser().getUseraccount()));
			parameters.add(new BasicNameValuePair("pageNo", "1"));
			parameters.add(new BasicNameValuePair("pageSize", "1"));
			parameters.add(new BasicNameValuePair("orderNumber",
					localGoodsOrderNumber));
			return netUtil.postWithoutCookie(
					API.API_LOCAL_BUSINESS_GOODS_ORDER_LIST, parameters, false,
					false);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			refreshLayout.setRefreshing(false);
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
						JSONArray array = object.optJSONArray("dataList");
						if (array != null) {
							if (array.length() > 0) {
								localGoodsOrder = new ModelLocalGoodsOrder(
										array.optJSONObject(0));
								showOrderDetail();
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class Received extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("retailOrderID",
					localGoodsOrder.getId()));
			parameters.add(new BasicNameValuePair("retailOrderNumber",
					localGoodsOrder.getRetailOrderNumber()));
			parameters.add(new BasicNameValuePair("retailState", "已收货"));
			return netUtil.postWithoutCookie(
					API.API_LOCAL_BUSINESS_GOODS_ORDER_STATE_UPDATE,
					parameters, false, false);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
						new GetLocalGoodsOrderDetail().execute();
						return;
					}
					Notify.show(object.getString("message"));
					return;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			Notify.show("确认收货失败");
			return;
		}
	}

	private String getSecretPhone(String phone) {
		int length = phone.length();
		if (length > 3) {
			String string = "";
			if (length < 8) {
				string = phone.substring(0, 3) + "****";
			} else {
				string = phone.substring(0, 3) + "****"
						+ phone.substring(7, length);
			}
			return string;
		}
		return "***";
	}
}
