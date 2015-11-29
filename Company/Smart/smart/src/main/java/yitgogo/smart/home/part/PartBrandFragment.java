package yitgogo.smart.home.part;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.home.model.ModelHomeBrand;
import yitgogo.smart.product.ui.ProductBrandFragment;
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

import com.smartown.yitgogo.smart.R;

public class PartBrandFragment extends BaseNormalFragment {

	static PartBrandFragment brandFragment;
	List<ModelHomeBrand> brands;
	BrandAdapter brandAdapter;
	GridView brandList;
	int columWidth = 150;
	int columHeight = 100;

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
		brands = new ArrayList<ModelHomeBrand>();
		brandAdapter = new BrandAdapter();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.part_home_brand, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		brandList = (GridView) view.findViewById(R.id.brand_grid);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		brandList.setAdapter(brandAdapter);
	}

	@Override
	protected void registerViews() {
		brandList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				HomeData.getInstance().setBrandSelection(arg2);
				openWindow(ProductBrandFragment.class.getName(), "品牌专区");
			}
		});
	}

	public void refresh() {
		brands = HomeData.getInstance().getBrands();
		if (brands.size() > 0) {
			int colums = brands.size();
			brandList.setLayoutParams(new LinearLayout.LayoutParams(colums
					* columWidth, LinearLayout.LayoutParams.MATCH_PARENT));
			brandList.setColumnWidth(columWidth);
			brandList.setStretchMode(GridView.NO_STRETCH);
			brandList.setNumColumns(colums);
		}
		brandAdapter.notifyDataSetChanged();
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
				convertView = layoutInflater.inflate(R.layout.list_home_brand,
						null);
				holder.brandLogoImage = (ImageView) convertView
						.findViewById(R.id.list_home_brand_image);
				android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(
						columWidth, columHeight);
				convertView.setLayoutParams(params);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ModelHomeBrand homeBrand = brands.get(position);
			imageLoader.displayImage(
					getSmallImageUrl(homeBrand.getBrandLogo()),
					holder.brandLogoImage);
			return convertView;
		}

		class ViewHolder {
			ImageView brandLogoImage;
		}

	}

}
