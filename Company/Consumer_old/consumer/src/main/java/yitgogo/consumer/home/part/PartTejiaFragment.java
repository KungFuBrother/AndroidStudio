package yitgogo.consumer.home.part;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNormalFragment;
import yitgogo.consumer.home.model.ModelSaleTejia;
import yitgogo.consumer.home.model.ModelSaleTejiaProduct;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dtr.zxing.activity.CaptureActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;

public class PartTejiaFragment extends BaseNormalFragment {

	static PartTejiaFragment tejiaFragment;
	LinearLayout imageLayout;
	ModelSaleTejia saleTejia;

	List<ImageView> tejiaImagesA, tejiaImagesB, tejiaImagesC;

	List<ModelSaleTejiaProduct> pagedSaleProductsA, pagedSaleProductsB,
			pagedSaleProductsC;

	public static PartTejiaFragment getTejiaFragment() {
		if (tejiaFragment == null) {
			tejiaFragment = new PartTejiaFragment();
		}
		return tejiaFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		measureScreen();
		saleTejia = new ModelSaleTejia();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_part_tejia, null);
		findViews(view);
		return view;
	}

	@Override
	protected void findViews(View view) {
		tejiaImagesA = new ArrayList<ImageView>();
		tejiaImagesB = new ArrayList<ImageView>();
		tejiaImagesC = new ArrayList<ImageView>();
		imageLayout = (LinearLayout) view.findViewById(R.id.home_tejia_layout);
		tejiaImagesA.add((ImageView) view.findViewById(R.id.tejia_1));
		tejiaImagesB.add((ImageView) view.findViewById(R.id.tejia_2));
		tejiaImagesB.add((ImageView) view.findViewById(R.id.tejia_3));
		tejiaImagesC.add((ImageView) view.findViewById(R.id.tejia_4));
		tejiaImagesC.add((ImageView) view.findViewById(R.id.tejia_5));
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				screenWidth, screenWidth / 2);
		imageLayout.setLayoutParams(layoutParams);
		registerViews();
	}

	public void initViews() {
		if (saleTejia.isEmpty()) {
			return;
		}
		pagedSaleProductsA = saleTejia.getGroupA().getNextPageTejiaProducts();
		pagedSaleProductsB = saleTejia.getGroupB().getNextPageTejiaProducts();
		pagedSaleProductsC = saleTejia.getGroupC().getNextPageTejiaProducts();
		for (int i = 0; i < tejiaImagesA.size(); i++) {
			tejiaImagesA.get(i).setImageResource(0);
			if (pagedSaleProductsA.size() > i) {
				ImageLoader.getInstance().displayImage(
						getSmallImageUrl(pagedSaleProductsA.get(i).getImg()),
						tejiaImagesA.get(i));
			}
		}
		for (int i = 0; i < tejiaImagesB.size(); i++) {
			tejiaImagesB.get(i).setImageResource(0);
			if (pagedSaleProductsB.size() > i) {
				ImageLoader.getInstance().displayImage(
						getSmallImageUrl(pagedSaleProductsB.get(i).getImg()),
						tejiaImagesB.get(i));
			}
		}
		for (int i = 0; i < tejiaImagesC.size(); i++) {
			tejiaImagesC.get(i).setImageResource(0);
			if (pagedSaleProductsC.size() > i) {
				ImageLoader.getInstance().displayImage(
						getSmallImageUrl(pagedSaleProductsC.get(i).getImg()),
						tejiaImagesC.get(i));
			}
		}
	}

	@Override
	protected void registerViews() {
		for (int i = 0; i < tejiaImagesA.size(); i++) {
			final int index = i;
			tejiaImagesA.get(i).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String productId = pagedSaleProductsA.get(index)
							.getProductId();
					if (productId.length() > 0) {
						showProductDetail(productId, "特价商品",
								CaptureActivity.SALE_TYPE_TEJIA);
					}
				}
			});
		}
		for (int i = 0; i < tejiaImagesB.size(); i++) {
			final int index = i;
			tejiaImagesB.get(i).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String productId = pagedSaleProductsB.get(index)
							.getProductId();
					if (productId.length() > 0) {
						showProductDetail(productId, "特价商品",
								CaptureActivity.SALE_TYPE_TEJIA);
					}
				}
			});
		}
		for (int i = 0; i < tejiaImagesC.size(); i++) {
			final int index = i;
			tejiaImagesC.get(i).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					String productId = pagedSaleProductsC.get(index)
							.getProductId();
					if (productId.length() > 0) {
						showProductDetail(productId, "特价商品",
								CaptureActivity.SALE_TYPE_TEJIA);
					}
				}
			});
		}
	}

	public void refresh(String result) {
		if (result.length() > 0) {
			JSONObject object;
			try {
				object = new JSONObject(result);
				if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
					JSONObject dataMap = object.optJSONObject("dataMap");
					saleTejia = new ModelSaleTejia(dataMap);
					initViews();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (saleTejia.isEmpty()) {
			getView().setVisibility(View.GONE);
		} else {
			getView().setVisibility(View.VISIBLE);
		}
	}

}
