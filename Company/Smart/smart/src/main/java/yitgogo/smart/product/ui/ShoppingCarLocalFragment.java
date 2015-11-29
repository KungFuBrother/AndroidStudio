package yitgogo.smart.product.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.local.LocalCarBuyFragment;
import yitgogo.smart.local.model.LocalCarController;
import yitgogo.smart.local.model.ModelLocalCar;
import yitgogo.smart.local.model.ModelLocalCarGoods;
import yitgogo.smart.order.model.ModelStorePostInfo;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.InnerListView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

public class ShoppingCarLocalFragment extends BaseNotifyFragment implements
		OnClickListener {

	// 购物车部分
	ListView carList;
	CarAdapter carAdapter;
	TextView selectAllButton, deleteButton;
	List<ModelLocalCar> localCars;

	TextView totalPriceTextView, buyButton;
	double totalMoney = 0;
	int getStorePostInfoCount = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_shopping_car_platform);
		init();
		findViews();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(ShoppingCarLocalFragment.class.getName());
	}

	private void init() {
		localCars = LocalCarController.getLocalCars();
		carAdapter = new CarAdapter();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(ShoppingCarLocalFragment.class.getName());
		refresh();
	}

	protected void findViews() {
		carList = (ListView) contentView.findViewById(R.id.car_list);
		selectAllButton = (TextView) contentView
				.findViewById(R.id.car_selectall);
		deleteButton = (TextView) contentView.findViewById(R.id.car_delete);
		totalPriceTextView = (TextView) contentView
				.findViewById(R.id.car_total);

		buyButton = (TextView) contentView.findViewById(R.id.car_buy);

		initViews();
		registerViews();
	}

	protected void initViews() {
		carList.setAdapter(carAdapter);
	}

	@Override
	protected void registerViews() {
		selectAllButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
		buyButton.setOnClickListener(this);
	}

	private void refresh() {
		localCars = LocalCarController.getLocalCars();
		carAdapter.notifyDataSetChanged();
		if (localCars.isEmpty()) {
			loadingEmpty("购物车还没有添加商品");
		}
		if (getStorePostInfoCount == 0) {
			new GetStoreInfo().execute();
		} else {
			countTotalPrice();
		}
		getStorePostInfoCount++;
	}

	private void countTotalPrice() {
		totalMoney = 0;
		for (int i = 0; i < localCars.size(); i++) {
			totalMoney += localCars.get(i).getTotalMoney();
		}
		totalPriceTextView.setText(Parameters.CONSTANT_RMB
				+ decimalFormat.format(totalMoney));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.car_selectall:
			LocalCarController.selectAll();
			refresh();
			break;

		case R.id.car_delete:
			LocalCarController.deleteSelectedGoods();
			refresh();
			break;

		case R.id.car_buy:
			openWindow(LocalCarBuyFragment.class.getName(), "确认订单");
			break;

		default:
			break;
		}
	}

	class CarAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return localCars.size();
		}

		@Override
		public Object getItem(int position) {
			return localCars.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.list_local_car,
						null);
				holder = new ViewHolder();
				holder.selectionImageView = (ImageView) convertView
						.findViewById(R.id.local_car_store_selection);
				holder.selectButton = (LinearLayout) convertView
						.findViewById(R.id.local_car_store_select);
				holder.storeImageView = (ImageView) convertView
						.findViewById(R.id.local_car_store_image);
				holder.storeTextView = (TextView) convertView
						.findViewById(R.id.local_car_store_name);
				holder.storePostInfoTextView = (TextView) convertView
						.findViewById(R.id.local_car_store_postinfo);
				holder.moneyTextView = (TextView) convertView
						.findViewById(R.id.local_car_store_money);
				holder.moneyDetailTextView = (TextView) convertView
						.findViewById(R.id.local_car_store_money_detail);
				holder.goodsListView = (InnerListView) convertView
						.findViewById(R.id.local_car_goods);
				holder.diliverListView = (InnerListView) convertView
						.findViewById(R.id.diliver_type);
				holder.paymentListView = (InnerListView) convertView
						.findViewById(R.id.payment_type);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (localCars.get(position).isSelected()) {
				holder.selectionImageView
						.setImageResource(R.drawable.iconfont_check_checked);
			} else {
				holder.selectionImageView
						.setImageResource(R.drawable.iconfont_check_normal);
			}
			final ModelLocalCar localCar = localCars.get(position);
			imageLoader.displayImage(localCar.getStore().getImghead(),
					holder.storeImageView);
			holder.storeTextView.setText(localCar.getStore().getServicename());
			holder.storePostInfoTextView
					.setText(getStorePostInfoString(localCar.getStorePostInfo()));
			holder.moneyTextView.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(localCar.getTotalMoney()));
			holder.moneyDetailTextView.setText(getMoneyDetailString(
					localCar.getGoodsMoney(), localCar.getPostFee()));
			holder.goodsListView.setAdapter(new CarGoodsAdapter(localCar));
			holder.diliverListView.setAdapter(new DiliverAdapter(localCar));
			holder.paymentListView.setAdapter(new PaymentAdapter(localCar));
			holder.selectButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					LocalCarController.selectStore(localCar);
					refresh();
				}
			});
			holder.diliverListView
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							if (localCar.getDiliver().getType() != localCar
									.getDilivers().get(arg2).getType()) {
								LocalCarController.selectDiliver(localCar,
										localCar.getDilivers().get(arg2));
								refresh();
							}
						}
					});
			holder.paymentListView
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							if (localCar.getPayment().getType() != localCar
									.getPayments().get(arg2).getType()) {
								LocalCarController.selectPayment(localCar,
										localCar.getPayments().get(arg2));
								refresh();
							}
						}
					});
			return convertView;
		}

		class ViewHolder {
			ImageView selectionImageView, storeImageView;
			TextView storeTextView, storePostInfoTextView, moneyTextView,
					moneyDetailTextView;
			LinearLayout selectButton;
			InnerListView goodsListView, diliverListView, paymentListView;
		}
	}

	class CarGoodsAdapter extends BaseAdapter {

		ModelLocalCar localCar = new ModelLocalCar();

		public CarGoodsAdapter(ModelLocalCar localCar) {
			this.localCar = localCar;
		}

		@Override
		public int getCount() {
			return localCar.getCarGoods().size();
		}

		@Override
		public Object getItem(int position) {
			return localCar.getCarGoods().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int index = position;
			ViewHolder holder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.list_car, null);
				holder = new ViewHolder();
				holder.addButton = (ImageView) convertView
						.findViewById(R.id.list_car_count_add);
				holder.countText = (TextView) convertView
						.findViewById(R.id.list_car_count);
				holder.deleteButton = (ImageView) convertView
						.findViewById(R.id.list_car_count_delete);
				holder.goodNameText = (TextView) convertView
						.findViewById(R.id.list_car_title);
				holder.goodsImageView = (ImageView) convertView
						.findViewById(R.id.list_car_image);
				holder.goodsPriceText = (TextView) convertView
						.findViewById(R.id.list_car_price);
				holder.guigeText = (TextView) convertView
						.findViewById(R.id.list_car_guige);
				holder.stateText = (TextView) convertView
						.findViewById(R.id.list_car_state);
				holder.selectButton = (LinearLayout) convertView
						.findViewById(R.id.list_car_select);
				holder.selectionImageView = (ImageView) convertView
						.findViewById(R.id.list_car_selection);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.addButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					LocalCarController.addCount(localCar.getCarGoods().get(
							index));
					refresh();
				}
			});
			holder.deleteButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					LocalCarController.deleteCount(localCar.getCarGoods().get(
							index));
					refresh();
				}
			});
			holder.selectButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					LocalCarController
							.select(localCar.getCarGoods().get(index));
					refresh();
				}
			});
			ModelLocalCarGoods goods = localCar.getCarGoods().get(position);
			holder.countText.setText(goods.getGoodsCount() + "");
			if (goods.isSelected()) {
				holder.selectionImageView
						.setImageResource(R.drawable.iconfont_check_checked);
			} else {
				holder.selectionImageView
						.setImageResource(R.drawable.iconfont_check_normal);
			}
			holder.goodNameText.setText(goods.getGoods()
					.getRetailProdManagerName());
			imageLoader.displayImage(getSmallImageUrl(goods.getGoods()
					.getBigImgUrl()), holder.goodsImageView);
			holder.goodsPriceText.setText("¥"
					+ decimalFormat.format(goods.getGoods().getRetailPrice()));
			return convertView;
		}

		class ViewHolder {
			ImageView selectionImageView, goodsImageView, addButton,
					deleteButton;
			TextView goodNameText, goodsPriceText, guigeText, countText,
					stateText;
			LinearLayout selectButton;
		}
	}

	class DiliverAdapter extends BaseAdapter {

		ModelLocalCar localCar = new ModelLocalCar();

		public DiliverAdapter(ModelLocalCar localCar) {
			this.localCar = localCar;
		}

		@Override
		public int getCount() {
			return localCar.getDilivers().size();
		}

		@Override
		public Object getItem(int position) {
			return localCar.getDilivers().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(
						R.layout.list_diliver_payment, null);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.diliver_payment_check);
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.diliver_payment_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (localCar.getDiliver().getType() == localCar.getDilivers()
					.get(position).getType()) {
				viewHolder.imageView
						.setImageResource(R.drawable.iconfont_check_checked);
			} else {
				viewHolder.imageView
						.setImageResource(R.drawable.iconfont_check_normal);
			}
			viewHolder.textView.setText(localCar.getDilivers().get(position)
					.getName());
			return convertView;
		}

	}

	class PaymentAdapter extends BaseAdapter {

		ModelLocalCar localCar = new ModelLocalCar();

		public PaymentAdapter(ModelLocalCar localCar) {
			this.localCar = localCar;
		}

		@Override
		public int getCount() {
			return localCar.getPayments().size();
		}

		@Override
		public Object getItem(int position) {
			return localCar.getPayments().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(
						R.layout.list_diliver_payment, null);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.diliver_payment_check);
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.diliver_payment_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (localCar.getPayment().getType() == localCar.getPayments()
					.get(position).getType()) {
				viewHolder.imageView
						.setImageResource(R.drawable.iconfont_check_checked);
			} else {
				viewHolder.imageView
						.setImageResource(R.drawable.iconfont_check_normal);
			}
			viewHolder.textView.setText(localCar.getPayments().get(position)
					.getName());
			return convertView;
		}

	}

	class ViewHolder {
		ImageView imageView;
		TextView textView;
	}

	class GetStoreInfo extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			for (int i = 0; i < localCars.size(); i++) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("no", localCars
						.get(i).getStore().getNo()));
				// {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],"totalCount":1,"dataMap":{"whetherToUseStockSystem":false,"hawManyPackages":50.0,"autoPurchase":false,"supportForDelivery":true,"postage":10.0},"object":null}
				String result = MissionController.post(API.API_STORE_SEND_FEE,
						nameValuePairs);
				if (!TextUtils.isEmpty(result)) {
					JSONObject object;
					try {
						object = new JSONObject(result);
						if (object.optString("state").equalsIgnoreCase(
								"SUCCESS")) {
							ModelStorePostInfo storePostInfo = new ModelStorePostInfo(
									object.optJSONObject("dataMap"));
							localCars.get(i).setStorePostInfo(storePostInfo);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			LocalCarController.save(localCars);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			hideLoading();
			refresh();
		}

	}

}
