package yitgogo.smart.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.local.LocalGoodsDetailFragment;
import yitgogo.smart.local.model.ModelLocalGoods;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.InnerGridView;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

public class ProductSearchLocalGoodsFragment extends BaseNotifyFragment {

	PullToRefreshScrollView refreshScrollView;
	InnerGridView productGridView;

	List<ModelLocalGoods> localGoods;
	GoodsAdapter goodsAdapter;

	int pageSize = 12, pageNo = 0;

	String searchName = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_search_products);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(ProductSearchLocalGoodsFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(ProductSearchLocalGoodsFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		refresh();
	}

	private void init() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("searchName")) {
				searchName = bundle.getString("searchName");
			}
		}
		localGoods = new ArrayList<ModelLocalGoods>();
		goodsAdapter = new GoodsAdapter();
	}

	@Override
	protected void findViews() {
		refreshScrollView = (PullToRefreshScrollView) contentView
				.findViewById(R.id.product_search_scroll);
		productGridView = (InnerGridView) contentView
				.findViewById(R.id.product_search_list);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		productGridView.setAdapter(goodsAdapter);
		refreshScrollView.setMode(Mode.BOTH);
	}

	@Override
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
						getGoods();
					}
				});
		productGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bundle bundle = new Bundle();
				bundle.putString("goodsId", localGoods.get(arg2).getId());
				openWindow(LocalGoodsDetailFragment.class.getName(), localGoods
						.get(arg2).getRetailProdManagerName(), bundle);
			}
		});
	}

	private void refresh() {
		refreshScrollView.setMode(Mode.BOTH);
		pageNo = 0;
		localGoods.clear();
		goodsAdapter.notifyDataSetChanged();
		getGoods();
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
		pageNo++;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("pageNo", pageNo + ""));
		nameValuePairs.add(new BasicNameValuePair("pageSize", pageSize + ""));
		nameValuePairs.add(new BasicNameValuePair("retailProdManagerName",
				searchName));
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
						refreshScrollView.onRefreshComplete();
					}

					@Override
					public void onSuccess(NetworkMissionMessage message) {
						super.onSuccess(message);
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
												refreshScrollView
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
						refreshScrollView.setMode(Mode.PULL_FROM_START);
						if (localGoods.isEmpty()) {
							loadingEmpty();
						}
					}

				});

	}

}