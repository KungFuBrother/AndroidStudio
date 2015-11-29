package yitgogo.smart.local;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.local.model.ModelLocalService;
import yitgogo.smart.local.model.ModelLocalServiceClass;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.InnerGridView;
import android.R.color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

public class LocalServiceFragment extends BaseNotifyFragment {

	ListView serviceClassListView;

	PullToRefreshScrollView localServiceRefreshScrollView;
	InnerGridView serviceProductGridView;

	List<ModelLocalService> localServices;
	ServiceAdapter serviceAdapter;
	LocalServiceClass localServiceClass;
	ServiceClassAdapter serviceClassAdapter;

	int pageSize = 12;
	int pageNo = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_local_business_service);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(LocalServiceFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(LocalServiceFragment.class.getName());
	}

	private void init() {
		localServices = new ArrayList<ModelLocalService>();
		serviceAdapter = new ServiceAdapter();
		localServiceClass = new LocalServiceClass();
		serviceClassAdapter = new ServiceClassAdapter();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getService();
		getServiceClasses();
	}

	@Override
	protected void findViews() {
		serviceClassListView = (ListView) contentView
				.findViewById(R.id.local_business_service_class);
		localServiceRefreshScrollView = (PullToRefreshScrollView) contentView
				.findViewById(R.id.local_business_service_scroll);
		serviceProductGridView = (InnerGridView) contentView
				.findViewById(R.id.local_business_service_product);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		serviceClassListView.setAdapter(serviceClassAdapter);
		serviceProductGridView.setAdapter(serviceAdapter);
	}

	@Override
	protected void registerViews() {
		serviceClassListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (localServiceClass.getSelection() == arg2) {
					return;
				}
				localServiceClass.setSelection(arg2);
				serviceClassAdapter.notifyDataSetChanged();
				refresh();
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

		localServiceRefreshScrollView.setMode(Mode.BOTH);
		localServiceRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						refresh();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						getService();
					}
				});
	}

	private void refresh() {
		localServiceRefreshScrollView.setMode(Mode.BOTH);
		pageNo = 0;
		localServices.clear();
		serviceAdapter.notifyDataSetChanged();
		getService();
	}

	/**
	 * {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[{"id":1,
	 * "classValueName"
	 * :"KTV","organizationId":"1068"},{"id":2,"classValueName":"美食"
	 * ,"organizationId"
	 * :"1068"},{"id":3,"classValueName":"美发","organizationId":"1068"
	 * },{"id":4,"classValueName"
	 * :"休闲","organizationId":"1068"},{"id":5,"classValueName"
	 * :"酒店","organizationId"
	 * :"1068"},{"id":6,"classValueName":"农家乐","organizationId"
	 * :"1068"}],"totalCount":1,"dataMap":{},"object":null}
	 */
	private void getServiceClasses() {
		NetworkContent networkContent = new NetworkContent(
				API.API_LOCAL_BUSINESS_SERVICE_CLASS);
		networkContent.addParameters("shebei", Device.getDeviceCode());
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
						hideLoading();
					}

					@Override
					public void onSuccess(NetworkMissionMessage message) {
						super.onSuccess(message);
						localServiceClass = new LocalServiceClass(message
								.getResult());
						serviceClassAdapter.notifyDataSetChanged();
					}
				});
	}

	private void getService() {
		pageNo++;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("pageNo", pageNo + ""));
		nameValuePairs.add(new BasicNameValuePair("pageSize", pageSize + ""));
		if (localServiceClass.getSelection() > 0) {
			nameValuePairs.add(new BasicNameValuePair("classValueId",
					localServiceClass.getServiceClasses()
							.get(localServiceClass.getSelection()).getId()));
		}
		// if (priceSort.getSelection() > 0) {
		// parameters.add(new BasicNameValuePair("pricePaixu", (priceSort
		// .getSelection() - 1) + ""));
		// }
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
					public void onSuccess(NetworkMissionMessage message) {
						super.onSuccess(message);
						localServiceRefreshScrollView.onRefreshComplete();
						if (!TextUtils.isEmpty(message.getResult())) {
							try {
								JSONObject object = new JSONObject(message
										.getResult());
								if (object.optString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONArray array = object
											.optJSONArray("dataList");
									if (array != null) {
										if (array.length() > 0) {
											if (array.length() < pageSize) {
												localServiceRefreshScrollView
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
						localServiceRefreshScrollView
								.setMode(Mode.PULL_FROM_START);
						if (localServices.size() == 0) {
							loadingEmpty();
						}
					}
				});
	}

	class LocalServiceClass {

		List<ModelLocalServiceClass> serviceClasses = new ArrayList<ModelLocalServiceClass>();
		int selection = 0;

		public LocalServiceClass() {
		}

		public LocalServiceClass(String result) {
			if (result.length() > 0) {
				serviceClasses.add(new ModelLocalServiceClass());
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						JSONArray array = object.optJSONArray("dataList");
						if (array != null) {
							for (int i = 0; i < array.length(); i++) {
								JSONObject jsonObject = array.optJSONObject(i);
								if (jsonObject != null) {
									serviceClasses
											.add(new ModelLocalServiceClass(
													jsonObject));
								}
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		public int getSelection() {
			return selection;
		}

		public void setSelection(int selection) {
			this.selection = selection;
		}

		public List<ModelLocalServiceClass> getServiceClasses() {
			return serviceClasses;
		}

	}

	class ServiceClassAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return localServiceClass.getServiceClasses().size();
		}

		@Override
		public Object getItem(int position) {
			return localServiceClass.getServiceClasses().get(position);
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
						R.layout.list_local_business_class, null);
				viewHolder.selector = convertView
						.findViewById(R.id.local_business_class_selector);
				viewHolder.serviceClassName = (TextView) convertView
						.findViewById(R.id.local_business_class_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.serviceClassName.setText(localServiceClass
					.getServiceClasses().get(position).getClassValueName());
			if (position == localServiceClass.getSelection()) {
				viewHolder.selector
						.setBackgroundResource(R.color.textColorCompany);
				viewHolder.serviceClassName.setTextColor(getResources()
						.getColor(R.color.textColorCompany));
			} else {
				viewHolder.selector.setBackgroundResource(color.transparent);
				viewHolder.serviceClassName.setTextColor(getResources()
						.getColor(R.color.textColorSecond));
			}
			return convertView;
		}

		class ViewHolder {
			TextView serviceClassName;
			View selector;
		}
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

}
