package yitgogo.consumer.user.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.user.model.User;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class UserScoreFragment extends BaseNotifyFragment {

	TextView scoreTotalTextView, signButton;
	LinearLayout detailButton, shareButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_user_score);
		findViews();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UserScoreFragment.class.getName());
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UserScoreFragment.class.getName());
		if (User.getUser().isLogin()) {
			new GetUserScore().execute();
			new GetSignState().execute();
		}
	}

	@Override
	protected void findViews() {
		scoreTotalTextView = (TextView) contentView
				.findViewById(R.id.score_total);
		signButton = (TextView) contentView.findViewById(R.id.score_sign);
		detailButton = (LinearLayout) contentView
				.findViewById(R.id.score_detail);
		shareButton = (LinearLayout) contentView.findViewById(R.id.score_share);
		registerViews();
	}

	@Override
	protected void registerViews() {
		signButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new SignUp().execute();
			}
		});
		detailButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(UserScoreDetailFragment.class.getName(), "积分详情");
			}
		});
		shareButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(UserShareFragment.class.getName(), "推荐好友");
			}
		});
	}

	/**
	 * 获取当前积分
	 */
	class GetUserScore extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
			valuePairs.add(new BasicNameValuePair("memberAccount", User
					.getUser().getUseraccount()));
			return netUtil.postWithCookie(API.API_USER_JIFEN, valuePairs);
		}

		@Override
		protected void onPostExecute(String result) {
			// {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],"totalCount":1,"dataMap":{},"object":{"id":116,"totalBonus":0,"memberAccount":"13032889558"}}
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
						JSONObject jifenObject = object.optJSONObject("object");
						if (jifenObject != null) {
							String score = jifenObject.optString("totalBonus");
							if (!score.equalsIgnoreCase("null")) {
								scoreTotalTextView.setText(score);
							} else {
								scoreTotalTextView.setText("0");
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * 判断是否已经签到
	 */
	class GetSignState extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
			valuePairs.add(new BasicNameValuePair("userAccount", User.getUser()
					.getUseraccount()));
			return netUtil.postWithCookie(API.API_USER_SIGN_STATE, valuePairs);
		}

		@Override
		protected void onPostExecute(String result) {
			// {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],"totalCount":1,"dataMap":{},"object":{"signId":15,"useraccount":"13668192000","signTime":null,"signCount":0,"signAllAccount":0,"isSign":0}}
			hideLoading();
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
						JSONObject jsonObject = object.getJSONObject("object");
						String isSign = jsonObject.getString("isSign");
						if (!isSign.equals("0")) {
							signButton.setText("今日已签到");
							signButton.setTextColor(getResources().getColor(
									R.color.textColorThird));
							signButton.setClickable(false);
						} else {
							signButton.setText("签到领积分");
							signButton.setTextColor(getResources().getColor(
									R.color.textColorSecond));
							signButton.setClickable(true);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 签到
	 */
	class SignUp extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
			signButton.setText("今日已签到");
			signButton.setTextColor(getResources().getColor(
					R.color.textColorThird));
			signButton.setClickable(false);
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
			valuePairs.add(new BasicNameValuePair("userAccount", User.getUser()
					.getUseraccount()));
			return netUtil.postWithCookie(API.API_USER_SIGN, valuePairs);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
						new GetSignState().execute();
						new GetUserScore().execute();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}
	}

}
