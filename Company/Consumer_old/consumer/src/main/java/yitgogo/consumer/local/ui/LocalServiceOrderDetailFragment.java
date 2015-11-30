package yitgogo.consumer.local.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.local.model.ModelLocalServiceOrder;
import yitgogo.consumer.local.model.ModelLocalServiceOrderGoods;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.view.InnerListView;
import yitgogo.consumer.view.NormalAskDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class LocalServiceOrderDetailFragment extends BaseNotifyFragment {

	SwipeRefreshLayout refreshLayout;
	TextView orderNumberText, orderStateText, orderDateText, senderTextView,
			senderNameTextView, senderPhoneTextView, userNameText,
			userPhoneText, userAddressText, moneyText, paymentText,
			deliverText;
	TextView payButton, receiveButton;
	InnerListView productList;
	ModelLocalServiceOrder localServiceOrder;
	List<ModelLocalServiceOrderGoods> serviceOrderGoods;
	OrderProductAdapter orderProductAdapter;
	String localServiceOrderId = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_order_service_detail);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(LocalServiceOrderDetailFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(LocalServiceOrderDetailFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		new GetLocalServiceOrderDetail().execute();
	}

	private void init() {
		serviceOrderGoods = new ArrayList<ModelLocalServiceOrderGoods>();
		orderProductAdapter = new OrderProductAdapter();
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("localServiceOrderId")) {
				localServiceOrderId = bundle.getString("localServiceOrderId");
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
				new GetLocalServiceOrderDetail().execute();
			}
		});
		payButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				payMoney(localServiceOrder.getOrderNumber(),
						localServiceOrder.getOrderPrice(),
						PayFragment.ORDER_TYPE_LS);
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
		serviceOrderGoods = localServiceOrder.getOrderGoods();
		orderProductAdapter.notifyDataSetChanged();
		orderNumberText.setText("订单号：" + localServiceOrder.getOrderNumber());
		orderStateText.setText(localServiceOrder.getOrderState());
		orderDateText.setText(localServiceOrder.getOrderDate());
		senderTextView.setText(localServiceOrder.getSupplyBean()
				.getServicename());
		senderNameTextView.setText(localServiceOrder.getSupplyBean()
				.getContacts());
		senderPhoneTextView.setText(localServiceOrder.getSupplyBean()
				.getContactphone()
				+ " / "
				+ localServiceOrder.getSupplyBean().getContacttelephone());
		userNameText.setText(localServiceOrder.getCustomerName());
		userPhoneText.setText(getSecretPhone(localServiceOrder
				.getCustomerPhone()));
		userAddressText.setText(localServiceOrder.getDeliveryAddress()
				+ localServiceOrder.getMustAddress());
		moneyText.setText(Parameters.CONSTANT_RMB
				+ decimalFormat.format(localServiceOrder.getOrderPrice()));
		paymentText.setText(localServiceOrder.getPaymentType());
		deliverText.setText(localServiceOrder.getDeliveryType());
		initAciotnBar();
	}

	private void initAciotnBar() {
		if (localServiceOrder.getOrderState().equalsIgnoreCase("新订单")) {
			payButton.setVisibility(View.VISIBLE);
			return;
		}
		if (localServiceOrder.getOrderState().equalsIgnoreCase("已发货")) {
			receiveButton.setVisibility(View.VISIBLE);
			return;
		}
		payButton.setVisibility(View.GONE);
		receiveButton.setVisibility(View.GONE);
	}

	class OrderProductAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return serviceOrderGoods.size();
		}

		@Override
		public Object getItem(int position) {
			return serviceOrderGoods.get(position);
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

			ModelLocalServiceOrderGoods orderGoods = serviceOrderGoods
					.get(position);
			holder.productNameText.setText(orderGoods.getProductName());
			holder.productPriceText.setText("¥"
					+ decimalFormat.format(orderGoods.getProductUnitPrice()));
			holder.productCountText.setText(" × " + orderGoods.getProductNum());
			ImageLoader.getInstance().displayImage(orderGoods.getImg(),
					holder.image);
			return convertView;
		}

		class ViewHolder {
			ImageView image;
			TextView productNameText, productAttrText, productPriceText,
					productCountText;
		}
	}

	class GetLocalServiceOrderDetail extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("localOrderId",
					localServiceOrderId));
			return netUtil.postWithoutCookie(
					API.API_LOCAL_BUSINESS_SERVICE_ORDER_DETAIL, parameters,
					false, false);
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
						JSONObject orderObject = object.optJSONObject("object");
						if (orderObject != null) {
							localServiceOrder = new ModelLocalServiceOrder(
									orderObject);
							showOrderDetail();
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
			parameters.add(new BasicNameValuePair("localOrderId",
					localServiceOrder.getId()));
			parameters.add(new BasicNameValuePair("localOrderNumber",
					localServiceOrder.getOrderNumber()));
			parameters.add(new BasicNameValuePair("orderState", "已收货"));
			return netUtil.postWithoutCookie(
					API.API_LOCAL_BUSINESS_SERVICE_ORDER_STATE_UPDATE,
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
						new GetLocalServiceOrderDetail().execute();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
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
