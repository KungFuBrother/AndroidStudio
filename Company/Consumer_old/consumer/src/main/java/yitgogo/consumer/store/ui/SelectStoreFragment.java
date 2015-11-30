package yitgogo.consumer.store.ui;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.GetLocalBusinessState;
import yitgogo.consumer.main.ui.MainActivity;
import yitgogo.consumer.view.FragmentTabAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RadioGroup;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class SelectStoreFragment extends BaseNotifyFragment {

	RadioGroup radioGroup;
	List<Fragment> fragments;
	FragmentTabAdapter fragmentTabAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_store_select);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(SelectStoreFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(SelectStoreFragment.class.getName());
	}

	private void init() {
		measureScreen();
		setShowConnectionState(false);
		fragments = new ArrayList<Fragment>();
		fragments.add(new StoreLocateFragment());
		fragments.add(new StoreAreaFragment());
	}

	@Override
	protected void findViews() {
		radioGroup = (RadioGroup) contentView
				.findViewById(R.id.select_store_tabs);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		fragmentTabAdapter = new FragmentTabAdapter(getActivity(), fragments,
				R.id.select_store_content, radioGroup);
	}

	@Override
	protected void registerViews() {
		onBackButtonClick(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getLocalBusinessState();
			}
		});

	}

	private void getLocalBusinessState() {
		GetLocalBusinessState localBusinessState = new GetLocalBusinessState() {

			@Override
			protected void onPostExecute(Boolean showLocalBusiness) {
				Intent intent = new Intent(getActivity(), MainActivity.class);
				intent.putExtra("showLocalBusiness", showLocalBusiness);
				startActivity(intent);
				getActivity().finish();
			}
		};
		localBusinessState.execute();
	}

}
