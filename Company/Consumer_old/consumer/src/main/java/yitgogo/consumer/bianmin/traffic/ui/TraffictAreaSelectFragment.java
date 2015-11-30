package yitgogo.consumer.bianmin.traffic.ui;

import yitgogo.consumer.BaseNotifyFragment;

public class TraffictAreaSelectFragment extends BaseNotifyFragment {

	// ListView areaListView;
	// ProvinceAdapetr provinceAdapetr;
	// List<ModelProvince> provinces;
	//
	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.fragment_bianmin_traffic_area);
	// init();
	// findViews();
	// }
	//
	// @Override
	// public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	// {
	// super.onViewCreated(view, savedInstanceState);
	// new GetProvince().execute();
	// }
	//
	// private void init() {
	// provinces = new ArrayList<ModelProvince>();
	// provinceAdapetr = new ProvinceAdapetr();
	// }
	//
	// @Override
	// protected void findViews() {
	// areaListView = (ListView) contentView
	// .findViewById(R.id.traffic_area_list);
	// initViews();
	// registerViews();
	// }
	//
	// @Override
	// protected void initViews() {
	// areaListView.setAdapter(provinceAdapetr);
	// }
	//
	// class ProvinceAdapetr extends BaseAdapter {
	//
	// @Override
	// public int getCount() {
	// return provinces.size();
	// }
	//
	// @Override
	// public Object getItem(int position) {
	// return provinces.get(position);
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// return position;
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// ViewHolder holder;
	// if (convertView == null) {
	// holder = new ViewHolder();
	// convertView = layoutInflater.inflate(
	// R.layout.list_bianmin_traffic_area, null);
	// holder.provinceTextView = (TextView) convertView
	// .findViewById(R.id.area_province);
	// holder.cityGridView = (InnerGridView) convertView
	// .findViewById(R.id.area_city);
	// convertView.setTag(holder);
	// } else {
	// holder = (ViewHolder) convertView.getTag();
	// }
	// holder.provinceTextView.setText(provinces.get(position).getName());
	// holder.cityGridView.setAdapter(new CityAdapetr(provinces.get(
	// position).getCities()));
	// return convertView;
	// }
	//
	// class ViewHolder {
	// TextView provinceTextView;
	// InnerGridView cityGridView;
	// }
	//
	// }
	//
	// class CityAdapetr extends BaseAdapter {
	//
	// List<ModelCity> cities = new ArrayList<ModelCity>();
	//
	// public CityAdapetr(List<ModelCity> cities) {
	// this.cities = cities;
	// }
	//
	// @Override
	// public int getCount() {
	// return cities.size();
	// }
	//
	// @Override
	// public Object getItem(int position) {
	// return cities.get(position);
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// return position;
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// ViewHolder holder;
	// if (convertView == null) {
	// holder = new ViewHolder();
	// convertView = layoutInflater.inflate(R.layout.list_class_min,
	// null);
	// holder.textView = (TextView) convertView
	// .findViewById(R.id.class_min_name);
	// FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
	// FrameLayout.LayoutParams.MATCH_PARENT,
	// ScreenUtil.dip2px(36));
	// holder.textView.setLayoutParams(layoutParams);
	// convertView.setTag(holder);
	// } else {
	// holder = (ViewHolder) convertView.getTag();
	// }
	// holder.textView.setText(cities.get(position).getName());
	// return convertView;
	// }
	//
	// class ViewHolder {
	// TextView textView;
	// }
	// }
	//
	// class GetProvince extends AsyncTask<Void, Void, String> {
	//
	// @Override
	// protected void onPreExecute() {
	// showLoading();
	// }
	//
	// @Override
	// protected String doInBackground(Void... params) {
	// return netUtil.postWithoutCookie(API.API_BIANMIN_TRAFFIC_PROVINCE,
	// null);
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// if (result.length() > 0) {
	// try {
	// JSONObject object = new JSONObject(result);
	// if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
	// JSONArray array = object.optJSONArray("dataList");
	// if (array != null) {
	// for (int i = 0; i < array.length(); i++) {
	// provinces.add(new ModelProvince(array
	// .optJSONObject(i)));
	// }
	// if (provinces.size() > 0) {
	// new GetCities().execute();
	// }
	// }
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// }
	//
	// class GetCities extends AsyncTask<Void, Void, String> {
	//
	// @Override
	// protected void onPreExecute() {
	// showLoading();
	// }
	//
	// @Override
	// protected String doInBackground(Void... params) {
	// for (int i = 0; i < provinces.size(); i++) {
	// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	// nameValuePairs.add(new BasicNameValuePair("provId", provinces
	// .get(i).getId()));
	// String result = netUtil.postWithoutCookie(
	// API.API_BIANMIN_TRAFFIC_CITY, nameValuePairs);
	// provinces.get(i).setCities(result);
	// }
	// return "";
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// hideLoading();
	// provinceAdapetr.notifyDataSetChanged();
	// }
	//
	// }
}
