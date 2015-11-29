package yitgogo.consumer.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtr.zxing.activity.CaptureActivity;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.RequestParam;

public class UserRegisterFragment extends BaseNetworkFragment implements OnClickListener {

    EditText phoneEdit, smscodeEdit, passwordEdit, passwordConfirmEdit,
            inviteCodeEditText;
    TextView getSmscodeButton;
    ImageView showPassword, scanButton;
    Button registerButton;
    boolean isShown = false, isFinish = false;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.obj != null) {
                getSmscodeButton.setText(msg.obj + "s");
            } else {
                getSmscodeButton.setEnabled(true);
                getSmscodeButton.setTextColor(getResources().getColor(
                        R.color.textColorSecond));
                getSmscodeButton.setText("获取验证码");
            }
        }

        ;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_register);
        findViews();
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initViews() {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UserRegisterFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UserRegisterFragment.class.getName());
    }

    @Override
    public void onDestroy() {
        isFinish = true;
        super.onDestroy();
    }

    @Override
    protected void findViews() {
        phoneEdit = (EditText) contentView
                .findViewById(R.id.user_register_phone);
        smscodeEdit = (EditText) contentView
                .findViewById(R.id.user_register_smscode);
        passwordEdit = (EditText) contentView
                .findViewById(R.id.user_register_password);
        passwordConfirmEdit = (EditText) contentView
                .findViewById(R.id.user_register_password_confirm);
        inviteCodeEditText = (EditText) contentView
                .findViewById(R.id.user_register_invitecode);
        scanButton = (ImageView) contentView
                .findViewById(R.id.user_register_invitecode_scan);
        getSmscodeButton = (TextView) contentView
                .findViewById(R.id.user_register_smscode_get);
        registerButton = (Button) contentView
                .findViewById(R.id.user_register_enter);
        showPassword = (ImageView) contentView
                .findViewById(R.id.user_register_password_show);
        registerViews();
    }

    @Override
    protected void registerViews() {
        getSmscodeButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        showPassword.setOnClickListener(this);
        onBackButtonClick(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(UserLoginFragment.class.getName(), "会员登录");
                getActivity().finish();
            }
        });
        scanButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent, 5);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 5) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    if (bundle.containsKey("userCode")) {
                        inviteCodeEditText
                                .setText(bundle.getString("userCode"));
                    }
                }
            }
        }
    }

    private void register() {
        if (!isPhoneNumber(phoneEdit.getText().toString())) {
            ApplicationTool.showToast("请输入正确的手机号");
        } else if (smscodeEdit.length() != 6) {
            ApplicationTool.showToast("请输入您收到的验证码");
        } else if (passwordEdit.length() == 0) {
            ApplicationTool.showToast("请输入密码");
        } else if (passwordConfirmEdit.length() == 0) {
            ApplicationTool.showToast("请确认密码");
        } else if (!passwordEdit.getText().toString()
                .equals(passwordConfirmEdit.getText().toString())) {
            ApplicationTool.showToast("两次输入的密码不相同 ");
        } else {
            userRegister();
        }
    }

    private void showPassword() {
        if (isShown) {
            passwordEdit.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
            passwordConfirmEdit
                    .setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
        } else {
            passwordEdit
                    .setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
            passwordConfirmEdit
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

    private void userRegister() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("phone", phoneEdit.getText().toString()));
        requestParams.add(new RequestParam("smsCode", smscodeEdit.getText().toString()));
        requestParams.add(new RequestParam("password", getEncodedPassWord(passwordEdit.getText().toString().trim())));
        requestParams.add(new RequestParam("refereeCode", inviteCodeEditText.getText().toString()));
        requestParams.add(new RequestParam("spNo", Store.getStore().getStoreNumber()));
        post(API.API_USER_REGISTER, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            ApplicationTool.showToast("注册成功");
                            Bundle bundle = new Bundle();
                            bundle.putString("phone", phoneEdit.getText().toString());
                            jump(UserLoginFragment.class.getName(), "会员登录", bundle);
                            getActivity().finish();
                        } else {
                            ApplicationTool.showToast(object.optString("message"));
                        }
                    } catch (JSONException e) {
                        ApplicationTool.showToast("注册失败");
                        e.printStackTrace();
                    }
                } else {
                    ApplicationTool.showToast("注册失败");
                }
            }
        });
    }

    private void getSmscode() {
        if (!isPhoneNumber(phoneEdit.getText().toString())) {
            ApplicationTool.showToast("请输入正确的手机号");
            return;
        }
        getSmscodeButton.setEnabled(false);
        getSmscodeButton.setTextColor(getResources().getColor(R.color.textColorThird));
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("phone", phoneEdit.getText().toString()));
        post(API.API_USER_REGISTER_SMSCODE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (result.length() > 0) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            ApplicationTool.showToast("验证码已发送至您的手机");
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    int time = 60;
                                    while (time > -1) {
                                        if (isFinish) {
                                            break;
                                        }
                                        try {
                                            Message message = new Message();
                                            if (time > 0) {
                                                message.obj = time;
                                            }
                                            handler.sendMessage(message);
                                            Thread.sleep(1000);
                                            time--;
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();
                        } else {
                            getSmscodeButton.setEnabled(true);
                            getSmscodeButton.setTextColor(getResources().getColor(
                                    R.color.textColorSecond));
                            getSmscodeButton.setText("获取验证码");
                            ApplicationTool.showToast(object.optString("message"));
                        }
                    } catch (JSONException e) {
                        getSmscodeButton.setEnabled(true);
                        getSmscodeButton.setTextColor(getResources().getColor(
                                R.color.textColorSecond));
                        getSmscodeButton.setText("获取验证码");
                        ApplicationTool.showToast("获取验证码失败");
                        e.printStackTrace();
                    }
                } else {
                    getSmscodeButton.setEnabled(true);
                    getSmscodeButton.setTextColor(getResources().getColor(
                            R.color.textColorSecond));
                    getSmscodeButton.setText("获取验证码");
                    ApplicationTool.showToast("获取验证码失败");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_register_smscode_get:
                getSmscode();
                break;

            case R.id.user_register_enter:
                register();
                break;

            case R.id.user_register_password_show:
                showPassword();
                break;

            default:
                break;
        }
    }

}
