package yitgogo.consumer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.dtr.zxing.activity.CaptureActivity;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.bianmin.ModelBianminOrderResult;
import yitgogo.consumer.local.model.ModelLocalCar;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.model.ModelOrderResult;
import yitgogo.consumer.order.model.ModelStorePostInfo;
import yitgogo.consumer.order.ui.OrderFragment;
import yitgogo.consumer.product.ui.PlatformProductDetailFragment;
import yitgogo.consumer.product.ui.ProductDetailFragment;
import yitgogo.consumer.product.ui.ProductListFragment;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.MD5;
import yitgogo.consumer.tools.Parameters;

public abstract class BaseFragment extends Fragment {

    public LayoutInflater layoutInflater;
    public DecimalFormat decimalFormat;
    public int pagenum = 0, pagesize = 12;
    public SimpleDateFormat simpleDateFormat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutInflater = LayoutInflater.from(getActivity());
        decimalFormat = new DecimalFormat("0.00");
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 数据初始化
     */
    protected abstract void init();

    protected void findViews(View view) {
    }

    /**
     * 初始化UI
     */
    protected abstract void initViews();

    /**
     * 注册监听UI
     */
    protected abstract void registerViews();

    /**
     * jump ui
     *
     * @param fragment
     */
    protected void jump(String fragment) {
        jump(fragment, "");
    }

    protected void jump(String fragment, String title) {
        jump(fragment, title, null);
    }

    protected void jump(String fragment, String title, Bundle bundle) {
        Intent intent = new Intent(getActivity(), ContainerActivity.class);
        if (!TextUtils.isEmpty(fragment)) {
            intent.putExtra("fragment", fragment);
        }
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra("title", title);
        }
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        startActivity(intent);
    }

    /**
     * 显示商品列表
     *
     * @param title 标题
     * @param value 参数值
     * @param type  参数类型/产品类型
     */
    protected void jumpProductList(String title, String value, int type) {
        Bundle bundle = new Bundle();
        bundle.putString("value", value);
        bundle.putInt("type", type);
        jump(ProductListFragment.class.getName(), title, bundle);
    }

    protected void showProductDetail(String productId, String productName,
                                     int saleType) {
        Bundle bundle = new Bundle();
        bundle.putString("productId", productId);
        if (saleType == CaptureActivity.SALE_TYPE_NONE) {
            jump(PlatformProductDetailFragment.class.getName(), productName, bundle);
            return;
        }
        bundle.putInt("saleType", saleType);
        jump(ProductDetailFragment.class.getName(), productName, bundle);
    }

    private ContainerActivity getContainerActivity() {
        ContainerActivity containerActivity = (ContainerActivity) getActivity();
        return containerActivity;
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

    /**
     * @param originalUrl json得到的图片链接
     * @return 切图链接
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
     * @return 切图链接
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
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        // String telRegex = "[1][3578]\\d{9}";
        // if (TextUtils.isEmpty(number))
        // return false;
        // else
        // return number.matches(telRegex);
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
                int productCount = 0;
                ArrayList<String> orderNumbers = new ArrayList<String>();
                for (int i = 0; i < platformOrderResult.length(); i++) {
                    ModelOrderResult orderResult = new ModelOrderResult(
                            platformOrderResult.optJSONObject(i));
                    orderNumbers.add(orderResult.getOrdernumber());
                    payPrice += orderResult.getZhekouhou();
                    // productCount+= orderResult.get
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

    protected void showOrder(int orderType) {
        Bundle bundle = new Bundle();
        bundle.putInt("orderType", orderType);
        jump(OrderFragment.class.getName(), "我的订单", bundle);
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

    protected String getSecretPhone(String phone) {
        int length = phone.length();
        if (length > 3) {
            String string = "";
            if (length < 8) {
                string = phone.substring(0, 3) + "****";
            } else {
                string = phone.substring(0, 3) + "****"
                        + phone.substring(7, length);
            }
            return string;
        }
        return "***";
    }

    protected String getSecretCardNuber(String cardNumber) {
        if (cardNumber.length() > 4) {
            return "**** **** **** "
                    + cardNumber.substring(cardNumber.length() - 4,
                    cardNumber.length());
        }
        return "**** **** **** " + cardNumber;
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
        contentValues.put("imageWidth", ApplicationTool.getScreenWidth() / 2);
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
