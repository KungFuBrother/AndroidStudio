package yitgogo.consumer.local.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.local.model.ModelLocalGoods;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.view.InnerListView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author Tiger
 * 
 * @Description 易商圈-本地商品搜索
 */
public class LocalGoodsSearchFragment extends BaseNotifyFragment {

	PullToRefreshScrollView refreshScrollView;
	InnerListView listView;

	List<ModelLocalGoods> localGoods;
	GoodsAdapter goodsAdapter;

	String productName = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_local_business_search);
		init();
		findViews();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(LocalGoodsSearchFragment.class.getName());
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(LocalGoodsSearchFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		refresh();
	}

	private void init() {
		measureScreen();
		localGoods = new ArrayList<ModelLocalGoods>();
		goodsAdapter = new GoodsAdapter();

		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("productName")) {
				productName = bundle.getString("productName");
			}
		}

	}

	@Override
	protected void findViews() {
		refreshScrollView = (PullToRefreshScrollView) contentView
				.findViewById(R.id.local_business_search_refresh);
		listView = (InnerListView) contentView
				.findViewById(R.id.local_business_search_list);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		refreshScrollView.setMode(Mode.BOTH);
		listView.setAdapter(goodsAdapter);
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
						new GetGoods().execute();
					}
				});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				Bundle bundle = new Bundle();
				bundle.putString("id", localGoods.get(paramInt).getId());
				jump(LocalGoodsDetailFragment.class.getName(),
						localGoods.get(paramInt).getRetailProdManagerName(),
						bundle);

			}
		});
	}

	private void refresh() {
		refreshScrollView.setMode(Mode.BOTH);
		pagenum = 0;
		localGoods.clear();
		goodsAdapter.notifyDataSetChanged();
		new GetGoods().execute();
	}

	class GetGoods extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			if (pagenum == 0) {
				showLoading();
			}
			pagenum++;
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("pageNo", pagenum + ""));
			parameters.add(new BasicNameValuePair("pageSize", pagesize + ""));
			parameters.add(new BasicNameValuePair("retailProdManagerName",
					productName));
			parameters.add(new BasicNameValuePair("serviceProviderID", Store
					.getStore().getStoreId()));
			return netUtil.postWithoutCookie(API.API_LOCAL_BUSINESS_GOODS,
					parameters, useCache, true);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			refreshScrollView.onRefreshComplete();
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						JSONArray array = object.optJSONArray("dataList");
						if (array != null) {
							if (array.length() > 0) {
								if (array.length() < pagesize) {
									refreshScrollView
											.setMode(Mode.PULL_FROM_START);
								}
								for (int i = 0; i < array.length(); i++) {
									JSONObject goods = array.optJSONObject(i);
									if (goods != null) {
										localGoods.add(new ModelLocalGoods(
												goods));
									}
								}
								goodsAdapter.notifyDataSetChanged();
								return;
							} else {
								refreshScrollView.setMode(Mode.PULL_FROM_START);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (localGoods.size() == 0) {
					loadingEmpty();
				}
			} else {
				loadingFailed();
			}
		}
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
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.list_product,
						null);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.list_product_image);
				holder.nameTextView = (TextView) convertView
						.findViewById(R.id.list_product_name);
				holder.priceTextView = (TextView) convertView
						.findViewById(R.id.list_product_price);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ImageLoader.getInstance().displayImage(
					getSmallImageUrl(localGoods.get(position).getBigImgUrl()),
					holder.imageView);
			holder.nameTextView.setText(localGoods.get(position)
					.getRetailProdManagerName());
			holder.priceTextView.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(localGoods.get(position)
							.getRetailPrice()));
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView priceTextView, nameTextView;
		}
	}

}
