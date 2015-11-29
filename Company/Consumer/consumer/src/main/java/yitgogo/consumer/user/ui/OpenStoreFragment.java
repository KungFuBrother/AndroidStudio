package yitgogo.consumer.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.main.ui.MainActivity;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.RequestParam;

/*
 * 店铺注册
 */
public class OpenStoreFragment extends BaseNetworkFragment implements
        OnClickListener {
    LayoutInflater inflater;
    /*
     * 参数：shopname(店铺名)，businessno(营业执照号)，cardnumber（身份证号），contacts（联系人），
     * serviceaddress
     * （店铺地址），contactphone（联系电话），contacttelephone（联系座机），email（邮箱），reid
     * （区域id），starttime(服务开始时间)，endtime（服务结束时间）
     */
    EditText et_store_shopname, et_store_businessno, et_store_cardnumber,
            et_store_contacts, et_store_serviceaddress, et_store_contactphone,
            et_store_contacttelephone, et_store_email;
    // 确认注册
    TextView tv_register;

    /*
     * 正则表达式
     */
    // 1.身份证
    String is_cardnumber = "^(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])$";
    // 3.email
    String is_email = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)";

    // 注册店铺参数：shopname(店铺名)，businessno(营业执照号)，cardnumber（身份证号），contacts（联系人），serviceaddress（店铺地址），contactphone（联系电话），contacttelephone（联系座机），email（邮箱），reid（区域id），starttime(服务开始时间)，endtime（服务结束时间）
    String shopname = null;
    String businessno = null;
    String cardnumber = null;
    String contacts = null;
    String serviceaddress = null;
    String contactphone = null;
    String contacttelephone = null;
    String email = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_mian);
        findViews();
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void registerViews() {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(OpenStoreFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(OpenStoreFragment.class.getName());
    }

    @Override
    protected void findViews() {
        et_store_shopname = (EditText) contentView
                .findViewById(R.id.store_shopname);
        et_store_businessno = (EditText) contentView
                .findViewById(R.id.store_businessno);
        et_store_cardnumber = (EditText) contentView
                .findViewById(R.id.store_cardnumber);

        et_store_contacts = (EditText) contentView
                .findViewById(R.id.store_contacts);
        et_store_serviceaddress = (EditText) contentView
                .findViewById(R.id.store_serviceaddress);
        et_store_contactphone = (EditText) contentView
                .findViewById(R.id.store_contactphone);

        et_store_contacttelephone = (EditText) contentView
                .findViewById(R.id.store_contacttelephone);
        et_store_email = (EditText) contentView.findViewById(R.id.store_email);
        tv_register = (TextView) contentView.findViewById(R.id.store_register);
        tv_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.store_register) {
            shopname = et_store_shopname.getText().toString();
            businessno = et_store_businessno.getText().toString();
            cardnumber = et_store_cardnumber.getText().toString();
            contacts = et_store_contacts.getText().toString();
            serviceaddress = et_store_serviceaddress.getText().toString();
            contactphone = et_store_contactphone.getText().toString();
            contacttelephone = et_store_contacttelephone.getText().toString();
            email = et_store_email.getText().toString();
            // 判断
            if (shopname.length() < 1) {
                ApplicationTool.showToast("请输入店铺名称");
                return;
            }
            if (businessno.length() < 1) {
                ApplicationTool.showToast("请输入营业执照号");
                return;
            }

            if (isCardnumber(cardnumber)) {
                ApplicationTool.showToast("请输入正确的省份证号码");
                return;
            }
            if (contacts.length() < 1) {
                ApplicationTool.showToast("请输入联系人");
                return;
            }
            if (serviceaddress.length() < 1) {
                ApplicationTool.showToast("请输入店铺地址");
                return;
            }
            if (!isPhoneNumber(contactphone)) {
                ApplicationTool.showToast("请输入正确的手机号码");
                return;
            }
            if (contacttelephone.length() < 8) {
                ApplicationTool.showToast("请输入正确的联系座机号码");
                return;
            }
            if (isEmail(email)) {
                ApplicationTool.showToast("请输入正确的邮箱地址");
                return;
            }
            registerStore();
        }
    }

    private void registerStore() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("shopname", shopname));
        requestParams.add(new RequestParam("businessno", businessno));
        requestParams.add(new RequestParam("cardnumber", cardnumber));
        requestParams.add(new RequestParam("contacts", contacts));
        requestParams.add(new RequestParam("serviceaddress", serviceaddress));
        requestParams.add(new RequestParam("contactphone", contactphone));
        requestParams.add(new RequestParam("contacttelephone", contacttelephone));
        requestParams.add(new RequestParam("email", email));
        post(API.API_USER_OPEN_STORE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject info;
                    try {
                        info = new JSONObject(result);
                        if (info.getString("state").equalsIgnoreCase("SUCCESS")) {
                            ApplicationTool.showToast("申请开店成功！");
                            // jumpToMain();
                        } else {
                            Toast.makeText(getActivity(),
                                    info.getString("message"), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 跳转到登录界面
     */
    private void jumpToMain() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    /*
     * 正则表达式判断
     */
    // 身份证
    public boolean isCardnumber(String s) {
        Pattern p = Pattern.compile(is_cardnumber);

        return !(p.matcher(s).matches());
    }

    // email
    public boolean isEmail(String s) {
        Pattern p = Pattern.compile(is_email);
        return !(p.matcher(s).matches());
    }
}
