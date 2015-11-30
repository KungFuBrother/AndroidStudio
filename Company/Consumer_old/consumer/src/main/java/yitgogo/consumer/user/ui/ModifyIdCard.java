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
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class ModifyIdCard extends BaseNotifyFragment {

	LinearLayout editor;
	Button modify;
	TextView accountText, idOld, idNew;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_user_idcard);
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(ModifyIdCard.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(ModifyIdCard.class.getName());
	}

	protected void findViews() {
		accountText = (TextView) contentView
				.findViewById(R.id.user_info_idcard_account);
		idOld = (TextView) contentView.findViewById(R.id.user_info_idcard_old);
		idNew = (TextView) contentView.findViewById(R.id.user_info_idcard_new);
		modify = (Button) contentView.findViewById(R.id.user_idcard_modify);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		accountText.setText(User.getUser().getUseraccount());
		idOld.setText(User.getUser().getIdcard());
	}

	@Override
	protected void registerViews() {
		modify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				modify();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void modify() {
		String idcard = idNew.getText().toString().trim();
		if (TextUtils.isEmpty(idcard)) {
			Toast.makeText(getActivity(), "请输入要绑定的身份证号码", Toast.LENGTH_SHORT)
					.show();
		} else if (idcard.length() != 15 & idcard.length() != 18) {
			Toast.makeText(getActivity(), "身份证号码格式不正确", Toast.LENGTH_SHORT)
					.show();
		} else {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("account", User.getUser()
					.getUseraccount()));
			nameValuePairs.add(new BasicNameValuePair("idcard", idcard));
			new Modify().execute(nameValuePairs);
		}
	}

	class Modify extends AsyncTask<List<NameValuePair>, Void, String> {
		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(List<NameValuePair>... arg0) {
			return netUtil.postWithCookie(API.API_USER_MODIFY_IDCARD, arg0[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			JSONObject data;
			try {
				data = new JSONObject(result);
				if (data.getString("state").equalsIgnoreCase("SUCCESS")) {
					Toast.makeText(getActivity(), "绑定身份证成功", Toast.LENGTH_SHORT)
							.show();
					getActivity().finish();
				} else {
					Toast.makeText(getActivity(), data.getString("message"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}
}
