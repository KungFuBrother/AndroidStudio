package yitgogo.consumer.user.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.view.Notify;
import android.content.Intent;
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
import android.widget.Toast;

import com.dtr.zxing.activity.CaptureActivity;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class UserRegisterFragment extends BaseNotifyFragment implements
		OnClickListener {

	EditText phoneEdit, smscodeEdit, passwordEdit, passwordConfirmEdit,
			inviteCodeEditText;
	TextView getSmscodeButton;
	ImageView showPassword, scanButton;
	Button registerButton;
	boolean isShown = false, isFinish = false;

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
		setContentView(R.layout.fragment_user_register);
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UserRegisterFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(UserRegisterFragment.class.getName());
	}

	@Override
	public void onDestroy() {
		isFinish = true;
		super.onDestroy();
	}

	@Override
	protected void findViews() {
		phoneEdit = (EditText) contentView
				.findViewById(R.id.user_register_phone);
		smscodeEdit = (EditText) contentView
				.findViewById(R.id.user_register_smscode);
		passwordEdit = (EditText) contentView
				.findViewById(R.id.user_register_password);
		passwordConfirmEdit = (EditText) contentView
				.findViewById(R.id.user_register_password_confirm);
		inviteCodeEditText = (EditText) contentView
				.findViewById(R.id.user_register_invitecode);
		scanButton = (ImageView) contentView
				.findViewById(R.id.user_register_invitecode_scan);
		getSmscodeButton = (TextView) contentView
				.findViewById(R.id.user_register_smscode_get);
		registerButton = (Button) contentView
				.findViewById(R.id.user_register_enter);
		showPassword = (ImageView) contentView
				.findViewById(R.id.user_register_password_show);
		registerViews();
	}

	@Override
	protected void registerViews() {
		getSmscodeButton.setOnClickListener(this);
		registerButton.setOnClickListener(this);
		showPassword.setOnClickListener(this);
		onBackButtonClick(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(UserLoginFragment.class.getName(), "会员登录");
				getActivity().finish();
			}
		});
		scanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CaptureActivity.class);
				startActivityForResult(intent, 5);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 5) {
			if (data != null) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					if (bundle.containsKey("userCode")) {
						inviteCodeEditText
								.setText(bundle.getString("userCode"));
					}
				}
			}
		}
	}

	private void register() {
		if (!isPhoneNumber(phoneEdit.getText().toString())) {
			Notify.show("请输入正确的手机号");
		} else if (smscodeEdit.length() != 6) {
			Notify.show("请输入您收到的验证码");
		} else if (passwordEdit.length() == 0) {
			Notify.show("请输入密码");
		} else if (passwordConfirmEdit.length() == 0) {
			Notify.show("请确认密码");
		} else if (!passwordEdit.getText().toString()
				.equals(passwordConfirmEdit.getText().toString())) {
			Notify.show("两次输入的密码不相同 ");
		} else {
			new Register().execute();
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

	class Register extends AsyncTask<Void, Void, String> {

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
			nameValuePairs.add(new BasicNameValuePair("refereeCode",
					inviteCodeEditText.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("spNo", Store.getStore()
					.getStoreNumber()));
			return netUtil
					.postWithCookie(API.API_USER_REGISTER, nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						Toast.makeText(getActivity(), "注册成功",
								Toast.LENGTH_SHORT).show();
						Bundle bundle = new Bundle();
						bundle.putString("phone", phoneEdit.getText()
								.toString());
						jump(UserLoginFragment.class.getName(), "会员登录", bundle);
						getActivity().finish();
					} else {
						Notify.show(object.optString("message"));
					}
				} catch (JSONException e) {
					Notify.show("注册失败");
					e.printStackTrace();
				}
			} else {
				Notify.show("注册失败");
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
			return netUtil.postAndSaveCookie(API.API_USER_REGISTER_SMSCODE,
					nameValuePairs);
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
		case R.id.user_register_smscode_get:
			getSmscode();
			break;

		case R.id.user_register_enter:
			register();
			break;

		case R.id.user_register_password_show:
			showPassword();
			break;

		default:
			break;
		}
	}

}
