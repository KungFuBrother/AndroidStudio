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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class UserLoginFragment extends BaseNotifyFragment implements
		OnClickListener {

	EditText nameEdit, passwordEdit;
	Button loginButton;
	TextView registerButton, passwordButton;
	ImageView showPassword;
	boolean isShown = false;
	String phone = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_user_login);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UserLoginFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UserLoginFragment.class.getName());
	}

	private void init() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("phone")) {
				phone = bundle.getString("phone");
			}
		}
	}

	@Override
	protected void findViews() {
		nameEdit = (EditText) contentView.findViewById(R.id.user_login_name);
		passwordEdit = (EditText) contentView
				.findViewById(R.id.user_login_password);
		loginButton = (Button) contentView.findViewById(R.id.user_login_login);
		registerButton = (TextView) contentView
				.findViewById(R.id.user_login_register);
		passwordButton = (TextView) contentView
				.findViewById(R.id.user_login_findpassword);
		showPassword = (ImageView) contentView
				.findViewById(R.id.user_login_password_show);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		nameEdit.setText(phone);
	}

	@Override
	protected void registerViews() {
		loginButton.setOnClickListener(this);
		registerButton.setOnClickListener(this);
		passwordButton.setOnClickListener(this);
		showPassword.setOnClickListener(this);
	}

	private void login() {
		if (!isPhoneNumber(nameEdit.getText().toString())) {
			Notify.show("请输入正确的手机号");
		} else if (passwordEdit.length() == 0) {
			Notify.show("请输入密码");
		} else {
			new Login().execute();
		}
	}

	private void showPassword() {
		if (isShown) {
			passwordEdit.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		} else {
			passwordEdit
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
		}
		isShown = !isShown;
		if (isShown) {
			showPassword.setImageResource(R.drawable.ic_hide);
		} else {
			showPassword.setImageResource(R.drawable.ic_show);
		}
	}

	class Login extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading("正在登录...");
		}

		@Override
		protected String doInBackground(Void... arg0) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("phone", nameEdit
					.getText().toString()));
			nameValuePairs
					.add(new BasicNameValuePair("password",
							getEncodedPassWord(passwordEdit.getText()
									.toString().trim())));
			return netUtil
					.postAndSaveCookie(API.API_USER_LOGIN, nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result) {
			// {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],"totalCount":1,"dataMap":{},"object":{"id":470,"useraccount":"13032889558","realname":"Tiger","phone":"13032889558","area":null,"address":"凤凰大厦","uImg":null,"addtime":"2014-11-10 16:43:09","email":"1076192306@qq.com","sex":"男","age":"21","birthday":755971200000,"idcard":"513030199311056012","spid":"0","memtype":"手机"}}
			// {"message":"登陆失败,账号或密码错误！","state":"ERROR"}
			hideLoading();
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						Notify.show("登录成功");
						Content.saveStringContent(
								Parameters.CACHE_KEY_MONEY_SN,
								object.optString("cacheKey"));
						JSONObject userObject = object.optJSONObject("object");
						if (userObject != null) {
							Content.saveStringContent(
									Parameters.CACHE_KEY_USER_JSON,
									userObject.toString());
							Content.saveStringContent(
									Parameters.CACHE_KEY_USER_PASSWORD,
									getEncodedPassWord(passwordEdit.getText()
											.toString().trim()));
							User.init(getActivity());
						}
						getActivity().finish();
					} else {
						Notify.show(object.optString("message"));
					}
				} catch (JSONException e) {
					Notify.show("登录失败");
					e.printStackTrace();
				}
			} else {
				Notify.show("登录失败");
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_login_login:
			login();
			break;

		case R.id.user_login_register:
			jump(UserRegisterFragment.class.getName(), "注册");
			getActivity().finish();
			break;

		case R.id.user_login_password_show:
			showPassword();
			break;

		case R.id.user_login_findpassword:
			jump(UserFindPasswordFragment.class.getName(), "重设密码");
			break;

		default:
			break;
		}
	}

}
