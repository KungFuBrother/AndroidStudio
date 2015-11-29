package yitgogo.smart;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.home.HomeFragment;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.view.Notify;

public class MachineActiveFragment extends DialogFragment {

    View dialogView;

    LinearLayout loadingLayout;
    TextView loadingTextView;

    EditText numberEditText1, numberEditText2, numberEditText3,
            numberEditText4, numberEditText5;
    Button activeButton, exitButton;

    LayoutInflater layoutInflater;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(MachineActiveFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(MachineActiveFragment.class.getName());
    }

    private void init() {
        layoutInflater = LayoutInflater.from(getActivity());
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView, new LayoutParams(880,
                LayoutParams.WRAP_CONTENT));
        return dialog;
    }

    private void findViews() {
        dialogView = layoutInflater.inflate(
                R.layout.dialog_fragment_machine_active, null);
        loadingLayout = (LinearLayout) dialogView
                .findViewById(R.id.active_loading_layout);
        loadingTextView = (TextView) dialogView
                .findViewById(R.id.active_loading_text);
        numberEditText1 = (EditText) dialogView
                .findViewById(R.id.active_number_1);
        numberEditText2 = (EditText) dialogView
                .findViewById(R.id.active_number_2);
        numberEditText3 = (EditText) dialogView
                .findViewById(R.id.active_number_3);
        numberEditText4 = (EditText) dialogView
                .findViewById(R.id.active_number_4);
        numberEditText5 = (EditText) dialogView
                .findViewById(R.id.active_number_5);
        activeButton = (Button) dialogView.findViewById(R.id.active_button);
        exitButton = (Button) dialogView.findViewById(R.id.active_exit);
        registerViews();
    }

    private void registerViews() {
        numberEditText1.addTextChangedListener(new TextWatcher() {

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
                if (numberEditText1.length() >= 5) {
                    numberEditText2.requestFocus();
                }
            }
        });
        numberEditText2.addTextChangedListener(new TextWatcher() {

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
                if (numberEditText2.length() >= 5) {
                    numberEditText3.requestFocus();
                }
            }
        });
        numberEditText3.addTextChangedListener(new TextWatcher() {

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
                if (numberEditText3.length() >= 5) {
                    numberEditText4.requestFocus();
                }
            }
        });
        numberEditText4.addTextChangedListener(new TextWatcher() {

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
                if (numberEditText4.length() >= 5) {
                    numberEditText5.requestFocus();
                }
            }
        });
        numberEditText2.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (numberEditText2.length() == 0) {
                        numberEditText1.requestFocus(EditText.FOCUS_RIGHT);
                    }
                }
                return false;
            }
        });
        numberEditText3.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (numberEditText3.length() == 0) {
                        numberEditText2.requestFocus(EditText.FOCUS_RIGHT);
                    }
                }
                return false;
            }
        });
        numberEditText4.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (numberEditText4.length() == 0) {
                        numberEditText3.requestFocus(EditText.FOCUS_RIGHT);
                    }
                }
                return false;
            }
        });
        numberEditText5.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (numberEditText5.length() == 0) {
                        numberEditText4.requestFocus(EditText.FOCUS_RIGHT);
                    }
                }
                return false;
            }
        });
        activeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (numberEditText1.length() < 5
                        || numberEditText2.length() < 5
                        || numberEditText3.length() < 5
                        || numberEditText4.length() < 5
                        || numberEditText5.length() < 5) {

                } else {
                    bindMachine();
                }
            }
        });
        exitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private String getActiveCodeString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("YT");
        stringBuilder.append(numberEditText1.getText().toString().trim());
        stringBuilder.append("-");
        stringBuilder.append(numberEditText2.getText().toString().trim());
        stringBuilder.append("-");
        stringBuilder.append(numberEditText3.getText().toString().trim());
        stringBuilder.append("-");
        stringBuilder.append(numberEditText4.getText().toString().trim());
        stringBuilder.append("-");
        stringBuilder.append(numberEditText5.getText().toString().trim());
        return stringBuilder.toString().trim();
    }

    private void showLoading(String text) {
        loadingLayout.setVisibility(View.VISIBLE);
        loadingTextView.setText(text);
    }

    private void hideLoading() {
        loadingLayout.setVisibility(View.GONE);
    }

    /**
     * @author Tiger
     * @Url http://beta.yitos.net/api/agency/mechanismManage/equipment/
     * updateMachine
     * @Parameters [machine=d6e016073f1c12d1ab1c6183300144bd,
     * activetion=YT40388-71098-40495-39413-78277]
     * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[ ],
     * "totalCount":1,"dataMap":{},"object":null}
     */
    private void bindMachine() {
        NetworkContent networkContent = new NetworkContent(API.API_MACHINE_BIND);
        networkContent.addParameters("machine", Device.getDeviceCode());
        networkContent.addParameters("activetion", getActiveCodeString());
        MissionController.startNetworkMission(getActivity(), networkContent,
                new OnNetworkListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoading("正在激活设备...");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        hideLoading();
                    }

                    @Override
                    public void onFail(NetworkMissionMessage message) {
                        super.onFail(message);
                        showLoading("激活失败！");
                    }

                    @Override
                    public void onSuccess(NetworkMissionMessage message) {
                        super.onSuccess(message);
                        if (!TextUtils.isEmpty(message.getResult())) {
                            try {
                                JSONObject object = new JSONObject(message
                                        .getResult());
                                if (object.optString("state").equalsIgnoreCase(
                                        "SUCCESS")) {
                                    activeSuccess();
                                    return;
                                }
                                Notify.show("激活失败！"
                                        + object.getString("message"));
                                return;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Notify.show("激活失败！");
                    }
                });
    }

    private void activeSuccess() {
        Notify.show("恭喜！设备激活成功！");
        jump(HomeFragment.class.getName(), "", null);
        dismiss();
        getActivity().finish();
    }

    private void jump(String fragmentName, String title, Bundle parameters) {
        Intent intent = new Intent(getActivity(), ContainerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("title", title);
        if (parameters != null) {
            bundle.putBundle("parameters", parameters);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
