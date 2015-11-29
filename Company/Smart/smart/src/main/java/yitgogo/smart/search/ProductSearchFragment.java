package yitgogo.smart.search;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.view.Notify;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

public class ProductSearchFragment extends BaseNotifyFragment {

	RadioGroup searchTypeGroup;
	EditText searchEditText;
	ImageView searchButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_search);
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(ProductSearchFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(ProductSearchFragment.class.getName());
	}

	@Override
	protected void findViews() {
		searchTypeGroup = (RadioGroup) contentView
				.findViewById(R.id.search_type);
		searchEditText = (EditText) contentView.findViewById(R.id.search_edit);
		searchButton = (ImageView) contentView.findViewById(R.id.search_search);
		registerViews();
	}

	@Override
	protected void registerViews() {
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search();
			}
		});
	}

	private void search() {
		if (searchEditText.length() == 0) {
			Notify.show("请输入商品名称");
		} else {
			String searchName = searchEditText.getText().toString();
			Bundle bundle = new Bundle();
			bundle.putString("searchName", searchName);
			switch (searchTypeGroup.getCheckedRadioButtonId()) {
			case R.id.search_type_yitgogo:
				openWindow(ProductSearchPlatformFragment.class.getName(),
						"搜索\"" + searchName + "\"", bundle);
				break;
			case R.id.search_type_local_goods:
				openWindow(ProductSearchLocalGoodsFragment.class.getName(),
						"搜索\"" + searchName + "\"", bundle);
				break;
			case R.id.search_type_local_service:
				openWindow(ProductSearchLocaServiceFragment.class.getName(),
						"搜索\"" + searchName + "\"", bundle);
				break;
			default:
				break;
			}
		}
	}

}
