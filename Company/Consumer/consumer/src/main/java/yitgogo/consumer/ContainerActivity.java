package yitgogo.consumer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import yitgogo.consumer.main.ui.MainActivity;
import yitgogo.consumer.product.ui.ProductListFragment;
import yitgogo.consumer.store.ui.SelectStoreFragment;
import yitgogo.consumer.tools.ApplicationTool;

public class ContainerActivity extends BaseActivity {

    LinearLayout backButton;
    TextView titleText;
    LinearLayout titleLayout;

    String fragment = "";
    String title = "";
    Bundle bundle = new Bundle();
    Fragment contentFragment = new Fragment();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_container);
        init();
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
        if (intent != null) {
            if (intent.hasExtra("fragment")) {
                fragment = intent.getStringExtra("fragment");
            }
            if (intent.hasExtra("title")) {
                title = intent.getStringExtra("title");
            }
            if (intent.hasExtra("bundle")) {
                bundle = intent.getBundleExtra("bundle");
            }
        }
        if (!TextUtils.isEmpty(fragment)) {
            try {
                contentFragment = (Fragment) Class.forName(fragment).newInstance();
                if (bundle != null) {
                    contentFragment.setArguments(bundle);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void findViews() {
        titleLayout = (LinearLayout) findViewById(R.id.container_title_layout);
        backButton = (LinearLayout) findViewById(R.id.container_back);
        titleText = (TextView) findViewById(R.id.container_title);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        titleText.setText(title);
        if (contentFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_fragment, contentFragment).commit();
        }
    }

    @Override
    protected void registerViews() {
        backButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 添加标题栏图片按钮
     *
     * @param imageResId
     * @param tag
     * @param onClickListener
     */
    public void addImageButton(int imageResId, String tag,
                               OnClickListener onClickListener) {
        ImageView imageView = new ImageView(this);
        LayoutParams params = new LayoutParams(ApplicationTool.dip2px(48),
                LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        imageView.setTag(tag);
        imageView.setBackgroundResource(R.drawable.selector_trans_divider);
        imageView.setImageResource(imageResId);
        imageView.setScaleType(ScaleType.CENTER_INSIDE);
        imageView.setOnClickListener(onClickListener);
        titleLayout.addView(imageView);
    }

    /**
     * 添加标题栏文字按钮
     *
     * @param text
     * @param onClickListener
     */
    public void addTextButton(String text, OnClickListener onClickListener) {
        TextView textView = new TextView(this);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(layoutParams);
        textView.setPadding(ApplicationTool.dip2px(8), 0, ApplicationTool.dip2px(8), 0);
        textView.setText(text);
        textView.setMinWidth(ApplicationTool.dip2px(48));
        textView.setBackgroundResource(R.drawable.selector_trans_divider);
        textView.setTextColor(getResources().getColor(R.color.textColorSecond));
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(onClickListener);
        titleLayout.addView(textView);
    }

    /**
     * fragment设置返回按钮点击事件
     *
     * @param onClickListener
     */
    public void onBackButtonClick(OnClickListener onClickListener) {
        backButton.setOnClickListener(onClickListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (fragment.equals(ProductListFragment.class.getName())) {
            ProductListFragment productListFragment = (ProductListFragment) contentFragment;
            if (productListFragment.onKeyDown(keyCode, event)) {
                return true;
            }
        } else if (fragment.equals(SelectStoreFragment.class.getName())) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                getLocalBusinessState();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getLocalBusinessState() {
        GetLocalBusinessState localBusinessState = new GetLocalBusinessState() {

            @Override
            protected void onPostExecute(Boolean showLocalBusiness) {
                Intent intent = new Intent(ContainerActivity.this, MainActivity.class);
                intent.putExtra("showLocalBusiness", showLocalBusiness);
                startActivity(intent);
            }
        };
        localBusinessState.execute();
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            ApplicationTool.log("onDown", event.getX() + "," + event.getY());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            ApplicationTool.log("onFling", event1.getX() + "," + event1.getY()
                    + "------>" + event2.getX() + "," + event2.getY());
            return true;
        }
    }

}
