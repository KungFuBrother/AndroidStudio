package yitgogo.smart.home.part;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.local.LocalServiceDetailFragment;
import yitgogo.smart.local.NongfuFragment;
import yitgogo.smart.local.model.ModelLocalService;
import yitgogo.smart.local.model.ModelNongfu;
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

public class PartNongfuFragment extends BaseNormalFragment {

	static PartNongfuFragment nongfuFragment;

	InnerGridView nongfuGridView;
	LinearLayout moreButton;

	List<ModelLocalService> localServices = new ArrayList<ModelLocalService>();
	ServiceAdapter serviceAdapter = new ServiceAdapter();

	public static PartNongfuFragment getNongfuFragment() {
		if (nongfuFragment == null) {
			nongfuFragment = new PartNongfuFragment();
		}
		return nongfuFragment;
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
		View view = inflater.inflate(R.layout.part_home_nongfu, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		moreButton = (LinearLayout) view.findViewById(R.id.nongfu_more);
		nongfuGridView = (InnerGridView) view.findViewById(R.id.nongfu_grid);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		nongfuGridView.setAdapter(serviceAdapter);
	}

	@Override
	protected void registerViews() {
		moreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openWindow(NongfuFragment.class.getName(), "农副产品");
			}
		});
		nongfuGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bundle bundle = new Bundle();
				bundle.putString("object", localServices.get(arg2)
						.getJsonObject().toString());
				openWindow(LocalServiceDetailFragment.class.getName(),
						localServices.get(arg2).getProductName(), bundle);
			}
		});
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
							ModelNongfu modelNongfu = new ModelNongfu(
									array.optJSONObject(i));
							localServices.add(modelNongfu.getLocalService());
						}
						if (localServices.size() > 0) {
							int colums = localServices.size();
							nongfuGridView
									.setLayoutParams(new LinearLayout.LayoutParams(
											colums * HomeData.columWidth,
											LinearLayout.LayoutParams.MATCH_PARENT));
							nongfuGridView.setColumnWidth(HomeData.columWidth);
							nongfuGridView.setStretchMode(GridView.NO_STRETCH);
							nongfuGridView.setNumColumns(colums);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		serviceAdapter.notifyDataSetChanged();
		if (localServices.isEmpty()) {
			getView().setVisibility(View.GONE);
		} else {
			getView().setVisibility(View.VISIBLE);
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
