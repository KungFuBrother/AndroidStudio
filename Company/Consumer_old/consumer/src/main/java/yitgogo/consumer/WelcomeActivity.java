package yitgogo.consumer;

import yitgogo.consumer.tools.Content;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class WelcomeActivity extends BaseActivity {

	int images[] = { R.drawable.welcome_1, R.drawable.welcome_2,
			R.drawable.welcome_3, R.drawable.welcome_4, R.drawable.welcome_5 };
	ViewPager viewPager;
	ImageAdapter imageAdapter;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (!Content.getBooleanContent("welcome", true)) {
			startActivity(new Intent(WelcomeActivity.this,
					EntranceActivity.class));
			finish();
			return;
		}
		setContentView(R.layout.activity_welcome);
		imageAdapter = new ImageAdapter();
		findViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(WelcomeActivity.class.getName());
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(WelcomeActivity.class.getName());
	}

	@Override
	protected void findViews() {
		viewPager = (ViewPager) findViewById(R.id.welcome_pager);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		viewPager.setAdapter(imageAdapter);
	}

	private class ImageAdapter extends PagerAdapter {

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			final int index = position;
			View imageLayout = layoutInflater.inflate(
					R.layout.adapter_viewpager, view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.view_pager_img);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			ProgressBar progressBar = (ProgressBar) imageLayout
					.findViewById(R.id.view_pager_loading);
			progressBar.setVisibility(View.GONE);
			imageView.setImageResource(images[position]);
			imageLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (index == images.length - 1) {
						Content.saveBooleanContent("welcome", false);
						startActivity(new Intent(WelcomeActivity.this,
								EntranceActivity.class));
						finish();
					} else {
						viewPager.setCurrentItem(
								viewPager.getCurrentItem() + 1, true);
					}
				}
			});
			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}

}
