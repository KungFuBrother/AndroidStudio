package yitgogo.consumer.home.part;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNormalFragment;
import yitgogo.consumer.home.model.ModelHomeBrand;
import yitgogo.consumer.product.ui.ProductListFragment;
import yitgogo.consumer.view.InnerGridView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;

public class PartBrandFragment extends BaseNormalFragment {

	static PartBrandFragment brandFragment;
	List<ModelHomeBrand> brands;
	BrandAdapter brandAdapter;
	InnerGridView brandList;
	HorizontalScrollView horizontalScrollView;

	public static PartBrandFragment getBrandFragment() {
		if (brandFragment == null) {
			brandFragment = new PartBrandFragment();
		}
		return brandFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		measureScreen();
		brands = new ArrayList<ModelHomeBrand>();
		brandAdapter = new BrandAdapter();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_part_brand, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		brandList = (InnerGridView) view.findViewById(R.id.part_brand_list);
		horizontalScrollView = (HorizontalScrollView) view
				.findViewById(R.id.part_brand_horizontal_scroll);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		brandList.setAdapter(brandAdapter);
		// LayoutParams layoutParams = new
		// LayoutParams(LayoutParams.WRAP_CONTENT,
		// screenWidth / 5 * 2);
		// horizontalScrollView.setLayoutParams(layoutParams);
	}

	@Override
	protected void registerViews() {
		brandList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				jumpProductList(brands.get(arg2).getBrandName(),
						brands.get(arg2).getBrandId(),
						ProductListFragment.TYPE_BRAND);
			}
		});
	}

	public void refresh(String result) {
		brands.clear();
		brandAdapter.notifyDataSetChanged();
		if (result.length() > 0) {
			JSONObject object;
			try {
				object = new JSONObject(result);
				if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONArray array = object.optJSONArray("dataList");
					if (array != null) {
						for (int i = 0; i < array.length(); i++) {
							brands.add(new ModelHomeBrand(array
									.optJSONObject(i)));
						}
						if (brands.size() > 0) {
							getView().setVisibility(View.VISIBLE);
							int colums = 0;
							if (brands.size() < 8) {
								colums = 4;
							} else {
								if (brands.size() % 2 == 0) {
									colums = brands.size() / 2;
								} else {
									colums = brands.size() / 2 + 1;
								}
							}
							brandList
									.setLayoutParams(new LinearLayout.LayoutParams(
											colums * (screenWidth / 4),
											LinearLayout.LayoutParams.MATCH_PARENT));
							brandList.setColumnWidth(screenWidth / 4);
							brandList.setStretchMode(GridView.NO_STRETCH);
							brandList.setNumColumns(colums);
							brandAdapter.notifyDataSetChanged();
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (brands.isEmpty()) {
			getView().setVisibility(View.GONE);
		} else {
			getView().setVisibility(View.VISIBLE);
		}
	}

	class BrandAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return brands.size();
		}

		@Override
		public Object getItem(int position) {
			return brands.get(position);
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
				convertView = layoutInflater.inflate(
						R.layout.list_home_class_brand, null);
				holder.brandLogoImage = (ImageView) convertView
						.findViewById(R.id.list_class_brand_image);
				holder.brandNameText = (TextView) convertView
						.findViewById(R.id.list_class_brand_name);
				android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(
						android.widget.AbsListView.LayoutParams.MATCH_PARENT,
						screenWidth / 5);
				// holder.brandNameText.setVisibility(View.GONE);
				convertView.setLayoutParams(params);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ModelHomeBrand homeBrand = brands.get(position);
			holder.brandNameText.setText(homeBrand.getBrandName());
			ImageLoader.getInstance().displayImage(homeBrand.getBrandLogo(),
					holder.brandLogoImage);
			return convertView;
		}

		class ViewHolder {
			TextView brandNameText;
			ImageView brandLogoImage;
		}

	}

}
