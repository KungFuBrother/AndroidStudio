package yitgogo.consumer.local.ui;

import yitgogo.consumer.BaseNormalFragment;
import yitgogo.consumer.home.model.ModelLocalStore;
import yitgogo.consumer.store.model.ModelStoreSelected;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class StorePartInfoFragment extends BaseNormalFragment {

	LinearLayout enterButton;
	ImageView imageView;
	TextView nameTextView, addressTextView, phoneTextView;

	ModelLocalStore localStore = new ModelLocalStore();
	ModelStoreSelected storeSelected = new ModelStoreSelected();
	int type = 0;

	final static int TYPE_LOCAL = 0;
	final static int TYPE_PRODUCT = 1;

	public StorePartInfoFragment(ModelLocalStore localStore) {
		this.localStore = localStore;
		type = TYPE_LOCAL;
	}

	public StorePartInfoFragment(ModelStoreSelected storeSelected) {
		this.storeSelected = storeSelected;
		type = TYPE_PRODUCT;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(StorePartInfoFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(StorePartInfoFragment.class.getName());
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.store_part_info, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		enterButton = (LinearLayout) view.findViewById(R.id.store_info_enter);
		imageView = (ImageView) view.findViewById(R.id.store_info_image);
		nameTextView = (TextView) view.findViewById(R.id.store_info_name);
		addressTextView = (TextView) view.findViewById(R.id.store_info_address);
		phoneTextView = (TextView) view.findViewById(R.id.store_info_phone);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		switch (type) {
		case TYPE_LOCAL:
			ImageLoader.getInstance().displayImage(localStore.getImg(),
					imageView);
			nameTextView.setText(localStore.getShopname());
			addressTextView.setText(localStore.getAddress());
			// phoneTextView.setText(localStore.get);
			break;

		case TYPE_PRODUCT:
			ImageLoader.getInstance().displayImage(storeSelected.getImghead(),
					imageView);
			nameTextView.setText(storeSelected.getServicename());
			addressTextView.setText(storeSelected.getServiceaddress());
			phoneTextView.setText(storeSelected.getContactphone());
			break;
		default:
			break;
		}
	}

	@Override
	protected void registerViews() {
		enterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}

}
