package yitgogo.smart.home.part;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.home.model.ModelScoreProduct;
import yitgogo.smart.sale.ui.ProductScoreDetailFragment;
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

public class PartScoreFragment extends BaseNormalFragment {

	static PartScoreFragment scoreFragment;
	GridView gridView;
	List<ModelScoreProduct> scoreProducts;
	ScoreAdapter scoreAdapter;

	public static PartScoreFragment getScoreFragment() {
		if (scoreFragment == null) {
			scoreFragment = new PartScoreFragment();
		}
		return scoreFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		measureScreen();
		scoreProducts = new ArrayList<ModelScoreProduct>();
		scoreAdapter = new ScoreAdapter();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.part_home_score, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		gridView = (GridView) view.findViewById(R.id.score_grid);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		gridView.setAdapter(scoreAdapter);
	}

	@Override
	protected void registerViews() {
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bundle bundle = new Bundle();
				bundle.putString("productId", scoreProducts.get(arg2).getId());
				openWindow(ProductScoreDetailFragment.class.getName(),
						scoreProducts.get(arg2).getName(), bundle);
			}
		});
	}

	public void refresh(String result) {
		scoreProducts.clear();
		scoreAdapter.notifyDataSetChanged();
		if (result.length() > 0) {
			JSONObject object;
			try {
				object = new JSONObject(result);
				if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONArray array = object.optJSONArray("dataList");
					if (array != null) {
						for (int i = 0; i < array.length(); i++) {
							scoreProducts.add(new ModelScoreProduct(array
									.optJSONObject(i)));
						}
						if (scoreProducts.size() > 0) {
							int colums = scoreProducts.size();
							gridView.setLayoutParams(new LinearLayout.LayoutParams(
									colums * HomeData.columWidth,
									LinearLayout.LayoutParams.MATCH_PARENT));
							gridView.setColumnWidth(HomeData.columWidth);
							gridView.setStretchMode(GridView.NO_STRETCH);
							gridView.setNumColumns(colums);
							scoreAdapter.notifyDataSetChanged();
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (scoreProducts.isEmpty()) {
			getView().setVisibility(View.GONE);
		} else {
			getView().setVisibility(View.VISIBLE);
		}
	}

	class ScoreAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return scoreProducts.size();
		}

		@Override
		public Object getItem(int position) {
			return scoreProducts.get(position);
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
				viewHolder.scoreTextView = (TextView) convertView
						.findViewById(R.id.list_local_miaosha_price_original);
				viewHolder.priceTextView = (TextView) convertView
						.findViewById(R.id.list_local_miaosha_price);
				viewHolder.nameTextView = (TextView) convertView
						.findViewById(R.id.list_local_miaosha_name);
				FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT,
						HomeData.imageHeight);
				viewHolder.imageView.setLayoutParams(layoutParams);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			imageLoader.displayImage(
					getSmallImageUrl(scoreProducts.get(position).getImgs()),
					viewHolder.imageView);
			viewHolder.nameTextView.setText(scoreProducts.get(position)
					.getName());
			viewHolder.scoreTextView.setText(scoreProducts.get(position)
					.getJifen() + "积分+");
			viewHolder.priceTextView.setText(Parameters.CONSTANT_RMB
					+ decimalFormat.format(scoreProducts.get(position)
							.getJifenjia()));
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView priceTextView, scoreTextView, nameTextView;
		}
	}
}
