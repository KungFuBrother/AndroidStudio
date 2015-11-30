package yitgogo.consumer.main.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.home.model.ModelListPrice;
import yitgogo.consumer.home.model.ModelProduct;
import yitgogo.consumer.home.part.PartAdsFragment;
import yitgogo.consumer.home.task.GetAds;
import yitgogo.consumer.product.ui.ClassesFragment;
import yitgogo.consumer.product.ui.ProductSearchFragment;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.view.InnerGridView;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dtr.zxing.activity.CaptureActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class HomeYitgogoFragment extends BaseNotifyFragment {

	PullToRefreshScrollView refreshScrollView;
	InnerGridView productGridView;
	List<ModelProduct> products;
	HashMap<String, ModelListPrice> priceMap;
	ProductAdapter productAdapter;

	ImageView classButton, searchButton;
	PartAdsFragment adsFragment;

	GetAds getAds;

	String currentStoreId = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_home_yitgogo);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(HomeYitgogoFragment.class.getName());
		if (!currentStoreId.equals(Store.getStore().getStoreId())) {
			useCache = true;
			currentStoreId = Store.getStore().getStoreId();
			refresh();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(HomeYitgogoFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		showDisconnectMargin();
	}

	private void init() {
		measureScreen();
		products = new ArrayList<ModelProduct>();
		priceMap = new HashMap<String, ModelListPrice>();
		productAdapter = new ProductAdapter();
		adsFragment = new PartAdsFragment();
	}

	@Override
	protected void findViews() {
		refreshScrollView = (PullToRefreshScrollView) contentView
				.findViewById(R.id.home_yitgogo_refresh);
		productGridView = (InnerGridView) contentView
				.findViewById(R.id.home_yitgogo_product_list);
		classButton = (ImageView) contentView
				.findViewById(R.id.home_yitgogo_class);
		searchButton = (ImageView) contentView
				.findViewById(R.id.home_yitgogo_search);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		refreshScrollView.setMode(Mode.BOTH);
		productGridView.setAdapter(productAdapter);
		getFragmentManager().beginTransaction()
				.replace(R.id.home_yitgogo_ads_layout, adsFragment).commit();
	}

	@Override
	protected void registerViews() {
		refreshScrollView
				.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						useCache = false;
						refresh();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						new GetProduct().execute();
					}
				});
		productGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				showProductDetail(products.get(arg2).getId(), products
						.get(arg2).getProductName(),
						CaptureActivity.SALE_TYPE_NONE);
			}
		});
		classButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(ClassesFragment.class.getName(), "商品分类");
			}
		});
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(ProductSearchFragment.class.getName(), "商品搜索", true);
			}
		});
	}

	private void refresh() {
		getAds();
		refreshScrollView.setMode(Mode.BOTH);
		pagenum = 0;
		products.clear();
		productAdapter.notifyDataSetChanged();
		new GetProduct().execute();
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
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.grid_product,
						null);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.grid_product_image);
				holder.nameTextView = (TextView) convertView
						.findViewById(R.id.grid_product_name);
				holder.priceTextView = (TextView) convertView
						.findViewById(R.id.grid_product_price);
				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, screenWidth / 25 * 16);
				convertView.setLayoutParams(params);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ModelProduct product = products.get(position);
			holder.nameTextView.setText(product.getProductName());
			if (priceMap.containsKey(product.getId())) {
				holder.priceTextView.setText(Parameters.CONSTANT_RMB
						+ decimalFormat.format(priceMap.get(product.getId())
								.getPrice()));
			}
			ImageLoader.getInstance().displayImage(
					getSmallImageUrl(product.getImg()), holder.imageView);
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView priceTextView, nameTextView;
		}
	}

	private void getAds() {
		if (getAds != null) {
			if (getAds.getStatus() == Status.RUNNING) {
				return;
			}
		}
		getAds = new GetAds() {
			@Override
			protected void onPostExecute(String result) {
				adsFragment.refresh(result);
			}
		};
		getAds.execute(useCache);
	}

	class GetProduct extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			if (pagenum == 0) {
				showLoading();
			}
			pagenum++;
		}

		@Override
		protected String doInBackground(Void... arg0) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("jmdId", Store.getStore()
					.getStoreId()));
			params.add(new BasicNameValuePair("pageNo", pagenum + ""));
			params.add(new BasicNameValuePair("pageSize", pagesize + ""));
			return netUtil.postWithoutCookie(API.API_PRODUCT_LIST, params,
					useCache, true);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			refreshScrollView.onRefreshComplete();
			if (result.length() > 0) {
				JSONObject info;
				try {
					info = new JSONObject(result);
					if (info.getString("state").equalsIgnoreCase("SUCCESS")) {
						JSONArray productArray = info.optJSONArray("dataList");
						if (productArray != null) {
							if (productArray.length() > 0) {
								if (productArray.length() < pagesize) {
									refreshScrollView
											.setMode(Mode.PULL_FROM_START);
								}
								StringBuilder stringBuilder = new StringBuilder();
								for (int i = 0; i < productArray.length(); i++) {
									ModelProduct product = new ModelProduct(
											productArray.getJSONObject(i));
									products.add(product);
									if (i > 0) {
										stringBuilder.append(",");
									}
									stringBuilder.append(product.getId());
								}
								productAdapter.notifyDataSetChanged();
								new GetPriceList().execute(stringBuilder
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
	}

	class GetPriceList extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
			valuePairs.add(new BasicNameValuePair("jmdId", Store.getStore()
					.getStoreId()));
			valuePairs.add(new BasicNameValuePair("productId", params[0]));
			return netUtil.postWithoutCookie(API.API_PRICE_LIST, valuePairs,
					false, false);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
						JSONArray priceArray = object.getJSONArray("dataList");
						if (priceArray.length() > 0) {
							for (int i = 0; i < priceArray.length(); i++) {
								ModelListPrice priceList = new ModelListPrice(
										priceArray.getJSONObject(i));
								priceMap.put(priceList.getProductId(),
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
	}

}
