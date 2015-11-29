package yitgogo.consumer.user.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;

public class ModifySecret extends BaseNetworkFragment {

    Button modify;
    TextView accountText, secretOld, secretNew, secretVerify;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_secret);
        findViews();
    }

    @Override
    protected void init() {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ModifySecret.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ModifySecret.class.getName());
    }

    protected void findViews() {
        accountText = (TextView) contentView
                .findViewById(R.id.user_info_secret_account);
        secretOld = (TextView) contentView
                .findViewById(R.id.user_info_secret_old);
        secretNew = (TextView) contentView
                .findViewById(R.id.user_info_secret_new);
        secretVerify = (TextView) contentView
                .findViewById(R.id.user_info_secret_verify);
        modify = (Button) contentView.findViewById(R.id.user_secret_modify);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        accountText.setText(User.getUser().getUseraccount());
    }

    @Override
    protected void registerViews() {
        modify.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                modify();
            }
        });
    }

    private void modify() {
        String oldpassword = secretOld.getText().toString().trim();
        String newpassword = secretNew.getText().toString().trim();
        String renewpassword = secretVerify.getText().toString().trim();
        if (TextUtils.isEmpty(oldpassword)) {
            ApplicationTool.showToast("请输入旧密码");
        } else if (TextUtils.isEmpty(newpassword)) {
            ApplicationTool.showToast("请输入新密码");
        } else if (TextUtils.isEmpty(renewpassword)) {
            ApplicationTool.showToast("请确认新密码");
        } else if (newpassword.equalsIgnoreCase(oldpassword)) {
            ApplicationTool.showToast("新密码与旧密码相同");
        } else if (!newpassword.equalsIgnoreCase(renewpassword)) {
            ApplicationTool.showToast("两次输入的新密码不相同");
        } else {
            modifySecret(oldpassword, newpassword);
        }
    }

    private void modifySecret(String oldpassword, String newpassword) {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("useraccount", User.getUser().getUseraccount()));
        requestParams.add(new RequestParam("oldpassword", getEncodedPassWord(oldpassword)));
        requestParams.add(new RequestParam("newpassword", getEncodedPassWord(newpassword)));
        post(API.API_USER_MODIFY_SECRET, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        Content.removeContent(Parameters.CACHE_KEY_USER_JSON);
                        Content.removeContent(Parameters.CACHE_KEY_USER_PASSWORD);
                        Content.removeContent(Parameters.CACHE_KEY_COOKIE);
                        User.init(getActivity());
                        ApplicationTool.showToast("修改成功,请重新登录");
                        jump(UserLoginFragment.class.getName(), "会员登录");
                        getActivity().finish();
                    } else {
                        ApplicationTool.showToast(object.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
