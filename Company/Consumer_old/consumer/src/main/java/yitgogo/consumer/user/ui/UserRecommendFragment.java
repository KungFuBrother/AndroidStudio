package yitgogo.consumer.user.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.user.model.ModelRecommend;
import yitgogo.consumer.user.model.User;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class UserRecommendFragment extends BaseNotifyFragment {

	ListView recommendListView;
	TextView countTextView, moneyTextView;
	List<ModelRecommend> recommends;
	RecommendAdapter recommendAdapter;
	Statistics statistics = new Statistics();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_user_recommend);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UserRecommendFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UserRecommendFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		new GetStatistics().execute();
		new GetRecommendList().execute();
	}

	private void init() {
		recommends = new ArrayList<ModelRecommend>();
		recommendAdapter = new RecommendAdapter();
	}

	@Override
	protected void findViews() {
		recommendListView = (ListView) contentView
				.findViewById(R.id.recommend_list);
		countTextView = (TextView) contentView
				.findViewById(R.id.recommend_count);
		moneyTextView = (TextView) contentView
				.findViewById(R.id.recommend_money);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		recommendListView.setAdapter(recommendAdapter);
	}

	@Override
	protected void registerViews() {
	}

	private void showStatistics() {
		countTextView.setText(statistics.getNum() + "");
		moneyTextView.setText(statistics.getBonus() + "");
	}

	class RecommendAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return recommends.size();
		}

		@Override
		public Object getItem(int position) {
			return recommends.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.list_recommend,
						null);
				holder = new ViewHolder();
				holder.accountTextView = (TextView) convertView
						.findViewById(R.id.list_recommend_account);
				holder.scoreTextView = (TextView) convertView
						.findViewById(R.id.list_recommend_score);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ModelRecommend recommend = recommends.get(position);
			holder.accountTextView.setText(recommend.getMemberAccount());
			holder.scoreTextView.setText(recommend.getTotalBonus());
			return convertView;
		}

		class ViewHolder {
			TextView accountTextView, scoreTextView;
		}
	}

	/**
	 * 获取我推荐的会员总人数和会员消费总金额
	 * 
	 * @author Tiger
	 * 
	 * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],
	 *         "totalCount":1,"dataMap":{"num":0,"bonus":0},"object":null}
	 */
	class GetStatistics extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("memberAccount", User
					.getUser().getUseraccount()));
			return netUtil.postWithCookie(API.API_USER_RECOMMEND_STATISTICS,
					nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result) {
			statistics = new Statistics(result);
			showStatistics();
		}
	}

	/**
	 * @author Tiger
	 * 
	 * @Result 
	 *         {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[{"id"
	 *         :null, "totalBonus"
	 *         :0,"memberAccount":"15882972602"}],"totalCount":1,"dataMap":{}
	 *         ,"object":null}
	 * 
	 */
	class GetRecommendList extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			recommends.clear();
			recommendAdapter.notifyDataSetChanged();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("memberAccount", User
					.getUser().getUseraccount()));
			return netUtil.postWithCookie(API.API_USER_RECOMMEND_LIST,
					nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.length() > 0) {
				try {
					JSONObject jsonObject = new JSONObject(result);
					if (jsonObject.getString("state").equalsIgnoreCase(
							"SUCCESS")) {
						JSONArray array = jsonObject.optJSONArray("dataList");
						if (array != null) {
							for (int i = 0; i < array.length(); i++) {
								recommends.add(new ModelRecommend(array
										.optJSONObject(i)));
							}
							recommendAdapter.notifyDataSetChanged();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}
	}

	class Statistics {
		long num = 0;
		long bonus = 0;

		public Statistics() {
		}

		public Statistics(String result) {
			if (result.length() > 0) {
				try {
					JSONObject object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						JSONObject jsonObject = object.getJSONObject("dataMap");
						if (jsonObject != null) {
							num = jsonObject.optLong("num");
							bonus = jsonObject.optLong("bonus");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}

		public long getNum() {
			return num;
		}

		public long getBonus() {
			return bonus;
		}

		@Override
		public String toString() {
			return "Statistics [num=" + num + ", bonus=" + bonus + "]";
		}

	}
}
