package yitgogo.consumer.bianmin.phoneCharge.ui;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNormalFragment;
import yitgogo.consumer.view.FragmentTabAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class PhoneChargeFragment extends BaseNormalFragment {

	LinearLayout backButton;
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
		MobclickAgent.onPageStart(PhoneChargeFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(PhoneChargeFragment.class.getName());
	}

	private void init() {
		fragments = new ArrayList<Fragment>();
		fragments.add(new PhoneChargeFastFragment());
		fragments.add(new PhoneChargeSlowFragment());
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bianmin_phone_charge,
				null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		backButton = (LinearLayout) view.findViewById(R.id.title_back);
		radioGroup = (RadioGroup) view.findViewById(R.id.phone_charge_tabs);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		fragmentTabAdapter = new FragmentTabAdapter(getActivity(), fragments,
				R.id.phone_charge_content, radioGroup);
	}

	@Override
	protected void registerViews() {
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
	}
}
