package yitgogo.consumer.money.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.ContainerActivity;
import yitgogo.consumer.money.task.HavePayPasswordTask;
import yitgogo.consumer.tools.MD5;
import yitgogo.consumer.tools.ScreenUtil;
import yitgogo.consumer.view.Notify;
import yitgogo.consumer.view.PasswordView;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class PayPasswordDialog extends DialogFragment {

	LayoutInflater layoutInflater;

	View dialogView;
	LinearLayout passwordLayout;
	LinearLayout loadingLayout;
	LinearLayout titleLayout;
	TextView titleTextView;

	List<PasswordView> passwordViews = new ArrayList<PasswordView>();
	List<TextView> inputNumbers = new ArrayList<TextView>();
	int[] inputNumberIds = { R.id.password_input_0, R.id.password_input_1,
			R.id.password_input_2, R.id.password_input_3,
			R.id.password_input_4, R.id.password_input_5,
			R.id.password_input_6, R.id.password_input_7,
			R.id.password_input_8, R.id.password_input_9 };
	int[] passwordViewIds = { R.id.password_code_1, R.id.password_code_2,
			R.id.password_code_3, R.id.password_code_4, R.id.password_code_5,
			R.id.password_code_6 };
	TextView deleteButton, okButton;

	List<NameValuePair> parameters = new ArrayList<NameValuePair>();

	public String payPassword = "";
	String title = "请输入支付密码";
	boolean setPayPassword = false;

	HavePayPasswordTask havePayPasswordTask;

	public PayPasswordDialog(String title, boolean setPayPassword) {
		this.title = title;
		this.setPayPassword = setPayPassword;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(PayPasswordDialog.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(PayPasswordDialog.class.getName());
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity());
		dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutParams layoutParams = new LayoutParams(
				ScreenUtil.getScreenWidth(), ScreenUtil.getScreenWidth());
		dialog.setContentView(dialogView, layoutParams);
		if (!setPayPassword) {
			havePayPassword();
		}
		return dialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if (havePayPasswordTask != null) {
			if (havePayPasswordTask.getStatus() == Status.RUNNING) {
				havePayPasswordTask.cancel(true);
			}
		}
	}

	private void init() {
		layoutInflater = LayoutInflater.from(getActivity());
	}

	private void findViews() {
		dialogView = layoutInflater.inflate(R.layout.dialog_pay_password, null);
		titleLayout = (LinearLayout) dialogView
				.findViewById(R.id.password_title_layout);
		titleTextView = (TextView) dialogView
				.findViewById(R.id.password_title_lable);
		passwordLayout = (LinearLayout) dialogView
				.findViewById(R.id.password_code_layout);
		loadingLayout = (LinearLayout) dialogView
				.findViewById(R.id.password_loading_layout);
		deleteButton = (TextView) dialogView
				.findViewById(R.id.password_input_delete);
		okButton = (TextView) dialogView.findViewById(R.id.password_input_ok);
		for (int i = 0; i < passwordViewIds.length; i++) {
			passwordViews.add((PasswordView) dialogView
					.findViewById(passwordViewIds[i]));
		}
		for (int i = 0; i < inputNumberIds.length; i++) {
			inputNumbers.add((TextView) dialogView
					.findViewById(inputNumberIds[i]));
		}
		initViews();
	}

	private void initViews() {
		titleTextView.setText(title);
		passwordLayout.post(new Runnable() {

			@Override
			public void run() {
				int width = passwordLayout.getWidth();
				FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT, width / 6);
				passwordLayout.setLayoutParams(layoutParams);
			}
		});
		for (int i = 0; i < inputNumbers.size(); i++) {
			final int code = i;
			inputNumbers.get(i).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					inputPassword(code);
				}
			});
		}
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deletePassword();
			}
		});
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = getPassword();
				if (!TextUtils.isEmpty(password)) {
					payPassword = MD5.GetMD5Code(password);
					dismiss();
				}
			}
		});
	}

	private void inputPassword(int code) {
		for (int i = 0; i < passwordViews.size(); i++) {
			if (!passwordViews.get(i).haveCode()) {
				passwordViews.get(i).setCode(code);
				return;
			}
		}
	}

	private void deletePassword() {
		for (int i = passwordViews.size() - 1; i > -1; i--) {
			if (passwordViews.get(i).haveCode()) {
				passwordViews.get(i).deleteCode();
				return;
			}
		}
	}

	private String getPassword() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < passwordViews.size(); i++) {
			if (!passwordViews.get(i).haveCode()) {
				return "";
			}
			builder.append(passwordViews.get(i).getCode());
		}
		return builder.toString().trim();
	}

	private void showLoading() {
		loadingLayout.setVisibility(View.VISIBLE);
	}

	private void hideLoading() {
		loadingLayout.setVisibility(View.GONE);
	}

	private void havePayPassword() {
		if (havePayPasswordTask != null) {
			if (havePayPasswordTask.getStatus() == Status.RUNNING) {
				return;
			}
		}
		havePayPasswordTask = new HavePayPasswordTask() {

			@Override
			protected void onPreExecute() {
				showLoading();
			}

			@Override
			protected void onPostExecute(String result) {
				hideLoading();
				if (!TextUtils.isEmpty(result)) {
					try {
						JSONObject object = new JSONObject(result);
						if (object.optString("state").equalsIgnoreCase(
								"success")) {
							JSONObject jsonObject = object
									.optJSONObject("databody");
							if (jsonObject != null) {
								if (jsonObject.optBoolean("pwd")) {
									// 已设置支付密码
								} else {
									// 未设置支付密码
									Notify.show("请先设置支付密码");
									jump(PayPasswordSetFragment.class.getName(),
											"设置支付密码");
									dismiss();
								}
								return;
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				Notify.show("访问服务器失败，请稍候再试！");
				dismiss();
			}
		};
		havePayPasswordTask.execute();
	}

	protected void jump(String fragmentName, String fragmentTitle) {
		Intent intent = new Intent(getActivity(), ContainerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("fragmentName", fragmentName);
		bundle.putString("fragmentTitle", fragmentTitle);
		intent.putExtras(bundle);
		startActivity(intent);
	}

}
