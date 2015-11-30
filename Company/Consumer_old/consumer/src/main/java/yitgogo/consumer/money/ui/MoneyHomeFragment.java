package yitgogo.consumer.money.ui;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.activity.ActivityFragment;
import yitgogo.consumer.bianmin.game.ui.GameFilterFragment;
import yitgogo.consumer.bianmin.phoneCharge.ui.PhoneChargeFragment;
import yitgogo.consumer.bianmin.qq.ui.QQChargeFragment;
import yitgogo.consumer.bianmin.telephone.ui.TelePhoneChargeFragment;
import yitgogo.consumer.bianmin.traffic.ui.TraffictSearchFragment;
import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.money.task.GetBankCards;
import yitgogo.consumer.money.task.HavePayPasswordTask;
import yitgogo.consumer.money.task.LoginMoneyTask;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.view.Notify;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class MoneyHomeFragment extends BaseNotifyFragment {

	FrameLayout cashLayout, bankCardLayout, takeOutButton, bianminPhoneButton,
			bianminKuandaiButton, bianminQQButton, bianminGameButton,
			bianminTrafficButton, changePayPassword, findPayPassword,
			shakeButton;
	LinearLayout accountLayout, bianminLayout;
	TextView cashTextView, bankCardTextView, passwordTextView;

	LoginMoneyTask loginMoneyTask;
	GetBankCards getBankCards;
	HavePayPasswordTask havePayPasswordTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_money_home);
		init();
		findViews();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(MoneyHomeFragment.class.getName());
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(MoneyHomeFragment.class.getName());
		loginMoney();
	}

	@Override
	public void onDestroy() {
		stopAsyncTasks();
		super.onDestroy();
	}

	private void init() {
		measureScreen();
	}

	@Override
	protected void findViews() {
		cashLayout = (FrameLayout) contentView
				.findViewById(R.id.money_account_cash_layout);
		accountLayout = (LinearLayout) contentView
				.findViewById(R.id.money_account_layout);
		cashTextView = (TextView) contentView
				.findViewById(R.id.money_account_cash);
		bankCardLayout = (FrameLayout) contentView
				.findViewById(R.id.money_account_bankcard_layout);
		bankCardTextView = (TextView) contentView
				.findViewById(R.id.money_account_bankcard);
		takeOutButton = (FrameLayout) contentView
				.findViewById(R.id.money_home_take_out);
		bianminPhoneButton = (FrameLayout) contentView
				.findViewById(R.id.money_bianmin_phone);
		bianminKuandaiButton = (FrameLayout) contentView
				.findViewById(R.id.money_bianmin_kuandai);
		bianminQQButton = (FrameLayout) contentView
				.findViewById(R.id.money_bianmin_qq);
		bianminGameButton = (FrameLayout) contentView
				.findViewById(R.id.money_bianmin_game);
		bianminTrafficButton = (FrameLayout) contentView
				.findViewById(R.id.money_bianmin_traffic);
		changePayPassword = (FrameLayout) contentView
				.findViewById(R.id.money_pay_password_change);
		passwordTextView = (TextView) contentView
				.findViewById(R.id.money_pay_password_change_lable);
		findPayPassword = (FrameLayout) contentView
				.findViewById(R.id.money_pay_password_find);
		bianminLayout = (LinearLayout) contentView
				.findViewById(R.id.money_bianmin_layout);
		shakeButton = (FrameLayout) contentView.findViewById(R.id.money_shake);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				screenWidth);
		bianminLayout.setLayoutParams(layoutParams);
		LayoutParams accountLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, screenWidth / 2);
		accountLayout.setLayoutParams(accountLayoutParams);
	}

	@Override
	protected void registerViews() {
		cashLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(TradeHistoryFragment.class.getName(), "余额交易明细");
			}
		});
		bankCardLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(BankCardFragment.class.getName(), "我的银行卡");
			}
		});
		takeOutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(TakeOutFragment.class.getName(), "提现");
			}
		});
		bianminPhoneButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(PhoneChargeFragment.class.getName(), "手机充值");
			}
		});
		bianminKuandaiButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(TelePhoneChargeFragment.class.getName(), "固话宽带充值");
			}
		});
		bianminQQButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(QQChargeFragment.class.getName(), "QQ充值");
			}
		});
		bianminGameButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(GameFilterFragment.class.getName(), "游戏充值");
			}
		});
		bianminTrafficButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(TraffictSearchFragment.class.getName(), "违章查询");
			}
		});
		shakeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(ActivityFragment.class.getName(), "摇一摇");
			}
		});
	}

	private void stopAsyncTasks() {
		if (loginMoneyTask != null) {
			if (loginMoneyTask.getStatus() == Status.RUNNING) {
				loginMoneyTask.cancel(true);
			}
		}
		if (getBankCards != null) {
			if (getBankCards.getStatus() == Status.RUNNING) {
				getBankCards.cancel(true);
			}
		}
		if (havePayPasswordTask != null) {
			if (havePayPasswordTask.getStatus() == Status.RUNNING) {
				havePayPasswordTask.cancel(true);
			}
		}
	}

	private void loginMoney() {
		if (loginMoneyTask != null) {
			if (loginMoneyTask.getStatus() == Status.RUNNING) {
				return;
			}
		}
		loginMoneyTask = new LoginMoneyTask() {

			@Override
			protected void onPreExecute() {
				showLoading();
			}

			@Override
			protected void onPostExecute(String result) {
				hideLoading();
				super.onPostExecute(result);
				if (MoneyAccount.getMoneyAccount().isLogin()) {
					cashTextView.setText(Parameters.CONSTANT_RMB
							+ decimalFormat.format(MoneyAccount
									.getMoneyAccount().getBalance()));
					getBankCards();
					havePayPassword();
				} else {
					getActivity().finish();
				}
			}
		};
		loginMoneyTask.execute();
	}

	private void getBankCards() {
		if (getBankCards != null) {
			if (getBankCards.getStatus() == Status.RUNNING) {
				return;
			}
		}
		getBankCards = new GetBankCards() {
			@Override
			protected void onPreExecute() {
				showLoading();
			}

			@Override
			protected void onPostExecute(String result) {
				hideLoading();
				super.onPostExecute(result);
				bankCardTextView.setText(MoneyAccount.getMoneyAccount()
						.getBankCards().size()
						+ "");
			}
		};
		getBankCards.execute();
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
									findPayPassword.setVisibility(View.VISIBLE);
									findPayPassword
											.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View v) {
													jump(PayPasswordFindFragment.class
															.getName(),
															"找回支付密码");
												}
											});
									passwordTextView.setText("修改支付密码");
									changePayPassword
											.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View v) {
													jump(PayPasswordChangeFragment.class
															.getName(),
															"修改支付密码");
												}
											});
								} else {
									// 未设置支付密码
									findPayPassword
											.setVisibility(View.INVISIBLE);
									findPayPassword
											.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View v) {
												}
											});
									passwordTextView.setText("设置支付密码");
									changePayPassword
											.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View v) {
													jump(PayPasswordSetFragment.class
															.getName(),
															"设置支付密码");
												}
											});
								}
								return;
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		};
		havePayPasswordTask.execute();
	}

}
