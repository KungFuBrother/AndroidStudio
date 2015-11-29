package yitgogo.consumer.bianmin.telephone.ui;

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

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.bianmin.ModelBianminOrderResult;
import yitgogo.consumer.bianmin.ModelChargeInfo;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.InnerGridView;

public class TelePhoneChargeFragment extends BaseNetworkFragment {

    EditText areaCodeEditText, numberEditText;
    InnerGridView carrierGridView, typeGridView, amountGridView;
    TextView areaTextView, amountTextView, chargeButton;
    CarrierAdapter carrierAdapter;
    TypeAdapter typeAdapter;
    AmountAdapter amountAdapter;

    int[] amountsTelecom = {10, 20, 30, 50, 100, 300};
    int[] amountsUnicom = {50, 100};
    String[] carriers = {"中国电信", "中国联通"};
    String[] types = {"固话", "宽带"};

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

    @Override
    protected void init() {
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
                    phoneCharge();
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
                convertView = layoutInflater.inflate(R.layout.list_class_min,
                        null);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.class_min_name);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        ApplicationTool.dip2px(36));
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
                convertView = layoutInflater.inflate(R.layout.list_class_min,
                        null);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.class_min_name);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        ApplicationTool.dip2px(36));
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
                convertView = layoutInflater.inflate(R.layout.list_class_min,
                        null);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.class_min_name);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        ApplicationTool.dip2px(36));
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

    private void getTelePhoneChargeInfo() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("phoneno", acountNumber));
        if (carrierSelection == 0) {
            requestParams.add(new RequestParam("pervalue", amountsTelecom[amountSelection] + ""));
        } else if (carrierSelection == 1) {
            requestParams.add(new RequestParam("pervalue", amountsUnicom[amountSelection] + ""));
        }
        requestParams.add(new RequestParam("teltype", (carrierSelection + 1) + ""));
        requestParams.add(new RequestParam("chargeType", (typeSelection + 1) + ""));
        post(API.API_BIANMIN_TELEPHONE_CHARGE_INFO, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject infoObject = object.optJSONObject("object");
                            chargeInfo = new ModelChargeInfo(infoObject);
                            if (chargeInfo.getSellprice() > 0) {
                                areaTextView.setText(chargeInfo.getArea());
                                amountTextView.setText(decimalFormat
                                        .format(chargeInfo.getSellprice()));
                                return;
                            }
                        }
                        amountTextView.setText("");
                        ApplicationTool.showToast(object.optString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void phoneCharge() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("phoneno", acountNumber));
        if (carrierSelection == 0) {
            requestParams.add(new RequestParam("pervalue", amountsTelecom[amountSelection] + ""));
        } else if (carrierSelection == 1) {
            requestParams.add(new RequestParam("pervalue", amountsUnicom[amountSelection] + ""));
        }
        requestParams.add(new RequestParam("teltype", (carrierSelection + 1) + ""));
        requestParams.add(new RequestParam("chargeType", (typeSelection + 1) + ""));
        if (User.getUser().isLogin()) {
            requestParams.add(new RequestParam("memberAccount", User.getUser().getUseraccount()));
        }
        post(API.API_BIANMIN_TELEPHONE_CHARGE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
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
                        ApplicationTool.showToast(object.optString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
