package yitgogo.consumer.home.part;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNormalFragment;
import yitgogo.consumer.home.model.ModelSaleTheme;
import yitgogo.consumer.product.ui.WebFragment;
import yitgogo.consumer.tools.ScreenUtil;
import yitgogo.consumer.view.AutoScrollViewPager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;

public class PartThemeFragment extends BaseNormalFragment {

	static PartThemeFragment themeFragment;
	AutoScrollViewPager viewPager;
	List<ModelSaleTheme> saleThemes;
	SaleThemeAdapter saleThemeAdapter;

	public static PartThemeFragment getThemeFragment() {
		if (themeFragment == null) {
			themeFragment = new PartThemeFragment();
		}
		return themeFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		measureScreen();
		saleThemes = new ArrayList<ModelSaleTheme>();
		saleThemeAdapter = new SaleThemeAdapter();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_part_theme, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		viewPager = (AutoScrollViewPager) view
				.findViewById(R.id.part_theme_pager);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		viewPager.setAdapter(saleThemeAdapter);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, screenWidth / 3);
		layoutParams.setMargins(0, 0, 0, ScreenUtil.dip2px(8));
		viewPager.setLayoutParams(layoutParams);
		viewPager.setInterval(6000);
		viewPager.setAutoScrollDurationFactor(5);
		viewPager.startAutoScroll();
	}

	@Override
	protected void registerViews() {

	}

	public void refresh(String result) {
		saleThemes.clear();
		saleThemeAdapter.notifyDataSetChanged();
		if (result.length() > 0) {
			JSONObject object;
			try {
				object = new JSONObject(result);
				if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONArray array = object.optJSONArray("dataList");
					if (array != null) {
						for (int i = 0; i < array.length(); i++) {
							saleThemes.add(new ModelSaleTheme(array
									.optJSONObject(i)));
						}
						saleThemeAdapter.notifyDataSetChanged();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (saleThemes.isEmpty()) {
			getView().setVisibility(View.GONE);
		} else {
			getView().setVisibility(View.VISIBLE);
		}
	}

	class SaleThemeAdapter extends PagerAdapter {

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return saleThemes.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			final int index = position;
			View imageLayout = layoutInflater.inflate(
					R.layout.adapter_viewpager, view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.view_pager_img);
			ProgressBar spinner = (ProgressBar) imageLayout
					.findViewById(R.id.view_pager_loading);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			spinner.setVisibility(View.GONE);
			ImageLoader.getInstance().displayImage(
					saleThemes.get(position).getMoblieImg(), imageView);
			imageLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putInt("type", WebFragment.TYPE_URL);
					bundle.putString("url", saleThemes.get(index)
							.getMoblieUrl());
					jump(WebFragment.class.getName(), saleThemes.get(index)
							.getThemeName(), bundle);
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
