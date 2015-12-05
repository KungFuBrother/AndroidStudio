package yitgogo.consumer;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
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

import com.dtr.zxing.activity.CaptureActivity;
import com.smartown.yitian.gogo.R;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.bianmin.ModelBianminOrderResult;
import yitgogo.consumer.bianmin.phoneCharge.ui.PhoneChargeFragment;
import yitgogo.consumer.local.model.ModelLocalCar;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.model.ModelOrderResult;
import yitgogo.consumer.order.model.ModelStorePostInfo;
import yitgogo.consumer.order.ui.OrderFragment;
import yitgogo.consumer.product.ui.ProductDetailFragment;
import yitgogo.consumer.product.ui.ProductListFragment;
import yitgogo.consumer.tools.MD5;
import yitgogo.consumer.tools.NetUtil;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.ScreenUtil;

/**
 * 有通知功能的fragment
 *
 * @author Tiger
 */
public class BaseNotifyFragment extends Fragment {

    public LayoutInflater layoutInflater;
    public NetUtil netUtil;
    public int screenWidth = 0, screenHeight = 0;
    public int pagenum = 0, pagesize = 12;
    public DecimalFormat decimalFormat;
    public SimpleDateFormat simpleDateFormat;
    public boolean showConnectionState = true;
    public boolean useCache = true;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        layoutInflater = LayoutInflater.from(getActivity());
        netUtil = NetUtil.getInstance();
        decimalFormat = new DecimalFormat("0.00");
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

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
        super.onDestroy();
    }

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

    protected void findViews() {

    }

    protected void initViews() {

    }

    protected void registerViews() {
    }

    protected void reload() {
        failButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                reload();
            }
        });
    }

    private void initReceiver() {
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(
                        ConnectivityManager.CONNECTIVITY_ACTION)) {
                    if (showConnectionState) {
                        checkConnection();
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
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

    protected void setShowConnectionState(boolean showConnectionState) {
        this.showConnectionState = showConnectionState;
    }

    protected void showDisconnectMargin() {
        disconnectMargin.setVisibility(View.VISIBLE);
    }

    private void checkConnection() {
        if (isConnected()) {
            disconnectLayout.setVisibility(View.GONE);
        } else {
            disconnectLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void showLoading() {
        loadingText.setText("请稍候...");
        emptyLayout.setVisibility(View.GONE);
        failLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    protected void showLoading(String text) {
        loadingText.setText(text);
        emptyLayout.setVisibility(View.GONE);
        failLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    protected void hideLoading() {
        loadingLayout.setVisibility(View.GONE);
    }

    protected void loadingEmpty() {
        emptyText.setText("暂无数据");
        emptyLayout.setVisibility(View.VISIBLE);
    }

    protected void loadingEmpty(String text) {
        emptyText.setText(text);
        emptyLayout.setVisibility(View.VISIBLE);
    }

    protected void loadingFailed() {
        failLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 带参数的fragment跳转
     *
     * @param fragmentName
     * @param fragmentTitle
     * @param parameters
     */
    protected void jumpFull(String fragmentName, String fragmentTitle,
                            Bundle parameters) {
        Intent intent = new Intent(getActivity(), ContainerFullActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("fragmentTitle", fragmentTitle);
        bundle.putBundle("parameters", parameters);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void jump(String fragmentName, String fragmentTitle) {
        if (fragmentName.equals(PhoneChargeFragment.class.getName())) {
            jump(fragmentName, fragmentTitle, true);
            return;
        }
        Intent intent = new Intent(getActivity(), ContainerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("fragmentTitle", fragmentTitle);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void jumpForResult(String fragmentName, String fragmentTitle, Bundle parameters, int requestCode) {
        Intent intent = new Intent(getActivity(), ContainerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("fragmentTitle", fragmentTitle);
        bundle.putBundle("parameters", parameters);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 可隐藏container标题栏的跳转
     *
     * @param fragmentName
     * @param fragmentTitle
     * @param hideTitle
     */
    protected void jump(String fragmentName, String fragmentTitle,
                        boolean hideTitle) {
        Intent intent = new Intent(getActivity(), ContainerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("fragmentTitle", fragmentTitle);
        bundle.putBoolean("hideTitle", hideTitle);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 带参数的fragment跳转
     *
     * @param fragmentName
     * @param fragmentTitle
     * @param parameters
     */
    protected void jump(String fragmentName, String fragmentTitle,
                        Bundle parameters) {
        Intent intent = new Intent(getActivity(), ContainerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("fragmentTitle", fragmentTitle);
        bundle.putBundle("parameters", parameters);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 带参数的fragment跳转
     *
     * @param fragmentName
     * @param fragmentTitle
     * @param hideTitle
     */
    protected void jump(String fragmentName, String fragmentTitle,
                        Bundle parameters, boolean hideTitle) {
        Intent intent = new Intent(getActivity(), ContainerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("fragmentTitle", fragmentTitle);
        bundle.putBundle("parameters", parameters);
        bundle.putBoolean("hideTitle", hideTitle);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 显示商品列表
     *
     * @param fragmentTitle 标题
     * @param value         参数值
     * @param type          参数类型/产品类型
     */
    protected void jumpProductList(String fragmentTitle, String value, int type) {
        Bundle bundle = new Bundle();
        bundle.putString("value", value);
        bundle.putInt("type", type);
        jump(ProductListFragment.class.getName(), fragmentTitle, bundle);
    }

    protected void showOrder(int orderType) {
        Bundle bundle = new Bundle();
        bundle.putInt("orderType", orderType);
        jump(OrderFragment.class.getName(), "我的订单", bundle);
    }

    protected void showProductDetail(String productId, String productName,
                                     int saleType) {
        Bundle bundle = new Bundle();
        bundle.putString("productId", productId);
        bundle.putInt("saleType", saleType);
        jump(ProductDetailFragment.class.getName(), productName, bundle);
    }

    protected void measureScreen() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
    }

    protected void addImageButton(int imageResId, String tag,
                                  OnClickListener onClickListener) {
        getContainerActivity().addImageButton(imageResId, tag, onClickListener);
    }

    protected void addTextButton(String text, OnClickListener onClickListener) {
        getContainerActivity().addTextButton(text, onClickListener);
    }

    /**
     * fragment设置返回按钮点击事件
     *
     * @param onClickListener
     */
    protected void onBackButtonClick(OnClickListener onClickListener) {
        getContainerActivity().onBackButtonClick(onClickListener);
    }

    private ContainerActivity getContainerActivity() {
        ContainerActivity containerActivity = (ContainerActivity) getActivity();
        return containerActivity;
    }

    /**
     * 获取圆角位图的方法
     *
     * @param bitmap 需要转化成圆角的位图
     * @param pixels 圆角的度数，数值越大，圆角越大
     * @return 处理后的圆角位图
     */
    protected Bitmap getRoundCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * @param originalUrl json得到的图片链接
     * @return formatedUrl 切图链接
     * @author Tiger
     */
    protected String getSmallImageUrl(String originalUrl) {
        String formatedUrl = "";
        if (!TextUtils.isEmpty(originalUrl)) {
            formatedUrl = originalUrl;
            if (originalUrl.contains("images.")) {
                formatedUrl = originalUrl.replace("images.", "imageprocess.")
                        + "@!350";
            }
        }
        return formatedUrl;
    }

    /**
     * @param originalUrl json得到的图片链接
     * @return formatedUrl 切图链接
     * @author Tiger
     */
    protected String getBigImageUrl(String originalUrl) {
        String formatedUrl = "";
        if (!TextUtils.isEmpty(originalUrl)) {
            formatedUrl = originalUrl;
            if (originalUrl.contains("images.")) {
                formatedUrl = originalUrl.replace("images.", "imageprocess.")
                        + "@!600";
            }
        }
        return formatedUrl;
    }

    /**
     * 通过接口地址和参数组成唯一字符串，作为用于缓存数据的键
     *
     * @param api_url    接口地址
     * @param parameters 网络请求参数
     * @return 缓存数据的键
     */
    protected String getCacheKey(String api_url, List<NameValuePair> parameters) {
        // TODO Auto-generated method stub
        StringBuilder builder = new StringBuilder();
        builder.append(api_url);
        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                if (i == 0) {
                    builder.append("?");
                } else {
                    builder.append("&");
                }
                builder.append(parameters.get(i).getName());
                builder.append("=");
                builder.append(parameters.get(i).getValue());
            }
        }
        return builder.toString();
    }

    /**
     * 验证手机格式
     */
    protected boolean isPhoneNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            return number.length() == 11;
        }
    }

    /**
     * 判断是否连接网络
     *
     * @return
     */
    protected boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
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

    protected String getHtmlFormated(String baseHtml) {
        String head = "<head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<style>img{max-width: 100%; width:auto; height:auto;}</style>"
                + "</head>";
        return "<html>" + head + "<body>" + baseHtml + "</body></html>";
    }

    protected String getEncodedPassWord(String password) {
        return MD5.GetMD5Code(password + "{xiaozongqi}");
    }

    protected String getSecretCardNuber(String cardNumber) {
        if (cardNumber.length() > 4) {
            return "**** **** **** "
                    + cardNumber.substring(cardNumber.length() - 4,
                    cardNumber.length());
        }
        return "**** **** **** " + cardNumber;
    }

    protected void payMoney(ModelBianminOrderResult bianminOrderResult) {
        ArrayList<String> orderNumbers = new ArrayList<String>();
        orderNumbers.add(bianminOrderResult.getOrderNumber());
        payMoney(orderNumbers, bianminOrderResult.getSellPrice(),
                PayFragment.ORDER_TYPE_BM);
    }

    protected void payMoney(String orderNumber, double totalMoney, int orderType) {
        ArrayList<String> orderNumbers = new ArrayList<String>();
        orderNumbers.add(orderNumber);
        payMoney(orderNumbers, totalMoney, orderType);
    }

    protected void payMoney(ArrayList<String> orderNumbers, double totalMoney,
                            int orderType) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("orderNumbers", orderNumbers);
        bundle.putDouble("totalMoney", totalMoney);
        bundle.putInt("orderType", orderType);
        // bundle.putInt("productCount", productCount);
        jump(PayFragment.class.getName(), "订单支付", bundle);
    }

    /**
     * 易田商城下单成功后支付
     *
     * @param platformOrderResult 下单返回订单的结果
     */
    protected void payMoney(JSONArray platformOrderResult) {
        if (platformOrderResult != null) {
            if (platformOrderResult != null) {
                double payPrice = 0;
                ArrayList<String> orderNumbers = new ArrayList<String>();
                for (int i = 0; i < platformOrderResult.length(); i++) {
                    ModelOrderResult orderResult = new ModelOrderResult(
                            platformOrderResult.optJSONObject(i));
                    orderNumbers.add(orderResult.getOrdernumber());
                    payPrice += orderResult.getZhekouhou();
                    payPrice += orderResult.getFreight();
                }
                if (orderNumbers.size() > 0) {
                    if (payPrice > 0) {
                        payMoney(orderNumbers, payPrice,
                                PayFragment.ORDER_TYPE_YY);
                    }
                }
            }
        }
    }

    protected ContentValues getShareCodeContent(String userAccount,
                                                String userCode) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("codeType", CaptureActivity.CODE_TYPE_SHARE);
        JSONObject dataObject = new JSONObject();
        dataObject.put("userAccount", userAccount);
        dataObject.put("userCode", userCode);
        object.put("data", dataObject);
        ContentValues contentValues = new ContentValues();
        contentValues.put("content", Base64.encodeToString(object.toString()
                .getBytes(), Base64.DEFAULT));
        contentValues.put("imageWidth", ScreenUtil.getScreenWidth() / 2);
        return contentValues;
    }

    protected String getStorePostInfoString(ModelStorePostInfo storePostInfo) {
        return "配送费:" + Parameters.CONSTANT_RMB
                + decimalFormat.format(storePostInfo.getPostage()) + ",店铺购物满"
                + Parameters.CONSTANT_RMB
                + decimalFormat.format(storePostInfo.getHawManyPackages())
                + "免配送费";
    }

    protected String getMoneyDetailString(double goodsMoney, double postFee) {
        return "商品:" + Parameters.CONSTANT_RMB
                + decimalFormat.format(goodsMoney) + "+配送费:"
                + Parameters.CONSTANT_RMB + decimalFormat.format(postFee);
    }

    protected String getDiliverPayString(ModelLocalCar localCar) {
        return localCar.getDiliver().getName() + "、"
                + localCar.getPayment().getName();
    }

}
