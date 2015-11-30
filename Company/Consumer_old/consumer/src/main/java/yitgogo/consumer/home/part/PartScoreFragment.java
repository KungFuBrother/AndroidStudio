package yitgogo.consumer.home.part;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNormalFragment;
import yitgogo.consumer.home.model.ModelScoreProduct;
import yitgogo.consumer.main.ui.MainActivity;
import yitgogo.consumer.product.ui.ProductScoreFragment;
import yitgogo.consumer.product.ui.ScoreProductDetailFragment;
import yitgogo.consumer.tools.Parameters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;

public class PartScoreFragment extends BaseNormalFragment {

    static PartScoreFragment scoreFragment;
    LinearLayout moreButton;
    RecyclerView recyclerView;
    List<ModelScoreProduct> scoreProducts;
    ScoreProductAdapter scoreProductAdapter;

    public static PartScoreFragment getScoreFragment() {
        if (scoreFragment == null) {
            scoreFragment = new PartScoreFragment();
        }
        return scoreFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        measureScreen();
        scoreProducts = new ArrayList<ModelScoreProduct>();
        scoreProductAdapter = new ScoreProductAdapter();
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_part_score, null);
        findViews(view);
        return view;
    }

    @Override
    protected void findViews(View view) {
        moreButton = (LinearLayout) view.findViewById(R.id.part_score_more);
        recyclerView = (RecyclerView) view.findViewById(R.id.part_score_list);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                screenWidth, screenWidth / 21 * 11);
        recyclerView.setLayoutParams(layoutParams);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(scoreProductAdapter);
    }

    @Override
    protected void registerViews() {
        moreButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(ProductScoreFragment.class.getName(), "积分购");
            }
        });
    }

    public void refresh(String result) {
        scoreProducts.clear();
        scoreProductAdapter.notifyDataSetChanged();
        if (result.length() > 0) {
            JSONObject object;
            try {
                object = new JSONObject(result);
                if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                    JSONArray array = object.optJSONArray("dataList");
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            scoreProducts.add(new ModelScoreProduct(array
                                    .optJSONObject(i)));
                        }
                        scoreProductAdapter.notifyDataSetChanged();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (scoreProducts.isEmpty()) {
            getView().setVisibility(View.GONE);
        } else {
            getView().setVisibility(View.VISIBLE);
        }
    }

    class ScoreProductAdapter extends RecyclerView.Adapter<ViewHolder> {

        class ScoreViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView nameTextView, scoreTextView, priceTextView;

            public ScoreViewHolder(View view) {
                super(view);
                LayoutParams params = new LayoutParams(screenWidth / 5 * 2,
                        screenWidth / 21 * 11);
                view.setLayoutParams(params);
                imageView = (ImageView) view
                        .findViewById(R.id.score_product_image);
                nameTextView = (TextView) view
                        .findViewById(R.id.score_product_name);
                scoreTextView = (TextView) view
                        .findViewById(R.id.score_product_score);
                priceTextView = (TextView) view
                        .findViewById(R.id.score_product_price);
            }
        }

        @Override
        public int getItemCount() {
            return scoreProducts.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final int index = position;
            ScoreViewHolder holder = (ScoreViewHolder) viewHolder;
            ImageLoader.getInstance().displayImage(
                    getSmallImageUrl(scoreProducts.get(position).getImgs()),
                    holder.imageView);
            holder.nameTextView.setText(scoreProducts.get(position).getName());
            holder.priceTextView.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(scoreProducts.get(position)
                    .getJifenjia()));
            holder.scoreTextView.setText("+"
                    + scoreProducts.get(position).getJifen() + "积分");
            holder.itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("productId", scoreProducts.get(index)
                            .getId());
                    jump(ScoreProductDetailFragment.class.getName(),
                            scoreProducts.get(index).getName(), bundle);
                }
            });
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
            View view = layoutInflater.inflate(
                    R.layout.list_product_score_home, null);
            ScoreViewHolder viewHolder = new ScoreViewHolder(view);
            return viewHolder;
        }

    }

}
