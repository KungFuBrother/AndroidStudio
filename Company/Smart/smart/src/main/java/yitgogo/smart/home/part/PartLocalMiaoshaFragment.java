package yitgogo.smart.home.part;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.local.LocalSaleMiaoshaDetailFragment;
import yitgogo.smart.local.model.ModelLocalSaleMiaosha;
import yitgogo.smart.tools.Parameters;
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
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;

public class PartLocalMiaoshaFragment extends BaseNormalFragment {

	static PartLocalMiaoshaFragment localMiaoshaFragment;
	GridView gridView;
	List<ModelLocalSaleMiaosha> localSaleMiaoshas;
	ProductAdapter productAdapter;

	public static PartLocalMiaoshaFragment getLocalMiaoshaFragment() {
		if (localMiaoshaFragment == null) {
			localMiaoshaFragment = new PartLocalMiaoshaFragment();
		}
		return localMiaoshaFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		measureScreen();
		localSaleMiaoshas = new ArrayList<ModelLocalSaleMiaosha>();
		productAdapter = new ProductAdapter();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.part_home_local_sale_miaosha,
				null);
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
				bundle.putString("id", localSaleMiaoshas.get(arg2)
						.getProductId());
				openWindow(LocalSaleMiaoshaDetailFragment.class.getName(),
						localSaleMiaoshas.get(arg2).getProductName(), bundle);
			}
		});
	}

	public void refresh(String result) {
		localSaleMiaoshas.clear();
		productAdapter.notifyDataSetChanged();
		if (result.length() > 0) {
			JSONObject object;
			try {
				object = new JSONObject(result);
				if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONArray array = object.optJSONArray("dataList");
					if (array != null) {
						for (int i = 0; i < array.length(); i++) {
							localSaleMiaoshas.add(new ModelLocalSaleMiaosha(
									array.optJSONObject(i)));
						}
						if (localSaleMiaoshas.size() > 0) {
							int colums = localSaleMiaoshas.size();
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
		if (localSaleMiaoshas.isEmpty()) {
			getView().setVisibility(View.GONE);
		} else {
			getView().setVisibility(View.VISIBLE);
		}
	}

	class ProductAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return localSaleMiaoshas.size();
		}

		@Override
		public Object getItem(int position) {
			return localSaleMiaoshas.get(position);
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
						R.layout.list_local_sale_miaosha, null);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.list_local_miaosha_image);
				viewHolder.originalPriceTextView = (TextView) convertView
						.findViewById(R.id.list_local_miaosha_price_original);
				viewHolder.priceTextView = (TextView) convertView
						.findViewById(R.id.list_local_miaosha_price);
				viewHolder.timeTextView = (TextView) convertView
						.findViewById(R.id.list_local_miaosha_time);
				viewHolder.nameTextView = (TextView) convertView
						.findViewById(R.id.list_local_miaosha_name);
				viewHolder.timeTextView.setVisibility(View.VISIBLE);
				FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT,
						HomeData.imageHeight);
				viewHolder.imageView.setLayoutParams(layoutParams);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			imageLoader.displayImage(
					getSmallImageUrl(localSaleMiaoshas.get(position)
							.getPromotionImg()), viewHolder.imageView);
			Date currentTime = Calendar.getInstance().getTime();
			Date startTime = new Date(localSaleMiaoshas.get(position)
					.getSpikeTime());
			if (startTime.before(currentTime)) {
				if (localSaleMiaoshas.get(position).getNumbers() <= 0) {
					viewHolder.timeTextView.setText("抢购结束");
				} else {
					viewHolder.timeTextView.setText("抢购中");
				}
			} else {
				viewHolder.timeTextView.setText("开始时间:"
						+ simpleDateFormat.format(startTime));
			}
			viewHolder.nameTextView.setText(localSaleMiaoshas.get(position)
					.getProductName());
			viewHolder.priceTextView.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(localSaleMiaoshas.get(position)
							.getPromotionalPrice()));
			viewHolder.originalPriceTextView.setText("原价:"
					+ Parameters.CONSTANT_RMB
					+ decimalFormat.format(localSaleMiaoshas.get(position)
							.getProductPrice()));
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView priceTextView, originalPriceTextView, nameTextView,
					timeTextView;
		}
	}

}
