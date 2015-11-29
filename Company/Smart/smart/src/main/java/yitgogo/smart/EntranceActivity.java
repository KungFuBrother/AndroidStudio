package yitgogo.smart;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.home.HomeFragment;
import yitgogo.smart.model.VersionInfo;
import yitgogo.smart.task.CommonTask;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.PackageTool;
import yitgogo.smart.view.DownloadDialog;

public class EntranceActivity extends BaseActivity {

    LinearLayout loadingLayout;
    TextView loadingTextView, versionTextView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        neverShowAds();
        setContentView(R.layout.fragment_entrance);
        findViews();
        checkUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(EntranceActivity.class.getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(EntranceActivity.class.getName());
    }

    @Override
    protected void findViews() {
        loadingLayout = (LinearLayout) findViewById(R.id.entrance_loading_layout);
        loadingTextView = (TextView) findViewById(R.id.entrance_loading_text);
        versionTextView = (TextView) findViewById(R.id.entrance_version_text);
        initViews();
    }

    @Override
    protected void initViews() {
        versionTextView.setText("版本:" + PackageTool.getVersionName() + "(" + PackageTool.getVersionCode() + ")");
    }

    private void showLoading(String text) {
        loadingLayout.setVisibility(View.VISIBLE);
        loadingTextView.setText(text);
    }

    private void hideLoading() {
        loadingLayout.setVisibility(View.GONE);
    }

    private void activeSuccess() {
        jump(HomeFragment.class.getName(), "", null);
        finish();
    }

    private void checkUpdate() {
        CommonTask.checkUpdate(this, new OnNetworkListener() {
            @Override
            public void onStart() {
                super.onStart();
                showLoading("正在检查更新...");
            }

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                VersionInfo versionInfo = new VersionInfo(message.getResult());
                if (versionInfo.getVerCode() > PackageTool.getVersionCode()) {
                    // 网络上的版本大于此安装版本，需要更新
                    DownloadDialog downloadDialog = new DownloadDialog(
                            versionInfo) {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            super.onDismiss(dialog);
                            getMachineState();
                        }
                    };
                    downloadDialog.show(getSupportFragmentManager(), null);
                    return;
                }
                getMachineState();
            }

            @Override
            public void onFail(NetworkMissionMessage message) {
                super.onFail(message);
                getMachineState();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                hideLoading();
            }

        });
    }

    private void getMachineState() {
        CommonTask.getMachineState(this, new OnNetworkListener() {
            @Override
            public void onStart() {
                super.onStart();
                showLoading("查询设备状态中，请稍候...");
            }

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                if (TextUtils.isEmpty(message.getResult())) {
                    alert("访问服务器出现异常", "访问服务器出现异常，请检查网络连接或联系客服人员！");
                } else {
                    try {
                        JSONObject object = new JSONObject(message.getResult());
                        if (object.optString("message").equals("stop")) {
                            alert("此设备已被停用", "此设备已被停用！");
                        } else if (object.optString("message").equals("noexit")) {
                            new MachineActiveFragment().show(
                                    getSupportFragmentManager(), null);
                        } else if (object.optString("message").equals("ok")) {
                            activeSuccess();
                        } else {
                            alert("访问出错！",
                                    "访问出错！" + object.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFail(NetworkMissionMessage message) {
                super.onFail(message);
                alert("访问出错！", message.getMessage());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                hideLoading();
            }

        });
    }

}
