package yitgogo.consumer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import yitgogo.consumer.product.ui.ProductDetailFragment;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.MissionController;
import yitgogo.consumer.tools.RequestParam;

public class BaseActivity extends FragmentActivity {

    public LayoutInflater layoutInflater;
    public int pagenum = 0, pagesize = 10;
    public DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        init();
    }

    private void init() {
        // if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
        // getWindow().addFlags(
        // WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // getWindow().addFlags(
        // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        // setTranslucentStatus(true);
        // SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // tintManager.setStatusBarTintEnabled(true);
        // tintManager.setStatusBarTintResource(R.color.actionbar_bg);
        // SystemBarConfig config = tintManager.getConfig();
        // listViewDrawer.setPadding(0, config.getPixelInsetTop(true), 0,
        // config.getPixelInsetBottom());
        // }
        layoutInflater = LayoutInflater.from(this);
        decimalFormat = new DecimalFormat("0.00");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MissionController.cancelNetworkMission(this);
    }

    protected void findViews() {

    }

    protected void initViews() {

    }

    protected void registerViews() {

    }

    protected void jump(String fragmentName, String fragmentTitle) {
        Intent intent = new Intent(BaseActivity.this, ContainerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("fragmentTitle", fragmentTitle);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 带参数的fragment跳转
     *
     * @param fragmentName
     * @param fragmentTitle
     */
    protected void jump(String fragmentName, String fragmentTitle,
                        Bundle parameters) {
        Intent intent = new Intent(BaseActivity.this, ContainerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("fragmentTitle", fragmentTitle);
        bundle.putBundle("parameters", parameters);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void showProductDetail(String productId) {
        Intent intent = new Intent(BaseActivity.this,
                ProductDetailFragment.class);
        intent.putExtra("productId", productId);
        startActivity(intent);
    }

    /**
     * @param originalUrl json得到的图片链接
     * @return formatedUrl 切图链接
     * @author Tiger
     */
    protected String getSmallImageUrl(String originalUrl) {
        if (originalUrl.length() > 0) {
            if (originalUrl.contains(".")) {

                String formation = originalUrl.substring(
                        originalUrl.lastIndexOf("."), originalUrl.length());
                StringBuilder imgBuilder = new StringBuilder(originalUrl);
                return imgBuilder.replace(originalUrl.lastIndexOf("."),
                        originalUrl.length(), "_350" + formation).toString();
            }
        }
        return originalUrl;
    }

    /**
     * 判断是否连接网络
     *
     * @return
     */
    protected boolean isConnected() {
        // TODO Auto-generated method stub
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null) {
            if (connectivityManager.getActiveNetworkInfo().isAvailable()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }



    /**
     * add a new post network mission
     */
    protected void post(String url, List<RequestParam> requestParams, OnNetworkListener onNetworkListener) {
        if (!isConnected()) {
            ApplicationTool.log("ApplicationTool", "Request disconnect");
            return;
        }
        ApplicationTool.log("ApplicationTool", "Request url=" + url);
        ApplicationTool.log("ApplicationTool", "Request requestParams=" + requestParams);
        Request.Builder builder = new Request.Builder();
        builder.tag(this);
        builder.url(url);
        if (requestParams != null) {
            FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
            for (int i = 0; i < requestParams.size(); i++) {
                formEncodingBuilder.add(requestParams.get(i).getKey(), requestParams.get(i).getValue());
            }
            builder.post(formEncodingBuilder.build());
        }
        Request request = builder.build();
        startNetworkMission(request, onNetworkListener);
    }

    private void startNetworkMission(Request request, final OnNetworkListener onNetworkListener) {
        Call call = MissionController.getOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                onNetworkListener.onFailure(request);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                onNetworkListener.onResponse(response);
            }
        });
    }

    public abstract class OnNetworkListener {

        public static final int FLAG_FAIL = 0;
        public static final int FLAG_SUCCESS = 1;

        Handler handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FLAG_FAIL:
                        onFailure((String) msg.obj);
                        break;

                    case FLAG_SUCCESS:
                        onResponse((String) msg.obj);
                        break;

                    default:
                        break;
                }
            }
        };

        public void onFailure(Request request) {
            handler.sendMessage(Message.obtain(handler, FLAG_FAIL, "Called when the request could not be executed due to cancellation, a connectivity problem or timeout."));
        }

        public void onResponse(Response response) {
            if (response.code() >= 400 && response.code() <= 599) {
                handler.sendMessage(Message.obtain(handler, FLAG_FAIL, "ResponseCode " + response.code()));
                return;
            }
            try {
                handler.sendMessage(Message.obtain(handler, FLAG_SUCCESS, response.body().string()));
            } catch (IOException e) {
                handler.sendMessage(Message.obtain(handler, FLAG_FAIL, e.getMessage()));
            }
        }

        private void onFailure(String failReason) {
            ApplicationTool.log("ApplicationTool", "Request onFailure=" + failReason);
            onSuccess("");
        }

        private void onResponse(String result) {
            ApplicationTool.log("ApplicationTool", "Request onResponse=" + result);
            onSuccess(result);
        }

        public abstract void onSuccess(String result);

    }
}
