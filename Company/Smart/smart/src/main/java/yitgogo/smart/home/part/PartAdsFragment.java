package yitgogo.smart.home.part;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.smartown.yitgogo.smart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.AdsActivity;
import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.home.model.ModelAds;
import yitgogo.smart.view.AutoScrollViewPager;

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
        ads = new ArrayList<ModelAds>();
        adsAdapter = new AdsAdapter();
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.part_home_ads, null);
        findViews(view);
        return view;
    }

    @Override
    protected void findViews(View view) {
        viewPager = (AutoScrollViewPager) view.findViewById(R.id.ads_pager);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        viewPager.setAdapter(adsAdapter);
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
        AdsActivity.adses=new ArrayList<>();
        if (result.length() > 0) {
            JSONObject object;
            try {
                object = new JSONObject(result);
                if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                    JSONArray array = object.optJSONArray("dataList");
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            ModelAds ad = new ModelAds(array.getJSONObject(i));
                            if (ad.getAdverPosition().equals("头部广告")) {
                                ads.add(ad);
                            } else if (ad.getAdverPosition().equals("全屏广告")) {
                                AdsActivity.adses.add(ad);
                            }
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
            View imageLayout = layoutInflater.inflate(
                    R.layout.adapter_viewpager, view, false);
            assert imageLayout != null;
            ImageView imageView = (ImageView) imageLayout
                    .findViewById(R.id.view_pager_img);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            if (ads.get(position).getAdsImages().size() > 0) {
                imageLoader.displayImage(ads.get(position).getAdsImages()
                        .get(0).getAdverImg(), imageView);
            }
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
