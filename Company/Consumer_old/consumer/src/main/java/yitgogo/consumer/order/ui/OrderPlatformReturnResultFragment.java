package yitgogo.consumer.order.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;

import yitgogo.consumer.BaseNotifyFragment;

/**
 * Created by Tiger on 2015-11-26.
 */
public class OrderPlatformReturnResultFragment extends BaseNotifyFragment {

    TextView totalMoneyTextView, returnMoneyTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_platform_order_return_result);
        findViews();
    }

    @Override
    protected void findViews() {
        totalMoneyTextView = (TextView) contentView.findViewById(R.id.fragment_platform_return_result_total);
        returnMoneyTextView = (TextView) contentView.findViewById(R.id.fragment_platform_return_result_return);
    }

    private void init() {

    }

    @Override
    protected void initViews() {
        totalMoneyTextView.setText("总计退款金额");
        returnMoneyTextView.setText("(含补偿金额￥10.00)");
    }

    @Override
    protected void registerViews() {
    }
}
