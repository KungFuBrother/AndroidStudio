package yitgogo.smart.home.part;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.local.LocalBusinessFragment;
import yitgogo.smart.local.LocalServiceDetailFragment;
import yitgogo.smart.local.LoveFreshFragment;
import yitgogo.smart.local.model.ModelLocalService;
import yitgogo.smart.tools.Parameters;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;

public class PartFreshFragment extends BaseNormalFragment {

	static PartFreshFragment freshFragment;
	GridView gridView;
	List<ModelLocalService> freshProducts;
	ProductAdapter productAdapter;
	LinearLayout moreButton;

	public static PartFreshFragment getFreshFragment() {
		if (freshFragment == null) {
			freshFragment = new PartFreshFragment();
		}
		return freshFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		measureScreen();
		freshProducts = new ArrayList<ModelLocalService>();
		productAdapter = new ProductAdapter();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.part_home_fresh, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		moreButton = (LinearLayout) view.findViewById(R.id.fresh_more);
		gridView = (GridView) view.findViewById(R.id.fresh_grid);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		gridView.setAdapter(productAdapter);
	}

	@Override
	protected void registerViews() {
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bundle bundle = new Bundle();
				bundle.putString("productId", freshProducts.get(arg2).getId());
				openWindow(LocalServiceDetailFragment.class.getName(),
						freshProducts.get(arg2).getProductName(), bundle);
			}
		});
		moreButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openWindow(LoveFreshFragment.class.getName(), "爱新鲜");
			}
		});
	}

	public void refresh(String result) {
		freshProducts.clear();
		productAdapter.notifyDataSetChanged();
		if (result.length() > 0) {
			JSONObject object;
			try {
				object = new JSONObject(result);
				if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONArray array = object.optJSONArray("dataList");
					if (array != null) {
						for (int i = 0; i < array.length(); i++) {
							freshProducts.add(new ModelLocalService(array
									.optJSONObject(i)));
						}
						if (freshProducts.size() > 0) {
							int colums = freshProducts.size();
							gridView.setLayoutParams(new LinearLayout.LayoutParams(
									colums * HomeData.columWidth,
									LinearLayout.LayoutParams.MATCH_PARENT));
							gridView.setColumnWidth(HomeData.columWidth);
							gridView.setStretchMode(GridView.NO_STRETCH);
							gridView.setNumColumns(colums);
							productAdapter.notifyDataSetChanged();
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (freshProducts.isEmpty()) {
			getView().setVisibility(View.GONE);
		} else {
			getView().setVisibility(View.VISIBLE);
		}
	}

	class ProductAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return freshProducts.size();
		}

		@Override
		public Object getItem(int position) {
			return freshProducts.get(position);
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
						LinearLayout.LayoutParams.MATCH_PARENT,
						HomeData.imageHeight);
				viewHolder.imageView.setLayoutParams(layoutParams);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			ModelLocalService localService = freshProducts.get(position);
			imageLoader.displayImage(getSmallImageUrl(localService.getImg()),
					viewHolder.imageView);
			viewHolder.priceTextView.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(localService.getProductPrice()));
			viewHolder.nameTextView.setText(localService.getProductName());
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView priceTextView, nameTextView;
		}
	}

}
