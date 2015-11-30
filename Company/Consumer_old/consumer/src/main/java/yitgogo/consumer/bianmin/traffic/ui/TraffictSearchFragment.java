package yitgogo.consumer.bianmin.traffic.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.bianmin.traffic.model.ModelCarType;
import yitgogo.consumer.bianmin.traffic.model.ModelCity;
import yitgogo.consumer.bianmin.traffic.model.ModelProvince;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ScreenUtil;
import yitgogo.consumer.view.Notify;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class TraffictSearchFragment extends BaseNotifyFragment {

	TextView areaTextView, carTypeTextView;
	EditText plateNumberEditText, frameNumberEditText, engineNumberEditText;
	Button searchButton;

	List<ModelProvince> provinces;
	ProvinceAdapetr provinceAdapetr;
	List<ModelCity> cities;
	CityAdapetr cityAdapetr;
	List<ModelCarType> carTypes;
	CarTypeAdapetr carTypeAdapetr;

	ModelProvince province = new ModelProvince();
	ModelCity city = new ModelCity();
	ModelCarType carType = new ModelCarType();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_bianmin_traffic_search);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TraffictSearchFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TraffictSearchFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	private void init() {
		measureScreen();
		provinces = new ArrayList<ModelProvince>();
		provinceAdapetr = new ProvinceAdapetr();
		cities = new ArrayList<ModelCity>();
		cityAdapetr = new CityAdapetr();
		carTypes = new ArrayList<ModelCarType>();
		carTypeAdapetr = new CarTypeAdapetr();
	}

	@Override
	protected void findViews() {
		areaTextView = (TextView) contentView
				.findViewById(R.id.traffic_search_area);
		carTypeTextView = (TextView) contentView
				.findViewById(R.id.traffic_search_cartype);
		plateNumberEditText = (EditText) contentView
				.findViewById(R.id.traffic_search_plate_number);
		frameNumberEditText = (EditText) contentView
				.findViewById(R.id.traffic_search_frame_number);
		engineNumberEditText = (EditText) contentView
				.findViewById(R.id.traffic_search_engine_number);
		searchButton = (Button) contentView
				.findViewById(R.id.traffic_search_search);
		registerViews();
	}

	@Override
	protected void registerViews() {
		areaTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (provinces.size() <= 0) {
					new GetProvince().execute();
				} else {
					new ProvinceDialog().show(getFragmentManager(), null);
				}
			}
		});
		carTypeTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (carTypes.size() <= 0) {
					new GetCarType().execute();
				} else {
					new CarTypeDialog().show(getFragmentManager(), null);
				}
			}
		});
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search();
			}
		});
	}

	private void search() {
		if (TextUtils.isEmpty(province.getName())) {
			Notify.show("请选择所在城市");
		} else if (TextUtils.isEmpty(city.getName())) {
			Notify.show("请选择所在城市");
		} else if (TextUtils.isEmpty(carType.getId())) {
			Notify.show("请选择车型");
		} else if (TextUtils.isEmpty(plateNumberEditText.getText().toString()
				.trim())) {
			Notify.show("请输入车牌号");
		} else if (TextUtils.isEmpty(frameNumberEditText.getText().toString()
				.trim())) {
			Notify.show("请输入车架号");
		} else if (TextUtils.isEmpty(engineNumberEditText.getText().toString()
				.trim())) {
			Notify.show("请输入发动机号");
		} else {
			Bundle bundle = new Bundle();
			bundle.putString("provName", province.getName());
			bundle.putString("cityName", city.getName());
			bundle.putString("plateNumber", plateNumberEditText.getText()
					.toString().trim());
			bundle.putString("frameNumber", frameNumberEditText.getText()
					.toString().trim());
			bundle.putString("engineNumber", engineNumberEditText.getText()
					.toString().trim());
			bundle.putString("vehicleTypeId", carType.getId());
			jump(TraffictHistoryFragment.class.getName(), "违章查询", bundle);
		}
	}

	class ProvinceDialog extends DialogFragment {

		View dialogView;
		ListView listView;
		TextView titleTextView, button;

		@Override
		public void onCreate(@Nullable Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			findViews();
		}

		@Override
		@NonNull
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Dialog dialog = new Dialog(getActivity());
			dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(dialogView, new LayoutParams(
					LayoutParams.MATCH_PARENT, screenWidth));
			return dialog;
		}

		private void findViews() {
			dialogView = layoutInflater.inflate(R.layout.dialog_list, null);
			titleTextView = (TextView) dialogView
					.findViewById(R.id.dialog_title);
			button = (TextView) dialogView.findViewById(R.id.dialog_button);
			listView = (ListView) dialogView.findViewById(R.id.dialog_list);
			initViews();
		}

		private void initViews() {
			titleTextView.setText("选择所在省");
			button.setText("取消");
			listView.setAdapter(provinceAdapetr);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					province = provinces.get(arg2);
					new GetCities().execute();
					dismiss();
				}
			});
		}
	}

	class CityDialog extends DialogFragment {

		View dialogView;
		ListView listView;
		TextView titleTextView, button;

		@Override
		public void onCreate(@Nullable Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			findViews();
		}

		@Override
		@NonNull
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Dialog dialog = new Dialog(getActivity());
			dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(dialogView, new LayoutParams(
					LayoutParams.MATCH_PARENT, screenWidth));
			return dialog;
		}

		private void findViews() {
			dialogView = layoutInflater.inflate(R.layout.dialog_list, null);
			titleTextView = (TextView) dialogView
					.findViewById(R.id.dialog_title);
			button = (TextView) dialogView.findViewById(R.id.dialog_button);
			listView = (ListView) dialogView.findViewById(R.id.dialog_list);
			initViews();
		}

		private void initViews() {
			titleTextView.setText("选择所在城市");
			button.setText("取消");
			listView.setAdapter(cityAdapetr);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					city = cities.get(arg2);
					areaTextView.setText(province.getName() + ""
							+ city.getName());
					dismiss();
				}
			});
		}
	}

	class CarTypeDialog extends DialogFragment {

		View dialogView;
		ListView listView;
		TextView titleTextView, button;

		@Override
		public void onCreate(@Nullable Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			findViews();
		}

		@Override
		@NonNull
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Dialog dialog = new Dialog(getActivity());
			dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(dialogView, new LayoutParams(
					LayoutParams.MATCH_PARENT, screenWidth));
			return dialog;
		}

		private void findViews() {
			dialogView = layoutInflater.inflate(R.layout.dialog_list, null);
			titleTextView = (TextView) dialogView
					.findViewById(R.id.dialog_title);
			button = (TextView) dialogView.findViewById(R.id.dialog_button);
			listView = (ListView) dialogView.findViewById(R.id.dialog_list);
			initViews();
		}

		private void initViews() {
			titleTextView.setText("选择车型");
			button.setText("取消");
			listView.setAdapter(carTypeAdapetr);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					carType = carTypes.get(arg2);
					carTypeTextView.setText(carType.getName());
					dismiss();
				}
			});
		}
	}

	class ProvinceAdapetr extends BaseAdapter {

		@Override
		public int getCount() {
			return provinces.size();
		}

		@Override
		public Object getItem(int position) {
			return provinces.get(position);
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
				convertView = layoutInflater.inflate(R.layout.list_class_main,
						null);
				holder.textView = (TextView) convertView
						.findViewById(R.id.class_main_name);
				holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
				holder.textView.setGravity(Gravity.CENTER_VERTICAL);
				holder.textView.setPadding(ScreenUtil.dip2px(24), 0,
						ScreenUtil.dip2px(24), 0);
				FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT,
						ScreenUtil.dip2px(48));
				holder.textView.setLayoutParams(layoutParams);
				convertView
						.setBackgroundResource(R.drawable.selector_trans_divider);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textView.setText(provinces.get(position).getName());
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}

	class GetProvince extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
			provinces.clear();
			provinceAdapetr.notifyDataSetChanged();
		}

		@Override
		protected String doInBackground(Void... params) {
			return netUtil.postWithoutCookie(API.API_BIANMIN_TRAFFIC_PROVINCE,
					null, true, true);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				try {
					JSONObject object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						JSONArray array = object.optJSONArray("dataList");
						if (array != null) {
							for (int i = 0; i < array.length(); i++) {
								provinces.add(new ModelProvince(array
										.optJSONObject(i)));
							}
							if (provinces.size() > 0) {
								provinceAdapetr.notifyDataSetChanged();
								new ProvinceDialog().show(getFragmentManager(),
										null);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class GetCities extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
			cities.clear();
			cityAdapetr.notifyDataSetChanged();
		}

		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("provId", province
					.getId()));
			return netUtil.postWithoutCookie(API.API_BIANMIN_TRAFFIC_CITY,
					nameValuePairs, true, true);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				try {
					JSONObject object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						JSONArray array = object.optJSONArray("dataList");
						if (array != null) {
							for (int i = 0; i < array.length(); i++) {
								cities.add(new ModelCity(array.optJSONObject(i)));
							}
							if (cities.size() > 0) {
								cityAdapetr.notifyDataSetChanged();
								new CityDialog().show(getFragmentManager(),
										null);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
	}

	class CityAdapetr extends BaseAdapter {

		@Override
		public int getCount() {
			return cities.size();
		}

		@Override
		public Object getItem(int position) {
			return cities.get(position);
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
				convertView = layoutInflater.inflate(R.layout.list_class_main,
						null);
				holder.textView = (TextView) convertView
						.findViewById(R.id.class_main_name);
				holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
				holder.textView.setGravity(Gravity.CENTER_VERTICAL);
				holder.textView.setPadding(ScreenUtil.dip2px(24), 0,
						ScreenUtil.dip2px(24), 0);
				FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT,
						ScreenUtil.dip2px(48));
				holder.textView.setLayoutParams(layoutParams);
				convertView
						.setBackgroundResource(R.drawable.selector_trans_divider);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textView.setText(cities.get(position).getName());
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}

	class GetCarType extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
			carTypes.clear();
			carTypeAdapetr.notifyDataSetChanged();
		}

		@Override
		protected String doInBackground(String... params) {
			return netUtil.postWithoutCookie(API.API_BIANMIN_TRAFFIC_CARTYPE,
					null, true, true);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				try {
					JSONObject object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						JSONArray array = object.optJSONArray("dataList");
						if (array != null) {
							for (int i = 0; i < array.length(); i++) {
								carTypes.add(new ModelCarType(array
										.optJSONObject(i)));
							}
							if (carTypes.size() > 0) {
								carTypeAdapetr.notifyDataSetChanged();
								new CarTypeDialog().show(getFragmentManager(),
										null);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
	}

	class CarTypeAdapetr extends BaseAdapter {

		@Override
		public int getCount() {
			return carTypes.size();
		}

		@Override
		public Object getItem(int position) {
			return carTypes.get(position);
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
				convertView = layoutInflater.inflate(R.layout.list_class_main,
						null);
				holder.textView = (TextView) convertView
						.findViewById(R.id.class_main_name);
				holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
				holder.textView.setGravity(Gravity.CENTER_VERTICAL);
				holder.textView.setPadding(ScreenUtil.dip2px(24), 0,
						ScreenUtil.dip2px(24), 0);
				FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT,
						ScreenUtil.dip2px(48));
				holder.textView.setLayoutParams(layoutParams);
				convertView
						.setBackgroundResource(R.drawable.selector_trans_divider);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textView.setText(carTypes.get(position).getName());
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}
}
