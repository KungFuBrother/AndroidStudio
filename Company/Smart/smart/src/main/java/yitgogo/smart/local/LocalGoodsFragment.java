package yitgogo.smart.local;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.local.model.ModelLocalGoods;
import yitgogo.smart.local.model.ModelLocalGoodsClass;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.InnerGridView;
import yitgogo.smart.view.InnerListView;
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

public class LocalGoodsFragment extends BaseNotifyFragment {

	ListView goodsClassListView;

	PullToRefreshScrollView localGoodsRefreshScrollView;
	InnerGridView goodsProductGridView;

	List<ModelLocalGoods> localGoods;
	GoodsAdapter goodsAdapter;
	LocalGoodsClasses localGoodsClasses;
	GoodsClassAdapter goodsClassAdapter;
	int pageSize = 12;
	int pageNo = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_local_business_goods);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(LocalGoodsFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(LocalGoodsFragment.class.getName());
	}

	private void init() {
		localGoods = new ArrayList<ModelLocalGoods>();
		goodsAdapter = new GoodsAdapter();
		localGoodsClasses = new LocalGoodsClasses();
		goodsClassAdapter = new GoodsClassAdapter();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getGoods();
		getGoodsClasses();
	}

	@Override
	protected void findViews() {
		goodsClassListView = (ListView) contentView
				.findViewById(R.id.local_business_goods_class);
		localGoodsRefreshScrollView = (PullToRefreshScrollView) contentView
				.findViewById(R.id.local_business_goods_scroll);
		goodsProductGridView = (InnerGridView) contentView
				.findViewById(R.id.local_business_goods_product);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		goodsClassListView.setAdapter(goodsClassAdapter);
		goodsProductGridView.setAdapter(goodsAdapter);
	}

	@Override
	protected void registerViews() {
		goodsClassListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				localGoodsClasses.setSelection(arg2);
				localGoodsClasses.getGoodsClasses().get(arg2).setSelection(-1);
				goodsClassAdapter.notifyDataSetChanged();
				getGoodsClasses2();
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
		localGoodsRefreshScrollView.setMode(Mode.BOTH);
		localGoodsRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						refresh();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						getGoods();
					}
				});
	}

	private void refresh() {
		localGoodsRefreshScrollView.setMode(Mode.BOTH);
		pageNo = 0;
		localGoods.clear();
		goodsAdapter.notifyDataSetChanged();
		getGoods();
	}

	private void getGoods() {
		pageNo++;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("pageNo", pageNo + ""));
		nameValuePairs.add(new BasicNameValuePair("pageSize", pageSize + ""));
		if (localGoodsClasses.getSelection() >= 0) {
			if (localGoodsClasses.getGoodsClasses().size() > localGoodsClasses
					.getSelection()) {
				if (localGoodsClasses.getGoodsClasses()
						.get(localGoodsClasses.getSelection()).getSelection() >= 0) {
					if (localGoodsClasses.getGoodsClasses()
							.get(localGoodsClasses.getSelection())
							.getSubClasses().size() > localGoodsClasses
							.getGoodsClasses()
							.get(localGoodsClasses.getSelection())
							.getSelection()) {
						nameValuePairs.add(new BasicNameValuePair(
								"retailProTypeValueID", localGoodsClasses
										.getGoodsClasses()
										.get(localGoodsClasses.getSelection())
										.getSubClasses()
										.get(localGoodsClasses
												.getGoodsClasses()
												.get(localGoodsClasses
														.getSelection())
												.getSelection()).getId()));
					}
				}
			}
		}
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
					public void onSuccess(NetworkMissionMessage message) {
						super.onSuccess(message);
						localGoodsRefreshScrollView.onRefreshComplete();
						if (!TextUtils.isEmpty(message.getResult())) {
							JSONObject object;
							try {
								object = new JSONObject(message.getResult());
								if (object.optString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONArray array = object
											.optJSONArray("dataList");
									if (array != null) {
										if (array.length() > 0) {
											if (array.length() < pageSize) {
												localGoodsRefreshScrollView
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
						localGoodsRefreshScrollView
								.setMode(Mode.PULL_FROM_START);
						if (localGoods.isEmpty()) {
							loadingEmpty();
						}
					}
				});
	}

	private void getGoodsClasses() {
		NetworkContent networkContent = new NetworkContent(
				API.API_LOCAL_BUSINESS_GOODS_CLASS_PRIMARY);
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
						try {
							localGoodsClasses = new LocalGoodsClasses(message
									.getResult());
							goodsClassAdapter.notifyDataSetChanged();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void getGoodsClasses2() {
		NetworkContent networkContent = new NetworkContent(
				API.API_LOCAL_BUSINESS_GOODS_CLASS_SECOND);
		networkContent.addParameters("shebei", Device.getDeviceCode());
		networkContent.addParameters("productTypeValueID", localGoodsClasses
				.getGoodsClasses().get(localGoodsClasses.getSelection())
				.getId());
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
						try {
							LocalGoodsClasses goodsClasses = new LocalGoodsClasses(
									message.getResult());
							localGoodsClasses
									.getGoodsClasses()
									.get(localGoodsClasses.getSelection())
									.setSubClasses(
											goodsClasses.getGoodsClasses());
							goodsClassAdapter.notifyDataSetChanged();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	class LocalGoodsClasses {

		List<ModelLocalGoodsClass> goodsClasses = new ArrayList<ModelLocalGoodsClass>();
		int selection = 0;

		public LocalGoodsClasses() {
		}

		public LocalGoodsClasses(String result) throws JSONException {
			if (result.length() > 0) {
				JSONObject object = new JSONObject(result);
				if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONArray array = object.optJSONArray("dataList");
					if (array != null) {
						for (int i = 0; i < array.length(); i++) {
							goodsClasses.add(new ModelLocalGoodsClass(array
									.getJSONObject(i)));
						}
					}
				}
			}
		}

		public List<ModelLocalGoodsClass> getGoodsClasses() {
			return goodsClasses;
		}

		public int getSelection() {
			return selection;
		}

		public void setSelection(int selection) {
			this.selection = selection;
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

	class GoodsClassAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return localGoodsClasses.getGoodsClasses().size();
		}

		@Override
		public Object getItem(int position) {
			return localGoodsClasses.getGoodsClasses().get(position);
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
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.list_class_mid,
						null);
				holder.nameText = (TextView) convertView
						.findViewById(R.id.class_mid_name);
				holder.minLayout = (LinearLayout) convertView
						.findViewById(R.id.class_mid_min_layout);
				holder.minClassesList = (InnerListView) convertView
						.findViewById(R.id.class_mid_subclasses);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (localGoodsClasses.getSelection() == position) {
				holder.minLayout.setVisibility(View.VISIBLE);
			} else {
				holder.minLayout.setVisibility(View.GONE);
			}
			holder.nameText.setText(localGoodsClasses.getGoodsClasses()
					.get(position).getRetailProdTypeValueName());
			holder.minClassesList.setAdapter(new GoodsClassAdapter2(
					localGoodsClasses.getGoodsClasses().get(position)));
			holder.minClassesList
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View view,
								int arg2, long arg3) {
							if (localGoodsClasses.getGoodsClasses().get(index)
									.getSelection() == arg2) {
								return;
							}
							localGoodsClasses.getGoodsClasses().get(index)
									.setSelection(arg2);
							notifyDataSetChanged();
							refresh();
						}
					});
			return convertView;
		}

		class ViewHolder {
			TextView nameText;
			LinearLayout minLayout;
			InnerListView minClassesList;
		}

	}

	class GoodsClassAdapter2 extends BaseAdapter {

		ModelLocalGoodsClass goodsClass;

		public GoodsClassAdapter2(ModelLocalGoodsClass goodsClass) {
			this.goodsClass = goodsClass;
		}

		@Override
		public int getCount() {
			return goodsClass.getSubClasses().size();
		}

		@Override
		public Object getItem(int position) {
			return goodsClass.getSubClasses().get(position);
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
				viewHolder.view = convertView
						.findViewById(R.id.local_business_class_selector);
				viewHolder.serviceClassName = (TextView) convertView
						.findViewById(R.id.local_business_class_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (goodsClass.getSelection() == position) {
				viewHolder.view.setBackgroundResource(R.color.textColorCompany);
				viewHolder.serviceClassName.setTextColor(getResources()
						.getColor(R.color.textColorCompany));
			} else {
				viewHolder.view.setBackgroundResource(color.transparent);
				viewHolder.serviceClassName.setTextColor(getResources()
						.getColor(R.color.textColorSecond));
			}
			viewHolder.serviceClassName.setText(goodsClass.getSubClasses()
					.get(position).getRetailProdTypeValueName());
			return convertView;
		}

		class ViewHolder {
			View view;
			TextView serviceClassName;
		}
	}
}
