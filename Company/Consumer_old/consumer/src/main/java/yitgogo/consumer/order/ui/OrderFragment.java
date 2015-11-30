package yitgogo.consumer.order.ui;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNormalFragment;
import yitgogo.consumer.bianmin.order.BianminOrderFragment;
import yitgogo.consumer.local.ui.LocalGoodsOrderFragment;
import yitgogo.consumer.local.ui.LocalServiceOrderFragment;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.suning.ui.SuningOrderListFragment;
import yitgogo.consumer.view.FragmentTabAdapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class OrderFragment extends BaseNormalFragment {

    RadioGroup radioGroup;
    List<Fragment> fragments;
    FragmentTabAdapter fragmentTabAdapter;
    int orderType = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(OrderFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(OrderFragment.class.getName());
    }

    private void init() {
        measureScreen();
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("orderType")) {
                int i = bundle.getInt("orderType");
                switch (i) {
                    case PayFragment.ORDER_TYPE_YY:
                        orderType = 0;
                        break;

                    case PayFragment.ORDER_TYPE_YD:
                        orderType = 0;
                        break;

                    case PayFragment.ORDER_TYPE_SN:
                        orderType = 1;
                        break;

                    case PayFragment.ORDER_TYPE_LP:
                        orderType = 2;
                        break;

                    case PayFragment.ORDER_TYPE_LS:
                        orderType = 3;
                        break;

                    case PayFragment.ORDER_TYPE_BM:
                        orderType = 4;
                        break;

                    default:
                        orderType = 0;
                        break;
                }
            }
        }
        fragments = new ArrayList<>();
        fragments.add(new OrderPlatformFragment());
        fragments.add(new SuningOrderListFragment());
        fragments.add(new LocalGoodsOrderFragment());
        fragments.add(new LocalServiceOrderFragment());
        fragments.add(new BianminOrderFragment());
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, null);
        findViews(view);
        return view;
    }

    @Override
    protected void findViews(View view) {
        radioGroup = (RadioGroup) view.findViewById(R.id.order_tabs);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        fragmentTabAdapter = new FragmentTabAdapter(getActivity(), fragments,
                R.id.order_content, radioGroup, orderType);
    }

    @Override
    protected void registerViews() {
    }

}
