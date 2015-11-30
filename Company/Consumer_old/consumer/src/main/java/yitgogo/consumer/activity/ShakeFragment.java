package yitgogo.consumer.activity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.activity.model.ModelActivity;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.LogUtil;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.NormalAskDialog;
import yitgogo.consumer.view.Notify;

import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class ShakeFragment extends BaseNotifyFragment implements
        SensorEventListener {

    ImageView themeImageView, handImageView;
    SensorManager sensorManager = null;
    Vibrator vibrator = null;
    RotateAnimation rotateAnimation;
    ModelActivity activity = new ModelActivity();

    LinearLayout timeLayout;
    TextView timeDayTextView, timeHourTextView, timeMinuteTextView,
            timeSecondsTextView, timeStringTextView;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            showDownTime();
        }

        ;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_shake);
        init();
        findViews();
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("activity")) {
                try {
                    activity = new ModelActivity(new JSONObject(
                            bundle.getString("activity")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        sensorManager = (SensorManager) getActivity().getSystemService(
                Service.SENSOR_SERVICE);
        vibrator = (Vibrator) getActivity().getSystemService(
                Service.VIBRATOR_SERVICE);
        rotateAnimation = new RotateAnimation(-20.0f, 20.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.7f);
        rotateAnimation.setRepeatMode(RotateAnimation.REVERSE);
        rotateAnimation.setRepeatCount(4);
        rotateAnimation.setDuration(100);
    }

    @Override
    protected void findViews() {
        themeImageView = (ImageView) contentView.findViewById(R.id.shake_theme);
        handImageView = (ImageView) contentView.findViewById(R.id.shake_hand);

        timeLayout = (LinearLayout) contentView
                .findViewById(R.id.shake_time_layout);
        timeDayTextView = (TextView) contentView
                .findViewById(R.id.shake_time_day);
        timeHourTextView = (TextView) contentView
                .findViewById(R.id.shake_time_hour);
        timeMinuteTextView = (TextView) contentView
                .findViewById(R.id.shake_time_minute);
        timeSecondsTextView = (TextView) contentView
                .findViewById(R.id.shake_time_seconds);
        timeStringTextView = (TextView) contentView
                .findViewById(R.id.shake_time_string);
        initViews();
    }

    @Override
    protected void initViews() {
        ImageLoader.getInstance().displayImage(activity.getActivityImg(),
                themeImageView);
        if (!isStarted()) {
            timeLayout.setVisibility(View.VISIBLE);
            timeStringTextView.setText("活动开始时间："
                    + activity.getActivityStartTime());
            showDownTime();
        } else {
            timeLayout.setVisibility(View.GONE);
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ShakeFragment.class.getName());
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ShakeFragment.class.getName());
        if (isStarted()) {
            timeLayout.setVisibility(View.GONE);
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if ((Math.abs(values[0]) > 16 || Math.abs(values[1]) > 16)) {
                vibrator.vibrate(500);
                handImageView.startAnimation(rotateAnimation);
                win();
            }
        }
    }

    private void win() {
        sensorManager.unregisterListener(this);
        int number = new Random().nextInt(activity.getWinExtent()) + 1;
        if (number == activity.getWinNum()) {
            new Win().execute();
        } else {
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * @author Tiger
     * @Url http://192.168.8.117:8888/member/activityManage/memberActivity
     * /memberWin
     * @Parameters [memberAccount=HY612813352788, activityId=4]
     * @Put_Cookie JSESSIONID=8910DCA3DBEEDB67315519D939E440F7;ytAuthId=8910D
     * CA3DBEEDB67315519D939E440F7
     * @Result 10-13 17:50:30.969: I/Request Result(4300):
     * {"message":"ok","state"
     * :"SUCCESS","cacheKey":null,"dataList":[],"totalCount"
     * :1,"dataMap":{"message":"活动结束啦","state":"failed"},"object":null}
     */
    class Win extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("memberAccount", User
                    .getUser().getUseraccount()));
            nameValuePairs.add(new BasicNameValuePair("activityId", activity
                    .getId()));
            return netUtil.postWithCookie(API.API_ACTIVITY_WIN, nameValuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONObject dataMap = object.optJSONObject("dataMap");
                        if (dataMap != null) {
                            if (dataMap.optString("state").equalsIgnoreCase(
                                    "success")) {
                                double winMoney = dataMap.optDouble("winMoney");
                                if (winMoney > 0) {
                                    new WinDialog(winMoney).show(
                                            getFragmentManager(), null);
                                }
                            } else {
                                NormalAskDialog askDialog = new NormalAskDialog(
                                        dataMap.optString("message"), "退出",
                                        "关闭") {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        super.onDismiss(dialog);
                                        if (makeSure) {
                                            ShakeFragment.this.getActivity()
                                                    .finish();
                                        }
                                    }
                                };
                                askDialog.show(getFragmentManager(), null);
                            }
                        }
                        return;
                    }
                    Notify.show(object.optString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class WinDialog extends DialogFragment {

        View dialogView;
        TextView moneyTextView;
        ImageView closeButton;
        double winMoney = 0;

        public WinDialog(double winMoney) {
            this.winMoney = winMoney;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            init();
            findViews();
        }

        private void init() {
            setCancelable(false);
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            return dialog;
        }

        private void findViews() {
            dialogView = layoutInflater.inflate(R.layout.dialog_activity_win,
                    null);
            dialogView.setBackgroundResource(android.R.color.transparent);
            moneyTextView = (TextView) dialogView
                    .findViewById(R.id.activity_win_money);
            closeButton = (ImageView) dialogView
                    .findViewById(R.id.activity_win_close);
            moneyTextView.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(winMoney));
            closeButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
    }

    private boolean isStarted() {
        try {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            long startTime = simpleDateFormat.parse(
                    activity.getActivityStartTime()).getTime();
            if (startTime <= currentTime) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showDownTime() {
        try {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            long startTime = simpleDateFormat.parse(
                    activity.getActivityStartTime()).getTime();
            if (startTime > currentTime) {
                long time = startTime - currentTime;
                long day = time / 86400000;
                long hour = time % 86400000 / 3600000;
                long minute = time % 3600000 / 60000;
                long seconds = time % 60000 / 1000;
                StringBuilder stringBuilder = new StringBuilder();
                if (day < 10) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(day);
                timeDayTextView.setText(stringBuilder.toString());

                stringBuilder = new StringBuilder();
                if (hour < 10) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(hour);
                timeHourTextView.setText(stringBuilder.toString());

                stringBuilder = new StringBuilder();
                if (minute < 10) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(minute);
                timeMinuteTextView.setText(stringBuilder.toString());

                stringBuilder = new StringBuilder();
                if (seconds < 10) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(seconds);
                timeSecondsTextView.setText(stringBuilder.toString());

                handler.sendEmptyMessageDelayed(0, 1000);
            } else {
                timeLayout.setVisibility(View.GONE);
                sensorManager.registerListener(ShakeFragment.this,
                        sensorManager
                                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_NORMAL);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
