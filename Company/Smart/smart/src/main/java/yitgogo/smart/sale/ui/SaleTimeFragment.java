package yitgogo.smart.sale.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.home.model.ModelSaleTime;
import yitgogo.smart.sale.model.ModelSaleTimeProduct;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.tools.QrCodeTool;
import yitgogo.smart.view.InnerGridView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

public class SaleTimeFragment extends BaseNotifyFragment {

	FrameLayout imageLayout;
	HorizontalScrollView horizontalScrollView;
	GridView imageGridView;

	PullToRefreshScrollView refreshScrollView;
	InnerGridView gridView;
	List<ModelSaleTime> saleTimes;
	SaleClassAdapter saleClassAdapter;
	ModelSaleTime saleTime;

	List<ModelSaleTimeProduct> products;
	HashMap<String, Double> prices;
	ProductAdapter productAdapter;

	int columWidth = 340;
	int columHeight = 140;
	int pageSize = 20, pageNo = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_fragment_sale_time);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(SaleTimeFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(SaleTimeFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		selectSaleClass(saleTimes.get(HomeData.getInstance()
				.getSaleTimeSelection()));
	}

	private void init() {
		saleTimes = HomeData.getInstance().getSaleTimes();
		saleClassAdapter = new SaleClassAdapter();
		products = new ArrayList<ModelSaleTimeProduct>();
		prices = new HashMap<String, Double>();
		productAdapter = new ProductAdapter();
	}

	protected void findViews() {
		imageLayout = (FrameLayout) contentView.findViewById(R.id.image_layout);
		horizontalScrollView = (HorizontalScrollView) contentView
				.findViewById(R.id.image_scroll);
		imageGridView = (GridView) contentView.findViewById(R.id.image_grid);
		refreshScrollView = (PullToRefreshScrollView) contentView
				.findViewById(R.id.sale_time_product_scroll);
		gridView = (InnerGridView) contentView
				.findViewById(R.id.sale_time_product_list);
		initViews();
		registerViews();
	}

	protected void initViews() {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, columHeight);
		imageLayout.setLayoutParams(layoutParams);
		if (saleTimes.size() > 0) {
			int colums = saleTimes.size();
			imageGridView.setLayoutParams(new LinearLayout.LayoutParams(colums
					* columWidth, LinearLayout.LayoutParams.MATCH_PARENT));
			imageGridView.setColumnWidth(columWidth);
			imageGridView.setStretchMode(GridView.NO_STRETCH);
			imageGridView.setNumColumns(colums);
		}
		imageGridView.setAdapter(saleClassAdapter);
		gridView.setAdapter(productAdapter);
	}

	private void refresh() {
		refreshScrollView.setMode(Mode.BOTH);
		pageNo = 0;
		products.clear();
		productAdapter.notifyDataSetChanged();
		getSaleTime();
	}

	private void selectSaleClass(ModelSaleTime modelSaleTime) {

		if (!saleTimes.isEmpty()) {
			if (modelSaleTime != null) {
				saleTime = modelSaleTime;
			} else {
				saleTime = saleTimes.get(0);
			}
			saleClassAdapter.notifyDataSetChanged();
			refresh();
		}

	}

	protected void registerViews() {
		refreshScrollView
				.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						refresh();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						getSaleTime();
					}
				});
		imageGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectSaleClass(saleTimes.get(arg2));
			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// if (products.get(arg2).getStarttime()) {
				//
				// }
				showProductDetail(products.get(arg2).getProductId(),
						QrCodeTool.SALE_TYPE_TIME);
			}
		});
	}

	class SaleClassAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return saleTimes.size();
		}

		@Override
		public Object getItem(int position) {
			return saleTimes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = layoutInflater.inflate(
						R.layout.list_image_scroll, null);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.scroll_image);
				viewHolder.selectionImageView = (ImageView) convertView
						.findViewById(R.id.scroll_image_selection);
				LayoutParams layoutParams = new LayoutParams(columWidth,
						columHeight);
				convertView.setLayoutParams(layoutParams);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (saleTime.getSaleClassId().equals(
					saleTimes.get(position).getSaleClassId())) {
				viewHolder.selectionImageView.setVisibility(View.VISIBLE);
			} else {
				viewHolder.selectionImageView.setVisibility(View.GONE);
			}
			imageLoader.displayImage(getSmallImageUrl(saleTimes.get(position)
					.getSaleClassImage()), viewHolder.imageView);
			return convertView;
		}

		class ViewHolder {
			ImageView imageView, selectionImageView;
		}

	}

	// 获取gridview里面的商品

	class ProductAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return products.size();
		}

		@Override
		public Object getItem(int position) {
			return products.get(position);
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
						R.layout.list_product_grid, null);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.list_product_image);
				viewHolder.priceTextView = (TextView) convertView
						.findViewById(R.id.list_product_price);
				viewHolder.nameTextView = (TextView) convertView
						.findViewById(R.id.list_product_name);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 256);
				viewHolder.imageView.setLayoutParams(layoutParams);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			ModelSaleTimeProduct product = products.get(position);
			viewHolder.nameTextView.setText(product.getProductName());
			if (prices.containsKey(product.getProductId())) {
				viewHolder.priceTextView.setText(Parameters.CONSTANT_RMB
						+ decimalFormat.format(prices.get(product
								.getProductId())));
			}
			imageLoader.displayImage(getSmallImageUrl(product.getProtionImg()),
					viewHolder.imageView);
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView priceTextView, nameTextView;
		}
	}

	private void getSaleTime() {
		pageNo++;
		NetworkContent networkContent = new NetworkContent(
				API.API_SALE_TIME_LIST);
		networkContent.addParameters("pageNo", pageNo + "");
		networkContent.addParameters("pageSize", pageSize + "");
		networkContent.addParameters("flag", "1");
		networkContent.addParameters("pcid", saleTime.getSaleClassId());
		networkContent.addParameters("machine", Device.getDeviceCode());
		MissionController.startNetworkMission(getActivity(), networkContent,
				new OnNetworkListener() {
					@Override
					public void onStart() {
						super.onStart();
						showLoading();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						refreshScrollView.onRefreshComplete();
						hideLoading();
					}

					@Override
					public void onSuccess(NetworkMissionMessage message) {
						super.onSuccess(message);
						if (!TextUtils.isEmpty(message.getResult())) {
							JSONObject info;
							try {
								info = new JSONObject(message.getResult());
								if (info.getString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONArray productArray = info
											.optJSONArray("dataList");
									if (productArray != null) {
										if (productArray.length() > 1) {
											if (productArray.length() < pageSize) {
												refreshScrollView
														.setMode(Mode.PULL_FROM_START);
											}
											StringBuilder stringBuilder = new StringBuilder();
											for (int i = 0; i < productArray
													.length() - 1; i++) {
												ModelSaleTimeProduct product = new ModelSaleTimeProduct(
														productArray
																.optJSONObject(i));
												products.add(product);
												if (i > 0) {
													stringBuilder.append(",");
												}
												stringBuilder.append(product
														.getProductId());
											}
											productAdapter
													.notifyDataSetChanged();
											getProductPrice(stringBuilder
													.toString());
											return;
										}
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						refreshScrollView.setMode(Mode.PULL_FROM_START);
						if (products.size() == 0) {
							loadingEmpty();
						}
					}

				});
	}

	private void getProductPrice(String productIds) {
		NetworkContent networkContent = new NetworkContent(API.API_SALE_PRICE);
		networkContent.addParameters("productId", productIds);
		networkContent.addParameters("type", "0");
		MissionController.startNetworkMission(getActivity(), networkContent,
				new OnNetworkListener() {
					@Override
					public void onSuccess(NetworkMissionMessage message) {
						super.onSuccess(message);
						if (!TextUtils.isEmpty(message.getResult())) {
							try {
								JSONObject object = new JSONObject(message
										.getResult());
								if (object.optString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONArray array = object
											.optJSONArray("dataList");
									if (array != null) {
										for (int i = 0; i < array.length(); i++) {
											JSONObject jsonObject = array
													.optJSONObject(i);
											if (jsonObject != null) {
												prices.put(
														jsonObject
																.optString("id"),
														jsonObject
																.optDouble("price"));
											}
											productAdapter
													.notifyDataSetChanged();
										}
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});

	}

}
