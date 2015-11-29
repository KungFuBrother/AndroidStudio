package yitgogo.smart.bianmin.phoneCharge.ui;

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

public class PhoneChargeSlowFragment extends BaseNotifyFragment {

    EditText numberEditText;
    InnerGridView amountGridView, timeGridView;
    TextView areaTextView, amountTextView, chargeButton;
    AmountAdapter amountAdapter;
    TimeAdapter timeAdapter;

    int[] amounts = {30, 50, 100};
    float[] times = {0.5f, 4, 12, 24, 48, 72};
    int amountSelection = 0, timeSelection = 0;
    ModelChargeInfo chargeInfo = new ModelChargeInfo();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bianmin_phone_charge_slow);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(PhoneChargeSlowFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(PhoneChargeSlowFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getChargeInfo();
    }

    private void init() {
        amountAdapter = new AmountAdapter();
        timeAdapter = new TimeAdapter();
    }

    @Override
    protected void findViews() {
        numberEditText = (EditText) contentView
                .findViewById(R.id.phone_charge_slow_number);
        amountGridView = (InnerGridView) contentView
                .findViewById(R.id.phone_charge_slow_amounts);
        timeGridView = (InnerGridView) contentView
                .findViewById(R.id.phone_charge_slow_time);
        areaTextView = (TextView) contentView
                .findViewById(R.id.phone_charge_slow_area);
        amountTextView = (TextView) contentView
                .findViewById(R.id.phone_charge_slow_amount);
        chargeButton = (TextView) contentView
                .findViewById(R.id.phone_charge_slow_charge);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        numberEditText.setText(BianminFragment.user.getPhone());
        amountGridView.setAdapter(amountAdapter);
        timeGridView.setAdapter(timeAdapter);
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
        amountGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                amountSelection = arg2;
                amountAdapter.notifyDataSetChanged();
                getChargeInfo();
            }
        });
        timeGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                timeSelection = arg2;
                timeAdapter.notifyDataSetChanged();
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

    class TimeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return times.length;
        }

        @Override
        public Object getItem(int position) {
            return times[position];
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
            if (timeSelection == position) {
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
            if (times[position] > 1) {
                holder.textView.setText((int) times[position] + "小时");
            } else {
                holder.textView.setText(times[position] + "小时");
            }
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }

    /**
     * 查询充值信息-慢充
     *
     * @author Tiger
     * @Url http://192.168.8.14:8888/api/facilitate/recharge/findPhoneInfo
     * @Parameters [phoneno=13032889558, pervalue=30, mctype=0.5]
     * @Result {"message":"暂时不支持此类号码的充值","state":"ERROR"}
     * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],
     * "totalCount"
     * :1,"dataMap":{},"object":{"cardid":"170227","cardname"
     * :"全国电信话费100元快充（活动通道）"
     * ,"inprice":null,"sellprice":"100.00","area":"四川南充电信"}}
     */
    private void getPhoneChargeInfo(String phoneNumber) {
        BianminTask.getPhoneChargeInfo(getActivity(), phoneNumber,
                amounts[amountSelection], times[timeSelection],
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
     * 添加充值订单-慢充
     *
     * @author Tiger
     * @Url http://192.168.8.14:8888/api/facilitate/recharge/addOrder/addPhoneOrder
     * @Parameters [phoneno=18048831447, pervalue=100, mctype=12]
     * @Result {"message":"ok","state" :"SUCCESS","cacheKey":null,"dataList":[
     * ],"totalCount":1,"dataMap"
     * :{"orderNumber":"YT3394858942"},"object":null}
     */
    private void phoneCharge(String phoneNumber) {
        BianminTask.phoneCharge(getActivity(),
                BianminFragment.user.getUseraccount(), phoneNumber,
                amounts[amountSelection], times[timeSelection],
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
