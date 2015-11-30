package yitgogo.consumer.main.ui;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNormalFragment;
import yitgogo.consumer.local.ui.LocalGoodsFragment;
import yitgogo.consumer.local.ui.LocalServiceFragment;
import yitgogo.consumer.store.model.Store;
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

public class LocalBusinessFragment extends BaseNormalFragment {

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
		MobclickAgent.onPageStart(LocalBusinessFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(LocalBusinessFragment.class.getName());
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_local_business, null);
		findViews(view);
		return view;
	}

	private void init() {
		fragments = new ArrayList<Fragment>();
		fragments.add(new LocalGoodsFragment());
		fragments.add(new LocalServiceFragment());
	}

	@Override
	protected void findViews(View view) {
		radioGroup = (RadioGroup) view.findViewById(R.id.local_business_tab);
		initViews();
	}

	@Override
	protected void initViews() {
		fragmentTabAdapter = new FragmentTabAdapter(getActivity(), fragments,
				R.id.local_business_content_fragment, radioGroup);
	}

}
