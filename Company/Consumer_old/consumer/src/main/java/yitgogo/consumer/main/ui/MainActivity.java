package yitgogo.consumer.main.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseActivity;
import yitgogo.consumer.suning.model.SuningManager;
import yitgogo.consumer.suning.ui.SuningAreaSelectFragment;
import yitgogo.consumer.view.FragmentTabHost;
import yitgogo.consumer.view.NormalAskDialog;

public class MainActivity extends BaseActivity {

    FrameLayout fragmentLayout;
    static FragmentTabHost tabHost;
    List<Class> fragments = new ArrayList<>();
    List<Integer> images = new ArrayList<>();
    List<String> lables = new ArrayList<>();

    boolean showLocalBusiness = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_main);
        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void init() {

        Intent intent = getIntent();
        if (intent.hasExtra("showLocalBusiness")) {
            showLocalBusiness = intent.getBooleanExtra("showLocalBusiness", false);
        }

        fragments.add(HomeFragment.class);
        images.add(R.drawable.selector_home_tab_main);
        lables.add("首页");

        fragments.add(HomeYitgogoFragment.class);
        images.add(R.drawable.selector_home_tab_yitgogo);
        lables.add("易商城");

        fragments.add(LocalBusinessFragment.class);
        images.add(R.drawable.selector_home_tab_local);
        lables.add("易商圈");

        fragments.add(HomeSuningFragment.class);
        images.add(R.drawable.selector_home_tab_score);
        lables.add("云商城");

        fragments.add(HomeUserFragment.class);
        images.add(R.drawable.selector_home_tab_user);
        lables.add("我");

    }

    public static void switchTab(int tabPosition) {
        tabHost.setCurrentTab(tabPosition);
    }

    @Override
    protected void findViews() {
        fragmentLayout = (FrameLayout) findViewById(R.id.container_fragment);
        tabHost = (FragmentTabHost) findViewById(R.id.main_tab);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        tabHost.setup(this, getSupportFragmentManager(), R.id.main_fragment);
        tabHost.setup(this, getSupportFragmentManager(), R.id.main_fragment);
        for (int i = 0; i < fragments.size(); i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            tabHost.addTab(tabHost.newTabSpec(fragments.get(i).getName())
                    .setIndicator(getTabItemView(i)), fragments.get(i), null);
        }
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                if (s.equals(HomeSuningFragment.class.getName())) {
                    if (TextUtils.isEmpty(SuningManager.getSuningAreas().getTown().getCode())) {
                        jump(SuningAreaSelectFragment.class.getName(), "设置云商城收货区域");
                    }
                }
            }
        });
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.item_home_tab, null);
        ImageView imageView = (ImageView) view
                .findViewById(R.id.item_tab_image);
        TextView textView = (TextView) view.findViewById(R.id.item_tab_lable);
        imageView.setImageResource(images.get(index));
        textView.setText(lables.get(index));
        return view;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            NormalAskDialog askDialog = new NormalAskDialog("确定要退出易田购购吗？",
                    "退出", "再逛逛") {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (makeSure) {
                        finish();
                    }
                    super.onDismiss(dialog);
                }
            };
            askDialog.show(getSupportFragmentManager(), null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
