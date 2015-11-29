package yitgogo.consumer.order.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.order.model.ModelProductOrder;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;

/**
 * Created by Tiger on 2015-11-26.
 */
public class OrderPlatformReturnFragment extends BaseNetworkFragment {

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
        productNameTextView = (TextView) findViewById(R.id.fragment_platform_return_product_name);
        productPriceTextView = (TextView) findViewById(R.id.fragment_platform_return_product_price);
        contactPhoneTextView = (TextView) findViewById(R.id.fragment_platform_return_phone);
        reasonEditText = (EditText) findViewById(R.id.fragment_platform_return_reason);
        commitButton = (Button) findViewById(R.id.fragment_platform_return_commit);
        initViews();
        registerViews();
    }

    @Override
    protected void init() {
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
                    returnProduct();
                } else {
                    ApplicationTool.showToast("请填写退货原因");
                }
            }
        });
    }

    //{"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],"totalCount":1,"dataMap":{},"object":null}
    private void returnProduct() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("saleId", "5"));
        requestParams.add(new RequestParam("supplierId", "5"));
        requestParams.add(new RequestParam("orderNumber", "YT3438821788"));
        requestParams.add(new RequestParam("productInfo", productOrder.getId()));
        requestParams.add(new RequestParam("reason", "测试测试测试退货"));
        post(API.API_ORDER_RETURN, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            jump(OrderPlatformReturnCommitedFragment.class.getName(), "申请退货");
                            getActivity().finish();
                        } else {
                            ApplicationTool.showToast(object.optString("message"));
                        }
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ApplicationTool.showToast("申请退货失败");
            }
        });
    }

}
