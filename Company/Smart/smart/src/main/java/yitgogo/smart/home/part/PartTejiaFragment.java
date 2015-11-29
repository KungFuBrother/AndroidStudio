package yitgogo.smart.home.part;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.home.model.ModelSaleTejia;
import yitgogo.smart.tools.QrCodeTool;
import yitgogo.smart.view.InnerGridView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.smartown.yitgogo.smart.R;

public class PartTejiaFragment extends BaseNormalFragment {

	static PartTejiaFragment tejiaFragment;
	ModelSaleTejia saleTejia;
	InnerGridView tejiaGridView;

	ProductAdapter productAdapter;

	public static PartTejiaFragment getTejiaFragment() {
		if (tejiaFragment == null) {
			tejiaFragment = new PartTejiaFragment();
		}
		return tejiaFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		measureScreen();
		saleTejia = new ModelSaleTejia();
		productAdapter = new ProductAdapter();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.part_home_sale_tejia, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		tejiaGridView = (InnerGridView) view.findViewById(R.id.tejia_grid);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		tejiaGridView.setAdapter(productAdapter);
	}

	@Override
	protected void registerViews() {
		tejiaGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				showProductDetail(saleTejia.getTejiaProducts().get(arg2)
						.getProductId(), QrCodeTool.SALE_TYPE_TEJIA);
			}
		});
	}

	public void refresh(String result) {
		if (result.length() > 0) {
			JSONObject object;
			try {
				object = new JSONObject(result);
				if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONObject dataMap = object.optJSONObject("dataMap");
					saleTejia = new ModelSaleTejia(dataMap);
					if (saleTejia.getTejiaProducts().size() > 0) {
						int colums = saleTejia.getTejiaProducts().size();
						tejiaGridView
								.setLayoutParams(new LinearLayout.LayoutParams(
										colums * HomeData.columWidth,
										LinearLayout.LayoutParams.MATCH_PARENT));
						tejiaGridView.setColumnWidth(HomeData.columWidth);
						tejiaGridView.setStretchMode(GridView.NO_STRETCH);
						tejiaGridView.setNumColumns(colums);
						productAdapter.notifyDataSetChanged();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (saleTejia.getTejiaProducts().isEmpty()) {
			getView().setVisibility(View.GONE);
		} else {
			getView().setVisibility(View.VISIBLE);
		}
	}

	class ProductAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return saleTejia.getTejiaProducts().size();
		}

		@Override
		public Object getItem(int position) {
			return saleTejia.getTejiaProducts().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.list_tejia, null);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.tejia_image);
				FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT,
						HomeData.imageHeight);
				viewHolder.imageView.setLayoutParams(layoutParams);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			imageLoader.displayImage(getSmallImageUrl(saleTejia
					.getTejiaProducts().get(position).getImg()),
					viewHolder.imageView);
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
		}
	}

}
