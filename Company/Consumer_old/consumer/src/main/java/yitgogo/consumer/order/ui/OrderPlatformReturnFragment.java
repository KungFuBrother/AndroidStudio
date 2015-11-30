package yitgogo.consumer.order.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.order.model.ModelProductOrder;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.NetUtil;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.view.Notify;

/**
 * Created by Tiger on 2015-11-26.
 */
public class OrderPlatformReturnFragment extends BaseNotifyFragment {

    TextView productNameTextView, productPriceTextView, contactPhoneTextView;
    EditText reasonEditText;
    Button commitButton;
    ModelProductOrder productOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_platform_order_return);
        init();
        findViews();
    }

    @Override
    protected void findViews() {
        productNameTextView = (TextView) contentView.findViewById(R.id.fragment_platform_return_product_name);
        productPriceTextView = (TextView) contentView.findViewById(R.id.fragment_platform_return_product_price);
        contactPhoneTextView = (TextView) contentView.findViewById(R.id.fragment_platform_return_phone);
        reasonEditText = (EditText) contentView.findViewById(R.id.fragment_platform_return_reason);
        commitButton = (Button) contentView.findViewById(R.id.fragment_platform_return_commit);
        initViews();
        registerViews();
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("object")) {
                try {
                    productOrder = new ModelProductOrder(new JSONObject(bundle.getString("object")));
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            productOrder = new ModelProductOrder(null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initViews() {
        productNameTextView.setText(productOrder.getProductName());
        productPriceTextView.setText(Parameters.CONSTANT_RMB + decimalFormat.format(productOrder.getUnitSellPrice()));
    }

    @Override
    protected void registerViews() {
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(reasonEditText.getText().toString())) {
                    new ReturnProduct().execute();
                } else {
                    Notify.show("请填写退货原因");
                }
            }
        });
    }

    class ReturnProduct extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("saleId", "5"));
            nameValuePairs.add(new BasicNameValuePair("supplierId", "5"));
            nameValuePairs.add(new BasicNameValuePair("orderNumber", "YT3438821788"));
            nameValuePairs.add(new BasicNameValuePair("productInfo", productOrder.getId()));
            nameValuePairs.add(new BasicNameValuePair("reason", "测试测试测试退货"));
            return NetUtil.getInstance().postWithoutCookie(API.API_ORDER_RETURN, nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        jump(OrderPlatformReturnCommitedFragment.class.getName(), "申请退货");
                        getActivity().finish();
                    } else {
                        Notify.show(object.optString("message"));
                    }
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Notify.show("申请退货失败");
        }
    }

}
