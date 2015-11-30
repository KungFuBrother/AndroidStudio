package yitgogo.consumer.local.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.local.model.ModelLocalService;
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
 * @Description 易商圈-本地服务搜索
 */
public class LocalServiceSearchFragment extends BaseNotifyFragment {

	PullToRefreshScrollView refreshScrollView;
	InnerListView listView;

	List<ModelLocalService> localServices;
	ServiceAdapter serviceAdapter;

	String productName = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_local_business_search);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(LocalServiceSearchFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(LocalServiceSearchFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		refresh();
	}

	private void init() {
		measureScreen();
		localServices = new ArrayList<ModelLocalService>();
		serviceAdapter = new ServiceAdapter();

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
		listView.setAdapter(serviceAdapter);
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
						new GetService().execute();
					}
				});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				Bundle bundle = new Bundle();
				bundle.putString("productId", localServices.get(paramInt)
						.getId());
				jump(LocalServiceDetailFragment.class.getName(), localServices
						.get(paramInt).getProductName(), bundle);
			}
		});
	}

	private void refresh() {
		refreshScrollView.setMode(Mode.BOTH);
		pagenum = 0;
		localServices.clear();
		serviceAdapter.notifyDataSetChanged();
		new GetService().execute();
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
					getSmallImageUrl(localServices.get(position).getImg()),
					holder.imageView);
			holder.nameTextView.setText(localServices.get(position)
					.getProductName());
			holder.priceTextView.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(localServices.get(position)
							.getProductPrice()));
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView priceTextView, nameTextView;
		}
	}

	class GetService extends AsyncTask<Void, Void, String> {

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
			parameters.add(new BasicNameValuePair("productName", productName));
			parameters.add(new BasicNameValuePair("organizationId", Store
					.getStore().getStoreId()));
			return netUtil.postWithoutCookie(API.API_LOCAL_BUSINESS_SERVICE,
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
									JSONObject jsonObject = array
											.optJSONObject(i);
									if (jsonObject != null) {
										localServices
												.add(new ModelLocalService(
														jsonObject));
									}
								}
								serviceAdapter.notifyDataSetChanged();
								return;
							} else {
								refreshScrollView.setMode(Mode.PULL_FROM_START);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (localServices.size() == 0) {
					loadingEmpty();
				}
			} else {
				loadingFailed();
			}
		}

	}
}
