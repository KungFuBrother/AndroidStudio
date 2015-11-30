package yitgogo.consumer.product.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.local.ui.LocalGoodsSearchFragment;
import yitgogo.consumer.local.ui.LocalServiceSearchFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ScreenUtil;
import yitgogo.consumer.view.Notify;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class ProductSearchFragment extends BaseNotifyFragment {

	public static final int SEARCH_TYPE_PLATFORM = 1;
	public static final int SEARCH_TYPE_LOCAL_PRODUCT = 2;
	public static final int SEARCH_TYPE_LOCAL_SERVICE = 3;

	ImageView backButton, searchButton;
	EditText wordsEdit;
	TextView searchTypeTextView;

	List<SearchType> searchTypes;
	SearchTypeAdapter searchTypeAdapter;
	SearchType searchType;

	GridView hotSearchGridView;
	List<String> searchWords;

	HotSearchAdapter hotSearchAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_product_search);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(ProductSearchFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(ProductSearchFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		new GetHotSearch().execute();
		showDisconnectMargin();
	}

	private void init() {
		measureScreen();
		searchWords = new ArrayList<String>();
		hotSearchAdapter = new HotSearchAdapter();

		searchTypes = new ArrayList<ProductSearchFragment.SearchType>();
		searchTypes.add(new SearchType(SEARCH_TYPE_PLATFORM, "易商城"));
		searchTypes.add(new SearchType(SEARCH_TYPE_LOCAL_PRODUCT, "本地商品"));
		searchTypes.add(new SearchType(SEARCH_TYPE_LOCAL_SERVICE, "本地服务"));
		searchTypeAdapter = new SearchTypeAdapter();
	}

	@Override
	protected void findViews() {
		backButton = (ImageView) contentView.findViewById(R.id.search_back);
		searchTypeTextView = (TextView) contentView
				.findViewById(R.id.search_type);
		searchButton = (ImageView) contentView.findViewById(R.id.search_search);
		wordsEdit = (EditText) contentView.findViewById(R.id.search_edit);
		hotSearchGridView = (GridView) contentView
				.findViewById(R.id.search_hot);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		selectSearchType(0);
		hotSearchGridView.setAdapter(hotSearchAdapter);
	}

	@Override
	protected void registerViews() {
		hotSearchGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				search(searchWords.get(arg2));
			}
		});
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search(wordsEdit.getText().toString());
			}
		});
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		searchTypeTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				new SearchTypeDialog().show(getFragmentManager(), null);
			}
		});
	}

	class GetHotSearch extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			searchWords.clear();
			hotSearchAdapter.notifyDataSetChanged();
		}

		@Override
		protected String doInBackground(Void... params) {
			return netUtil.postWithoutCookie(API.API_PRODUCT_SEARCH_HOT, null,
					true, true);
		}

		@Override
		protected void onPostExecute(String result) {
			// {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[{"searchName":"测试"},{"searchName":"空调"},{"searchName":"冰箱"},{"searchName":"海尔"},{"searchName":"1"},{"searchName":"海"}],"totalCount":1,"dataMap":{},"object":null}
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					JSONArray array = object.optJSONArray("dataList");
					if (array != null) {
						for (int i = 0; i < array.length(); i++) {
							JSONObject object2 = array.optJSONObject(i);
							if (object2 != null) {
								searchWords
										.add(object2.optString("searchName"));
							}
						}
						hotSearchAdapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void search(String words) {
		if (words.length() > 0) {
			switch (searchType.getType()) {
			case SEARCH_TYPE_PLATFORM:
				jumpProductList("搜索\"" + words + "\"", words,
						ProductListFragment.TYPE_NAME);
				break;
			case SEARCH_TYPE_LOCAL_PRODUCT:
				Bundle bundle1 = new Bundle();
				bundle1.putString("productName", words);
				jump(LocalGoodsSearchFragment.class.getName(), "搜索\"" + words
						+ "\"", bundle1);
				break;
			case SEARCH_TYPE_LOCAL_SERVICE:
				Bundle bundle2 = new Bundle();
				bundle2.putString("productName", words);
				jump(LocalServiceSearchFragment.class.getName(), "搜索\"" + words
						+ "\"", bundle2);
				break;

			default:
				jumpProductList("搜索\"" + words + "\"", words,
						ProductListFragment.TYPE_NAME);
				break;
			}
			getActivity().finish();
		} else {
			Notify.show("请输入关键字");
		}
	}

	class HotSearchAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return searchWords.size();
		}

		@Override
		public Object getItem(int position) {
			return searchWords.get(position);
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
				convertView = layoutInflater.inflate(R.layout.list_class_min,
						null);
				holder.nameText = (TextView) convertView
						.findViewById(R.id.class_min_name);
				holder.nameText.setLayoutParams(new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT, ScreenUtil
								.dip2px(36)));
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.nameText.setText(searchWords.get(position));
			return convertView;
		}

		class ViewHolder {
			TextView nameText;
		}
	}

	class SearchTypeDialog extends DialogFragment {

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
			titleTextView.setText("选择搜索类型");
			button.setText("取消");
			listView.setAdapter(searchTypeAdapter);
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
					selectSearchType(arg2);
					dismiss();
				}
			});
		}
	}

	private void selectSearchType(int position) {
		if (searchTypes.size() > position) {
			searchType = searchTypes.get(position);
			searchTypeTextView.setText(searchType.getName());
		}
	}

	class SearchTypeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return searchTypes.size();
		}

		@Override
		public Object getItem(int position) {
			return searchTypes.get(position);
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
			holder.textView.setText(searchTypes.get(position).getName());
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}

	class SearchType {

		int type = 0;
		String name = "";

		public SearchType(int type, String name) {
			this.name = name;
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public String getName() {
			return name;
		}

	}

}
