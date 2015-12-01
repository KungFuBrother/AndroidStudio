package yitgogo.consumer.suning.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.tools.Parameters;

/**
 * Created by Tiger on 2015-12-01.
 */
public class SuningOrderReturnAfterReceivedFragment extends BaseNotifyFragment {

    String productName = "";
    double productPrice = 0;

    TextView productNameTextView;
    TextView productPriceTextView;
    TextView phoneTextView;
    TextView ruleTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_suning_order_return_after_received);
        init();
        findViews();
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("productName")) {
                productName = bundle.getString("productName");
            }
            if (bundle.containsKey("productPrice")) {
                productPrice = bundle.getDouble("productPrice");
            }
        }
    }

    @Override
    protected void findViews() {
        productNameTextView = (TextView) contentView.findViewById(R.id.fragment_suning_return_product_name);
        productPriceTextView = (TextView) contentView.findViewById(R.id.fragment_suning_return_product_price);
        phoneTextView = (TextView) contentView.findViewById(R.id.fragment_suning_return_phone);
        ruleTextView = (TextView) contentView.findViewById(R.id.fragment_suning_return_rule);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        productNameTextView.setText(productName);
        productPriceTextView.setText(Parameters.CONSTANT_RMB + decimalFormat.format(productPrice));
    }

    @Override
    protected void registerViews() {
        phoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:4008156516");
                intent.setData(data);
                startActivity(intent);
            }
        });
        ruleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump(SuningOrderReturnRuleFragment.class.getName(), "退换货规则");
            }
        });
    }
}
