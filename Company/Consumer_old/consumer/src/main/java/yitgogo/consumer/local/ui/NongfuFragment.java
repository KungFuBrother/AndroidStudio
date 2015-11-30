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
import yitgogo.consumer.local.model.ModelNongfu;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.view.InnerListView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
 * @Description 易商圈-农副产品
 */
public class NongfuFragment extends BaseNotifyFragment {

	PullToRefreshScrollView refreshScrollView;
	InnerListView serviceList;

	List<ModelLocalService> localServices;
	ProductAdapter productAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_local_business_nongfu);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(NongfuFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(NongfuFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setShowConnectionState(false);
		refresh();
	}

	private void init() {
		measureScreen();
		localServices = new ArrayList<ModelLocalService>();
		productAdapter = new ProductAdapter();
	}

	@Override
	protected void findViews() {
		refreshScrollView = (PullToRefreshScrollView) contentView
				.findViewById(R.id.local_business_content_refresh);
		serviceList = (InnerListView) contentView
				.findViewById(R.id.local_business_content_list);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		refreshScrollView.setMode(Mode.BOTH);
		serviceList.setAdapter(productAdapter);
	}

	@Override
	protected void registerViews() {
		serviceList.setOnItemClickListener(new OnItemClickListener() {

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
						new GetService().execute();
					}
				});
	}

	private void refresh() {
		refreshScrollView.setMode(Mode.BOTH);
		pagenum = 0;
		localServices.clear();
		productAdapter.notifyDataSetChanged();
		new GetService().execute();
	}

	class ProductAdapter extends BaseAdapter {

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
				convertView = layoutInflater
						.inflate(R.layout.list_nongfu, null);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.nongfu_image);
				holder.nameTextView = (TextView) convertView
						.findViewById(R.id.nongfu_name);
				holder.priceTextView = (TextView) convertView
						.findViewById(R.id.nongfu_price);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final ModelLocalService localService = localServices.get(position);
			holder.nameTextView.setText(localService.getProductName());
			holder.priceTextView.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(localService.getProductPrice()));
			ImageLoader.getInstance().displayImage(
					getSmallImageUrl(localService.getImg()), holder.imageView);
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
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("pagenum", pagenum + ""));
			nameValuePairs
					.add(new BasicNameValuePair("pagesize", pagesize + ""));
			nameValuePairs.add(new BasicNameValuePair("providerId", Store
					.getStore().getStoreId()));
			return netUtil.postWithoutCookie(
					API.API_LOCAL_BUSINESS_SERVICE_NONGFU, nameValuePairs,
					false, false);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			refreshScrollView.onRefreshComplete();
			if (!TextUtils.isEmpty(result)) {
				try {
					JSONObject object = new JSONObject(result);
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
										ModelNongfu modelNongfu = new ModelNongfu(
												jsonObject);
										localServices.add(modelNongfu
												.getLocalService());
									}
								}
								productAdapter.notifyDataSetChanged();
								return;
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			refreshScrollView.setMode(Mode.PULL_FROM_START);
			if (localServices.isEmpty()) {
				loadingEmpty();
			}
		}
	}
}
