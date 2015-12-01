package yitgogo.consumer.home.part;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNormalFragment;
import yitgogo.consumer.local.model.ModelLocalGoods;
import yitgogo.consumer.local.model.ModelLocalService;
import yitgogo.consumer.local.ui.LocalGoodsDetailFragment;
import yitgogo.consumer.local.ui.LocalServiceDetailFragment;
import yitgogo.consumer.main.ui.MainActivity;

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

public class PartLocalBusinessFragment extends BaseNormalFragment {

    static PartLocalBusinessFragment localBusinessFragment;
    LinearLayout moreButton, imageLayout;

    int localProductImageIds[] = {R.id.local_product_1, R.id.local_product_2,
            R.id.local_product_3};
    int localServiceImageIds[] = {R.id.local_service_1, R.id.local_service_2,
            R.id.local_service_3};
    List<ImageView> localProductImages = new ArrayList<ImageView>();
    List<ImageView> localServiceImages = new ArrayList<ImageView>();

    List<ModelLocalGoods> localGoods = new ArrayList<ModelLocalGoods>();
    List<ModelLocalService> localServices = new ArrayList<ModelLocalService>();

    public static PartLocalBusinessFragment getLocalBusinessFragment() {
        if (localBusinessFragment == null) {
            localBusinessFragment = new PartLocalBusinessFragment();
        }
        return localBusinessFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        measureScreen();
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_part_local, null);
        findViews(view);
        return view;
    }

    @Override
    protected void findViews(View view) {
        moreButton = (LinearLayout) view.findViewById(R.id.part_local_more);
        imageLayout = (LinearLayout) view.findViewById(R.id.part_local_image);
        for (int i = 0; i < localProductImageIds.length; i++) {
            localProductImages.add((ImageView) view
                    .findViewById(localProductImageIds[i]));
        }
        for (int i = 0; i < localServiceImageIds.length; i++) {
            localServiceImages.add((ImageView) view
                    .findViewById(localServiceImageIds[i]));
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
                MainActivity.switchTab(2);
            }
        });
        for (int i = 0; i < localProductImages.size(); i++) {
            final int index = i;
            localProductImages.get(i).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (localGoods.size() > index) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", localGoods.get(index).getId());
                        jump(LocalGoodsDetailFragment.class.getName(),
                                localGoods.get(index)
                                        .getRetailProdManagerName(), bundle);
                    }
                }
            });
        }
        for (int i = 0; i < localServiceImages.size(); i++) {
            final int index = i;
            localServiceImages.get(i).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (localServices.size() > index) {
                        Bundle bundle = new Bundle();
                        bundle.putString("productId", localServices.get(index)
                                .getId());
                        jump(LocalServiceDetailFragment.class.getName(),
                                localServices.get(index).getProductName(),
                                bundle);
                    }
                }
            });
        }
    }

    private void refreshGoods() {
        for (int i = 0; i < localProductImages.size(); i++) {
            localProductImages.get(i).setImageResource(0);
            if (localGoods.size() > i) {
                ImageLoader.getInstance().displayImage(
                        getSmallImageUrl(localGoods.get(i).getBigImgUrl()),
                        localProductImages.get(i));
            }
        }
    }

    private void refreshService() {
        for (int i = 0; i < localServiceImages.size(); i++) {
            localServiceImages.get(i).setImageResource(0);
            if (localServices.size() > i) {
                ImageLoader.getInstance().displayImage(
                        getSmallImageUrl(localServices.get(i).getImg()),
                        localServiceImages.get(i));
            }
        }

    }

    public void refreshGoods(String result) {
        localGoods.clear();
        if (result.length() > 0) {
            JSONObject object;
            try {
                object = new JSONObject(result);
                if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                    JSONArray array = object.optJSONArray("dataList");
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject goods = array.optJSONObject(i);
                            if (goods != null) {
                                localGoods.add(new ModelLocalGoods(goods));
                            }
                        }
                        refreshGoods();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (localGoods.isEmpty() & localServices.isEmpty()) {
            getView().setVisibility(View.GONE);
        } else {
            getView().setVisibility(View.VISIBLE);
        }
    }

    public void refreshService(String result) {
        localServices.clear();
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
                                localServices.add(new ModelLocalService(
                                        jsonObject));
                            }
                        }
                        refreshService();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (localGoods.isEmpty() & localServices.isEmpty()) {
            getView().setVisibility(View.GONE);
        } else {
            getView().setVisibility(View.VISIBLE);
        }
    }

}
