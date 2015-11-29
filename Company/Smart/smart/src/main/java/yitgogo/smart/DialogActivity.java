package yitgogo.smart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import yitgogo.smart.home.QrcodeDialog;
import yitgogo.smart.product.ui.ShoppingCarFragment;
import yitgogo.smart.search.ProductSearchFragment;
import yitgogo.smart.tools.MissionController;

public class DialogActivity extends BaseActivity {

    private FrameLayout topLayout;

    private LinearLayout buttonLayout;
    private TextView titleTextView;

    private LinearLayout carLayout;
    private ImageView carButton;
    private ImageView qrcodeButton;
    private LinearLayout searchLayout;
    private ImageView searchButton;
    private ImageView closeButton;

    private TextView disconnectTextView;

    private String fragmentName = "", title = "";
    private Bundle parameters;
    private Fragment fragment;
    private Bundle bundle;

    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_dialog,
                null);
        LayoutParams layoutParams = new LayoutParams(1080, 1280);
        setContentView(view, layoutParams);
        init();
        findViews();
    }

    @Override
    protected void onResume() {
        topLayout.setVisibility(View.GONE);
        initReceiver();
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        topLayout.setVisibility(View.VISIBLE);
        unregisterReceiver(broadcastReceiver);
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        MissionController.cancelMissions(this);
        super.onDestroy();
    }

    private void init() {
        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("fragmentName")) {
                fragmentName = bundle.getString("fragmentName");
            }
            if (bundle.containsKey("title")) {
                title = bundle.getString("title");
            }
            if (bundle.containsKey("parameters")) {
                parameters = bundle.getBundle("parameters");
            }
        }
        if (fragmentName.length() > 0) {
            try {
                fragment = (Fragment) Class.forName(fragmentName).newInstance();
                if (parameters != null) {
                    fragment.setArguments(parameters);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void findViews() {
        buttonLayout = (LinearLayout) findViewById(R.id.dialog_activity_button_layout);
        titleTextView = (TextView) findViewById(R.id.dialog_activity_title);
        carLayout = (LinearLayout) findViewById(R.id.dialog_activity_car_layout);
        carButton = (ImageView) findViewById(R.id.dialog_activity_car);
        qrcodeButton = (ImageView) findViewById(R.id.dialog_activity_qrcode);
        searchLayout = (LinearLayout) findViewById(R.id.dialog_activity_search_layout);
        searchButton = (ImageView) findViewById(R.id.dialog_activity_search);
        closeButton = (ImageView) findViewById(R.id.dialog_activity_close);
        topLayout = (FrameLayout) findViewById(R.id.dialog_activity_top);
        disconnectTextView = (TextView) findViewById(R.id.dialog_activity_disconnect);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        titleTextView.setText(title);
        if (fragmentName.equals(ShoppingCarFragment.class.getName())) {
            carLayout.setVisibility(View.GONE);
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.dialog_activity_content, fragment).commit();
        }
    }

    @Override
    protected void registerViews() {
        searchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openWindow(ProductSearchFragment.class.getName(), "搜索商品");
            }
        });
        carButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openWindow(ShoppingCarFragment.class.getName(), "购物车");
            }
        });
        qrcodeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new QrcodeDialog().show(getSupportFragmentManager(), null);
            }
        });
        closeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        disconnectTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
    }

    /**
     * 添加标题栏文字按钮
     *
     * @param onClickListener
     */
    public void addTextButton(String text, OnClickListener onClickListener) {
        if (buttonLayout.getChildCount() == 0) {
            View view = new View(this);
            LayoutParams layoutParams = new LayoutParams(1, 20);
            view.setLayoutParams(layoutParams);
            view.setBackgroundResource(R.color.divider);
            buttonLayout.addView(view);
        }
        TextView textView = new TextView(this);
        LayoutParams params = new LayoutParams(96, LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);
        textView.setBackgroundResource(R.drawable.selector_trans_divider);
        textView.setOnClickListener(onClickListener);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 16);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.CENTER);
        buttonLayout.addView(textView);
        View view = new View(this);
        LayoutParams layoutParams = new LayoutParams(1, 20);
        view.setLayoutParams(layoutParams);
        view.setBackgroundResource(R.color.divider);
        buttonLayout.addView(view);
    }

    public void removeAllButtons() {
        buttonLayout.removeAllViews();
    }

    private void initReceiver() {
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(
                        ConnectivityManager.CONNECTIVITY_ACTION)) {
                    checkConnection();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * 判断是否连接网络
     *
     * @return
     */
    protected boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null) {
            if (connectivityManager.getActiveNetworkInfo().isAvailable()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void checkConnection() {
        if (isConnected()) {
            disconnectTextView.setVisibility(View.GONE);
        } else {
            disconnectTextView.setVisibility(View.VISIBLE);
        }
    }

    protected void openWindow(String fragmentName, String title) {
        Intent intent = new Intent(DialogActivity.this, DialogActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("title", title);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
