package yitgogo.smart.product.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.home.model.ModelClass;
import yitgogo.smart.home.model.ModelListPrice;
import yitgogo.smart.home.model.ModelProduct;
import yitgogo.smart.product.model.ModelAttrType;
import yitgogo.smart.product.model.ModelProductFilter;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.tools.QrCodeTool;
import yitgogo.smart.view.InnerListView;
import android.R.color;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

public class ProductClassesFragment extends BaseNotifyFragment {

	DrawerLayout drawerLayout;
	FrameLayout attrLayout;
	ListView attrListView;

	PullToRefreshScrollView refreshScrollView;
	ListView mainClassList;
	GridView productGridView;
	List<ModelClass> mainClasses;
	MainClassAdapter mainClassAdapter;
	List<ModelProduct> products;
	HashMap<String, ModelListPrice> priceMap;
	ProductAdapter productAdapter;

	// int mainSelection = -1, midSelection = -1, minSelection = -1;

	ModelClass mainClass = new ModelClass(), midClass = new ModelClass(),
			minClass = new ModelClass();

	ModelProductFilter productFilter;
	AttrAdapter attrAdapter;

	int attrSelection = -1;

	int pageSize = 12, pageNo = 0;

	LinearLayout areaSelector;
	TextView areaTextView;

	Areas areas;
	AreaAdapter areaAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_fragment_class);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(ProductClassesFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(ProductClassesFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getArea();
		selectMainClass(mainClasses.get(HomeData.getInstance()
				.getClassSelection()));
	}

	private void init() {
		mainClasses = HomeData.getInstance().getClasses();
		mainClassAdapter = new MainClassAdapter();
		products = new ArrayList<ModelProduct>();
		priceMap = new HashMap<String, ModelListPrice>();
		productAdapter = new ProductAdapter();
		productFilter = new ModelProductFilter();
		attrAdapter = new AttrAdapter();

		areas = new Areas();
		areaAdapter = new AreaAdapter();
	}

	protected void findViews() {
		drawerLayout = (DrawerLayout) contentView
				.findViewById(R.id.product_drawer);
		attrLayout = (FrameLayout) contentView.findViewById(R.id.attr_layout);
		refreshScrollView = (PullToRefreshScrollView) contentView
				.findViewById(R.id.product_class_scroll);
		attrListView = (ListView) contentView.findViewById(R.id.attr_list);
		mainClassList = (ListView) contentView.findViewById(R.id.class_list);
		productGridView = (GridView) contentView
				.findViewById(R.id.product_list);

		areaSelector = (LinearLayout) contentView
				.findViewById(R.id.product_area_select);
		areaTextView = (TextView) contentView.findViewById(R.id.product_area);
		initViews();
		registerViews();
	}

	protected void initViews() {
		mainClassList.setAdapter(mainClassAdapter);
		productGridView.setAdapter(productAdapter);
		attrListView.setAdapter(attrAdapter);
	}

	protected void registerViews() {
		refreshScrollView.setMode(Mode.BOTH);
		refreshScrollView
				.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						refresh();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						getProducts();
					}
				});
		productGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				showProductDetail(products.get(arg2).getId(),
						QrCodeTool.SALE_TYPE_NONE);
			}
		});
		areaSelector.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AreaDialog().show(getFragmentManager(), null);
			}
		});
	}

	private void selectAttr(int position) {
		if (attrSelection == position) {
			attrSelection = -1;
		} else {
			attrSelection = position;
		}
		attrAdapter.notifyDataSetChanged();
	}

	/**
	 * 选择一级分类时做的操作
	 * 
	 * @param position
	 */
	private void selectMainClass(ModelClass modelClass) {
		if (modelClass != null) {
			if (mainClass.getId().equals(modelClass.getId())) {
				mainClass = new ModelClass();
			} else {
				mainClass = modelClass;
				midClass = new ModelClass();
				minClass = new ModelClass();
				getMidClasses();
			}
			mainClassAdapter.notifyDataSetChanged();
		}
		// if (mainClasses.size() > position) {
		// if (mainSelection == position) {
		// mainSelection = -1;
		// } else {
		// mainSelection = position;
		// midSelection = -1;
		// minSelection = -1;
		// setTitle(mainClasses.get(mainSelection).getName());
		// }
		// mainClassList.smoothScrollToPosition(position);
		// }
	}

	/**
	 * 选择二级分类时做的操作
	 * 
	 * @param position
	 */
	private void selectMidClass(ModelClass modelClass) {
		if (modelClass != null) {
			if (midClass.getId().equals(modelClass.getId())) {
				midClass = new ModelClass();
			} else {
				midClass = modelClass;
				minClass = new ModelClass();
				if (midClass.getSubClasses().size() > 0) {
					selectMinClass(midClass.getSubClasses().get(0));
				}
			}
			mainClassAdapter.notifyDataSetChanged();
		}
		// if (mainClasses.get(mainSelection).getSubClasses().size() > position)
		// {
		// if (midSelection == position) {
		// midSelection = -1;
		// } else {
		// midSelection = position;
		// minSelection = -1;
		// setTitle(mainClasses.get(mainSelection).getSubClasses()
		// .get(midSelection).getName());
		// selectMinClass(0);
		// }
		// mainClassAdapter.notifyDataSetChanged();
		// }
	}

	/**
	 * 选择二级分类时做的操作
	 * 
	 * @param position
	 */
	private void selectMinClass(ModelClass modelClass) {
		if (modelClass != null) {
			if (minClass.getId().equals(modelClass.getId())) {
				return;
			}
			minClass = modelClass;
			mainClassAdapter.notifyDataSetChanged();

			removeAllButtons();
			productFilter = new ModelProductFilter();
			attrAdapter.notifyDataSetChanged();
			refresh();
			getAttributes();
		}
		// if (mainClasses.get(mainSelection).getSubClasses().get(midSelection)
		// .getSubClasses().size() > position) {
		// if (minSelection == position) {
		// return;
		// }
		// minSelection = position;
		// setTitle(mainClasses.get(mainSelection).getSubClasses()
		// .get(midSelection).getSubClasses().get(minSelection)
		// .getName());
		// mainClassAdapter.notifyDataSetChanged();
		//
		// }
	}

	private void refresh() {
		refreshScrollView.setMode(Mode.BOTH);
		products.clear();
		productAdapter.notifyDataSetChanged();
		pageNo = 0;
		getProducts();
	}

	private void getProducts() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		pageNo++;
		nameValuePairs.add(new BasicNameValuePair("pageNo", pageNo + ""));
		nameValuePairs.add(new BasicNameValuePair("pageSize", pageSize + ""));
		nameValuePairs.add(new BasicNameValuePair("classValueId", minClass
				.getId()));
		String brandSelection = "", attrSelection = "", attrExtendSelection = "";
		for (int i = 0; i < productFilter.getAllAttrs().size(); i++) {
			ModelAttrType attrType = productFilter.getAllAttrs().get(i);
			if (attrType.getSelection() > 0) {
				switch (attrType.getType()) {
				case ModelAttrType.TYPE_BRAND:
					brandSelection = attrType.getAttrValues()
							.get(attrType.getSelection()).getId();
					break;

				case ModelAttrType.TYPE_ATTR:
					if (attrSelection.length() > 0) {
						attrSelection += ",";
					}
					attrSelection += attrType.getAttrValues()
							.get(attrType.getSelection()).getId();
					break;

				case ModelAttrType.TYPE_ATTR_EXTEND:
					if (attrExtendSelection.length() > 0) {
						attrExtendSelection += ",";
					}
					attrExtendSelection += attrType.getAttrValues()
							.get(attrType.getSelection()).getId();
					break;

				default:
					break;
				}
			}
		}
		if (brandSelection.length() > 0) {
			nameValuePairs
					.add(new BasicNameValuePair("brandId", brandSelection));
		}
		if (attrSelection.length() > 0) {
			nameValuePairs.add(new BasicNameValuePair("avId", attrSelection));
		}
		if (attrExtendSelection.length() > 0) {
			nameValuePairs.add(new BasicNameValuePair("aveIds",
					attrExtendSelection));
		}
		if (areas.notEmpty()) {
			if (areas.getSelection() > -1) {
				if (areas.getSelection() < areas.getAreas().size()) {
					nameValuePairs.add(new BasicNameValuePair("areaId", areas
							.getAreas().get(areas.getSelection()).getId()));
				}
			}
		}
		ProductTask.getProducts(getActivity(), nameValuePairs,
				new OnNetworkListener() {

					@Override
					public void onStart() {
						super.onStart();
						showLoading();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						refreshScrollView.onRefreshComplete();
						hideLoading();
					}

					@Override
					public void onSuccess(NetworkMissionMessage message) {
						super.onSuccess(message);
						if (!TextUtils.isEmpty(message.getResult())) {
							JSONObject info;
							try {
								info = new JSONObject(message.getResult());
								if (info.getString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONArray productArray = info
											.getJSONArray("dataList");
									if (productArray != null) {
										if (productArray.length() > 0) {
											if (productArray.length() < pageSize) {
												refreshScrollView
														.setMode(Mode.PULL_FROM_START);
											}
											StringBuilder stringBuilder = new StringBuilder();
											for (int i = 0; i < productArray
													.length(); i++) {
												ModelProduct product = new ModelProduct(
														productArray
																.getJSONObject(i));
												products.add(product);
												if (i > 0) {
													stringBuilder.append(",");
												}
												stringBuilder.append(product
														.getId());
											}
											productAdapter
													.notifyDataSetChanged();
											getProductPrice(stringBuilder
													.toString());
											return;
										}
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						refreshScrollView.setMode(Mode.PULL_FROM_START);
						if (products.isEmpty()) {
							loadingEmpty();
						}
					}
				});
	}

	private void getProductPrice(String productIds) {
		ProductTask.getProductPrice(getActivity(), productIds,
				new OnNetworkListener() {

					@Override
					public void onSuccess(NetworkMissionMessage requestMessage) {
						super.onSuccess(requestMessage);
						if (!TextUtils.isEmpty(requestMessage.getResult())) {
							JSONObject object;
							try {
								object = new JSONObject(requestMessage
										.getResult());
								if (object.getString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONArray priceArray = object
											.getJSONArray("dataList");
									if (priceArray.length() > 0) {
										for (int i = 0; i < priceArray.length(); i++) {
											ModelListPrice priceList = new ModelListPrice(
													priceArray.getJSONObject(i));
											priceMap.put(
													priceList.getProductId(),
													priceList);
										}
										productAdapter.notifyDataSetChanged();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

	class MainClassAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mainClasses.size();
		}

		@Override
		public Object getItem(int position) {
			return mainClasses.get(position);
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
				holder.nameText = (TextView) convertView
						.findViewById(R.id.class_main_name);
				holder.midLayout = (LinearLayout) convertView
						.findViewById(R.id.class_main_mid_layout);
				holder.midListView = (InnerListView) convertView
						.findViewById(R.id.class_main_mid);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final ModelClass modelClass = mainClasses.get(position);
			if (mainClass.getId().equals(modelClass.getId())) {
				convertView
						.setBackgroundResource(R.color.theme_title_background);
				holder.midLayout.setVisibility(View.VISIBLE);
				holder.midListView.setAdapter(new MidClassAdapter(mainClass
						.getSubClasses()));
			} else {
				convertView
						.setBackgroundResource(R.drawable.selector_white_divider);
				holder.midLayout.setVisibility(View.GONE);
			}
			holder.nameText.setText(modelClass.getName());
			holder.nameText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					selectMainClass(modelClass);
				}
			});
			return convertView;
		}

		class ViewHolder {
			TextView nameText;
			LinearLayout midLayout;
			InnerListView midListView;
		}
	}

	class MidClassAdapter extends BaseAdapter {

		List<ModelClass> midClasses;

		public MidClassAdapter(List<ModelClass> midClasses) {
			this.midClasses = midClasses;
		}

		@Override
		public int getCount() {
			return midClasses.size();
		}

		@Override
		public Object getItem(int position) {
			return midClasses.get(position);
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
				convertView = layoutInflater.inflate(R.layout.list_class_mid,
						null);
				holder.nameText = (TextView) convertView
						.findViewById(R.id.class_mid_name);
				holder.minLayout = (LinearLayout) convertView
						.findViewById(R.id.class_mid_min_layout);
				holder.minClassesList = (InnerListView) convertView
						.findViewById(R.id.class_mid_subclasses);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final ModelClass modelClass = midClasses.get(position);
			if (midClass.getId().equals(modelClass.getId())) {
				holder.minLayout.setVisibility(View.VISIBLE);
			} else {
				holder.minLayout.setVisibility(View.GONE);
			}
			holder.nameText.setText(modelClass.getName());
			holder.minClassesList.setAdapter(new MinClassAdapter(modelClass
					.getSubClasses()));
			holder.nameText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					selectMidClass(modelClass);
				}
			});
			return convertView;
		}

		class ViewHolder {
			TextView nameText;
			LinearLayout minLayout;
			InnerListView minClassesList;
		}
	}

	class MinClassAdapter extends BaseAdapter {

		List<ModelClass> minClasses;

		public MinClassAdapter(List<ModelClass> minClasses) {
			this.minClasses = minClasses;
		}

		@Override
		public int getCount() {
			return minClasses.size();
		}

		@Override
		public Object getItem(int position) {
			return minClasses.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = layoutInflater.inflate(
						R.layout.list_local_business_class, null);
				viewHolder.selector = convertView
						.findViewById(R.id.local_business_class_selector);
				viewHolder.className = (TextView) convertView
						.findViewById(R.id.local_business_class_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final ModelClass modelClass = minClasses.get(position);
			if (minClass.getId().equals(modelClass.getId())) {
				viewHolder.selector
						.setBackgroundResource(R.color.textColorCompany);
				viewHolder.className.setTextColor(getResources().getColor(
						R.color.textColorCompany));
			} else {
				viewHolder.selector.setBackgroundResource(color.transparent);
				viewHolder.className.setTextColor(getResources().getColor(
						R.color.textColorThird));
			}
			viewHolder.className.setText(modelClass.getName());
			viewHolder.className.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					selectMinClass(modelClass);
				}
			});
			return convertView;
		}

		class ViewHolder {
			TextView className;
			View selector;
		}

	}

	class AttrAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return productFilter.getAllAttrs().size();
		}

		@Override
		public Object getItem(int position) {
			return productFilter.getAllAttrs().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int index = position;
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.list_class_attr,
						null);
				holder.attrTypeLayout = (LinearLayout) convertView
						.findViewById(R.id.class_attr_type_layout);
				holder.attrTypeTextView = (TextView) convertView
						.findViewById(R.id.class_attr_type_name);
				holder.attrValueTextView = (TextView) convertView
						.findViewById(R.id.class_attr_type_value);
				holder.attrValueLayout = (LinearLayout) convertView
						.findViewById(R.id.class_attr_value_layout);
				holder.attrValueList = (InnerListView) convertView
						.findViewById(R.id.class_attr_value_list);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (attrSelection == position) {
				holder.attrValueLayout.setVisibility(View.VISIBLE);
			} else {
				holder.attrValueLayout.setVisibility(View.GONE);
			}
			holder.attrTypeTextView.setText(productFilter.getAllAttrs()
					.get(position).getName());
			holder.attrValueTextView.setText(productFilter
					.getAllAttrs()
					.get(position)
					.getAttrValues()
					.get(productFilter.getAllAttrs().get(position)
							.getSelection()).getName());
			holder.attrValueList.setAdapter(new AttrValueAdapter(productFilter
					.getAllAttrs().get(position)));
			holder.attrTypeLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					selectAttr(index);
				}
			});
			holder.attrValueList
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View view,
								int arg2, long arg3) {
							productFilter.getAllAttrs().get(attrSelection)
									.setSelection(arg2);
							selectAttr(-1);
							refresh();
							drawerLayout.closeDrawers();
						}
					});
			return convertView;
		}

		class ViewHolder {
			TextView attrTypeTextView, attrValueTextView;
			LinearLayout attrTypeLayout, attrValueLayout;
			InnerListView attrValueList;
		}
	}

	class AttrValueAdapter extends BaseAdapter {

		ModelAttrType attrType;

		public AttrValueAdapter(ModelAttrType attrType) {
			this.attrType = attrType;
		}

		@Override
		public int getCount() {
			return attrType.getAttrValues().size();
		}

		@Override
		public Object getItem(int position) {
			return attrType.getAttrValues().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = layoutInflater.inflate(
						R.layout.list_local_business_class, null);
				viewHolder.selector = convertView
						.findViewById(R.id.local_business_class_selector);
				viewHolder.nameText = (TextView) convertView
						.findViewById(R.id.local_business_class_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (position == attrType.getSelection()) {
				viewHolder.selector
						.setBackgroundResource(R.color.textColorCompany);
				viewHolder.nameText.setTextColor(getResources().getColor(
						R.color.textColorCompany));
			} else {
				viewHolder.selector.setBackgroundResource(color.transparent);
				viewHolder.nameText.setTextColor(getResources().getColor(
						R.color.textColorThird));
			}
			viewHolder.nameText.setText(attrType.getAttrValues().get(position)
					.getName());
			return convertView;
		}

		class ViewHolder {
			TextView nameText;
			View selector;
		}
	}

	class ProductAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return products.size();
		}

		@Override
		public Object getItem(int position) {
			return products.get(position);
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
			ModelProduct product = products.get(position);
			imageLoader.displayImage(getSmallImageUrl(product.getImg()),
					viewHolder.imageView);
			if (priceMap.containsKey(product.getId())) {
				viewHolder.priceTextView.setText(Parameters.CONSTANT_RMB
						+ decimalFormat.format(priceMap.get(product.getId())
								.getPrice()));
			}
			viewHolder.nameTextView.setText(product.getProductName());
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView priceTextView, nameTextView;
		}
	}

	/**
	 * 根据选择的大分类获取中小分类
	 */
	private void getMidClasses() {
		NetworkContent networkContent = new NetworkContent(
				API.API_PRODUCT_CLASS_MID);
		networkContent.addParameters("shebei", Device.getDeviceCode());
		networkContent.addParameters("superiorClassId", mainClass.getId());
		MissionController.startNetworkMission(getActivity(), networkContent,
				new OnNetworkListener() {
					@Override
					public void onStart() {
						super.onStart();
						showLoading();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						hideLoading();
					}

					@Override
					public void onSuccess(NetworkMissionMessage message) {
						super.onSuccess(message);
						if (!TextUtils.isEmpty(message.getResult())) {
							JSONObject object;
							try {
								object = new JSONObject(message.getResult());
								if (object.getString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONArray midClassArray = object
											.optJSONArray("dataList");
									if (midClassArray != null) {
										List<ModelClass> classes = new ArrayList<ModelClass>();
										if (midClassArray.length() > 0) {
											for (int i = 0; i < midClassArray
													.length(); i++) {
												classes.add(new ModelClass(
														midClassArray
																.getJSONObject(i)));
											}
											if (classes.size() > 0) {
												mainClass
														.setSubClasses(classes);
												selectMidClass(classes.get(0));
											}
										}
									}
								} else {
									loadingEmpty();
								}
							} catch (JSONException e) {
								loadingEmpty();
								e.printStackTrace();
							}
						}
					}
				});
	}

	/**
	 * 通过分类ID获取对应属性数据，用于筛选
	 */
	private void getAttributes() {
		NetworkContent networkContent = new NetworkContent(
				API.API_PRODUCT_CLASS_ATTR);
		networkContent.addParameters("minClassId", minClass.getId());
		networkContent.addParameters("shebei", Device.getDeviceCode());
		MissionController.startNetworkMission(getActivity(), networkContent,
				new OnNetworkListener() {
					@Override
					public void onStart() {
						super.onStart();
						showLoading();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						hideLoading();
					}

					@Override
					public void onSuccess(NetworkMissionMessage message) {
						super.onSuccess(message);
						if (!TextUtils.isEmpty(message.getResult())) {
							try {
								JSONObject object = new JSONObject(message
										.getResult());
								if (object.optString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONObject attrObject = object
											.optJSONObject("dataMap");
									if (attrObject != null) {
										productFilter = new ModelProductFilter(
												attrObject);
										if (productFilter.getAllAttrs().size() > 0) {
											attrAdapter.notifyDataSetChanged();
											addTextButton("筛选",
													new OnClickListener() {

														@Override
														public void onClick(
																View v) {
															drawerLayout
																	.openDrawer(attrLayout);
														}
													});
										}
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

	class AreaDialog extends DialogFragment {

		View dialogView;

		ListView areaListView;
		ImageView closeButton;

		@Override
		public void onCreate(@Nullable Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			findViews();
		}

		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Dialog dialog = new Dialog(getActivity());
			dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(dialogView, new LayoutParams(320,
					LayoutParams.WRAP_CONTENT));
			return dialog;
		}

		private void findViews() {
			dialogView = layoutInflater.inflate(R.layout.dialog_fragment_area,
					null);
			areaListView = (ListView) dialogView
					.findViewById(R.id.dialog_area_list);
			closeButton = (ImageView) dialogView
					.findViewById(R.id.dialog_area_close);
			initViews();
		}

		private void initViews() {
			areaListView.setAdapter(areaAdapter);
			areaListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					areas.setSelection(arg2);
					areaAdapter.notifyDataSetChanged();
					areaTextView.setText(areas.getAreas()
							.get(areas.getSelection()).getValuename());
					refresh();
					dismiss();
				}
			});
			closeButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}

	}

	private void getArea() {
		NetworkContent networkContent = new NetworkContent(API.API_AREA);
		networkContent.addParameters("machine", Device.getDeviceCode());
		MissionController.startNetworkMission(getActivity(), networkContent,
				new OnNetworkListener() {

					@Override
					public void onSuccess(NetworkMissionMessage requestMessage) {
						super.onSuccess(requestMessage);
						areas = new Areas(requestMessage);
						areaAdapter.notifyDataSetChanged();
					}
				});
	}

	class AreaAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return areas.getAreas().size();
		}

		@Override
		public Object getItem(int arg0) {
			return areas.getAreas().get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ViewHolder viewHolder;
			if (arg1 == null) {
				arg1 = layoutInflater.inflate(R.layout.list_area, null);
				viewHolder = new ViewHolder();
				viewHolder.checkImageView = (ImageView) arg1
						.findViewById(R.id.area_check);
				viewHolder.nameTextView = (TextView) arg1
						.findViewById(R.id.area_name);
				arg1.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) arg1.getTag();
			}
			viewHolder.nameTextView.setText(areas.getAreas().get(arg0)
					.getValuename());
			if (arg0 == areas.getSelection()) {
				viewHolder.checkImageView
						.setImageResource(R.drawable.iconfont_check_checked);
			} else {
				viewHolder.checkImageView
						.setImageResource(R.drawable.iconfont_check_normal);
			}
			return arg1;
		}

		class ViewHolder {
			TextView nameTextView;
			ImageView checkImageView;
		}

	}

	class Areas {

		List<Area> areas = new ArrayList<ProductClassesFragment.Area>();

		int selection = -1;

		public Areas() {
		}

		public Areas(NetworkMissionMessage requestMessage) {
			if (!TextUtils.isEmpty(requestMessage.getResult())) {
				try {
					JSONObject object = new JSONObject(
							requestMessage.getResult());
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						JSONArray jsonArray = object.optJSONArray("dataList");
						if (jsonArray != null) {
							for (int i = 0; i < jsonArray.length(); i++) {
								areas.add(new Area(jsonArray.optJSONObject(i)));
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		public List<Area> getAreas() {
			return areas;
		}

		public int getSelection() {
			return selection;
		}

		public void setSelection(int selection) {
			this.selection = selection;
		}

		public boolean notEmpty() {
			if (areas != null) {
				if (!areas.isEmpty()) {
					return true;
				}
			}
			return false;
		}

	}

	class Area {

		String id = "", valuename = "";

		public Area(JSONObject object) {
			if (object != null) {
				if (object.has("id")) {
					if (!object.optString("id").equalsIgnoreCase("null")) {
						id = object.optString("id");
					}
				}
				if (object.has("valuename")) {
					if (!object.optString("valuename").equalsIgnoreCase("null")) {
						valuename = object.optString("valuename");
					}
				}
			}
		}

		public String getId() {
			return id;
		}

		public String getValuename() {
			return valuename;
		}

	}

}
