package yitgogo.smart.bianmin.phoneCharge.ui;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.bianmin.BianminFragment;
import yitgogo.smart.bianmin.ModelBianminOrderResult;
import yitgogo.smart.bianmin.ModelChargeInfo;
import yitgogo.smart.task.BianminTask;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.InnerGridView;
import yitgogo.smart.view.Notify;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

public class PhoneChargeFastFragment extends BaseNotifyFragment {

	EditText numberEditText;
	InnerGridView gridView;
	TextView areaTextView, amountTextView, chargeButton;
	AmountAdapter amountAdapter;

	int[] amounts = { 10, 20, 30, 50, 100, 300, 500 };
	int amountSelection = 0;
	ModelChargeInfo chargeInfo = new ModelChargeInfo();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_bianmin_phone_charge_fast);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(PhoneChargeFastFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(PhoneChargeFastFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getChargeInfo();
	}

	private void init() {
		amountAdapter = new AmountAdapter();
	}

	@Override
	protected void findViews() {
		numberEditText = (EditText) contentView
				.findViewById(R.id.phone_charge_fast_number);
		gridView = (InnerGridView) contentView
				.findViewById(R.id.phone_charge_fast_amounts);
		areaTextView = (TextView) contentView
				.findViewById(R.id.phone_charge_fast_area);
		amountTextView = (TextView) contentView
				.findViewById(R.id.phone_charge_fast_amount);
		chargeButton = (TextView) contentView
				.findViewById(R.id.phone_charge_fast_charge);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		gridView.setAdapter(amountAdapter);
	}

	@Override
	protected void registerViews() {
		numberEditText.addTextChangedListener(new TextWatcher() {

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
				getChargeInfo();
			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				amountSelection = arg2;
				amountAdapter.notifyDataSetChanged();
				getChargeInfo();
			}
		});
		chargeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				charge();
			}
		});
	}

	private void getChargeInfo() {
		String phoneNumber = numberEditText.getText().toString();
		if (isPhoneNumber(phoneNumber)) {
			getPhoneChargeInfo(phoneNumber);
		} else {
			amountTextView.setText("");
		}
	}

	private void charge() {
		String phoneNumber = numberEditText.getText().toString();
		if (isPhoneNumber(phoneNumber)) {
			if (chargeInfo.getSellprice() > 0) {
				phoneCharge(phoneNumber);
			}
		} else {
			Notify.show("请输入正确的手机号");
		}
	}

	class AmountAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return amounts.length;
		}

		@Override
		public Object getItem(int position) {
			return amounts[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = layoutInflater.inflate(R.layout.list_text, null);
				holder.textView = (TextView) convertView
						.findViewById(R.id.text_text);
				FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT, 48);
				holder.textView.setLayoutParams(layoutParams);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (amountSelection == position) {
				holder.textView.setTextColor(getResources().getColor(
						R.color.textColorCompany));
				holder.textView
						.setBackgroundResource(R.drawable.back_white_rec_border_orange);
			} else {
				holder.textView.setTextColor(getResources().getColor(
						R.color.textColorSecond));
				holder.textView
						.setBackgroundResource(R.drawable.selector_white_rec_border);
			}
			holder.textView.setText(amounts[position] + "元");
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}

	/**
	 * 查询充值信息-快充
	 * 
	 * @author Tiger
	 * 
	 * @Url http://192.168.8.14:8888/api/facilitate/recharge/findPhoneInfo
	 * 
	 * @Parameters[phoneno=13032889558, pervalue=50]
	 * 
	 * @Result {"message":"ok","state" :"SUCCESS","cacheKey"
	 *         :null,"dataList":[],"totalCount" :1,"dataMap":{},"object"
	 *         :{"cardid":"151803" ,"cardname":"四川联通话费50元直充","inprice" :"49.25"
	 *         ,"sellprice":"49.78","area":"四川成都联通"}}
	 * 
	 * @Result {"message":"运营商地区维护，暂不能充值","state":"ERROR"}
	 */
	private void getPhoneChargeInfo(String phoneNumber) {
		BianminTask.getPhoneChargeInfo(getActivity(), phoneNumber,
				amounts[amountSelection], -1, new OnNetworkListener() {
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
								if (object.optString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONObject infoObject = object
											.optJSONObject("object");
									chargeInfo = new ModelChargeInfo(infoObject);
									if (chargeInfo.getSellprice() > 0) {
										areaTextView.setText(chargeInfo
												.getArea());
										amountTextView.setText(Parameters.CONSTANT_RMB
												+ decimalFormat.format(chargeInfo
														.getSellprice()));
										return;
									}
								}
								amountTextView.setText("");
								Notify.show(object.optString("message"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

	/**
	 * 添加充值订单-快充
	 * 
	 * @author Tiger
	 * 
	 * 
	 * @Url 
	 *      http://192.168.8.14:8888/api/facilitate/recharge/addOrder/addPhoneOrder
	 * 
	 * @Parameters [phoneno=13032889558, pervalue=50]
	 * 
	 * @Result {"message":"ok","state"
	 *         :"SUCCESS","cacheKey":null,"dataList":[],"totalCount"
	 *         :1,"dataMap":{"orderNumber":"YT4833113087"},"object":null}
	 */
	private void phoneCharge(String phoneNumber) {
		BianminTask.phoneCharge(getActivity(),
				BianminFragment.user.getUseraccount(), phoneNumber,
				amounts[amountSelection], -1, new OnNetworkListener() {
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
								if (object.optString("state").equalsIgnoreCase(
										"SUCCESS")) {
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
								Notify.show(object.optString("message"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

}
