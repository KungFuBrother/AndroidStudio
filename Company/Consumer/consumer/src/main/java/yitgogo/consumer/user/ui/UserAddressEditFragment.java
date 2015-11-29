package yitgogo.consumer.user.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.local.model.ModelAddress;
import yitgogo.consumer.store.ui.StoreAreaFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;

/**
 * 用户收货地址管理（添加/修改）
 *
 * @author Tiger
 */
public class UserAddressEditFragment extends BaseNetworkFragment {

    EditText nameEditText, phoneEditText, addressEditText, telephoneEditText,
            postcodeEditText, emailEditText;
    TextView areaTextView;
    Button addButton;

    String addressId = "";
    public static String areaName = "", areaId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_address_edit);
        init();
        findViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UserAddressEditFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAddressDetail();
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("addressId")) {
                addressId = bundle.getString("addressId");
            }
        }
    }

    @Override
    public void onDestroy() {
        areaName = "";
        areaId = "";
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UserAddressEditFragment.class.getName());
        areaTextView.setText(areaName);
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
                bundle.putBoolean("getArea", true);
                jump(StoreAreaFragment.class.getName(), "选择收货区域", bundle);
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
            ApplicationTool.showToast("请输入收货人姓名");
        } else if (phoneEditText.length() <= 0) {
            ApplicationTool.showToast("请输入收货人手机号");
        } else if (!isPhoneNumber(phoneEditText.getText().toString())) {
            ApplicationTool.showToast("请输入正确的手机号");
        } else if (areaId.length() <= 0) {
            ApplicationTool.showToast("请选择收货区域");
        } else if (addressEditText.length() <= 0) {
            ApplicationTool.showToast("请输入详细收货地址");
        } else if (telephoneEditText.length() > 0 & telephoneEditText.length() < 11) {
            ApplicationTool.showToast("请输入正确的固定电话号码");
        } else if (postcodeEditText.length() > 0 & postcodeEditText.length() < 6) {
            ApplicationTool.showToast("请输入正确的邮政编码");
        } else {
            if (addressId.length() > 0) {
                modifyAddress();
            } else {
                addAddress();
            }
        }
    }

    private void addAddress() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("memberAccount", User.getUser().getUseraccount()));
        requestParams.add(new RequestParam("personName", nameEditText.getText().toString()));
        requestParams.add(new RequestParam("phone", phoneEditText.getText().toString()));
        requestParams.add(new RequestParam("areaAddress", areaName));
        requestParams.add(new RequestParam("areaId", areaId));
        requestParams.add(new RequestParam("detailedAddress", addressEditText.getText().toString()));
        requestParams.add(new RequestParam("fixPhone", telephoneEditText.getText().toString()));
        requestParams.add(new RequestParam("postcode", postcodeEditText.getText().toString()));
        requestParams.add(new RequestParam("email", emailEditText.getText().toString()));
        post(API.API_USER_ADDRESS_ADD, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            ApplicationTool.showToast("添加成功");
                            getActivity().finish();
                        } else {
                            ApplicationTool.showToast(object.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getAddressDetail() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("id", addressId));
        post(API.API_USER_ADDRESS_DETAIL, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject dataMap = object.optJSONObject("dataMap");
                            if (dataMap != null) {
                                ModelAddress address = new ModelAddress(dataMap.optJSONObject("updateMemberAddress"));
                                nameEditText.setText(address.getPersonName());
                                areaTextView.setText(address.getAreaAddress());
                                addressEditText.setText(address.getDetailedAddress());
                                phoneEditText.setText(address.getPhone());
                                telephoneEditText.setText(address.getFixPhone());
                                postcodeEditText.setText(address.getPostcode());
                                emailEditText.setText(address.getEmail());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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
    private void modifyAddress() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("id", addressId));
        requestParams.add(new RequestParam("personName", nameEditText.getText().toString()));
        requestParams.add(new RequestParam("phone", phoneEditText.getText().toString()));
        requestParams.add(new RequestParam("areaAddress", areaName));
        requestParams.add(new RequestParam("areaId", areaId));
        requestParams.add(new RequestParam("detailedAddress", addressEditText.getText().toString()));
        requestParams.add(new RequestParam("fixPhone", telephoneEditText.getText().toString()));
        requestParams.add(new RequestParam("postcode", postcodeEditText.getText().toString()));
        requestParams.add(new RequestParam("email", emailEditText.getText().toString()));
        post(API.API_USER_ADDRESS_MODIFY, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            ApplicationTool.showToast("修改成功");
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
        });
    }

}
