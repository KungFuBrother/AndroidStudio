package yitgogo.smart.product.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.BaseNormalFragment;
import yitgogo.smart.view.FragmentTabAdapter;

public class ShoppingCarFragment extends BaseNormalFragment {

    RadioGroup radioGroup;
    List<Fragment> fragments;
    FragmentTabAdapter fragmentTabAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ShoppingCarFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ShoppingCarFragment.class.getName());
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = layoutInflater.inflate(
                R.layout.dialog_fragment_shopping_car, null);
        findViews(view);
        return view;
    }

    private void init() {
        fragments = new ArrayList<Fragment>();
        fragments.add(new ShoppingCarPlatformFragment());
        fragments.add(new yitgogo.smart.suning.ui.ShoppingCarFragment());
        fragments.add(new ShoppingCarLocalFragment());
    }

    @Override
    protected void findViews(View view) {
        radioGroup = (RadioGroup) view.findViewById(R.id.local_business_tab);
        initViews();
    }

    @Override
    protected void initViews() {
        fragmentTabAdapter = new FragmentTabAdapter(getActivity(), fragments,
                R.id.car_content, radioGroup);
    }

}
