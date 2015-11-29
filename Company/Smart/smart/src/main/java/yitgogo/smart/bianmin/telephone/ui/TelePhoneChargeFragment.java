package yitgogo.smart.bianmin.telephone.ui;

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
import yitgogo.smart.view.InnerGridView;
import yitgogo.smart.view.Notify;
import android.os.Bundle;
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

public class TelePhoneChargeFragment extends BaseNotifyFragment {

	EditText areaCodeEditText, numberEditText;
	InnerGridView carrierGridView, typeGridView, amountGridView;
	TextView areaTextView, amountTextView, chargeButton;
	CarrierAdapter carrierAdapter;
	TypeAdapter typeAdapter;
	AmountAdapter amountAdapter;

	int[] amountsTelecom = { 10, 20, 30, 50, 100, 300 };
	int[] amountsUnicom = { 50, 100 };
	String[] carriers = { "中国电信", "中国联通" };
	String[] types = { "固话", "宽带" };

	int amountSelection = 0, carrierSelection = 0, typeSelection = 0;
	ModelChargeInfo chargeInfo = new ModelChargeInfo();
	String acountNumber = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_bianmin_telephone_charge);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TelePhoneChargeFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TelePhoneChargeFragment.class.getName());
	}

	private void init() {
		carrierAdapter = new CarrierAdapter();
		typeAdapter = new TypeAdapter();
		amountAdapter = new AmountAdapter();
	}

	@Override
	protected void findViews() {
		areaCodeEditText = (EditText) contentView
				.findViewById(R.id.telephone_charge_area_code);
		numberEditText = (EditText) contentView
				.findViewById(R.id.telephone_charge_number);
		carrierGridView = (InnerGridView) contentView
				.findViewById(R.id.telephone_charge_carrier);
		typeGridView = (InnerGridView) contentView
				.findViewById(R.id.telephone_charge_type);
		amountGridView = (InnerGridView) contentView
				.findViewById(R.id.telephone_charge_amounts);
		areaTextView = (TextView) contentView
				.findViewById(R.id.telephone_charge_area);
		amountTextView = (TextView) contentView
				.findViewById(R.id.telephone_charge_amount);
		chargeButton = (TextView) contentView
				.findViewById(R.id.telephone_charge_charge);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		carrierGridView.setAdapter(carrierAdapter);
		typeGridView.setAdapter(typeAdapter);
		amountGridView.setAdapter(amountAdapter);
	}

	@Override
	protected void registerViews() {
		areaCodeEditText.addTextChangedListener(new TextWatcher() {

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
		carrierGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				carrierSelection = arg2;
				carrierAdapter.notifyDataSetChanged();
				amountSelection = 0;
				amountAdapter.notifyDataSetChanged();
				getChargeInfo();
			}
		});
		typeGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				typeSelection = arg2;
				typeAdapter.notifyDataSetChanged();
				getChargeInfo();
			}
		});
		amountGridView.setOnItemClickListener(new OnItemClickListener() {

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
		if (areaCodeEditText.length() >= 3) {
			if (numberEditText.length() >= 7) {
				acountNumber = areaCodeEditText.getText().toString().trim()
						+ "-" + numberEditText.getText().toString().trim();
				getTelePhoneChargeInfo();
				return;
			}
		}
		amountTextView.setText("");
	}

	private void charge() {
		if (areaCodeEditText.length() >= 3) {
			if (numberEditText.length() >= 7) {
				if (chargeInfo.getSellprice() > 0) {
					PhoneCharge();
				}
			}
		}
	}

	class CarrierAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return carriers.length;
		}

		@Override
		public Object getItem(int position) {
			return carriers[position];
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
			if (carrierSelection == position) {
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
			holder.textView.setText(carriers[position]);
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}

	class TypeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return types.length;
		}

		@Override
		public Object getItem(int position) {
			return types[position];
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
			if (typeSelection == position) {
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
			holder.textView.setText(types[position]);
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}

	class AmountAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (carrierSelection == 1) {
				return amountsUnicom.length;
			}
			return amountsTelecom.length;
		}

		@Override
		public Object getItem(int position) {
			if (carrierSelection == 1) {
				return amountsUnicom[position];
			}
			return amountsTelecom[position];
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
			if (carrierSelection == 0) {
				holder.textView.setText(amountsTelecom[position] + "元");
			} else if (carrierSelection == 1) {
				holder.textView.setText(amountsUnicom[position] + "元");
			}
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}

	/**
	 * 查询充值信息-固话宽带
	 * 
	 * @author Tiger
	 * 
	 * @Url http://192.168.8.14:8888/api/facilitate/recharge/fixtelquery
	 * @Parameters [phoneno=028-83222605, pervalue=10, teltype=1, chargeType=2]
	 * @Result {"message":"运营商地区维护，暂不能充值","state":"ERROR"}
	 * 
	 * @Parameters [phoneno=028-83222605, pervalue=300, teltype=1, chargeType=1]
	 * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],
	 *         "totalCount"
	 *         :1,"dataMap":{},"object":{"cardid":"191805","cardname"
	 *         :"四川电信话费300元直充"
	 *         ,"inprice":null,"sellprice":"298.47","area":"四川成都电信"}}
	 * 
	 * @Parameters [phoneno=028-83222605, pervalue=20, teltype=1, chargeType=2]
	 * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],
	 *         "totalCount"
	 *         :1,"dataMap":{},"object":{"cardid":"1608601","cardname"
	 *         :"四川电信固话20元直充"
	 *         ,"inprice":null,"sellprice":"19.94","area":"四川成都电信"}}
	 * 
	 * @Parameters [phoneno=028-83222605, pervalue=300, teltype=1, chargeType=2]
	 * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],
	 *         "totalCount"
	 *         :1,"dataMap":{},"object":{"cardid":"1608608","cardname"
	 *         :"四川电信固话300元直充"
	 *         ,"inprice":null,"sellprice":"298.78","area":"四川成都电信"}}
	 * 
	 * 
	 */
	private void getTelePhoneChargeInfo() {
		NetworkContent networkContent = new NetworkContent(
				API.API_BIANMIN_TELEPHONE_CHARGE_INFO);
		networkContent.addParameters("phoneno", acountNumber);
		if (carrierSelection == 0) {
			networkContent.addParameters("pervalue",
					amountsTelecom[amountSelection] + "");
		} else if (carrierSelection == 1) {
			networkContent.addParameters("pervalue",
					amountsUnicom[amountSelection] + "");
		}
		networkContent.addParameters("teltype", (carrierSelection + 1) + "");
		networkContent.addParameters("chargeType", (typeSelection + 1) + "");
		MissionController.startNetworkMission(getActivity(), networkContent,
				new OnNetworkListener() {
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
	 * 添加充值订单-固话宽带
	 * 
	 * @author Tiger
	 * 
	 * 
	 * @Url 
	 *      http://192.168.8.14:8888/api/facilitate/recharge/addOrder/addFixtelOrder
	 * @Parameters [phoneno=028-83222605, pervalue=300, teltype=1, chargeType=1,
	 *             memberAccount=HY345595695593]
	 * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],
	 *         "totalCount"
	 *         :1,"dataMap":{"orderNumber":"YT2754681775"},"object":null}
	 */
	private void PhoneCharge() {
		NetworkContent networkContent = new NetworkContent(
				API.API_BIANMIN_TELEPHONE_CHARGE);
		networkContent.addParameters("memberAccount",
				BianminFragment.user.getUseraccount());
		networkContent.addParameters("phoneno", acountNumber);
		if (carrierSelection == 0) {
			networkContent.addParameters("pervalue",
					amountsTelecom[amountSelection] + "");
		} else if (carrierSelection == 1) {
			networkContent.addParameters("pervalue",
					amountsUnicom[amountSelection] + "");
		}
		networkContent.addParameters("teltype", (carrierSelection + 1) + "");
		networkContent.addParameters("chargeType", (typeSelection + 1) + "");
		MissionController.startNetworkMission(getActivity(), networkContent,
				new OnNetworkListener() {
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
									getActivity().finish();
									return;
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
