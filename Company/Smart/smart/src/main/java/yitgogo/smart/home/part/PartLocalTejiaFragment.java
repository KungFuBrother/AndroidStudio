package yitgogo.smart.home.part;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.local.LocalSaleTejiaDetailFragment;
import yitgogo.smart.local.model.ModelLocalSaleTejia;
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

public class PartLocalTejiaFragment extends BaseNormalFragment {

	static PartLocalTejiaFragment localTejiaFragment;

	GridView gridView;
	List<ModelLocalSaleTejia> localSaleTejias;
	ProductAdapter productAdapter;

	public static PartLocalTejiaFragment getLocalTejiaFragment() {
		if (localTejiaFragment == null) {
			localTejiaFragment = new PartLocalTejiaFragment();
		}
		return localTejiaFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		measureScreen();
		localSaleTejias = new ArrayList<ModelLocalSaleTejia>();
		productAdapter = new ProductAdapter();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.part_home_local_sale_tejia, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		gridView = (GridView) view.findViewById(R.id.miaosha_grid);
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
				bundle.putString("id", localSaleTejias.get(arg2).getProductId());
				openWindow(LocalSaleTejiaDetailFragment.class.getName(),
						localSaleTejias.get(arg2).getProductName(), bundle);
			}
		});
	}

	public void refresh(String result) {
		localSaleTejias.clear();
		productAdapter.notifyDataSetChanged();
		if (result.length() > 0) {
			JSONObject object;
			try {
				object = new JSONObject(result);
				if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONArray array = object.optJSONArray("dataList");
					if (array != null) {
						for (int i = 0; i < array.length(); i++) {
							localSaleTejias.add(new ModelLocalSaleTejia(array
									.optJSONObject(i)));
						}
						if (localSaleTejias.size() > 0) {
							int colums = localSaleTejias.size();
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
		if (localSaleTejias.isEmpty()) {
			getView().setVisibility(View.GONE);
		} else {
			getView().setVisibility(View.VISIBLE);
		}
	}

	class ProductAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return localSaleTejias.size();
		}

		@Override
		public Object getItem(int position) {
			return localSaleTejias.get(position);
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
						R.layout.list_local_sale_tejia, null);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.list_local_tejia_image);
				viewHolder.priceTextView = (TextView) convertView
						.findViewById(R.id.list_local_tejia_price);
				viewHolder.originalPriceTextView = (TextView) convertView
						.findViewById(R.id.list_local_tejia_price_original);
				viewHolder.nameTextView = (TextView) convertView
						.findViewById(R.id.list_local_tejia_name);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						HomeData.imageHeight);
				viewHolder.imageView.setLayoutParams(layoutParams);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			imageLoader.displayImage(
					getSmallImageUrl(localSaleTejias.get(position)
							.getPromotionImg()), viewHolder.imageView);
			viewHolder.nameTextView.setText(localSaleTejias.get(position)
					.getProductName());
			viewHolder.priceTextView.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(localSaleTejias.get(position)
							.getPromotionalPrice()));
			viewHolder.originalPriceTextView.setText("原价:"
					+ Parameters.CONSTANT_RMB
					+ decimalFormat.format(localSaleTejias.get(position)
							.getProductPrice()));
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView originalPriceTextView, priceTextView, nameTextView;
		}
	}

}
