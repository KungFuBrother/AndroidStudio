package yitgogo.smart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import yitgogo.smart.tools.LogUtil;

public class BaseActivity extends FragmentActivity {


    Date date = new Date();
    boolean neverShowAds = false;
    boolean showing = false;
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (!neverShowAds) {
                if (showing) {
                    if (Calendar.getInstance().getTime().getTime() - date.getTime() >= 2 * 60 * 1000) {
                        startActivity(new Intent(BaseActivity.this, AdsActivity.class));
                    } else {
                        handler.sendEmptyMessageDelayed(0, 1000);
                    }
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showing = true;
        setNewDate();
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        showing = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        setNewDate();
        return super.dispatchTouchEvent(ev);
    }

    private void setNewDate() {
        date = Calendar.getInstance().getTime();
        LogUtil.logInfo("time", SimpleDateFormat.getDateTimeInstance().format(date));
    }

    protected void findViews() {

    }

    protected void initViews() {

    }

    protected void registerViews() {

    }

    protected void jump(String fragmentName, String title, Bundle parameters) {
        Intent intent = new Intent(BaseActivity.this, ContainerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("title", title);
        if (parameters != null) {
            bundle.putBundle("parameters", parameters);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void openWindow(String fragmentName, String title,
                              Bundle parameters) {
        Intent intent = new Intent(BaseActivity.this, DialogActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("title", title);
        if (parameters != null) {
            bundle.putBundle("parameters", parameters);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void openDialog(String fragmentName, String title,
                              Bundle parameters) {
        Intent intent = new Intent(BaseActivity.this, AlertDialogActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("title", title);
        if (parameters != null) {
            bundle.putBundle("parameters", parameters);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void alert(String titleString, String alertString) {
        Bundle bundle = new Bundle();
        bundle.putString("alertString", alertString);
        openDialog(AlertDialogFragment.class.getName(), titleString, bundle);
    }

    public void neverShowAds() {
        neverShowAds = true;
    }

}
