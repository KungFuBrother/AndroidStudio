package yitgogo.smart.product.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.home.model.ModelCar;
import yitgogo.smart.home.model.ModelListPrice;
import yitgogo.smart.product.model.ModelProduct;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.Content;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.Notify;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;

public class ShoppingCarPlatformFragment extends BaseNotifyFragment implements
		OnClickListener {
	// 购物车部分
	ListView carList;
	List<ModelCar> modelCars;
	HashMap<String, ModelListPrice> priceMap;
	CarAdapter carAdapter;
	TextView selectAllButton, deleteButton;
	JSONArray carArray;
	boolean allSelected = true;

	TextView totalPriceTextView, buyButton;
	double totalMoney = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_shopping_car_platform);
		init();
		findViews();
	}

	private void init() {
		modelCars = new ArrayList<ModelCar>();
		priceMap = new HashMap<String, ModelListPrice>();
		carAdapter = new CarAdapter();
	}

	@Override
	public void onResume() {
		super.onResume();
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
		buyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openWindow(ShoppingCarPlatformBuyFragment.class.getName(),
						"确认订单");

			}
		});
	}

	private void refresh() {
		modelCars.clear();
		carAdapter.notifyDataSetChanged();
		totalPriceTextView.setText("");
		try {
			carArray = new JSONArray(Content.getStringContent(
					Parameters.CACHE_KEY_CAR, "[]"));
			for (int i = 0; i < carArray.length(); i++) {
				modelCars.add(new ModelCar(carArray.getJSONObject(i)));
			}
			carAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (modelCars.size() > 0) {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < modelCars.size(); i++) {
				if (i > 0) {
					stringBuilder.append(",");
				}
				stringBuilder.append(modelCars.get(i).getProduct().getId());
			}
			getProductPrice(stringBuilder.toString());
		} else {
			loadingEmpty("购物车还没有添加商品");
		}
	}

	private void addCount(int position) {
		ModelProduct product = modelCars.get(position).getProduct();
		long originalCount = modelCars.get(position).getProductCount();
		try {
			if (priceMap.containsKey(product.getId())) {
				if (product.getNum() > originalCount) {
					carArray.getJSONObject(position).remove("productCount");
					carArray.getJSONObject(position).put("productCount",
							originalCount + 1);
					Content.saveStringContent(Parameters.CACHE_KEY_CAR,
							carArray.toString());
					refresh();
				} else {
					Notify.show("库存不足");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void deleteCount(int position) {
		long originalCount = modelCars.get(position).getProductCount();
		if (originalCount > 1) {
			try {
				carArray.getJSONObject(position).remove("productCount");
				carArray.getJSONObject(position).put("productCount",
						originalCount - 1);
				Content.saveStringContent(Parameters.CACHE_KEY_CAR,
						carArray.toString());
				refresh();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void select(int position) {
		boolean originalSelection = modelCars.get(position).isSelected();
		try {
			carArray.getJSONObject(position).remove("isSelected");
			carArray.getJSONObject(position).put("isSelected",
					!originalSelection);
			Content.saveStringContent(Parameters.CACHE_KEY_CAR,
					carArray.toString());
			refresh();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void countTotalPrice() {
		allSelected = true;
		totalMoney = 0;
		for (int i = 0; i < modelCars.size(); i++) {
			if (modelCars.get(i).isSelected()) {
				ModelProduct product = modelCars.get(i).getProduct();
				if (priceMap.containsKey(product.getId())) {
					double price = priceMap.get(product.getId()).getPrice();
					long count = modelCars.get(i).getProductCount();
					if (price > 0) {
						totalMoney += count * price;
					}
				}
			} else {
				allSelected = false;
			}
		}
		if (allSelected) {
			selectAllButton.setText("全不选");
		} else {
			selectAllButton.setText("全选");
		}
		totalPriceTextView.setText(Parameters.CONSTANT_RMB
				+ decimalFormat.format(totalMoney));
	}

	private void selectAll() {
		try {
			// 当前已全选，改为全不选
			if (allSelected) {
				for (int i = 0; i < carArray.length(); i++) {
					carArray.getJSONObject(i).remove("isSelected");
					carArray.getJSONObject(i).put("isSelected", false);
				}
			} else {
				for (int i = 0; i < carArray.length(); i++) {
					carArray.getJSONObject(i).remove("isSelected");
					carArray.getJSONObject(i).put("isSelected", true);
				}
			}
			Content.saveStringContent(Parameters.CACHE_KEY_CAR,
					carArray.toString());
			refresh();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void delete() {
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < modelCars.size(); i++) {
			if (!modelCars.get(i).isSelected()) {
				try {
					jsonArray.put(carArray.getJSONObject(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		Content.saveStringContent(Parameters.CACHE_KEY_CAR,
				jsonArray.toString());
		refresh();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.car_selectall:
			selectAll();
			break;

		case R.id.car_delete:
			delete();
			break;

		default:
			break;
		}
	}

	private void getProductPrice(String productIds) {
		ProductTask.getProductPrice(getActivity(), productIds,
				new OnNetworkListener() {

					@Override
					public void onSuccess(NetworkMissionMessage requestMessage) {
						super.onSuccess(requestMessage);
						if (!TextUtils.isEmpty(requestMessage.getResult())) {
							JSONObject object;
							try {
								object = new JSONObject(requestMessage
										.getResult());
								if (object.getString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONArray priceArray = object
											.optJSONArray("dataList");
									if (priceArray != null) {
										for (int i = 0; i < priceArray.length(); i++) {
											ModelListPrice priceList = new ModelListPrice(
													priceArray.getJSONObject(i));
											priceMap.put(
													priceList.getProductId(),
													priceList);
										}
										countTotalPrice();
										carAdapter.notifyDataSetChanged();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

	class CarAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return modelCars.size();
		}

		@Override
		public Object getItem(int position) {
			return modelCars.get(position);
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
				holder.goodsImage = (ImageView) convertView
						.findViewById(R.id.list_car_image);
				holder.goodsPriceText = (TextView) convertView
						.findViewById(R.id.list_car_price);
				holder.guigeText = (TextView) convertView
						.findViewById(R.id.list_car_guige);
				holder.stateText = (TextView) convertView
						.findViewById(R.id.list_car_state);
				holder.selectButton = (LinearLayout) convertView
						.findViewById(R.id.list_car_select);
				holder.selection = (ImageView) convertView
						.findViewById(R.id.list_car_selection);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.addButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					addCount(index);
				}
			});
			holder.deleteButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteCount(index);
				}
			});
			holder.selectButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					select(index);
				}
			});
			ModelCar modelCar = modelCars.get(position);
			ModelProduct product = modelCar.getProduct();

			holder.countText.setText(modelCar.getProductCount() + "");
			if (modelCar.isSelected()) {
				holder.selection
						.setImageResource(R.drawable.iconfont_check_checked);
			} else {
				holder.selection
						.setImageResource(R.drawable.iconfont_check_normal);
			}

			holder.goodNameText.setText(product.getProductName());
			holder.guigeText.setText(product.getAttName());
			imageLoader.displayImage(getSmallImageUrl(product.getImg()),
					holder.goodsImage);

			if (priceMap.containsKey(product.getId())) {
				ModelListPrice price = priceMap.get(product.getId());
				holder.goodsPriceText.setText("¥"
						+ decimalFormat.format(price.getPrice()));
				if (price.getNum() > 0) {
					if (price.getNum() < 5) {
						holder.stateText.setText("仅剩" + price.getNum()
								+ product.getUnit());
					} else {
						holder.stateText.setText("有货");
					}
				} else {
					holder.stateText.setText("无货");
				}
			}
			return convertView;
		}

		class ViewHolder {
			ImageView goodsImage, addButton, deleteButton;
			TextView goodNameText, goodsPriceText, guigeText, countText,
					stateText;
			LinearLayout selectButton;
			ImageView selection;
		}
	}

}
