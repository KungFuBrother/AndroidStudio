package yitgogo.consumer.home.part;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dtr.zxing.activity.CaptureActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import yitgogo.consumer.BaseNormalFragment;
import yitgogo.consumer.home.model.ModelKillPrice;
import yitgogo.consumer.home.model.ModelSaleMiaosha;
import yitgogo.consumer.product.ui.SaleMiaoshaFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.view.Notify;

public class PartMiaoshaFragment extends BaseNormalFragment {

    static PartMiaoshaFragment miaoshaFragment;
    LinearLayout moreButton;
    RecyclerView recyclerView;
    List<ModelSaleMiaosha> saleMiaoshas;
    MiaoshaAdapter miaoshaAdapter;
    HashMap<String, ModelKillPrice> killPriceHashMap;

    String result = "";

    public static PartMiaoshaFragment getMiaoshaFragment() {
        if (miaoshaFragment == null) {
            miaoshaFragment = new PartMiaoshaFragment();
        }
        return miaoshaFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        measureScreen();
        saleMiaoshas = new ArrayList<>();
        killPriceHashMap = new HashMap<>();
        miaoshaAdapter = new MiaoshaAdapter();
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_part_miaosha, null);
        findViews(view);
        return view;
    }

    @Override
    protected void findViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.part_miaosha_list);
        moreButton = (LinearLayout) view.findViewById(R.id.part_miaosha_more);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth, screenWidth / 10 * 6);
        recyclerView.setLayoutParams(layoutParams);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(miaoshaAdapter);
    }

    @Override
    protected void registerViews() {
        moreButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("result", result);
                jump(SaleMiaoshaFragment.class.getName(), "秒杀", bundle);
            }
        });
    }

    public void refresh(String result) {
        this.result = result;
        saleMiaoshas.clear();
        miaoshaAdapter.notifyDataSetChanged();
        if (result.length() > 0) {
            JSONObject object;
            try {
                object = new JSONObject(result);
                if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                    JSONArray array = object.optJSONArray("dataList");
                    if (array != null) {
                        if (array.length() > 10) {
                            for (int i = 0; i < 10; i++) {
                                saleMiaoshas.add(new ModelSaleMiaosha(array
                                        .optJSONObject(i)));
                            }
                        } else {
                            for (int i = 0; i < array.length(); i++) {
                                saleMiaoshas.add(new ModelSaleMiaosha(array
                                        .optJSONObject(i)));
                            }
                        }
                        miaoshaAdapter.notifyDataSetChanged();
                        new GetSalePrice().execute();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (saleMiaoshas.isEmpty()) {
            getView().setVisibility(View.GONE);
        } else {
            getView().setVisibility(View.VISIBLE);
        }
    }

    class MiaoshaAdapter extends RecyclerView.Adapter<ViewHolder> {

        class MiaoshaViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView priceTextView, salePriceTextView, stateTextView;

            public MiaoshaViewHolder(View view) {
                super(view);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth / 5 * 2, LinearLayout.LayoutParams.MATCH_PARENT);
                imageView = (ImageView) view.findViewById(R.id.item_kill_image);
                priceTextView = (TextView) view.findViewById(R.id.item_kill_price);
                salePriceTextView = (TextView) view.findViewById(R.id.item_kill_sale_price);
                stateTextView = (TextView) view.findViewById(R.id.item_kill_state);
                priceTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                view.setLayoutParams(layoutParams);
            }
        }

        @Override
        public int getItemCount() {
            return saleMiaoshas.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final int index = position;
            MiaoshaViewHolder holder = (MiaoshaViewHolder) viewHolder;
            ImageLoader.getInstance().displayImage(getSmallImageUrl(saleMiaoshas.get(position).getSeckillImg()), holder.imageView);
            if (killPriceHashMap.containsKey(saleMiaoshas.get(position).getProdutId())) {
                holder.priceTextView.setText("原价:" + Parameters.CONSTANT_RMB + decimalFormat.format(killPriceHashMap.get(saleMiaoshas.get(position).getProdutId()).getOriginalPrice()));
                holder.salePriceTextView.setText("秒杀价:" + Parameters.CONSTANT_RMB + decimalFormat.format(killPriceHashMap.get(saleMiaoshas.get(position).getProdutId()).getPrice()));
            }
            Date currentTime = Calendar.getInstance().getTime();
            Date startTime = new Date(saleMiaoshas.get(position).getSeckillTime());
            if (startTime.before(currentTime)) {
                if (saleMiaoshas.get(position).getSeckillNumber() > 0) {
                    holder.stateTextView.setText("正在秒杀");
                    holder.stateTextView.setBackgroundColor(Color.rgb(226, 59, 96));
                    holder.stateTextView.setTextColor(Color.rgb(255, 241, 0));
                } else {
                    holder.stateTextView.setText("已售罄");
                    holder.stateTextView.setBackgroundColor(Color.rgb(189, 189, 189));
                    holder.stateTextView.setTextColor(Color.rgb(255, 255, 255));
                }
            } else {
                holder.stateTextView.setText("未开始");
                holder.stateTextView.setBackgroundColor(Color.rgb(189, 189, 189));
                holder.stateTextView.setTextColor(Color.rgb(255, 255, 255));
            }
            holder.itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Date currentTime = Calendar.getInstance().getTime();
                    Date startTime = new Date(saleMiaoshas.get(index).getSeckillTime());
                    if (startTime.before(currentTime)) {
                        if (saleMiaoshas.get(index).getSeckillNumber() > 0) {
                            showProductDetail(saleMiaoshas.get(index).getProdutId(), saleMiaoshas.get(index).getProductName(), CaptureActivity.SALE_TYPE_MIAOSHA);
                        } else {
                            Notify.show("已售罄");
                        }
                    } else {
                        Notify.show("未开始");
                    }
                }
            });
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
            View view = layoutInflater.inflate(R.layout.list_product_miaosha, null);
            MiaoshaViewHolder viewHolder = new MiaoshaViewHolder(view);
            return viewHolder;
        }
    }

    class GetSalePrice extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < saleMiaoshas.size(); i++) {
                if (i > 0) {
                    builder.append(",");
                }
                builder.append(saleMiaoshas.get(i).getProdutId());
            }
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("productId", builder.toString()));
            nameValuePairs.add(new BasicNameValuePair("type", "2"));
            return netUtil.postWithoutCookie(API.API_SALE_PRICE,
                    nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.length() > 0) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONArray array = object.optJSONArray("dataList");
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = array.optJSONObject(i);
                                if (jsonObject != null) {
                                    killPriceHashMap.put(jsonObject.optString("id"), new ModelKillPrice(jsonObject));
                                }
                            }
                            miaoshaAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
