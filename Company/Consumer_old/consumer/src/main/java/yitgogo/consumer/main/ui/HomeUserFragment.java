package yitgogo.consumer.main.ui;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.money.ui.MoneyHomeFragment;
import yitgogo.consumer.order.ui.OrderFragment;
import yitgogo.consumer.product.ui.ShoppingCarFragment;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.store.ui.SelectStoreFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.PackageTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.model.VersionInfo;
import yitgogo.consumer.user.ui.OpenStoreFragment;
import yitgogo.consumer.user.ui.UserAddressFragment;
import yitgogo.consumer.user.ui.UserInfoFragment;
import yitgogo.consumer.user.ui.UserLoginFragment;
import yitgogo.consumer.user.ui.UserRecommendFragment;
import yitgogo.consumer.user.ui.UserScoreFragment;
import yitgogo.consumer.user.ui.UserShareFragment;
import yitgogo.consumer.view.DownloadDialog;
import yitgogo.consumer.view.NormalAskDialog;
import yitgogo.consumer.view.Notify;

public class HomeUserFragment extends BaseNotifyFragment implements
        OnClickListener {

    ImageView userHeadImageView, levelImageView, storeAutoImageView;
    TextView userNameTextView, userLevelTextView;

    TextView storeNameTextView, storeAddressTextView, storeContactTextView;

    LinearLayout infoButton, storeButton, storeAutoButton, addressButton,
            shareButton, userListButton, openStoreButton, updateButtton;

    TextView loginButton;

    LinearLayout entranceLayout;
    FrameLayout moneyEntranceButton, orderEntranceButton, scoreEntranceButton,
            carEntranceButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user);
        measureScreen();
        findViews();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showDisconnectMargin();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(HomeUserFragment.class.getName());
        initViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(HomeUserFragment.class.getName());
    }

    @Override
    protected void findViews() {
        userHeadImageView = (ImageView) contentView
                .findViewById(R.id.user_info_userhead);
        userNameTextView = (TextView) contentView
                .findViewById(R.id.user_info_name);
        levelImageView = (ImageView) contentView
                .findViewById(R.id.user_info_level_image);
        userLevelTextView = (TextView) contentView
                .findViewById(R.id.user_info_level);

        entranceLayout = (LinearLayout) contentView
                .findViewById(R.id.user_entrance);
        moneyEntranceButton = (FrameLayout) contentView
                .findViewById(R.id.user_entrance_money);
        orderEntranceButton = (FrameLayout) contentView
                .findViewById(R.id.user_entrance_order);
        scoreEntranceButton = (FrameLayout) contentView
                .findViewById(R.id.user_entrance_score);
        carEntranceButton = (FrameLayout) contentView
                .findViewById(R.id.user_entrance_car);

        openStoreButton = (LinearLayout) contentView
                .findViewById(R.id.user_open_store);
        infoButton = (LinearLayout) contentView.findViewById(R.id.user_info);

        storeAutoImageView = (ImageView) contentView
                .findViewById(R.id.user_store_auto_image);
        storeAutoButton = (LinearLayout) contentView
                .findViewById(R.id.user_store_auto);
        storeButton = (LinearLayout) contentView.findViewById(R.id.user_store);

        addressButton = (LinearLayout) contentView
                .findViewById(R.id.user_address);
        shareButton = (LinearLayout) contentView.findViewById(R.id.user_share);
        userListButton = (LinearLayout) contentView
                .findViewById(R.id.user_user_list);
        loginButton = (TextView) contentView.findViewById(R.id.user_login);
        updateButtton = (LinearLayout) contentView
                .findViewById(R.id.user_user_update);

        storeNameTextView = (TextView) contentView
                .findViewById(R.id.user_store_name);
        storeAddressTextView = (TextView) contentView
                .findViewById(R.id.user_store_address);
        storeContactTextView = (TextView) contentView
                .findViewById(R.id.user_store_contact);

        registerViews();
    }

    @Override
    protected void initViews() {
        if (Content.getBooleanContent(Parameters.CACHE_KEY_AUTO_LOCATE, true)) {
            storeAutoImageView
                    .setImageResource(R.drawable.iconfont_check_checked);
        } else {
            storeAutoImageView
                    .setImageResource(R.drawable.iconfont_check_normal);
        }
        storeNameTextView.setText(Store.getStore().getStoreName());
        storeAddressTextView.setText(Store.getStore().getStoreAddess());
        if (User.getUser().isLogin()) {
            loginButton.setText("注销");
            new GetUserInfo().execute();
        } else {
            userNameTextView.setText("");
            userLevelTextView.setText("");
            loginButton.setText("登录");
        }
        // getCarCount();
    }

    @Override
    protected void registerViews() {
        entranceLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, screenWidth / 5));
        loginButton.setOnClickListener(this);
        infoButton.setOnClickListener(this);
        addressButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        userListButton.setOnClickListener(this);
        moneyEntranceButton.setOnClickListener(this);
        orderEntranceButton.setOnClickListener(this);
        scoreEntranceButton.setOnClickListener(this);
        carEntranceButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(ShoppingCarFragment.class.getName(), "易商城购物车");
            }
        });
        storeAutoButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Content.saveBooleanContent(Parameters.CACHE_KEY_AUTO_LOCATE,
                        !Content.getBooleanContent(
                                Parameters.CACHE_KEY_AUTO_LOCATE, true));
                if (Content.getBooleanContent(Parameters.CACHE_KEY_AUTO_LOCATE,
                        true)) {
                    storeAutoImageView
                            .setImageResource(R.drawable.iconfont_check_checked);
                } else {
                    storeAutoImageView
                            .setImageResource(R.drawable.iconfont_check_normal);
                }
            }
        });
        storeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(SelectStoreFragment.class.getName(), "修改服务中心");
                getActivity().finish();
            }
        });
        openStoreButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(OpenStoreFragment.class.getName(), "申请开店");
            }
        });
        updateButtton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new CheckUpdate().execute();
            }
        });
    }

    // private void getCarCount() {
    // long count = 0;
    // try {
    // JSONArray carArray = new JSONArray(Content.getStringContent(
    // Parameters.CACHE_KEY_CAR, "[]"));
    // for (int i = 0; i < carArray.length(); i++) {
    // ModelCar car = new ModelCar(carArray.getJSONObject(i));
    // count += car.getProductCount();
    // }
    // } catch (JSONException e) {
    // e.printStackTrace();
    // }
    // }

    @Override
    public void onClick(View v) {
        if (User.getUser().isLogin()) {
            switch (v.getId()) {

                case R.id.user_info:
                    jump(UserInfoFragment.class.getName(), "我的资料");
                    break;

                case R.id.user_entrance_money:
                    jump(MoneyHomeFragment.class.getName(), "钱袋子");
                    break;

                case R.id.user_entrance_order:
                    jump(OrderFragment.class.getName(), "我的订单");
                    break;

                case R.id.user_entrance_score:
                    jump(UserScoreFragment.class.getName(), "赚积分");
                    break;

                case R.id.user_login:
                    NormalAskDialog askDialog = new NormalAskDialog("确定要注销吗？",
                            "确定", "取消") {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (makeSure) {
                                Content.removeContent(Parameters.CACHE_KEY_USER_JSON);
                                Content.removeContent(Parameters.CACHE_KEY_USER_PASSWORD);
                                Content.removeContent(Parameters.CACHE_KEY_COOKIE);
                                Content.removeContent(Parameters.CACHE_KEY_MONEY_SN);
                                User.init(getActivity());
                                MoneyAccount.init(null);
                                loginButton.setText("登录");
                                userNameTextView.setText("");
                                userLevelTextView.setText("");
                            }
                            super.onDismiss(dialog);
                        }
                    };
                    askDialog.show(getFragmentManager(), null);
                    break;

                case R.id.user_address:
                    jump(UserAddressFragment.class.getName(), "收货地址管理");
                    break;

                case R.id.user_share:
                    jump(UserShareFragment.class.getName(), "推荐好友");
                    break;

                case R.id.user_user_list:
                    jump(UserRecommendFragment.class.getName(), "推荐会员信息");
                    break;

                default:
                    break;
            }
        } else {
            jump(UserLoginFragment.class.getName(), "会员登录");
        }
    }

    /**
     * 获取会员等级
     */
    // class GetUserLevel extends AsyncTask<Void, Void, String> {
    //
    // @Override
    // protected String doInBackground(Void... params) {
    // List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
    // // valuePairs.add(new BasicNameValuePair("memberAccount",
    // // "13668192000"));
    // valuePairs.add(new BasicNameValuePair("memberAccount", User
    // .getUser().getUseraccount()));
    // return netUtil.postWithCookie(API.API_MEMBER_GRADE, valuePairs);
    // }
    //
    // @Override
    // protected void onPostExecute(String result) {
    // //
    // {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],"totalCount":1,"dataMap":{"grade":null},"object":null}
    // if (result.length() > 0) {
    // JSONObject object;
    // try {
    // object = new JSONObject(result);
    // if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
    // JSONObject jsonObject = object.getJSONObject("dataMap");
    // if (jsonObject != null) {
    // String level = jsonObject.optString("grade");
    // if (!level.equalsIgnoreCase("null")) {
    // userLevelTextView.setText(level);
    // } else {
    // userLevelTextView.setText("易田新人");
    // }
    // }
    // }
    // } catch (JSONException e) {
    // e.printStackTrace();
    // }
    //
    // }
    // }
    // }

    class GetUserInfo extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("username", User
                    .getUser().getUseraccount()));
            return netUtil
                    .postWithCookie(API.API_USER_INFO_GET, nameValuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (result.length() > 0) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONObject userObject = object.optJSONObject("object");
                        if (userObject != null) {
                            Content.saveStringContent(
                                    Parameters.CACHE_KEY_USER_JSON,
                                    userObject.toString());
                            User.init(getActivity());
                            showUserInfo();
                        }
                    } else {
                        Notify.show(object.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showUserInfo() {
        ImageLoader.getInstance().displayImage(User.getUser().getuImg(),
                userHeadImageView);
        if (TextUtils.isEmpty(User.getUser().getRealname())) {
            userNameTextView.setText(User.getUser().getUseraccount());
        } else {
            userNameTextView.setText(User.getUser().getRealname());
        }
        userLevelTextView.setText(User.getUser().getGrade().getGradeName());
        ImageLoader.getInstance().displayImage(
                User.getUser().getGrade().getGradeImg(), levelImageView);
    }

    /**
     * 检查更新
     *
     * @author Tiger
     */
    class CheckUpdate extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return netUtil
                    .postWithoutCookie(API.API_UPDATE, null, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            final VersionInfo versionInfo = new VersionInfo(result);
            if (versionInfo.getVerCode() > PackageTool.getVersionCode()) {
                // 网络上的版本大于此安装版本，需要更新
                NormalAskDialog askDialog = new NormalAskDialog(versionInfo) {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (makeSure) {
                            DownloadDialog downloadDialog = new DownloadDialog(
                                    versionInfo);
                            downloadDialog.show(getFragmentManager(), null);
                        }
                        super.onDismiss(dialog);
                    }
                };
                askDialog.show(getFragmentManager(), null);
                return;
            }
            Notify.show("已是最新版本");
        }
    }

}