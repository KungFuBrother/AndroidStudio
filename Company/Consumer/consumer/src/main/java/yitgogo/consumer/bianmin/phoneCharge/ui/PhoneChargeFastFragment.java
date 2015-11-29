package yitgogo.consumer.bianmin.phoneCharge.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.ImageView;
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

public class PhoneChargeFastFragment extends BaseNetworkFragment {

    EditText numberEditText;
    ImageView chooseImageView;
    InnerGridView gridView;
    TextView areaTextView, amountTextView, chargeButton;
    AmountAdapter amountAdapter;

    int[] amounts = {10, 20, 30, 50, 100, 300, 500};
    int amountSelection = 0;
    ModelChargeInfo chargeInfo = new ModelChargeInfo();
    String phoneNumber = "";

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = getActivity().managedQuery(contactData, null,
                            null, null, null);
                    cursor.moveToFirst();
                    String num = this.getContactPhone(cursor);
                    numberEditText.setText(num);
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void init() {
        amountAdapter = new AmountAdapter();
    }

    @Override
    protected void findViews() {
        numberEditText = (EditText) contentView
                .findViewById(R.id.phone_charge_fast_number);
        chooseImageView = (ImageView) contentView
                .findViewById(R.id.phone_charge_fast_number_choose);
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
        numberEditText.setText(User.getUser().getPhone());
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
        chooseImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                choosePhone();
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

    private void choosePhone() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    private String getContactPhone(Cursor cursor) {
        int phoneColumn = cursor
                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        String result = "";
        if (phoneNum > 0) {
            // 获得联系人的ID号
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            // 获得联系人电话的cursor
            Cursor phone = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                            + contactId, null, null);
            if (phone.moveToFirst()) {
                for (; !phone.isAfterLast(); phone.moveToNext()) {
                    int index = phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int typeindex = phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    int phone_type = phone.getInt(typeindex);
                    String phoneNumber = phone.getString(index);
                    result = phoneNumber.replaceAll("-", "");
                    // switch (phone_type) {//此处请看下方注释
                    // case 2:
                    // result = phoneNumber;
                    // break;
                    //
                    // default:
                    // break;
                    // }
                }
                if (!phone.isClosed()) {
                    phone.close();
                }
            }
        }
        return result;
    }

    private void getChargeInfo() {
        phoneNumber = numberEditText.getText().toString();
        if (isPhoneNumber(phoneNumber)) {
            getPhoneChargeInfo();
        } else {
            amountTextView.setText("");
        }
    }

    private void charge() {
        if (isPhoneNumber(phoneNumber)) {
            if (chargeInfo.getSellprice() > 0) {
                phoneCharge();
            }
        } else {
            ApplicationTool.showToast("请输入正确的手机号");
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
            holder.textView.setText(amounts[position] + "元");
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }

    private void getPhoneChargeInfo() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("phoneno", phoneNumber));
        requestParams.add(new RequestParam("pervalue", amounts[amountSelection] + ""));
        post(API.API_BIANMIN_PHONE_CHARGE_INFO, requestParams, new OnNetworkListener() {
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
        requestParams.add(new RequestParam("phoneno", phoneNumber));
        requestParams.add(new RequestParam("pervalue", amounts[amountSelection] + ""));
        if (User.getUser().isLogin()) {
            requestParams.add(new RequestParam("memberAccount", User.getUser().getUseraccount()));
        }
        post(API.API_BIANMIN_PHONE_CHARGE, requestParams, new OnNetworkListener() {
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
