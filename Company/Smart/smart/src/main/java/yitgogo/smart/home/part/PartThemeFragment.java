package yitgogo.smart.home.part;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.home.model.ModelSaleTheme;
import yitgogo.smart.sale.ui.SaleThemeFragment;
import yitgogo.smart.view.AutoScrollViewPager;
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

import com.smartown.yitgogo.smart.R;

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
		saleThemes = new ArrayList<ModelSaleTheme>();
		saleThemeAdapter = new SaleThemeAdapter();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.part_home_theme, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		viewPager = (AutoScrollViewPager) view.findViewById(R.id.theme_pager);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		viewPager.setAdapter(saleThemeAdapter);
		viewPager.setInterval(6000);
		viewPager.setAutoScrollDurationFactor(5);
		viewPager.startAutoScroll();
	}

	@Override
	protected void registerViews() {

	}

	public void refresh() {
		saleThemes = HomeData.getInstance().getSaleThemes();
		saleThemeAdapter.notifyDataSetChanged();
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
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageLoader.displayImage(saleThemes.get(position).getMoblieImg(),
					imageView);
			imageLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					HomeData.getInstance().setSaleThemeSelection(index);
					openWindow(SaleThemeFragment.class.getName(), "主题活动");
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
