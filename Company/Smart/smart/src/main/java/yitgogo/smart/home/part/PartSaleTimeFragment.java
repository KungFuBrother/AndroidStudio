package yitgogo.smart.home.part;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.home.model.ModelSaleTime;
import yitgogo.smart.sale.ui.SaleTimeFragment;
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

/**
 * 首页-限时促销
 * 
 * @author Tiger
 * 
 */
public class PartSaleTimeFragment extends BaseNormalFragment {

	static PartSaleTimeFragment saleTimeFragment;
	AutoScrollViewPager viewPager;
	List<ModelSaleTime> saleTimes;
	SaleTimeAdapter saleTimeAdapter;

	public static PartSaleTimeFragment getSaleTimeFragment() {
		if (saleTimeFragment == null) {
			saleTimeFragment = new PartSaleTimeFragment();
		}
		return saleTimeFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		saleTimes = new ArrayList<ModelSaleTime>();
		saleTimeAdapter = new SaleTimeAdapter();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.part_home_sale_time, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		viewPager = (AutoScrollViewPager) view
				.findViewById(R.id.sale_time_pager);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		viewPager.setAdapter(saleTimeAdapter);
		viewPager.setInterval(6000);
		viewPager.setAutoScrollDurationFactor(5);
		viewPager.startAutoScroll();
	}

	@Override
	protected void registerViews() {

	}

	public void refresh() {
		saleTimes = HomeData.getInstance().getSaleTimes();
		saleTimeAdapter.notifyDataSetChanged();
		if (saleTimes.isEmpty()) {
			getView().setVisibility(View.GONE);
		} else {
			getView().setVisibility(View.VISIBLE);
		}
	}

	class SaleTimeAdapter extends PagerAdapter {

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return saleTimes.size();
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
			imageLoader.displayImage(saleTimes.get(position)
					.getSaleClassImage(), imageView);
			imageLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					HomeData.getInstance().setSaleTimeSelection(index);
					openWindow(SaleTimeFragment.class.getName(), "限时促销");
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
