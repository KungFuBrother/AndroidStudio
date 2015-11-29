package yitgogo.smart.bianmin.qq.ui;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.bianmin.BianminFragment;
import yitgogo.smart.bianmin.ModelBianminOrderResult;
import yitgogo.smart.bianmin.ModelChargeInfo;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.Notify;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

public class QQChargeFragment extends BaseNotifyFragment {

	TextView priceTextView;
	EditText accountEditText, amountEditText;
	TextView chargeButton;

	ModelChargeInfo chargeInfo = new ModelChargeInfo();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_bianmin_qq_charge);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(QQChargeFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(QQChargeFragment.class.getName());
	}

	private void init() {
		measureScreen();
	}

	@Override
	protected void findViews() {
		priceTextView = (TextView) contentView
				.findViewById(R.id.qq_charge_price);
		accountEditText = (EditText) contentView
				.findViewById(R.id.qq_charge_account);
		amountEditText = (EditText) contentView
				.findViewById(R.id.qq_charge_amount);
		chargeButton = (TextView) contentView
				.findViewById(R.id.qq_charge_charge);
		initViews();
		registerViews();
	}

	@Override
	protected void registerViews() {
		amountEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				getPrice();
			}
		});
		chargeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				charge();
			}
		});
	}

	/**
	 * 
	 * @author Tiger
	 * 
	 * @Url http://192.168.8.14:8888/api/facilitate/recharge/findCoinInfo
	 * 
	 * @Parameters [num=123]
	 * 
	 * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],
	 *         "totalCount"
	 *         :1,"dataMap":{},"object":{"cardid":"220612","cardname"
	 *         :"Q币按元直充(点击购买更多面值)"
	 *         ,"pervalue":"1","inprice":null,"innum":null,"amounts"
	 *         :null,"sellprice":"121.67"}}
	 */
	private void getPrice() {
		if (amountEditText.length() > 0) {
			NetworkContent networkContent = new NetworkContent(
					API.API_BIANMIN_QQ_INFO);
			networkContent.addParameters("num", amountEditText.getText()
					.toString());
			MissionController.startNetworkMission(getActivity(),
					networkContent, new OnNetworkListener() {

						@Override
						public void onStart() {
							super.onStart();
							showLoading();
						}

						@Override
						public void onFinish() {
							super.onFinish();
							hideLoading();
						}

						@Override
						public void onSuccess(NetworkMissionMessage message) {
							super.onSuccess(message);
							if (!TextUtils.isEmpty(message.getResult())) {
								try {
									JSONObject object = new JSONObject(message
											.getResult());
									if (object.optString("state")
											.equalsIgnoreCase("SUCCESS")) {
										JSONObject infoObject = object
												.optJSONObject("object");
										chargeInfo = new ModelChargeInfo(
												infoObject);
										if (amountEditText.length() > 0) {
											if (chargeInfo.getSellprice() > 0) {
												priceTextView.setText(Parameters.CONSTANT_RMB
														+ decimalFormat
																.format(chargeInfo
																		.getSellprice()));
												return;
											}
										}
									}
									priceTextView.setText("");
									return;
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							priceTextView.setText("");
						}

					});
		} else {
			priceTextView.setText("");
		}
	}

	/**
	 * 
	 * @author Tiger
	 * 
	 * @Url 
	 *      http://192.168.8.14:8888/api/facilitate/recharge/addOrder/addGameOrder
	 * @Parameters [cardid=229501, game_area=81w上海电信, game_srv=320w上海电信一区,
	 *             game_userid=1076192306, pass=, cardnum=10,
	 *             memberAccount=HY345595695593]
	 * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList"
	 *         :[],"totalCount":1,"dataMap":{"sellPrice":"98.9",
	 *         "orderNumber":"YT4261694182"},"object":null}
	 */
	private void charge() {
		if (amountEditText.length() <= 0) {
			Notify.show("请输入充值数量");
		} else if (accountEditText.length() <= 0) {
			Notify.show("请输入要充值的QQ号");
		} else {
			if (chargeInfo.getSellprice() > 0) {
				NetworkContent networkContent = new NetworkContent(
						API.API_BIANMIN_GAME_QQ_CHARGE);
				networkContent.addParameters("cardid", chargeInfo.getCardid());
				networkContent.addParameters("game_userid", accountEditText
						.getText().toString());
				networkContent.addParameters("cardnum", amountEditText
						.getText().toString());
				networkContent.addParameters("memberAccount",
						BianminFragment.user.getUseraccount());
				MissionController.startNetworkMission(getActivity(),
						networkContent, new OnNetworkListener() {

							@Override
							public void onStart() {
								super.onStart();
								showLoading();
							}

							@Override
							public void onFinish() {
								super.onFinish();
								hideLoading();
							}

							@Override
							public void onSuccess(NetworkMissionMessage message) {
								super.onSuccess(message);
								if (!TextUtils.isEmpty(message.getResult())) {
									try {
										JSONObject object = new JSONObject(
												message.getResult());
										if (object.optString("state")
												.equalsIgnoreCase("SUCCESS")) {
											JSONObject dataMap = object
													.optJSONObject("dataMap");
											ModelBianminOrderResult orderResult = new ModelBianminOrderResult(
													dataMap);
											if (orderResult != null) {
												if (orderResult.getSellPrice() > 0) {
													payMoney(orderResult);
													return;
												}
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
								Notify.show("充值失败");
							}
						});
			}
		}
	}

}
