package yitgogo.consumer.user.ui;

import android.os.Bundle;
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
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;

public class ModifyPhone extends BaseNetworkFragment {

    Button modify;
    TextView accountText, phoneOldText, phoneNewText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_phone);
        findViews();
    }

    @Override
    protected void init() {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ModifyPhone.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ModifyPhone.class.getName());
    }

    protected void findViews() {
        accountText = (TextView) contentView
                .findViewById(R.id.user_info_phone_account);
        phoneOldText = (TextView) contentView
                .findViewById(R.id.user_info_phone_old);
        phoneNewText = (TextView) contentView
                .findViewById(R.id.user_info_phone_new);
        modify = (Button) contentView.findViewById(R.id.user_phone_modify);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        accountText.setText(User.getUser().getUseraccount());
        phoneOldText.setText(User.getUser().getPhone());
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
        String newphone = phoneNewText.getText().toString().trim();
        if (newphone.length() == 0) {
            ApplicationTool.showToast("请输入要绑定的手机号");
        } else if (newphone.length() != 11) {
            ApplicationTool.showToast("手机号格式不正确");
        } else {
            modifyPhone(newphone);
        }
    }

    private void modifyPhone(String newphone) {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("account", User.getUser().getUseraccount()));
        requestParams.add(new RequestParam("newphone", newphone));
        post(API.API_USER_MODIFY_PHONE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                        ApplicationTool.showToast("修改手机号成功");
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
