package yitgogo.consumer;

import android.app.Application;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;
import com.smartown.controller.shoppingcart.DataBaseHelper;
import com.smartown.yitian.gogo.R;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.suning.model.SuningCarController;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.LogUtil;
import yitgogo.consumer.tools.NetUtil;
import yitgogo.consumer.tools.PackageTool;
import yitgogo.consumer.tools.ScreenUtil;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.Notify;

public class YitgogoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        DataBaseHelper.init(this);
        PackageTool.init(this);
        LogUtil.setLogEnable(false);
        SuningCarController.init(this);
        Notify.init(this);
        NetUtil.init(this);
        Content.init(this);
        User.init(this);
        Store.init(this);
        ScreenUtil.init(this);
        CrashReport.initCrashReport(this, "900003445", false);
        initImageLoader();
        initUmeng();
    }

    private void initImageLoader() {
        L.writeDebugLogs(false);
        L.writeLogs(false);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading_default)
                .showImageForEmptyUri(R.drawable.loading_default)
                .showImageOnFail(R.drawable.loading_default)
                .resetViewBeforeLoading(true).cacheInMemory(false)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(
                this).memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .defaultDisplayImageOptions(options).build();
        ImageLoader.getInstance().init(configuration);
    }


    private void initUmeng() {
        //Edit in AndroidManifest.xml
//        AnalyticsConfig.setAppkey(this, "564143c067e58e7902003900");
        //Tencent 腾讯应用宝 Qihu 360手机助手 Baidu 百度手机助手 Wandoujia 豌豆荚 Update 自动更新
//        AnalyticsConfig.setChannel("Update");
        //session超时
        MobclickAgent.setSessionContinueMillis(2 * 60 * 1000);
        //账号统计
        if (User.getUser().isLogin()) {
            MobclickAgent.onProfileSignIn(User.getUser().getUseraccount());
//            MobclickAgent.onProfileSignIn("QQ","userid");
        }
        /**
         * 账号登出时需调用此接口，调用之后不再发送账号相关内容。
         */
//        MobclickAgent.onProfileSignOff();
        /**
         * 禁止默认的页面统计方式，这样将不会再自动统计Activity。
         * 然后需要做两步集成：
         1. 使用 onResume 和 onPause 方法统计时长, 这和基本统计中的情况一样(针对Activity)
         2. 使用 onPageStart 和 onPageEnd 方法统计页面(针对页面,页面可能是Activity 也可能是Fragment或View)
         */
        MobclickAgent.openActivityDurationTrack(false);
        /** 设置是否对日志信息进行加密, 默认false(不加密). */
        AnalyticsConfig.enableEncrypt(true);
        MobclickAgent.setDebugMode(false);
    }


}
