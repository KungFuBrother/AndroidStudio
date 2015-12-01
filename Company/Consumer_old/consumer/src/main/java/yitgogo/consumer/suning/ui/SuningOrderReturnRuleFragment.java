package yitgogo.consumer.suning.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.product.ui.WebFragment;

/**
 * Created by Tiger on 2015-12-01.
 */
public class SuningOrderReturnRuleFragment extends BaseNotifyFragment {

    TextView ruleTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_suning_order_return_rule);
        findViews();
    }

    @Override
    protected void findViews() {
        ruleTextView = (TextView) contentView.findViewById(R.id.fragment_suning_return_rule);
        registerViews();
    }

    @Override
    protected void registerViews() {
        ruleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", WebFragment.TYPE_URL);
                bundle.putString("url", "http://help.suning.com/page/id-205.htm");
                jump(WebFragment.class.getName(), "退换货规则", bundle);
            }
        });
    }
}
