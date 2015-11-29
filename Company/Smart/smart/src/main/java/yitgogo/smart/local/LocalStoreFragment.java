package yitgogo.smart.local;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.home.model.ModelLocalStore;
import yitgogo.smart.local.model.ModelLocalGoods;
import yitgogo.smart.local.model.ModelLocalService;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.InnerGridView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
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

public class LocalStoreFragment extends BaseNotifyFragment {

	FrameLayout imageLayout;
	HorizontalScrollView horizontalScrollView;
	GridView imageGridView;

	int columWidth = 340;
	int columHeight = 140;

	int goodsPage = 0, servicePage = 0;
	int pageSize = 12;

	TextView tabGoods, tabService;

	FrameLayout goodsLayout, serviceLayout;
	PullToRefreshScrollView goodsScrollView, serviceScrollView;
	InnerGridView goodsProductGridView, serviceProductGridView;

	List<ModelLocalStore> localStores;
	StoreAdapter storeAdapter;
	ModelLocalStore store;

	List<ModelLocalService> localServices;
	ServiceAdapter serviceAdapter;

	List<ModelLocalGoods> localGoods;
	GoodsAdapter goodsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_fragment_local_store);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(LocalStoreFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(LocalStoreFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		selectStore(localStores.get(HomeData.getInstance().getStoreSelection()));
	}

	private void init() {
		localStores = HomeData.getInstance().getLocalStores();
		storeAdapter = new StoreAdapter();
		localGoods = new ArrayList<ModelLocalGoods>();
		goodsAdapter = new GoodsAdapter();
		localServices = new ArrayList<ModelLocalService>();
		serviceAdapter = new ServiceAdapter();
	}

	protected void findViews() {
		imageLayout = (FrameLayout) contentView.findViewById(R.id.image_layout);
		horizontalScrollView = (HorizontalScrollView) contentView
				.findViewById(R.id.image_scroll);
		imageGridView = (GridView) contentView.findViewById(R.id.image_grid);
		tabGoods = (TextView) contentView.findViewById(R.id.store_tab_goods);
		tabService = (TextView) contentView
				.findViewById(R.id.store_tab_service);
		goodsLayout = (FrameLayout) contentView
				.findViewById(R.id.store_goods_layout);
		serviceLayout = (FrameLayout) contentView
				.findViewById(R.id.store_service_layout);
		goodsScrollView = (PullToRefreshScrollView) contentView
				.findViewById(R.id.store_goods_scroll);
		serviceScrollView = (PullToRefreshScrollView) contentView
				.findViewById(R.id.store_service_scroll);
		goodsProductGridView = (InnerGridView) contentView
				.findViewById(R.id.store_goods_list);
		serviceProductGridView = (InnerGridView) contentView
				.findViewById(R.id.store_service_list);
		initViews();
		registerViews();
	}

	protected void initViews() {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, columHeight);
		if (localStores.size() > 0) {
			int colums = localStores.size();
			imageGridView.setLayoutParams(new LinearLayout.LayoutParams(colums
					* columWidth, LinearLayout.LayoutParams.MATCH_PARENT));
			imageGridView.setColumnWidth(columWidth);
			imageGridView.setStretchMode(GridView.NO_STRETCH);
			imageGridView.setNumColumns(colums);
		}
		imageLayout.setLayoutParams(layoutParams);
		imageGridView.setAdapter(storeAdapter);
		serviceProductGridView.setAdapter(serviceAdapter);
		goodsProductGridView.setAdapter(goodsAdapter);
		showGoods();
	}

	private void selectStore(ModelLocalStore modelLocalStore) {

		if (!localStores.isEmpty()) {
			if (modelLocalStore != null) {
				store = modelLocalStore;
			} else {
				store = localStores.get(0);
			}
			storeAdapter.notifyDataSetChanged();
			refreshGoods();
			refreshService();
		}

	}

	private void showGoods() {
		goodsLayout.setVisibility(View.VISIBLE);
		serviceLayout.setVisibility(View.GONE);
		tabGoods.setTextColor(getResources().getColor(R.color.textColorCompany));
		tabService
				.setTextColor(getResources().getColor(R.color.textColorThird));
	}

	private void showService() {
		goodsLayout.setVisibility(View.GONE);
		serviceLayout.setVisibility(View.VISIBLE);
		tabGoods.setTextColor(getResources().getColor(R.color.textColorThird));
		tabService.setTextColor(getResources().getColor(
				R.color.textColorCompany));
	}

	private void refreshGoods() {
		goodsScrollView.setMode(Mode.BOTH);
		goodsPage = 0;
		localGoods.clear();
		goodsAdapter.notifyDataSetChanged();
		getGoods();
	}

	private void refreshService() {
		serviceScrollView.setMode(Mode.BOTH);
		servicePage = 0;
		localServices.clear();
		serviceAdapter.notifyDataSetChanged();
		getService();
	}

	protected void registerViews() {
		imageGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectStore(localStores.get(arg2));
			}
		});
		goodsProductGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bundle bundle = new Bundle();
				bundle.putString("goodsId", localGoods.get(arg2).getId());
				openWindow(LocalGoodsDetailFragment.class.getName(), localGoods
						.get(arg2).getRetailProdManagerName(), bundle);
			}
		});
		serviceProductGridView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Bundle bundle = new Bundle();
						bundle.putString("productId", localServices.get(arg2)
								.getId());
						openWindow(LocalServiceDetailFragment.class.getName(),
								localServices.get(arg2).getProductName(),
								bundle);
					}
				});
		tabGoods.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showGoods();
			}
		});
		tabService.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showService();
			}
		});

		goodsScrollView.setMode(Mode.BOTH);
		goodsScrollView
				.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						refreshGoods();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						getGoods();
					}
				});
		serviceScrollView.setMode(Mode.BOTH);
		serviceScrollView
				.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						refreshService();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						getService();
					}
				});
	}

	class GoodsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return localGoods.size();
		}

		@Override
		public Object getItem(int position) {
			return localGoods.get(position);
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
			ModelLocalGoods goods = localGoods.get(position);
			viewHolder.nameTextView.setText(goods.getRetailProdManagerName());
			viewHolder.priceTextView.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(goods.getRetailPrice()));
			imageLoader.displayImage(getSmallImageUrl(goods.getBigImgUrl()),
					viewHolder.imageView);
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView priceTextView, nameTextView;
		}
	}

	private void getGoods() {
		goodsPage++;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("pageNo", goodsPage + ""));
		nameValuePairs.add(new BasicNameValuePair("pageSize", pageSize + ""));
		nameValuePairs.add(new BasicNameValuePair("shopServiceProviderID",
				store.getId()));
		ProductTask.getLocalGoods(getActivity(), nameValuePairs,
				new OnNetworkListener() {

					@Override
					public void onStart() {
						super.onStart();
						showLoading();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						hideLoading();
					}

					@Override
					public void onSuccess(NetworkMissionMessage requestMessage) {
						super.onSuccess(requestMessage);
						goodsScrollView.onRefreshComplete();
						if (!TextUtils.isEmpty(requestMessage.getResult())) {
							JSONObject object;
							try {
								object = new JSONObject(requestMessage
										.getResult());
								if (object.optString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONArray array = object
											.optJSONArray("dataList");
									if (array != null) {
										if (array.length() > 0) {
											if (array.length() < pageSize) {
												goodsScrollView
														.setMode(Mode.PULL_FROM_START);
											}
											for (int i = 0; i < array.length(); i++) {
												JSONObject goods = array
														.optJSONObject(i);
												if (goods != null) {
													localGoods
															.add(new ModelLocalGoods(
																	goods));
												}
											}
											goodsAdapter.notifyDataSetChanged();
											return;
										}
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						goodsScrollView.setMode(Mode.PULL_FROM_START);
						if (localGoods.isEmpty()) {
							loadingEmpty();
						}
					}
				});
	}

	class ServiceAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return localServices.size();
		}

		@Override
		public Object getItem(int position) {
			return localServices.get(position);
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
			ModelLocalService localService = localServices.get(position);
			viewHolder.nameTextView.setText(localService.getProductName());
			viewHolder.priceTextView.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(localService.getProductPrice()));
			imageLoader.displayImage(getSmallImageUrl(localService.getImg()),
					viewHolder.imageView);
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView priceTextView, nameTextView;
		}
	}

	private void getService() {
		servicePage++;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("pageNo", servicePage + ""));
		nameValuePairs.add(new BasicNameValuePair("pageSize", pageSize + ""));
		nameValuePairs.add(new BasicNameValuePair("providerId", store.getId()));
		ProductTask.getLocalService(getActivity(), nameValuePairs,
				new OnNetworkListener() {

					@Override
					public void onStart() {
						super.onStart();
						showLoading();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						hideLoading();
					}

					@Override
					public void onSuccess(NetworkMissionMessage requestMessage) {
						super.onSuccess(requestMessage);
						serviceScrollView.onRefreshComplete();
						if (!TextUtils.isEmpty(requestMessage.getResult())) {
							try {
								JSONObject object = new JSONObject(
										requestMessage.getResult());
								if (object.optString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONArray array = object
											.optJSONArray("dataList");
									if (array != null) {
										if (array.length() > 0) {
											if (array.length() < pageSize) {
												serviceScrollView
														.setMode(Mode.PULL_FROM_START);
											}
											for (int i = 0; i < array.length(); i++) {
												JSONObject jsonObject = array
														.optJSONObject(i);
												if (jsonObject != null) {
													localServices
															.add(new ModelLocalService(
																	jsonObject));
												}
											}
											serviceAdapter
													.notifyDataSetChanged();
											return;
										}
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						serviceScrollView.setMode(Mode.PULL_FROM_START);
						if (localServices.isEmpty()) {
							loadingEmpty();
						}
					}
				});
	}

	class StoreAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return localStores.size();
		}

		@Override
		public Object getItem(int position) {
			return localStores.get(position);
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
						R.layout.list_store_scroll, null);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.list_store_image);
				viewHolder.selectionImageView = (ImageView) convertView
						.findViewById(R.id.list_store_address_selection);
				viewHolder.nameTextView = (TextView) convertView
						.findViewById(R.id.list_store_name);
				viewHolder.addressTextView = (TextView) convertView
						.findViewById(R.id.list_store_address);
				LayoutParams layoutParams = new LayoutParams(columWidth,
						columHeight);
				convertView.setLayoutParams(layoutParams);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (store.getId().equals(localStores.get(position).getId())) {
				viewHolder.selectionImageView.setVisibility(View.VISIBLE);
			} else {
				viewHolder.selectionImageView.setVisibility(View.GONE);
			}
			imageLoader.displayImage(getSmallImageUrl(localStores.get(position)
					.getImg()), viewHolder.imageView);
			viewHolder.nameTextView.setText(localStores.get(position)
					.getShopname());
			viewHolder.addressTextView.setText(localStores.get(position)
					.getAddress());
			return convertView;
		}

		class ViewHolder {
			ImageView imageView, selectionImageView;
			TextView nameTextView, addressTextView;
		}

	}

}
