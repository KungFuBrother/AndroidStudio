package yitgogo.smart;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.home.model.ModelAds;
import yitgogo.smart.home.model.ModelAdsImage;
import yitgogo.smart.view.AutoScrollViewPager;

/**
 * Created by Tiger on 2015-10-29.
 */
public class AdsActivity extends Activity {

    AutoScrollViewPager viewPager;
    AdsAdapter adsAdapter = new AdsAdapter();
    LayoutInflater layoutInflater;

    public static List<ModelAds> adses = new ArrayList<>();
    List<ModelAdsImage> adsImages = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ads);
        layoutInflater = LayoutInflater.from(this);
        for (int i = 0; i < adses.size(); i++) {
            if (adses.get(i).getAdsImages().isEmpty()) {
                if (!TextUtils.isEmpty(adses.get(i).getDefaultadver())) {
                    adsImages.add(new ModelAdsImage(adses.get(i).getDefaultadver()));
                }
            } else {
                adsImages.addAll(adses.get(i).getAdsImages());
            }
        }
        if (adsImages.isEmpty()) {
            adsImages.add(new ModelAdsImage("drawable://" + R.drawable.ads));
        }
        adsAdapter.notifyDataSetChanged();
        findViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(AdsActivity.class.getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(AdsActivity.class.getName());
    }

    private void findViews() {
        viewPager = (AutoScrollViewPager) findViewById(R.id.ads_pager);
        initViews();
    }

    private void initViews() {
        viewPager.setAdapter(adsAdapter);
        viewPager.setInterval(10000);
        viewPager.setAutoScrollDurationFactor(10);
        viewPager.startAutoScroll();
    }

    class AdsAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            if (adsImages.size() < 2) {
                return adsImages.size();
            }
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            ImageView imageView = new ImageView(AdsActivity.this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ImageLoader.getInstance().displayImage(adsImages.get(position % adsImages.size()).getAdverImg(), imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            view.addView(imageView);
            return imageView;
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
