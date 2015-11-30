package yitgogo.consumer.local.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.local.model.ModelLocalServiceOrder;
import yitgogo.consumer.local.model.ModelLocalServiceOrderGoods;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.InnerGridView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class LocalServiceOrderFragment extends BaseNotifyFragment {

	PullToRefreshListView orderList;
	List<ModelLocalServiceOrder> localServiceOrders;
	OrderAdapter orderAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_order_platform);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(LocalServiceOrderFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(LocalServiceOrderFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		new GetLocalServiceOrder().execute();
	}

	private void init() {
		measureScreen();
		localServiceOrders = new ArrayList<ModelLocalServiceOrder>();
		orderAdapter = new OrderAdapter();
	}

	@Override
	protected void findViews() {
		orderList = (PullToRefreshListView) contentView
				.findViewById(R.id.order_list);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		orderList.setMode(Mode.BOTH);
		orderList.setAdapter(orderAdapter);
	}

	@Override
	protected void registerViews() {
		orderList.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				localServiceOrders.clear();
				orderAdapter.notifyDataSetChanged();
				pagenum = 0;
				orderList.setMode(Mode.BOTH);
				new GetLocalServiceOrder().execute();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				new GetLocalServiceOrder().execute();
			}
		});

	}

	class GetLocalServiceOrder extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
			pagenum++;
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
			valuePairs.add(new BasicNameValuePair("pageNo", pagenum + ""));
			valuePairs.add(new BasicNameValuePair("pageSize", pagesize + ""));
			valuePairs.add(new BasicNameValuePair("memberNumber", User
					.getUser().getUseraccount()));
			return netUtil.postWithoutCookie(
					API.API_LOCAL_BUSINESS_SERVICE_ORDER_LIST, valuePairs,
					false, false);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			orderList.onRefreshComplete();
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						JSONArray array = object.optJSONArray("dataList");
						if (array != null) {
							if (array.length() > 0) {
								if (array.length() < pagesize) {
									orderList.setMode(Mode.PULL_FROM_START);
								}
								for (int i = 0; i < array.length(); i++) {
									localServiceOrders
											.add(new ModelLocalServiceOrder(
													array.optJSONObject(i)));
								}
								orderAdapter.notifyDataSetChanged();
								return;
							} else {
								orderList.setMode(Mode.PULL_FROM_START);
							}

						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (localServiceOrders.size() == 0) {
					loadingEmpty();
				}
			} else {
				loadingFailed();
			}
		}

	}

	private void showOrderDetail(int position) {
		Bundle bundle = new Bundle();
		bundle.putString("localServiceOrderId", localServiceOrders
				.get(position).getId());
		jump(LocalServiceOrderDetailFragment.class.getName(), "订单详情", bundle);
	}

	class OrderAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return localServiceOrders.size();
		}

		@Override
		public Object getItem(int position) {
			return localServiceOrders.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.list_order, null);
				holder.orderProducts = (InnerGridView) convertView
						.findViewById(R.id.list_order_product);
				holder.orderStateText = (TextView) convertView
						.findViewById(R.id.list_order_state);
				holder.orderMoneyText = (TextView) convertView
						.findViewById(R.id.list_order_money);
				holder.orderTimeText = (TextView) convertView
						.findViewById(R.id.list_order_date);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ModelLocalServiceOrder localServiceOrder = localServiceOrders
					.get(position);
			holder.orderStateText.setText(localServiceOrder.getOrderState());
			holder.orderTimeText.setText(localServiceOrder.getOrderDate());
			holder.orderMoneyText.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(localServiceOrder.getOrderPrice()));
			if (localServiceOrder.getOrderGoods().size() > 1) {
				holder.orderProducts.setNumColumns(4);
			} else {
				holder.orderProducts.setNumColumns(1);
			}
			holder.orderProducts.setAdapter(new OrderProductAdapter(
					localServiceOrder.getOrderGoods()));
			final int index = position;
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showOrderDetail(index);
				}
			});
			holder.orderProducts
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							showOrderDetail(index);
						}
					});
			return convertView;
		}

		class ViewHolder {
			InnerGridView orderProducts;
			TextView orderStateText, orderMoneyText, orderTimeText;
		}
	}

	class OrderProductAdapter extends BaseAdapter {

		List<ModelLocalServiceOrderGoods> goods;

		public OrderProductAdapter(List<ModelLocalServiceOrderGoods> goods) {
			this.goods = goods;
		}

		@Override
		public int getCount() {
			return goods.size();
		}

		@Override
		public Object getItem(int position) {
			return goods.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(
						R.layout.list_product_order, null);
				holder.image = (ImageView) convertView
						.findViewById(R.id.list_product_order_image);
				holder.productNameText = (TextView) convertView
						.findViewById(R.id.list_product_order_name);
				holder.productPriceText = (TextView) convertView
						.findViewById(R.id.list_product_order_price);
				if (goods.size() > 1) {
					holder.productNameText.setVisibility(View.GONE);
					holder.productPriceText.setVisibility(View.GONE);
					holder.image.setLayoutParams(new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT));
					convertView.setLayoutParams(new AbsListView.LayoutParams(
							AbsListView.LayoutParams.MATCH_PARENT,
							screenWidth / 4));
				}
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ModelLocalServiceOrderGoods orderGoods = goods.get(position);
			holder.productNameText.setText(orderGoods.getProductName());
			holder.productPriceText.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(orderGoods.getProductUnitPrice()));
			ImageLoader.getInstance().displayImage(orderGoods.getImg(),
					holder.image);
			return convertView;
		}

		class ViewHolder {
			ImageView image;
			TextView productNameText, productPriceText;
		}

	}

}
