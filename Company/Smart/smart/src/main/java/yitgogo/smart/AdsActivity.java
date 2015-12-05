package yitgogo.smart;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.home.model.ModelAds;
import yitgogo.smart.home.model.ModelAdsImage;

/**
 * Created by Tiger on 2015-10-29.
 */
public class AdsActivity extends Activity {

    ImageView imageView;

    public static List<ModelAds> adses = new ArrayList<>();
    List<ModelAdsImage> adsImages = new ArrayList<>();

    int count = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                ImageLoader.getInstance().displayImage(adsImages.get(count % adsImages.size()).getAdverImg(), imageView);
                count++;
                handler.sendEmptyMessageDelayed(0, 10000);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ads);
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
        imageView = (ImageView) findViewById(R.id.ads_image);
        initViews();
    }

    private void initViews() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        handler.sendEmptyMessage(0);
    }

}
