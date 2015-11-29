package yitgogo.consumer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.MissionController;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;

/**
 * 有通知功能的fragment
 *
 * @author Tiger
 */
public abstract class BaseNetworkFragment extends BaseFragment {

    LinearLayout emptyLayout;
    ImageView emptyImage;
    TextView emptyText;

    LinearLayout failLayout;
    Button failButton;
    TextView failText;

    LinearLayout disconnectLayout;
    TextView disconnectText;
    View disconnectMargin;

    LinearLayout loadingLayout;
    ProgressBar loadingProgressBar;
    TextView loadingText;

    FrameLayout contentLayout;
    public View contentView;
    BroadcastReceiver broadcastReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup base_fragment,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, null);
        findView(view);
        return view;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(broadcastReceiver);
        MissionController.cancelNetworkMission(getActivity());
        super.onDestroy();
    }

    /**
     * 绑定UI
     */
    protected abstract void findViews();

    private void findView(View view) {
        contentLayout = (FrameLayout) view
                .findViewById(R.id.base_fragment_content);
        emptyLayout = (LinearLayout) view
                .findViewById(R.id.base_fragment_empty);
        failLayout = (LinearLayout) view.findViewById(R.id.base_fragment_fail);
        disconnectLayout = (LinearLayout) view
                .findViewById(R.id.base_fragment_disconnect);
        disconnectMargin = view
                .findViewById(R.id.base_fragment_disconnect_margin);
        loadingLayout = (LinearLayout) view
                .findViewById(R.id.base_fragment_loading);
        emptyImage = (ImageView) view
                .findViewById(R.id.base_fragment_empty_image);
        emptyText = (TextView) view.findViewById(R.id.base_fragment_empty_text);
        failText = (TextView) view.findViewById(R.id.base_fragment_fail_text);
        disconnectText = (TextView) view
                .findViewById(R.id.base_fragment_disconnect_text);
        loadingText = (TextView) view
                .findViewById(R.id.base_fragment_loading_text);
        failButton = (Button) view.findViewById(R.id.base_fragment_fail_button);
        loadingProgressBar = (ProgressBar) view
                .findViewById(R.id.base_fragment_loading_progressbar);
        disconnectText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
        showContentView();
        initReceiver();
    }

    private void initReceiver() {
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(
                        ConnectivityManager.CONNECTIVITY_ACTION)) {
                    checkConnection();
//                    if (showConnectionState) {
//                    }
                }
            }
        };
        failButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                reload();
            }
        });
        IntentFilter intentFilter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    protected View findViewById(int viewId) {
        return contentView.findViewById(viewId);
    }

    protected void showContentView() {
        if (contentLayout.getChildCount() > 0) {
            contentLayout.removeAllViews();
        }
        if (contentView != null) {
            contentLayout.addView(contentView);
        }
    }

    protected void setContentView(int layoutId) {
        contentView = layoutInflater.inflate(layoutId, null);
    }

    protected View getContentView() {
        return contentView;
    }

    private void checkConnection() {
        if (isConnected()) {
            disconnectLayout.setVisibility(View.GONE);
        } else {
            disconnectLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void reload() {
    }

    /**
     * network mission start
     */
    protected void missionStart() {
        loadingLayout.setVisibility(View.VISIBLE);
        failLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
    }

    /**
     * network mission finished
     */
    protected void missionComplete() {
        loadingLayout.setVisibility(View.GONE);
        failLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
    }

    /**
     * network mission finished on fail
     */
    protected void missionFailed() {
        loadingLayout.setVisibility(View.GONE);
        failLayout.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
    }

    /**
     * network mission finished but not contain useable data
     */
    protected void missionNodata() {
        loadingLayout.setVisibility(View.GONE);
        failLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }

    protected String post(String url, List<RequestParam> requestParams) {
        if (!isConnected()) {
            ApplicationTool.log("ApplicationTool", "Request disconnect");
            return "";
        }
        ApplicationTool.log("ApplicationTool", "Request url=" + url);
        ApplicationTool.log("ApplicationTool", "Request requestParams=" + requestParams);
        Request.Builder builder = new Request.Builder();
        builder.tag(getActivity());
        builder.url(url);
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        if (requestParams != null) {
            for (int i = 0; i < requestParams.size(); i++) {
                formEncodingBuilder.add(requestParams.get(i).getKey(), requestParams.get(i).getValue());
            }
        }
        builder.post(formEncodingBuilder.build());
        Request request = builder.build();
        try {
            Response response = MissionController.getOkHttpClient().newCall(request).execute();
            if (response != null) {
                String result = response.body().string();
                ApplicationTool.log("ApplicationTool", "Request onResponse=" + result);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * add a new post network mission
     */
    protected void post(String url, OnNetworkListener onNetworkListener) {
        post(url, null, onNetworkListener);
    }

    /**
     * add a new post network mission
     */
    protected void post(String url, List<RequestParam> requestParams, OnNetworkListener onNetworkListener) {
//        if (!isConnected()) {
//            ApplicationTool.log("ApplicationTool", "Request disconnect");
//            return;
//        }
        ApplicationTool.log("ApplicationTool", "Request url=" + url);
        ApplicationTool.log("ApplicationTool", "Request requestParams=" + requestParams);
        Request.Builder builder = new Request.Builder();
        builder.header("version", ApplicationTool.getVersionName());
        if (url.startsWith(API.IP_PUBLIC)) {
            builder.header("cookie", Content.getStringContent(Parameters.CACHE_KEY_COOKIE, ""));
        } else if (url.startsWith(API.IP_MONEY)) {
            builder.header("cookie", Content.getStringContent(Parameters.CACHE_KEY_COOKIE_MONEY, ""));
        }
        builder.tag(getActivity());
        builder.url(url);
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        if (requestParams != null) {
            for (int i = 0; i < requestParams.size(); i++) {
                formEncodingBuilder.add(requestParams.get(i).getKey(), requestParams.get(i).getValue());
            }
        }
        builder.post(formEncodingBuilder.build());
        Request request = builder.build();
        startNetworkMission(request, onNetworkListener);
    }

    private void startNetworkMission(final Request request, final OnNetworkListener onNetworkListener) {
        if (loadingLayout.getVisibility() != View.VISIBLE) {
            missionStart();
        }
        Call call = MissionController.getOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                onNetworkListener.onFailure(request);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response != null) {
//                    if (response.body() != null) {
//                        String result = response.body().string();
//                        if (result.contains("NAUTH")) {
//                            ApplicationTool.log("ApplicationTool", "会话过期" + request.urlString());
//                            Content.removeContent(Parameters.CACHE_KEY_USER_JSON);
//                            Content.removeContent(Parameters.CACHE_KEY_USER_PASSWORD);
//                            Content.removeContent(Parameters.CACHE_KEY_COOKIE);
//                            Content.removeContent(Parameters.CACHE_KEY_MONEY_SN);
//                            User.init(getActivity());
//                            MoneyAccount.init(null);
//                        }
//                    }
                    if (response.headers() != null) {
                        if (response.headers().toMultimap().containsKey("Set-Cookie")) {
                            ApplicationTool.log("ApplicationTool", response.headers().toString());
                            if (response.request().urlString().equals(API.API_USER_LOGIN)) {
                                Content.saveStringContent(Parameters.CACHE_KEY_COOKIE, response.headers().toMultimap().get("Set-Cookie").toString());
                            } else if (response.request().urlString().equals(API.MONEY_LOGIN)) {
                                Content.saveStringContent(Parameters.CACHE_KEY_COOKIE_MONEY, response.headers().toMultimap().get("Set-Cookie").toString());
                            }
                        }
                    }
                }
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
//            missionFailed();
            missionComplete();
            onSuccess("");
        }

        private void onResponse(String result) {
            ApplicationTool.log("ApplicationTool", "Request onResponse=" + result);
            missionComplete();
            onSuccess(result);
        }

        public abstract void onSuccess(String result);

    }

}
