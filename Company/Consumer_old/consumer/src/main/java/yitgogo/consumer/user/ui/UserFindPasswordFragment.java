package yitgogo.consumer.user.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.view.Notify;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class UserFindPasswordFragment extends BaseNotifyFragment implements
		OnClickListener {

	EditText phoneEdit, smscodeEdit, passwordEdit, passwordConfirmEdit;
	TextView getSmscodeButton;
	ImageView showPassword;
	Button registerButton;
	boolean isShown = false;
	boolean isFinish = false;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.obj != null) {
				getSmscodeButton.setText(msg.obj + "s");
			} else {
				getSmscodeButton.setEnabled(true);
				getSmscodeButton.setTextColor(getResources().getColor(
						R.color.textColorSecond));
				getSmscodeButton.setText("获取验证码");
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_user_find_password);
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UserFindPasswordFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UserFindPasswordFragment.class.getName());
	}

	@Override
	public void onDestroy() {
		isFinish = true;
		super.onDestroy();
	}

	@Override
	protected void findViews() {
		phoneEdit = (EditText) contentView
				.findViewById(R.id.user_find_password_phone);
		smscodeEdit = (EditText) contentView
				.findViewById(R.id.user_find_password_smscode);
		passwordEdit = (EditText) contentView
				.findViewById(R.id.user_find_password_password);
		passwordConfirmEdit = (EditText) contentView
				.findViewById(R.id.user_find_password_password_confirm);
		getSmscodeButton = (TextView) contentView
				.findViewById(R.id.user_find_password_smscode_get);
		registerButton = (Button) contentView
				.findViewById(R.id.user_find_password_enter);
		showPassword = (ImageView) contentView
				.findViewById(R.id.user_find_password_password_show);
		registerViews();
	}

	@Override
	protected void registerViews() {
		getSmscodeButton.setOnClickListener(this);
		registerButton.setOnClickListener(this);
		showPassword.setOnClickListener(this);
	}

	private void findPassword() {
		if (!isPhoneNumber(phoneEdit.getText().toString())) {
			Notify.show("请输入正确的手机号");
		} else if (smscodeEdit.length() != 6) {
			Notify.show("请输入您收到的验证码");
		} else if (passwordEdit.length() == 0) {
			Notify.show("请输入新密码");
		} else if (passwordConfirmEdit.length() == 0) {
			Notify.show("请确认新密码");
		} else if (!passwordEdit.getText().toString()
				.equals(passwordConfirmEdit.getText().toString())) {
			Notify.show("两次输入的密码不相同 ");
		} else {
			new ResetPassword().execute();
		}
	}

	private void getSmscode() {
		if (isPhoneNumber(phoneEdit.getText().toString())) {
			new GetSmscode().execute();
		} else {
			Notify.show("请输入正确的手机号");
		}
	}

	private void showPassword() {
		if (isShown) {
			passwordEdit.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
			passwordConfirmEdit
					.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
		} else {
			passwordEdit
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
			passwordConfirmEdit
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

	class ResetPassword extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("phone", phoneEdit
					.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("smsCode", smscodeEdit
					.getText().toString()));
			nameValuePairs
					.add(new BasicNameValuePair("password",
							getEncodedPassWord(passwordEdit.getText()
									.toString().trim())));
			return netUtil.postWithCookie(API.API_USER_FIND_PASSWORD,
					nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						Notify.show("重设密码成功，请使用新密码登录");
						getActivity().finish();
					} else {
						Notify.show(object.optString("message"));
					}
				} catch (JSONException e) {
					Notify.show("重设密码失败");
					e.printStackTrace();
				}
			} else {
				Notify.show("重设密码失败");
			}
		}
	}

	class GetSmscode extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading("正在获取验证码...");
			getSmscodeButton.setEnabled(false);
			getSmscodeButton.setTextColor(getResources().getColor(
					R.color.textColorThird));
		}

		@Override
		protected String doInBackground(Void... arg0) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("phone", phoneEdit
					.getText().toString()));
			return netUtil.postAndSaveCookie(
					API.API_USER_FIND_PASSWORD_SMSCODE, nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result) {
			// {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],"totalCount":1,"dataMap":{},"object":null}
			hideLoading();
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						Notify.show("验证码已发送至您的手机");
						new Thread(new Runnable() {

							@Override
							public void run() {
								int time = 60;
								while (time > -1) {
									if (isFinish) {
										break;
									}
									try {
										Message message = new Message();
										if (time > 0) {
											message.obj = time;
										}
										handler.sendMessage(message);
										Thread.sleep(1000);
										time--;
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
						}).start();
					} else {
						getSmscodeButton.setEnabled(true);
						getSmscodeButton.setTextColor(getResources().getColor(
								R.color.textColorSecond));
						getSmscodeButton.setText("获取验证码");
						Notify.show(object.optString("message"));
					}
				} catch (JSONException e) {
					getSmscodeButton.setEnabled(true);
					getSmscodeButton.setTextColor(getResources().getColor(
							R.color.textColorSecond));
					getSmscodeButton.setText("获取验证码");
					Notify.show("获取验证码失败");
					e.printStackTrace();
				}
			} else {
				getSmscodeButton.setEnabled(true);
				getSmscodeButton.setTextColor(getResources().getColor(
						R.color.textColorSecond));
				getSmscodeButton.setText("获取验证码");
				Notify.show("获取验证码失败");
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_find_password_smscode_get:
			getSmscode();
			break;

		case R.id.user_find_password_enter:
			findPassword();
			break;

		case R.id.user_find_password_password_show:
			showPassword();
			break;

		default:
			break;
		}
	}

}
