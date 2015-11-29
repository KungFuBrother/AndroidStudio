package yitgogo.consumer.user.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;

public class UserScoreFragment extends BaseNetworkFragment {

    TextView scoreTotalTextView, signButton;
    LinearLayout detailButton, shareButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_score);
        findViews();
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initViews() {

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UserScoreFragment.class.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UserScoreFragment.class.getName());
        if (User.getUser().isLogin()) {
            getUserScore();
            getSignState();
        }
    }

    @Override
    protected void findViews() {
        scoreTotalTextView = (TextView) contentView
                .findViewById(R.id.score_total);
        signButton = (TextView) contentView.findViewById(R.id.score_sign);
        detailButton = (LinearLayout) contentView
                .findViewById(R.id.score_detail);
        shareButton = (LinearLayout) contentView.findViewById(R.id.score_share);
        registerViews();
    }

    @Override
    protected void registerViews() {
        signButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                userSignUp();
            }
        });
        detailButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(UserScoreDetailFragment.class.getName(), "积分详情");
            }
        });
        shareButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(UserShareFragment.class.getName(), "推荐好友");
            }
        });
    }

    private void getUserScore() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("memberAccount", User.getUser().getUseraccount()));
        post(API.API_USER_JIFEN, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject jifenObject = object.optJSONObject("object");
                            if (jifenObject != null) {
                                String score = jifenObject.optString("totalBonus");
                                if (!score.equalsIgnoreCase("null")) {
                                    scoreTotalTextView.setText(score);
                                } else {
                                    scoreTotalTextView.setText("0");
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getSignState() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("userAccount", User.getUser().getUseraccount()));
        post(API.API_USER_SIGN_STATE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject jsonObject = object.getJSONObject("object");
                            String isSign = jsonObject.getString("isSign");
                            if (!isSign.equals("0")) {
                                signButton.setText("今日已签到");
                                signButton.setTextColor(getResources().getColor(
                                        R.color.textColorThird));
                                signButton.setClickable(false);
                            } else {
                                signButton.setText("签到领积分");
                                signButton.setTextColor(getResources().getColor(
                                        R.color.textColorSecond));
                                signButton.setClickable(true);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void userSignUp() {
        signButton.setText("今日已签到");
        signButton.setTextColor(getResources().getColor(R.color.textColorThird));
        signButton.setClickable(false);
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("userAccount", User.getUser().getUseraccount()));
        post(API.API_USER_SIGN, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            getSignState();
                            getUserScore();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

}
