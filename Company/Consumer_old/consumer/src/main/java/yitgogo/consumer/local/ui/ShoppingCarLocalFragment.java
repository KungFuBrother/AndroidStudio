package yitgogo.consumer.local.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.local.model.LocalCarController;
import yitgogo.consumer.local.model.ModelLocalCar;
import yitgogo.consumer.local.model.ModelLocalCarGoods;
import yitgogo.consumer.order.model.ModelStorePostInfo;
import yitgogo.consumer.product.ui.ShoppingCarFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserLoginFragment;
import yitgogo.consumer.view.InnerGridView;
import yitgogo.consumer.view.InnerListView;
import yitgogo.consumer.view.Notify;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class ShoppingCarLocalFragment extends BaseNotifyFragment implements
		OnClickListener {

	LinearLayout normalLayout;
	ListView carList;
	CarAdapter carAdapter;
	TextView selectAllButton;
	List<ModelLocalCar> localCars;

	TextView totalPriceTextView, buyButton;
	double totalMoney = 0;
	int getStorePostInfoCount = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_shopping_car);
		init();
		findViews();
	}

	private void init() {
		measureScreen();
		localCars = LocalCarController.getLocalCars();
		carAdapter = new CarAdapter();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(ShoppingCarLocalFragment.class.getName());
		refresh();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(ShoppingCarLocalFragment.class.getName());
	}

	protected void findViews() {
		carList = (ListView) contentView.findViewById(R.id.car_list);
		selectAllButton = (TextView) contentView
				.findViewById(R.id.car_selectall);
		totalPriceTextView = (TextView) contentView
				.findViewById(R.id.car_total);

		buyButton = (TextView) contentView.findViewById(R.id.car_buy);

		normalLayout = (LinearLayout) contentView
				.findViewById(R.id.normal_layout);

		initViews();
		registerViews();
	}

	protected void initViews() {
		carList.setAdapter(carAdapter);
		addTextButton("易商城", new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(ShoppingCarFragment.class.getName(), "易商城购物车");
				getActivity().finish();
			}
		});
		addTextButton("云商城", new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(yitgogo.consumer.suning.ui.ShoppingCarFragment.class.getName(), "云商城购物车");
				getActivity().finish();
			}
		});
		addImageButton(R.drawable.get_goods_delete, "删除",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						showLoading();
						LocalCarController.deleteSelectedGoods();
						hideLoading();
						refresh();
					}
				});
	}

	@Override
	protected void registerViews() {
		selectAllButton.setOnClickListener(this);
		buyButton.setOnClickListener(this);
	}

	private void refresh() {
		showLoading();
		localCars = LocalCarController.getLocalCars();
		carAdapter.notifyDataSetChanged();
		if (localCars.isEmpty()) {
			loadingEmpty("购物车还没有添加商品");
			normalLayout.setVisibility(View.GONE);
		} else {
			normalLayout.setVisibility(View.VISIBLE);
		}
		if (getStorePostInfoCount == 0) {
			new GetStoreInfo().execute();
		} else {
			countTotalPrice();
		}
		getStorePostInfoCount++;
		hideLoading();
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
			showLoading();
			LocalCarController.selectAll();
			hideLoading();
			refresh();
			break;

		case R.id.car_buy:
			if (User.getUser().isLogin()) {
				if (totalMoney > 0) {
					jump(LocalCarBuyFragment.class.getName(), "确认订单");
				} else {
					Notify.show("请选择要购买商品");
				}
			} else {
				Notify.show("请先登录");
				jump(UserLoginFragment.class.getName(), "登录");
			}
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
				convertView = layoutInflater.inflate(R.layout.list_car_local,
						null);
				holder = new ViewHolder();
				holder.selectionImageView = (ImageView) convertView
						.findViewById(R.id.local_car_check);
				holder.selectButton = (LinearLayout) convertView
						.findViewById(R.id.local_car_selectall);
				holder.storeTextView = (TextView) convertView
						.findViewById(R.id.local_car_store);
				holder.storePostInfoTextView = (TextView) convertView
						.findViewById(R.id.local_car_post_info);
				holder.moneyTextView = (TextView) convertView
						.findViewById(R.id.local_car_total);
				holder.moneyDetailTextView = (TextView) convertView
						.findViewById(R.id.local_car_total_detail);
				holder.goodsListView = (InnerListView) convertView
						.findViewById(R.id.local_car_list);
				holder.diliverPayButton = (LinearLayout) convertView
						.findViewById(R.id.local_car_pay_diliver);
				holder.diliverPayTextView = (TextView) convertView
						.findViewById(R.id.local_car_pay_diliver_type);
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
			holder.storeTextView.setText(localCar.getStore().getServicename());
			holder.storePostInfoTextView
					.setText(getStorePostInfoString(localCar.getStorePostInfo()));
			holder.moneyTextView.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(localCar.getTotalMoney()));
			holder.moneyDetailTextView.setText(getMoneyDetailString(
					localCar.getGoodsMoney(), localCar.getPostFee()));
			holder.goodsListView.setAdapter(new CarGoodsAdapter(localCar));
			holder.diliverPayTextView.setText(getDiliverPayString(localCar));
			holder.selectButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showLoading();
					LocalCarController.selectStore(localCar);
					hideLoading();
					refresh();
				}
			});
			holder.diliverPayButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new DiliverPaymentDialog(localCar).show(
							getFragmentManager(), null);
				}
			});
			return convertView;
		}

		class ViewHolder {
			ImageView selectionImageView;
			TextView storeTextView, storePostInfoTextView, diliverPayTextView,
					moneyTextView, moneyDetailTextView;
			LinearLayout selectButton, diliverPayButton;
			InnerListView goodsListView;
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
			ViewHolder holder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(
						R.layout.list_car_local_goods, null);
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
			final ModelLocalCarGoods goods = localCar.getCarGoods().get(
					position);
			if (goods.isSelected()) {
				holder.selectionImageView
						.setImageResource(R.drawable.iconfont_check_checked);
			} else {
				holder.selectionImageView
						.setImageResource(R.drawable.iconfont_check_normal);
			}
			ImageLoader.getInstance().displayImage(
					getSmallImageUrl(goods.getGoods().getBigImgUrl()),
					holder.goodsImageView);
			holder.goodNameText.setText(goods.getGoods()
					.getRetailProdManagerName());
			holder.guigeText.setText(goods.getGoods().getAttName());
			holder.goodsPriceText.setText("¥"
					+ decimalFormat.format(goods.getGoods().getRetailPrice()));
			holder.countText.setText(goods.getGoodsCount() + "");
			holder.addButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showLoading();
					LocalCarController.addCount(goods);
					hideLoading();
					refresh();
				}
			});
			holder.deleteButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showLoading();
					LocalCarController.deleteCount(goods);
					hideLoading();
					refresh();
				}
			});
			holder.selectButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showLoading();
					LocalCarController.select(goods);
					hideLoading();
					refresh();
				}
			});
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
				String result = netUtil.postWithoutCookie(
						API.API_STORE_SEND_FEE, nameValuePairs, false, false);
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

	class DiliverPaymentDialog extends DialogFragment {

		View dialogView;
		InnerGridView diliverGridView, paymentGridView;
		TextView okButton;
		ModelLocalCar localCar;
		DiliverAdapter diliverAdapter;
		PaymentAdapter paymentAdapter;

		public DiliverPaymentDialog(ModelLocalCar localCar) {
			this.localCar = localCar;
			diliverAdapter = new DiliverAdapter(localCar);
			paymentAdapter = new PaymentAdapter(localCar);
		}

		@Override
		public void onCreate(@Nullable Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setCancelable(false);
			findViews();
		}

		@Override
		@NonNull
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Dialog dialog = new Dialog(getActivity());
			dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(dialogView, new LayoutParams(
					LayoutParams.MATCH_PARENT, screenWidth));
			return dialog;
		}

		private void findViews() {
			dialogView = layoutInflater.inflate(
					R.layout.dialog_diliver_payment, null);
			okButton = (TextView) dialogView.findViewById(R.id.dialog_ok);
			diliverGridView = (InnerGridView) dialogView
					.findViewById(R.id.diliver_types);
			paymentGridView = (InnerGridView) dialogView
					.findViewById(R.id.payment_types);
			initViews();
		}

		private void initViews() {
			diliverGridView.setAdapter(diliverAdapter);
			paymentGridView.setAdapter(paymentAdapter);
			okButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showLoading();
					LocalCarController.selectDiliverAndPayment(localCar,
							localCar.getDiliver(), localCar.getPayment());
					hideLoading();
					refresh();
					dismiss();
				}
			});
			diliverGridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (localCar.getDiliver().getType() != localCar
							.getDilivers().get(arg2).getType()) {
						localCar.setDiliver(localCar.getDilivers().get(arg2));
						diliverAdapter.notifyDataSetChanged();
					}
				}
			});
			paymentGridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (localCar.getPayment().getType() != localCar
							.getPayments().get(arg2).getType()) {
						localCar.setPayment(localCar.getPayments().get(arg2));
						paymentAdapter.notifyDataSetChanged();
					}
				}
			});
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
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.diliver_payment_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (localCar.getDiliver().getType() == localCar.getDilivers()
					.get(position).getType()) {
				viewHolder.textView.setTextColor(getResources().getColor(
						R.color.red));
				viewHolder.textView
						.setBackgroundResource(R.drawable.back_trans_rec_border_red);
			} else {
				viewHolder.textView.setTextColor(getResources().getColor(
						R.color.textColorSecond));
				viewHolder.textView
						.setBackgroundResource(R.drawable.back_trans_rec_border);
			}
			viewHolder.textView.setText(localCar.getDilivers().get(position)
					.getName());
			return convertView;
		}

		class ViewHolder {
			TextView textView;
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
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.diliver_payment_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (localCar.getPayment().getType() == localCar.getPayments()
					.get(position).getType()) {
				viewHolder.textView.setTextColor(getResources().getColor(
						R.color.red));
				viewHolder.textView
						.setBackgroundResource(R.drawable.back_trans_rec_border_red);
			} else {
				viewHolder.textView.setTextColor(getResources().getColor(
						R.color.textColorSecond));
				viewHolder.textView
						.setBackgroundResource(R.drawable.back_trans_rec_border);
			}
			viewHolder.textView.setText(localCar.getPayments().get(position)
					.getName());
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}

}
