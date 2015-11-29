package yitgogo.smart.home.part;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.local.LocalBusinessFragment;
import yitgogo.smart.local.LocalGoodsDetailFragment;
import yitgogo.smart.local.LocalServiceDetailFragment;
import yitgogo.smart.local.model.ModelLocalGoods;
import yitgogo.smart.local.model.ModelLocalService;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.InnerGridView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;

public class PartLocalBusinessFragment extends BaseNormalFragment {

	static PartLocalBusinessFragment localBusinessFragment;

	InnerGridView localGoodsGridView, localServiceGridView;
	LinearLayout moreButton;

	List<ModelLocalGoods> localGoods = new ArrayList<ModelLocalGoods>();
	GoodsAdapter goodsAdapter = new GoodsAdapter();

	List<ModelLocalService> localServices = new ArrayList<ModelLocalService>();
	ServiceAdapter serviceAdapter = new ServiceAdapter();

	public static PartLocalBusinessFragment getLocalBusinessFragment() {
		if (localBusinessFragment == null) {
			localBusinessFragment = new PartLocalBusinessFragment();
		}
		return localBusinessFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		measureScreen();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.part_home_local_business, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		moreButton = (LinearLayout) view.findViewById(R.id.local_business_more);
		localGoodsGridView = (InnerGridView) view
				.findViewById(R.id.local_goods_grid);
		localServiceGridView = (InnerGridView) view
				.findViewById(R.id.local_service_grid);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		localGoodsGridView.setAdapter(goodsAdapter);
		localServiceGridView.setAdapter(serviceAdapter);
	}

	@Override
	protected void registerViews() {
		moreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openWindow(LocalBusinessFragment.class.getName(), "易商圈");
			}
		});
		localGoodsGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bundle bundle = new Bundle();
				bundle.putString("goodsId", localGoods.get(arg2).getId());
				openWindow(LocalGoodsDetailFragment.class.getName(), localGoods
						.get(arg2).getRetailProdManagerName(), bundle);

			}
		});
		localServiceGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bundle bundle = new Bundle();
				bundle.putString("productId", localServices.get(arg2).getId());
				openWindow(LocalServiceDetailFragment.class.getName(),
						localServices.get(arg2).getProductName(), bundle);
			}
		});
	}

	public void refreshGoods(String result) {
		localGoods.clear();
		if (result.length() > 0) {
			JSONObject object;
			try {
				object = new JSONObject(result);
				if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONArray array = object.optJSONArray("dataList");
					if (array != null) {
						for (int i = 0; i < array.length(); i++) {
							localGoods.add(new ModelLocalGoods(array
									.optJSONObject(i)));
						}
						if (localGoods.size() > 0) {
							int colums = localGoods.size();
							localGoodsGridView
									.setLayoutParams(new LinearLayout.LayoutParams(
											colums * HomeData.columWidth,
											LinearLayout.LayoutParams.MATCH_PARENT));
							localGoodsGridView
									.setColumnWidth(HomeData.columWidth);
							localGoodsGridView
									.setStretchMode(GridView.NO_STRETCH);
							localGoodsGridView.setNumColumns(colums);
							goodsAdapter.notifyDataSetChanged();
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (localGoods.isEmpty() & localServices.isEmpty()) {
			getView().setVisibility(View.GONE);
		} else {
			getView().setVisibility(View.VISIBLE);
		}
	}

	public void refreshService(String result) {
		localServices.clear();
		if (result.length() > 0) {
			JSONObject object;
			try {
				object = new JSONObject(result);
				if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONArray array = object.optJSONArray("dataList");
					if (array != null) {
						for (int i = 0; i < array.length(); i++) {
							localServices.add(new ModelLocalService(array
									.optJSONObject(i)));
						}
						if (localServices.size() > 0) {
							int colums = localServices.size();
							localServiceGridView
									.setLayoutParams(new LinearLayout.LayoutParams(
											colums * HomeData.columWidth,
											LinearLayout.LayoutParams.MATCH_PARENT));
							localServiceGridView
									.setColumnWidth(HomeData.columWidth);
							localServiceGridView
									.setStretchMode(GridView.NO_STRETCH);
							localServiceGridView.setNumColumns(colums);
							serviceAdapter.notifyDataSetChanged();
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (localGoods.isEmpty() & localServices.isEmpty()) {
			getView().setVisibility(View.GONE);
		} else {
			getView().setVisibility(View.VISIBLE);
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
			imageLoader.displayImage(localService.getImg(),
					viewHolder.imageView);
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView priceTextView, nameTextView;
		}
	}

}
