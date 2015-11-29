package yitgogo.smart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitgogo.smart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import yitgogo.smart.bianmin.ModelBianminOrderResult;
import yitgogo.smart.order.model.ModelOrderResult;
import yitgogo.smart.order.model.ModelStorePostInfo;
import yitgogo.smart.order.ui.PayFragment;
import yitgogo.smart.product.ui.ProductDetailFragment;
import yitgogo.smart.tools.MD5;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.OnQrEncodeListener;
import yitgogo.smart.tools.Parameters;

/**
 * 有通知功能的fragment
 *
 * @author Tiger
 */
public class BaseNotifyFragment extends Fragment {

    public LayoutInflater layoutInflater;
    public int screenWidth = 0, screenHeight = 0;
    public DecimalFormat decimalFormat;
    public SimpleDateFormat simpleDateFormat;
    public ImageLoader imageLoader;

    public View contentView;

    private LinearLayout emptyLayout;
    private TextView emptyText;

    private LinearLayout loadingLayout;
    private TextView loadingText;

    private FrameLayout topLayout;

    private FrameLayout contentLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        topLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        topLayout.setVisibility(View.VISIBLE);
        super.onPause();
    }

    private void init() {
        layoutInflater = LayoutInflater.from(getActivity());
        imageLoader = ImageLoader.getInstance();
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

    private void findView(View view) {
        contentLayout = (FrameLayout) view
                .findViewById(R.id.base_fragment_content);
        topLayout = (FrameLayout) view.findViewById(R.id.base_fragment_top);
        emptyLayout = (LinearLayout) view
                .findViewById(R.id.base_fragment_empty);
        loadingLayout = (LinearLayout) view
                .findViewById(R.id.base_fragment_loading);
        emptyText = (TextView) view.findViewById(R.id.base_fragment_empty_text);
        loadingText = (TextView) view
                .findViewById(R.id.base_fragment_loading_text);
        showContentView();
    }

    protected void findViews() {

    }

    protected void initViews() {

    }

    protected void registerViews() {
    }

    protected void jump(String fragmentName, String title, Bundle parameters) {
        Intent intent = new Intent(getActivity(), ContainerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("title", title);
        if (parameters != null) {
            bundle.putBundle("parameters", parameters);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void openWindow(String fragmentName, String title) {
        Intent intent = new Intent(getActivity(), DialogActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("title", title);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void openWindow(String fragmentName, String title,
                              Bundle parameters) {
        Intent intent = new Intent(getActivity(), DialogActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentName", fragmentName);
        bundle.putString("title", title);
        bundle.putBundle("parameters", parameters);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void showProductDetail(String productId, int saleType) {
        Bundle bundle = new Bundle();
        bundle.putString("productId", productId);
        bundle.putInt("saleType", saleType);
        openWindow(ProductDetailFragment.class.getName(), "商品详情", bundle);
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

    protected void showLoading() {
        loadingText.setText("请稍候...");
        emptyLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    protected void showLoading(String text) {
        loadingText.setText(text);
        emptyLayout.setVisibility(View.GONE);
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

    protected void measureScreen() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
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
        PayFragment payFragment = new PayFragment() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getActivity().finish();
                super.onDismiss(dialog);
            }
        };
        payFragment.setArguments(bundle);
        payFragment.show(getFragmentManager(), null);
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
                }
                payMoney(orderNumbers, payPrice, PayFragment.ORDER_TYPE_YY);
            }
        }
    }

    protected void payMoney(ModelBianminOrderResult bianminOrderResult) {
        ArrayList<String> orderNumbers = new ArrayList<String>();
        orderNumbers.add(bianminOrderResult.getOrderNumber());
        payMoney(orderNumbers, bianminOrderResult.getSellPrice(),
                PayFragment.ORDER_TYPE_BM);
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

    // protected String getSmallImageUrl(String originalUrl) {
    // if (originalUrl.length() > 0) {
    // if (originalUrl.contains(".")) {
    //
    // String formation = originalUrl.substring(
    // originalUrl.lastIndexOf("."), originalUrl.length());
    // StringBuilder imgBuilder = new StringBuilder(originalUrl);
    // return imgBuilder.replace(originalUrl.lastIndexOf("."),
    // originalUrl.length(), "_600" + formation).toString();
    // }
    // }
    // return originalUrl;
    // }

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
     * 生成产品二维码数据
     *
     * @param productId     产品id
     * @param productNumber 产品编号
     * @param productName   产品名称
     * @param productType   产品类型
     * @param saleType      促销类型
     * @return 产品二维码数据
     * @throws JSONException
     */
    protected void qrEncodeProduct(String productId, String productNumber,
                                   String productName, int productType, int saleType,
                                   OnQrEncodeListener onQrEncodeListener) {
        try {
            JSONObject object = new JSONObject();
            object.put("codeType", 1);
            JSONObject dataObject = new JSONObject();
            dataObject.put("productType", productType);
            dataObject.put("productId", productId);
            dataObject.put("productNumber", productNumber);
            dataObject.put("productName", "商品详情");
            dataObject.put("saleType", saleType);
            object.put("data", dataObject);
            MissionController.startQrEncodeMission(getActivity(), Base64
                    .encodeToString(object.toString().getBytes(),
                            Base64.DEFAULT), 120, onQrEncodeListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加标题栏文字按钮
     *
     * @param imageResId
     * @param tag
     * @param onClickListener
     */
    public void addTextButton(String text, OnClickListener onClickListener) {
        getDialogActivity().addTextButton(text, onClickListener);
    }

    public void removeAllButtons() {
        getDialogActivity().removeAllButtons();
    }

    private DialogActivity getDialogActivity() {
        DialogActivity dialogActivity = (DialogActivity) getActivity();
        return dialogActivity;
    }

    protected String getStorePostInfoString(ModelStorePostInfo storePostInfo) {
        return "配送费:" + Parameters.CONSTANT_RMB
                + decimalFormat.format(storePostInfo.getPostage()) + ",店铺购物满"
                + Parameters.CONSTANT_RMB
                + decimalFormat.format(storePostInfo.getHawManyPackages())
                + "免配送费";
    }

    protected String getMoneyDetailString(double goodsMoney, double postFee) {
        return "(商品:" + Parameters.CONSTANT_RMB
                + decimalFormat.format(goodsMoney) + "+配送费:"
                + Parameters.CONSTANT_RMB + decimalFormat.format(postFee) + ")";
    }

}
