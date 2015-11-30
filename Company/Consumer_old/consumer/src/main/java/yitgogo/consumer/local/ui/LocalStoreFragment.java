package yitgogo.consumer.local.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.home.model.ModelLocalStore;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class LocalStoreFragment extends BaseNotifyFragment {

	PullToRefreshScrollView refreshScrollView;
	InnerListView listView;

	List<ModelLocalStore> localStores;
	StoreAdapter storeAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_local_store);
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
		new GetStore().execute();
	}

	private void init() {
		measureScreen();
		localStores = new ArrayList<ModelLocalStore>();
		storeAdapter = new StoreAdapter();
	}

	@Override
	protected void findViews() {
		refreshScrollView = (PullToRefreshScrollView) contentView
				.findViewById(R.id.store_refresh);
		listView = (InnerListView) contentView.findViewById(R.id.store_list);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		listView.setAdapter(storeAdapter);
	}

	@Override
	protected void registerViews() {
		refreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						new GetStore().execute();
					}
				});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bundle bundle = new Bundle();
				bundle.putString("storeId", localStores.get(arg2).getId());
				bundle.putString("storeName", localStores.get(arg2)
						.getShopname());
				jump(LocalStoreDetailFragment.class.getName(), "店铺街", bundle,
						true);
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
				viewHolder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.list_local_store,
						null);
				viewHolder.storeAddressTextView = (TextView) convertView
						.findViewById(R.id.list_store_address);
				viewHolder.storeNameTextView = (TextView) convertView
						.findViewById(R.id.list_store_name);
				viewHolder.storeImageView = (ImageView) convertView
						.findViewById(R.id.list_store_image);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			ImageLoader.getInstance().displayImage(
					localStores.get(position).getImg(),
					viewHolder.storeImageView);
			viewHolder.storeAddressTextView.setText(localStores.get(position)
					.getAddress());
			viewHolder.storeNameTextView.setText(localStores.get(position)
					.getShopname());
			return convertView;
		}

		class ViewHolder {
			TextView storeNameTextView, storeAddressTextView;
			ImageView storeImageView;
		}

	}

	class GetStore extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			localStores.clear();
			storeAdapter.notifyDataSetChanged();
			showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("storeId", Store
					.getStore().getStoreId()));
			return netUtil.postWithCookie(API.API_LOCAL_STORE_LIST,
					nameValuePairs);
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
							for (int i = 0; i < array.length(); i++) {
								localStores.add(new ModelLocalStore(array
										.optJSONObject(i)));
							}
							storeAdapter.notifyDataSetChanged();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
