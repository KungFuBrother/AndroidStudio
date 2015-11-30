package yitgogo.consumer.user.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.Notify;
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

public class ModifySecret extends BaseNotifyFragment {

	LinearLayout editor;
	Button modify;
	TextView accountText, secretOld, secretNew, secretVerify;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_user_secret);
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(ModifySecret.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(ModifySecret.class.getName());
	}

	protected void findViews() {
		accountText = (TextView) contentView
				.findViewById(R.id.user_info_secret_account);
		secretOld = (TextView) contentView
				.findViewById(R.id.user_info_secret_old);
		secretNew = (TextView) contentView
				.findViewById(R.id.user_info_secret_new);
		secretVerify = (TextView) contentView
				.findViewById(R.id.user_info_secret_verify);
		modify = (Button) contentView.findViewById(R.id.user_secret_modify);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		accountText.setText(User.getUser().getUseraccount());
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
		String oldpassword = secretOld.getText().toString().trim();
		String newpassword = secretNew.getText().toString().trim();
		String renewpassword = secretVerify.getText().toString().trim();
		if (TextUtils.isEmpty(oldpassword)) {
			Notify.show("请输入旧密码");
		} else if (TextUtils.isEmpty(newpassword)) {
			Notify.show("请输入新密码");
		} else if (TextUtils.isEmpty(renewpassword)) {
			Notify.show("请确认新密码");
		} else if (newpassword.equalsIgnoreCase(oldpassword)) {
			Notify.show("新密码与旧密码相同");
		} else if (!newpassword.equalsIgnoreCase(renewpassword)) {
			Notify.show("两次输入的新密码不相同");
		} else {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("useraccount", User
					.getUser().getUseraccount()));
			nameValuePairs.add(new BasicNameValuePair("oldpassword",
					getEncodedPassWord(oldpassword)));
			nameValuePairs.add(new BasicNameValuePair("newpassword",
					getEncodedPassWord(newpassword)));
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
			return netUtil.postWithCookie(API.API_USER_MODIFY_SECRET, arg0[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			JSONObject data;
			try {
				data = new JSONObject(result);
				if (data.getString("state").equalsIgnoreCase("SUCCESS")) {
					Content.removeContent(Parameters.CACHE_KEY_USER_JSON);
					Content.removeContent(Parameters.CACHE_KEY_USER_PASSWORD);
					Content.removeContent(Parameters.CACHE_KEY_COOKIE);
					User.init(getActivity());
					Notify.show("修改成功,请重新登录");
					jump(UserLoginFragment.class.getName(), "会员登录");
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
