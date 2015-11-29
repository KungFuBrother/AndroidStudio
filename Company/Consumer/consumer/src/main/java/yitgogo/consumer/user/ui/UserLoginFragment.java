package yitgogo.consumer.user.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class UserLoginFragment extends BaseNetworkFragment implements
        OnClickListener {

    EditText nameEdit, passwordEdit;
    Button loginButton;
    TextView registerButton, passwordButton;
    ImageView showPassword;
    boolean isShown = false;
    String phone = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_login);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UserLoginFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UserLoginFragment.class.getName());
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("phone")) {
                phone = bundle.getString("phone");
            }
        }
    }

    @Override
    protected void findViews() {
        nameEdit = (EditText) contentView.findViewById(R.id.user_login_name);
        passwordEdit = (EditText) contentView
                .findViewById(R.id.user_login_password);
        loginButton = (Button) contentView.findViewById(R.id.user_login_login);
        registerButton = (TextView) contentView
                .findViewById(R.id.user_login_register);
        passwordButton = (TextView) contentView
                .findViewById(R.id.user_login_findpassword);
        showPassword = (ImageView) contentView
                .findViewById(R.id.user_login_password_show);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        nameEdit.setText(phone);
    }

    @Override
    protected void registerViews() {
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        passwordButton.setOnClickListener(this);
        showPassword.setOnClickListener(this);
    }

    private void login() {
        if (!isPhoneNumber(nameEdit.getText().toString())) {
            ApplicationTool.showToast("请输入正确的手机号");
        } else if (passwordEdit.length() == 0) {
            ApplicationTool.showToast("请输入密码");
        } else {
            userLogin();
        }
    }

    private void showPassword() {
        if (isShown) {
            passwordEdit.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
        } else {
            passwordEdit
                    .setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
        }
        isShown = !isShown;
        if (isShown) {
            showPassword.setImageResource(R.drawable.ic_hide);
        } else {
            showPassword.setImageResource(R.drawable.ic_show);
        }
    }

    private void userLogin() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("phone", nameEdit.getText().toString()));
        requestParams.add(new RequestParam("password", getEncodedPassWord(passwordEdit.getText().toString().trim())));
        post(API.API_USER_LOGIN, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            ApplicationTool.showToast("登录成功");
                            Content.saveStringContent(Parameters.CACHE_KEY_MONEY_SN, object.optString("cacheKey"));
                            JSONObject userObject = object.optJSONObject("object");
                            if (userObject != null) {
                                Content.saveStringContent(Parameters.CACHE_KEY_USER_JSON, userObject.toString());
                                Content.saveStringContent(Parameters.CACHE_KEY_USER_PASSWORD, getEncodedPassWord(passwordEdit.getText().toString().trim()));
                                User.init(getActivity());
                            }
                            getActivity().finish();
                        } else {
                            ApplicationTool.showToast(object.optString("message"));
                        }
                    } catch (JSONException e) {
                        ApplicationTool.showToast("登录失败");
                        e.printStackTrace();
                    }
                } else {
                    ApplicationTool.showToast("登录失败");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_login_login:
                login();
                break;

            case R.id.user_login_register:
                jump(UserRegisterFragment.class.getName(), "注册");
                getActivity().finish();
                break;

            case R.id.user_login_password_show:
                showPassword();
                break;

            case R.id.user_login_findpassword:
                jump(UserFindPasswordFragment.class.getName(), "重设密码");
                break;

            default:
                break;
        }
    }

}
