package yitgogo.consumer.home.part;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNormalFragment;
import yitgogo.consumer.home.model.ModelAds;
import yitgogo.consumer.product.ui.SaleTimeListFragment;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import com.dtr.zxing.activity.CaptureActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;

public class PartAdsFragment extends BaseNormalFragment {

	static PartAdsFragment adsFragment;
	AutoScrollViewPager viewPager;
	List<ModelAds> ads;
	AdsAdapter adsAdapter;

	public static PartAdsFragment getAdsFragment() {
		if (adsFragment == null) {
			adsFragment = new PartAdsFragment();
		}
		return adsFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		measureScreen();
		ads = new ArrayList<ModelAds>();
		adsAdapter = new AdsAdapter();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_part_ads, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		viewPager = (AutoScrollViewPager) view
				.findViewById(R.id.part_ads_pager);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		viewPager.setAdapter(adsAdapter);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, screenWidth / 3);
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
		ads.clear();
		adsAdapter.notifyDataSetChanged();
		if (result.length() > 0) {
			JSONObject object;
			try {
				object = new JSONObject(result);
				if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONArray array = object.optJSONArray("dataList");
					if (array != null) {
						for (int i = 0; i < array.length(); i++) {
							ads.add(new ModelAds(array.getJSONObject(i)));
						}
						adsAdapter.notifyDataSetChanged();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (ads.isEmpty()) {
			getView().setVisibility(View.GONE);
		} else {
			getView().setVisibility(View.VISIBLE);
		}
	}

	class AdsAdapter extends PagerAdapter {

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return ads.size();
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
			if (ads.get(position).getAdverImg().length() > 0) {
				ImageLoader.getInstance().displayImage(
						ads.get(position).getAdverImg(), imageView);
			} else {
				ImageLoader.getInstance().displayImage(
						ads.get(position).getDefaultImg(), imageView);
			}
			imageLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 产品广告，跳转到产品详情界面
					if (ads.get(index).getType().contains("产品")) {
						String productId = "";
						if (ads.get(index).getAdverUrl().length() > 0) {
							productId = ads.get(index).getAdverUrl();
						} else {
							productId = ads.get(index).getDefaultUrl();
						}
						showProductDetail(productId, ads.get(index)
								.getAdvername(), CaptureActivity.SALE_TYPE_NONE);
					} else {
						// 主题广告，跳转到活动
						String saleClassId = "";
						if (ads.get(index).getAdverUrl().length() > 0) {
							saleClassId = ads.get(index).getAdverUrl();
						} else {
							saleClassId = ads.get(index).getDefaultUrl();
						}
						Bundle bundle = new Bundle();
						bundle.putString("saleClassId", saleClassId);
						jump(SaleTimeListFragment.class.getName(),
								ads.get(index).getAdvername(), bundle);
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
