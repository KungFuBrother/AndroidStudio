package yitgogo.consumer.bianmin.qq.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.bianmin.ModelBianminOrderResult;
import yitgogo.consumer.bianmin.ModelChargeInfo;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.Notify;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class QQChargeFragment extends BaseNotifyFragment {

	TextView priceTextView;
	EditText accountEditText, amountEditText;
	Button chargeButton;

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

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
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
		chargeButton = (Button) contentView.findViewById(R.id.qq_charge_charge);
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

	private void getPrice() {
		if (amountEditText.length() > 0) {
			new GetPrice().execute();
		} else {
			priceTextView.setText("");
		}
	}

	private void charge() {
		if (amountEditText.length() <= 0) {
			Notify.show("请输入充值数量");
		} else if (accountEditText.length() <= 0) {
			Notify.show("请输入要充值的QQ号");
		} else {
			if (chargeInfo.getSellprice() > 0) {
				new QQCharge().execute();
			}
		}
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
	class GetPrice extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			if (amountEditText.length() > 0) {
				nameValuePairs.add(new BasicNameValuePair("num", amountEditText
						.getText().toString().trim()));
			} else {
				return "";
			}
			return netUtil.postWithoutCookie(API.API_BIANMIN_QQ_INFO,
					nameValuePairs, false, false);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				try {
					JSONObject object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						JSONObject infoObject = object.optJSONObject("object");
						chargeInfo = new ModelChargeInfo(infoObject);
						if (amountEditText.length() > 0) {
							if (chargeInfo.getSellprice() > 0) {
								priceTextView.setText(Parameters.CONSTANT_RMB
										+ decimalFormat.format(chargeInfo
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
	class QQCharge extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("cardid", chargeInfo
					.getCardid()));
			nameValuePairs.add(new BasicNameValuePair("game_userid",
					accountEditText.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("cardnum", amountEditText
					.getText().toString().trim()));
			if (User.getUser().isLogin()) {
				nameValuePairs.add(new BasicNameValuePair("memberAccount", User
						.getUser().getUseraccount()));
			}
			return netUtil.postWithoutCookie(API.API_BIANMIN_GAME_QQ_CHARGE,
					nameValuePairs, false, false);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				try {
					JSONObject object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						JSONObject dataMap = object.optJSONObject("dataMap");
						ModelBianminOrderResult orderResult = new ModelBianminOrderResult(
								dataMap);
						if (orderResult != null) {
							if (orderResult.getSellPrice() > 0) {
								payMoney(orderResult);
								getActivity().finish();
								return;
							}
						}
					}
					Notify.show(object.optString("message"));
					return;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			Notify.show("充值失败");
		}
	}

}
