package yitgogo.consumer.home.part;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNormalFragment;
import yitgogo.consumer.local.model.ModelLocalService;
import yitgogo.consumer.local.ui.LocalServiceDetailFragment;
import yitgogo.consumer.local.ui.LoveFreshFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;

public class PartFreshFragment extends BaseNormalFragment {

	static PartFreshFragment freshFragment;
	LinearLayout moreButton, imageLayout;
	int freshImageIds[] = { R.id.love_fresh_1, R.id.love_fresh_2,
			R.id.love_fresh_3, R.id.love_fresh_4, R.id.love_fresh_5 };
	List<ImageView> freshImages = new ArrayList<ImageView>();
	List<ModelLocalService> freshProducts;

	public static PartFreshFragment getFreshFragment() {
		if (freshFragment == null) {
			freshFragment = new PartFreshFragment();
		}
		return freshFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		measureScreen();
		freshProducts = new ArrayList<ModelLocalService>();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_part_love_fresh, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		moreButton = (LinearLayout) view.findViewById(R.id.part_fresh_more);
		imageLayout = (LinearLayout) view.findViewById(R.id.part_fresh_image);
		for (int i = 0; i < freshImageIds.length; i++) {
			freshImages.add((ImageView) view.findViewById(freshImageIds[i]));
		}
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				screenWidth, screenWidth / 2);
		imageLayout.setLayoutParams(layoutParams);
	}

	@Override
	protected void registerViews() {
		moreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump(LoveFreshFragment.class.getName(), "爱新鲜");
			}
		});
		for (int i = 0; i < freshImages.size(); i++) {
			final int position = i;
			freshImages.get(i).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (freshProducts.size() > position) {
						Bundle bundle = new Bundle();
						bundle.putString("productId",
								freshProducts.get(position).getId());
						jump(LocalServiceDetailFragment.class.getName(),
								freshProducts.get(position).getProductName(),
								bundle);
					}
				}
			});
		}
	}

	private void showFreshData() {
		for (int i = 0; i < freshImages.size(); i++) {
			if (freshProducts.size() > i) {
				ImageLoader.getInstance().displayImage(
						getSmallImageUrl(freshProducts.get(i).getImg()),
						freshImages.get(i));
			}
		}
	}

	public void refresh(String result) {
		freshProducts.clear();
		if (result.length() > 0) {
			JSONObject object;
			try {
				object = new JSONObject(result);
				if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONArray array = object.optJSONArray("dataList");
					if (array != null) {
						for (int i = 0; i < array.length(); i++) {
							JSONObject jsonObject = array.optJSONObject(i);
							if (jsonObject != null) {
								freshProducts.add(new ModelLocalService(
										jsonObject));
							}
						}
						showFreshData();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (freshProducts.isEmpty()) {
			getView().setVisibility(View.GONE);
		} else {
			getView().setVisibility(View.VISIBLE);
		}
	}

}
