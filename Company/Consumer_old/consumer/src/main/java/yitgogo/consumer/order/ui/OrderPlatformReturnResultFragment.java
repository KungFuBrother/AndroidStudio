package yitgogo.consumer.order.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.NetUtil;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.view.Notify;

/**
 * Created by Tiger on 2015-11-26.
 */
public class OrderPlatformReturnResultFragment extends BaseNotifyFragment {

    TextView totalMoneyTextView, returnMoneyTextView;
    String productInfoId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_platform_order_return_result);
        init();
        findViews();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetReturnResult().execute();
    }

    @Override
    protected void findViews() {
        totalMoneyTextView = (TextView) contentView.findViewById(R.id.fragment_platform_return_result_total);
        returnMoneyTextView = (TextView) contentView.findViewById(R.id.fragment_platform_return_result_return);
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("productInfoId")) {
                productInfoId = bundle.getString("productInfoId");
            }
        }
    }

    @Override
    protected void initViews() {
        totalMoneyTextView.setText("总计退款金额");
        returnMoneyTextView.setText("(含补偿金额￥10.00)");
    }

    @Override
    protected void registerViews() {
    }

    //{"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],"totalCount":1,"dataMap":{"damages":10.0,"totalMoney":"410.0","responsibility":2},"object":null}
    class GetReturnResult extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... voids) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("productInfoId", productInfoId));
            return NetUtil.getInstance().postWithCookie(API.API_ORDER_RETURN_RESULT, nameValuePairs);
        }

        @Override
        protected void onPostExecute(String s) {
            hideLoading();
            if (!TextUtils.isEmpty(s)) {
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONObject dataMap = object.optJSONObject("dataMap");
                        if (dataMap != null) {
                            double damages = dataMap.optDouble("damages");
                            double totalMoney = dataMap.optDouble("totalMoney");
                            int responsibility = dataMap.optInt("responsibility");
                            totalMoneyTextView.setText("总计退款金额" + Parameters.CONSTANT_RMB + decimalFormat.format(totalMoney));
                            StringBuilder builder = new StringBuilder();
                            builder.append("(");
                            if (responsibility == 2) {
                                builder.append("含补偿金额");
                            } else {
                                builder.append("扣取补偿金额");
                            }
                            builder.append(Parameters.CONSTANT_RMB);
                            builder.append(decimalFormat.format(damages));
                            builder.append(")");
                            returnMoneyTextView.setText(builder.toString());
                            return;
                        }
                    }
                    Notify.show(object.optString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
