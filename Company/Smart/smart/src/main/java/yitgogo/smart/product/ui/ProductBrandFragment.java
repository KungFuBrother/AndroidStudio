package yitgogo.smart.product.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.home.model.ModelHomeBrand;
import yitgogo.smart.home.model.ModelListPrice;
import yitgogo.smart.home.model.ModelProduct;
import yitgogo.smart.task.ProductTask;
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

public class ProductBrandFragment extends BaseNotifyFragment {

	FrameLayout imageLayout;
	HorizontalScrollView horizontalScrollView;
	GridView imageGridView;

	int columWidth = 210;
	int columHeight = 140;

	int pageSize = 12, pageNo = 0;

	PullToRefreshScrollView refreshScrollView;
	InnerGridView productGridView;

	List<ModelHomeBrand> brands;
	BrandAdapter brandAdapter;
	ModelHomeBrand brand;

	List<ModelProduct> products;
	HashMap<String, ModelListPrice> priceMap;
	ProductAdapter productAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_fragment_product_brand);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(ProductBrandFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(ProductBrandFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		selectBrand(brands.get(HomeData.getInstance().getBrandSelection()));
	}

	private void init() {
		brands = HomeData.getInstance().getBrands();
		brandAdapter = new BrandAdapter();
		products = new ArrayList<ModelProduct>();
		priceMap = new HashMap<String, ModelListPrice>();
		productAdapter = new ProductAdapter();
	}

	protected void findViews() {
		imageLayout = (FrameLayout) contentView.findViewById(R.id.image_layout);
		horizontalScrollView = (HorizontalScrollView) contentView
				.findViewById(R.id.image_scroll);
		imageGridView = (GridView) contentView.findViewById(R.id.image_grid);

		refreshScrollView = (PullToRefreshScrollView) contentView
				.findViewById(R.id.brand_product_scroll);
		productGridView = (InnerGridView) contentView
				.findViewById(R.id.brand_product_list);
		initViews();
		registerViews();
	}

	protected void initViews() {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, columHeight);
		if (brands.size() > 0) {
			int colums = brands.size();
			imageGridView.setLayoutParams(new LinearLayout.LayoutParams(colums
					* columWidth, LinearLayout.LayoutParams.MATCH_PARENT));
			imageGridView.setColumnWidth(columWidth);
			imageGridView.setStretchMode(GridView.NO_STRETCH);
			imageGridView.setNumColumns(colums);
		}
		imageLayout.setLayoutParams(layoutParams);
		imageGridView.setAdapter(brandAdapter);
		productGridView.setAdapter(productAdapter);
	}

	private void refresh() {
		refreshScrollView.setMode(Mode.BOTH);
		pageNo = 0;
		products.clear();
		productAdapter.notifyDataSetChanged();
		getProducts();
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
											.getJSONArray("dataList");
									if (priceArray.length() > 0) {
										for (int i = 0; i < priceArray.length(); i++) {
											ModelListPrice priceList = new ModelListPrice(
													priceArray.getJSONObject(i));
											priceMap.put(
													priceList.getProductId(),
													priceList);
										}
										productAdapter.notifyDataSetChanged();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

	private void selectBrand(ModelHomeBrand modelHomeBrand) {
		if (!brands.isEmpty()) {
			if (modelHomeBrand != null) {
				brand = modelHomeBrand;
			} else {
				brand = brands.get(0);
			}
			brandAdapter.notifyDataSetChanged();
			refresh();
		}
	}

	protected void registerViews() {
		refreshScrollView.setMode(Mode.BOTH);
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
						getProducts();
					}
				});
		imageGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectBrand(brands.get(arg2));
			}
		});
		productGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				showProductDetail(products.get(arg2).getId(),
						QrCodeTool.SALE_TYPE_NONE);
			}
		});
	}

	private void getProducts() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		pageNo++;
		nameValuePairs.add(new BasicNameValuePair("pageNo", pageNo + ""));
		nameValuePairs.add(new BasicNameValuePair("pageSize", pageSize + ""));
		nameValuePairs
				.add(new BasicNameValuePair("brandId", brand.getBrandId()));
		ProductTask.getProducts(getActivity(), nameValuePairs,
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
						hideLoading();
						refreshScrollView.onRefreshComplete();
						if (!TextUtils.isEmpty(requestMessage.getResult())) {
							JSONObject info;
							try {
								info = new JSONObject(requestMessage
										.getResult());
								if (info.getString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONArray productArray = info
											.getJSONArray("dataList");
									if (productArray != null) {
										if (productArray.length() > 0) {
											if (productArray.length() < pageSize) {
												refreshScrollView
														.setMode(Mode.PULL_FROM_START);
											}
											StringBuilder stringBuilder = new StringBuilder();
											for (int i = 0; i < productArray
													.length(); i++) {
												ModelProduct product = new ModelProduct(
														productArray
																.getJSONObject(i));
												products.add(product);
												if (i > 0) {
													stringBuilder.append(",");
												}
												stringBuilder.append(product
														.getId());
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
						if (products.isEmpty()) {
							loadingEmpty();
						}
					}
				});
	}

	class BrandAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return brands.size();
		}

		@Override
		public Object getItem(int position) {
			return brands.get(position);
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
			if (brand.getBrandId().equals(brands.get(position).getBrandId())) {
				viewHolder.selectionImageView.setVisibility(View.VISIBLE);
			} else {
				viewHolder.selectionImageView.setVisibility(View.GONE);
			}
			imageLoader.displayImage(brands.get(position).getBrandLogo(),
					viewHolder.imageView);
			return convertView;
		}

		class ViewHolder {
			ImageView imageView, selectionImageView;
		}

	}

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
			ModelProduct product = products.get(position);
			imageLoader.displayImage(getSmallImageUrl(product.getImg()),
					viewHolder.imageView);
			if (priceMap.containsKey(product.getId())) {
				viewHolder.priceTextView.setText(Parameters.CONSTANT_RMB
						+ decimalFormat.format(priceMap.get(product.getId())
								.getPrice()));
			}
			viewHolder.nameTextView.setText(product.getProductName());
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView priceTextView, nameTextView;
		}
	}
}
