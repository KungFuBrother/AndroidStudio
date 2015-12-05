package yitgogo.consumer.user.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.store.SelectAreaFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.Notify;

/**
 * 用户收货地址管理（添加/修改）
 *
 * @author Tiger
 */
public class UserAddressEditFragment extends BaseNotifyFragment {

    EditText nameEditText, phoneEditText, addressEditText, telephoneEditText,
            postcodeEditText, emailEditText;
    TextView areaTextView;
    Button addButton;

    String addressId = "";
    String areaName = "", areaId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_address_edit);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UserAddressEditFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UserAddressEditFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetAddressDetail().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 22) {
            if (resultCode == 23) {
                areaName = data.getStringExtra("name");
                areaId = data.getStringExtra("id");
                areaTextView.setText(areaName);
            }
        }
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("addressId")) {
                addressId = bundle.getString("addressId");
            }
        }
    }

    @Override
    protected void findViews() {
        nameEditText = (EditText) contentView
                .findViewById(R.id.address_add_name);
        phoneEditText = (EditText) contentView
                .findViewById(R.id.address_add_phone);
        addressEditText = (EditText) contentView
                .findViewById(R.id.address_add_address);
        telephoneEditText = (EditText) contentView
                .findViewById(R.id.address_add_telephone);
        postcodeEditText = (EditText) contentView
                .findViewById(R.id.address_add_postcode);
        emailEditText = (EditText) contentView
                .findViewById(R.id.address_add_email);
        areaTextView = (TextView) contentView
                .findViewById(R.id.address_add_area);
        addButton = (Button) contentView.findViewById(R.id.address_add_add);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        if (addressId.length() > 0) {
            addButton.setText("修改");
        }
    }

    @Override
    protected void registerViews() {
        areaTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", SelectAreaFragment.TYPE_GET_AREA);
                jumpForResult(SelectAreaFragment.class.getName(), "选择区域", bundle, 22);
            }
        });
        addButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                editAddress();
            }
        });
    }

    private void editAddress() {
        if (nameEditText.length() <= 0) {
            Notify.show("请输入收货人姓名");
        } else if (phoneEditText.length() <= 0) {
            Notify.show("请输入收货人手机号");
        } else if (!isPhoneNumber(phoneEditText.getText().toString())) {
            Notify.show("请输入正确的手机号");
        } else if (areaId.length() <= 0) {
            Notify.show("请选择收货区域");
        } else if (addressEditText.length() <= 0) {
            Notify.show("请输入详细收货地址");
        } else if (telephoneEditText.length() > 0
                & telephoneEditText.length() < 11) {
            Notify.show("请输入正确的固定电话号码");
        } else if (postcodeEditText.length() > 0
                & postcodeEditText.length() < 6) {
            Notify.show("请输入正确的邮政编码");
        } else {
            if (addressId.length() > 0) {
                new ModifyAddress().execute();
            } else {
                new AddAdress().execute();
            }
        }
    }

    /**
     * 添加收货地址
     */

    class AddAdress extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("memberAccount", User
                    .getUser().getUseraccount()));
            nameValuePairs.add(new BasicNameValuePair("personName",
                    nameEditText.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("phone", phoneEditText
                    .getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("areaAddress", areaName));
            nameValuePairs.add(new BasicNameValuePair("areaId", areaId));
            nameValuePairs.add(new BasicNameValuePair("detailedAddress",
                    addressEditText.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("fixPhone",
                    telephoneEditText.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("postcode",
                    postcodeEditText.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("email", emailEditText
                    .getText().toString()));
            return netUtil.postWithCookie(API.API_USER_ADDRESS_ADD,
                    nameValuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (result.length() > 0) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                        Notify.show("添加成功");
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(),
                                object.getString("message"), Toast.LENGTH_SHORT)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取地址详情对象
     *
     * @author Tiger
     * @Json {"message":"ok","state":"SUCCESS"
     * ,"cacheKey":null,"dataList":[],"totalCount"
     * :1,"dataMap":{"updateMemberAddress"
     * :{"id":3,"personName":"赵晋","areaId":2421
     * ,"areaAddress":"四川省成都市金牛区","detailedAddress"
     * :"解放路二段6号凤凰大厦","phone":"18584182653"
     * ,"fixPhone":"","postcode":"","email":""
     * ,"isDefault":1,"memberAccount":"18584182653"
     * ,"millis":1438598019428},"secondId"
     * :269,"thirdId":2421,"firstId":23},"object":null}
     */
    class GetAddressDetail extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id", addressId));
            return netUtil.postWithCookie(API.API_USER_ADDRESS_DETAIL,
                    nameValuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (result.length() > 0) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONObject dataMap = object.optJSONObject("dataMap");
                        if (dataMap != null) {
                            JSONObject updateMemberAddress = dataMap
                                    .optJSONObject("updateMemberAddress");
                            if (updateMemberAddress != null) {
                                if (updateMemberAddress.has("personName")) {
                                    if (!updateMemberAddress.optString(
                                            "personName").equalsIgnoreCase(
                                            "null")) {
                                        nameEditText
                                                .setText(updateMemberAddress
                                                        .optString("personName"));
                                    }
                                }
                                if (updateMemberAddress.has("areaAddress")) {
                                    if (!updateMemberAddress.optString(
                                            "areaAddress").equalsIgnoreCase(
                                            "null")) {
                                        areaName = updateMemberAddress
                                                .optString("areaAddress");
                                        areaTextView.setText(areaName);
                                    }
                                }
                                if (updateMemberAddress.has("areaId")) {
                                    if (!updateMemberAddress
                                            .optString("areaId")
                                            .equalsIgnoreCase("null")) {
                                        areaId = updateMemberAddress
                                                .optString("areaId");
                                    }
                                }
                                if (updateMemberAddress.has("detailedAddress")) {
                                    if (!updateMemberAddress.optString(
                                            "detailedAddress")
                                            .equalsIgnoreCase("null")) {
                                        addressEditText
                                                .setText(updateMemberAddress
                                                        .optString("detailedAddress"));
                                    }
                                }
                                if (updateMemberAddress.has("phone")) {
                                    if (!updateMemberAddress.optString("phone")
                                            .equalsIgnoreCase("null")) {
                                        phoneEditText
                                                .setText(updateMemberAddress
                                                        .optString("phone"));
                                    }
                                }
                                if (updateMemberAddress.has("fixPhone")) {
                                    if (!updateMemberAddress.optString(
                                            "fixPhone")
                                            .equalsIgnoreCase("null")) {
                                        telephoneEditText
                                                .setText(updateMemberAddress
                                                        .optString("fixPhone"));
                                    }
                                }
                                if (updateMemberAddress.has("postcode")) {
                                    if (!updateMemberAddress.optString(
                                            "postcode")
                                            .equalsIgnoreCase("null")) {
                                        postcodeEditText
                                                .setText(updateMemberAddress
                                                        .optString("postcode"));
                                    }
                                }
                                if (updateMemberAddress.has("email")) {
                                    if (!updateMemberAddress.optString("email")
                                            .equalsIgnoreCase("null")) {
                                        emailEditText
                                                .setText(updateMemberAddress
                                                        .optString("email"));
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 修改地址信息
     *
     * @author Tiger
     */
    class ModifyAddress extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id", addressId));
            nameValuePairs.add(new BasicNameValuePair("personName",
                    nameEditText.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("phone", phoneEditText
                    .getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("areaAddress", areaName));
            nameValuePairs.add(new BasicNameValuePair("areaId", areaId));
            nameValuePairs.add(new BasicNameValuePair("detailedAddress",
                    addressEditText.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("fixPhone",
                    telephoneEditText.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("postcode",
                    postcodeEditText.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("email", emailEditText
                    .getText().toString()));
            return netUtil.postWithCookie(API.API_USER_ADDRESS_MODIFY,
                    nameValuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (result.length() > 0) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                        Notify.show("修改成功");
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(),
                                object.getString("message"), Toast.LENGTH_SHORT)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
