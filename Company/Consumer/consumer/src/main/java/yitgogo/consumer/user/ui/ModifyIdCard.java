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
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;

public class ModifyIdCard extends BaseNetworkFragment {

    Button modify;
    TextView accountText, idOld, idNew;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_idcard);
        findViews();
    }

    @Override
    protected void init() {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ModifyIdCard.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ModifyIdCard.class.getName());
    }

    protected void findViews() {
        accountText = (TextView) contentView
                .findViewById(R.id.user_info_idcard_account);
        idOld = (TextView) contentView.findViewById(R.id.user_info_idcard_old);
        idNew = (TextView) contentView.findViewById(R.id.user_info_idcard_new);
        modify = (Button) contentView.findViewById(R.id.user_idcard_modify);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        accountText.setText(User.getUser().getUseraccount());
        idOld.setText(User.getUser().getIdcard());
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
        String idcard = idNew.getText().toString().trim();
        if (TextUtils.isEmpty(idcard)) {
            ApplicationTool.showToast("请输入要绑定的身份证号码");
        } else if (idcard.length() != 15 & idcard.length() != 18) {
            ApplicationTool.showToast("身份证号码格式不正确");
        } else {
            modifyIdCard(idcard);
        }
    }

    private void modifyIdCard(String idcard) {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("account", User.getUser().getUseraccount()));
        requestParams.add(new RequestParam("idcard", idcard));
        post(API.API_USER_MODIFY_IDCARD, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            ApplicationTool.showToast("绑定身份证成功");
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

}
