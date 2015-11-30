package yitgogo.consumer.local.ui;

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
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class LocalStoreDetailFragment extends BaseNormalFragment {

	LinearLayout goBackButton;
	RadioGroup radioGroup;
	TextView nameTextView;
	List<Fragment> fragments;
	FragmentTabAdapter fragmentTabAdapter;

	String storeId = "", storeName = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(LocalStoreDetailFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(LocalStoreDetailFragment.class.getName());
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_local_store_detail, null);
		findViews(view);
		return view;
	}

	private void init() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("storeId")) {
				storeId = bundle.getString("storeId");
			}
			if (bundle.containsKey("storeName")) {
				storeName = bundle.getString("storeName");
			}
		}
		fragments = new ArrayList<Fragment>();
		fragments.add(new LocalStoreGoodsFragment(storeId));
		fragments.add(new LocalStoreServiceFragment(storeId));
	}

	@Override
	protected void findViews(View view) {
		goBackButton = (LinearLayout) view
				.findViewById(R.id.local_store_detail_back);
		radioGroup = (RadioGroup) view.findViewById(R.id.local_business_tab);
		nameTextView = (TextView) view
				.findViewById(R.id.local_store_detail_name);
		initViews();
		registerViews();
	}

	@Override
	protected void registerViews() {
		goBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				getActivity().finish();
			}
		});
	}

	@Override
	protected void initViews() {
		nameTextView.setText("正在浏览 " + storeName);
		fragmentTabAdapter = new FragmentTabAdapter(getActivity(), fragments,
				R.id.local_store_detail_content, radioGroup);
	}

}
