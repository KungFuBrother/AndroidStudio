package yitgogo.consumer;

import yitgogo.consumer.product.ui.ProductListFragment;
import yitgogo.consumer.tools.LogUtil;
import yitgogo.consumer.tools.ScreenUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class ContainerFullActivity extends BaseActivity {

	LinearLayout backButton;
	TextView titleText;
	LinearLayout titleLayout;

	String fragmentName = "", fragmentTitle = "";
	boolean hideTitle = false;
	Bundle parameters;
	Fragment fragment;
	Bundle bundle;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_container_full);
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
		bundle = getIntent().getExtras();
		if (bundle.containsKey("fragmentName")) {
			fragmentName = bundle.getString("fragmentName");
		}
		if (bundle.containsKey("fragmentTitle")) {
			fragmentTitle = bundle.getString("fragmentTitle");
		}
		if (bundle.containsKey("parameters")) {
			parameters = bundle.getBundle("parameters");
		}
		if (bundle.containsKey("hideTitle")) {
			hideTitle = bundle.getBoolean("hideTitle");
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
		titleLayout = (LinearLayout) findViewById(R.id.container_title_layout);
		backButton = (LinearLayout) findViewById(R.id.container_back);
		titleText = (TextView) findViewById(R.id.container_title);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		if (hideTitle) {
			titleLayout.setVisibility(View.GONE);
		}
		titleText.setText(fragmentTitle);
		if (fragment != null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.container_fragment, fragment).commit();
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
		LayoutParams params = new LayoutParams(ScreenUtil.dip2px(48),
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
	 * 添加标题栏图片按钮
	 * 
	 * @param imageResId
	 * @param tag
	 * @param onClickListener
	 */
	public void addTextButton(String text, OnClickListener onClickListener) {
		TextView textView = new TextView(this);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		textView.setLayoutParams(layoutParams);
		textView.setPadding(ScreenUtil.dip2px(8), 0, ScreenUtil.dip2px(8), 0);
		textView.setText(text);
		textView.setMinWidth(ScreenUtil.dip2px(48));
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
		if (fragmentName.equals(ProductListFragment.class.getName())) {
			ProductListFragment productListFragment = (ProductListFragment) fragment;
			if (productListFragment.onKeyDown(keyCode, event)) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent event) {
			LogUtil.logInfo("onDown", event.getX() + "," + event.getY());
			return true;
		}

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2,
				float velocityX, float velocityY) {
			LogUtil.logInfo("onFling", event1.getX() + "," + event1.getY()
					+ "------>" + event2.getX() + "," + event2.getY());
			return true;
		}
	}

}
