package yitgogo.consumer.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dtr.zxing.activity.CaptureActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.activity.model.ModelActivity;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserLoginFragment;
import yitgogo.consumer.view.InnerListView;
import yitgogo.consumer.view.Notify;

public class ActivityFragment extends BaseNotifyFragment {

    PullToRefreshScrollView refreshScrollView;
    InnerListView activityListView;

    List<ModelActivity> activities;
    ModelActivity activity = new ModelActivity();
    ImageAdapter imageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_list);
        init();
        findViews();
    }

    private void init() {
        measureScreen();
        activities = new ArrayList<ModelActivity>();
        imageAdapter = new ImageAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ActivityFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ActivityFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refresh();
    }

    @Override
    protected void findViews() {
        refreshScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.activity_refresh);
        activityListView = (InnerListView) contentView
                .findViewById(R.id.activity_list);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        activityListView.setAdapter(imageAdapter);
        refreshScrollView.setMode(Mode.PULL_FROM_START);
    }

    @Override
    protected void registerViews() {
        activityListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                activity = activities.get(arg2);
                if (User.getUser().isLogin()) {
                    new JoinActivityState().execute();
                } else {
                    jump(UserLoginFragment.class.getName(), "会员登录");
                }
            }
        });
        refreshScrollView
                .setOnRefreshListener(new OnRefreshListener<ScrollView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        refresh();
                    }

                });
    }

    private void refresh() {
        pagenum = 0;
        activities.clear();
        imageAdapter.notifyDataSetChanged();
        getActivities();
    }

    private void getActivities(){
        post(API.API_ACTIVITY_LIST, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                refreshScrollView.onRefreshComplete();
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    activities.add(new ModelActivity(array
                                            .optJSONObject(i)));
                                }
                                imageAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class ImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return activities.size();
        }

        @Override
        public Object getItem(int position) {
            return activities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.list_activity,
                        null);
                holder.imageView = (ImageView) convertView
                        .findViewById(R.id.activity_image);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.activity_name);
                LayoutParams params = new LayoutParams(
                        LayoutParams.MATCH_PARENT, screenWidth / 2);
                convertView.setLayoutParams(params);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ImageLoader.getInstance().displayImage(
                    activities.get(position).getTitleImg(), holder.imageView);
            holder.textView.setText(activities.get(position).getActivityName());
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }

    }

    class CodeDialog extends DialogFragment {

        View dialogView;
        TextView okButton;
        EditText codeEditText;
        ImageView closeButton, scanButton;

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
            dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            return dialog;
        }

        private void findViews() {
            dialogView = layoutInflater.inflate(R.layout.dialog_activity_code,
                    null);
            closeButton = (ImageView) dialogView
                    .findViewById(R.id.activity_code_close);
            scanButton = (ImageView) dialogView
                    .findViewById(R.id.activity_code_scan);
            codeEditText = (EditText) dialogView
                    .findViewById(R.id.activity_code_edit);
            okButton = (TextView) dialogView
                    .findViewById(R.id.activity_code_ok);
            okButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(codeEditText.getText().toString()
                            .trim())) {
                        Notify.show("请输入活动码");
                    } else {
                        new JoinActivity().execute(codeEditText.getText()
                                .toString().trim());
                        dismiss();
                    }
                }
            });
            closeButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            scanButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),
                            CaptureActivity.class);
                    startActivityForResult(intent, 5);
                    dismiss();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 5) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    if (bundle.containsKey("activityCode")) {
                        new JoinActivity().execute(bundle
                                .getString("activityCode"));
                    }
                }
            }
        }
    }

    private void joinActivityState(){

    }

    /**
     * @author Tiger
     * @Url http://192.168.8.80:8088/member/activityManage/memberActivity
     * /getIsJoin
     * @Parameters [memberAccount=HY048566511863, activityId=4,
     * activityNum=489799]
     * @Put_Cookie JSESSIONID=6D7768E6D
     * 6EC4B4FA5E5C7D3A602F511;ytAuthId=6D7768E6D6EC4B4FA5E5C7D3A602
     * F 5 1 1
     * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList"
     * :[],"totalCount":1,"dataMap":{"state":2},"object":null}
     */
    class JoinActivityState extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("memberAccount", User
                    .getUser().getUseraccount()));
            nameValuePairs.add(new BasicNameValuePair("activityId", activity
                    .getId()));
            return netUtil.postWithCookie(API.API_ACTIVITY_JOIN_STATE,
                    nameValuePairs);
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
                            switch (dataMap.optInt("state")) {
                                case 0:
                                    new CodeDialog().show(getFragmentManager(),
                                            null);
                                    break;
                                case 1:
                                    Bundle bundle = new Bundle();
                                    bundle.putString("activity", activity
                                            .getJsonObject().toString());
                                    jumpFull(ShakeFragment.class.getName(),
                                            activity.getActivityName(), bundle);
                                    // new JoinActivity().execute(activity
                                    // .getActivityNum());
                                    break;

                                default:
                                    break;
                            }
                            return;
                        }
                    }
                    Notify.show(object.optString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @author Tiger
     * @Url http://192.168.8.80:8088/member/activityManage/memberActivity
     * /getIsJoin
     * @Parameters [memberAccount=HY048566511863, activityId=4,
     * activityNum=489799]
     * @Put_Cookie JSESSIONID=6D7768E6D
     * 6EC4B4FA5E5C7D3A602F511;ytAuthId=6D7768E6D6EC4B4FA5E5C7D3A602
     * F 5 1 1
     * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList"
     * :[],"totalCount":1,"dataMap":{"state":2},"object":null}
     */
    class JoinActivity extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("memberAccount", User
                    .getUser().getUseraccount()));
            nameValuePairs.add(new BasicNameValuePair("activityId", activity
                    .getId()));
            nameValuePairs
                    .add(new BasicNameValuePair("activityNum", params[0]));
            return netUtil
                    .postWithCookie(API.API_ACTIVITY_JOIN, nameValuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONObject dataMap = object.optJSONObject("dataMap");
                        switch (dataMap.optInt("state")) {
                            case 0:
                                Bundle bundle = new Bundle();
                                bundle.putString("activity", activity
                                        .getJsonObject().toString());
                                jumpFull(ShakeFragment.class.getName(),
                                        activity.getActivityName(), bundle);
                                break;
                            // case 1:
                            // Notify.show("参与次数已达上限");
                            // break;
                            // case 2:
                            // Notify.show("活动未开始");
                            // break;
                            // case 3:
                            // Notify.show("活动已结束");
                            // break;
                            case 4:
                                Notify.show("活动码填写错误");
                                break;
                            default:
                                break;
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

}
