package yitgogo.consumer.product.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dtr.zxing.activity.CaptureActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

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

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.home.model.ModelKillPrice;
import yitgogo.consumer.home.model.ModelSaleMiaosha;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.NetUtil;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.view.Notify;

/**
 * 本地秒杀
 */
public class SaleMiaoshaFragment extends BaseNotifyFragment {

    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;

    List<ModelSaleMiaosha> saleMiaoshas;
    HashMap<String, ModelKillPrice> killPriceHashMap;
    MiaoshaAdapter miaoshaAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sale_miaosha);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(SaleMiaoshaFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(SaleMiaoshaFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetMiaoshaProduct().execute();
    }

    private void init() {
        measureScreen();
        saleMiaoshas = new ArrayList<>();
        killPriceHashMap = new HashMap<>();
        miaoshaAdapter = new MiaoshaAdapter();
    }

    @Override
    protected void findViews() {
        refreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.miaosha_refresh);
        recyclerView = (RecyclerView) contentView.findViewById(R.id.miaosha_list);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(miaoshaAdapter);
    }

    @Override
    protected void registerViews() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void refresh() {
        useCache = false;
        new GetMiaoshaProduct().execute();
    }

    class MiaoshaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        class MiaoshaViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView priceTextView, salePriceTextView, stateTextView;

            public MiaoshaViewHolder(View view) {
                super(view);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, screenWidth / 2);
                imageView = (ImageView) view.findViewById(R.id.item_kill_image);
                priceTextView = (TextView) view.findViewById(R.id.item_kill_price);
                salePriceTextView = (TextView) view.findViewById(R.id.item_kill_sale_price);
                stateTextView = (TextView) view.findViewById(R.id.item_kill_state);
                priceTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                imageView.setLayoutParams(layoutParams);
            }
        }

        @Override
        public int getItemCount() {
            return saleMiaoshas.size();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {

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
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
            View view = layoutInflater.inflate(R.layout.list_product_miaosha, null);
            MiaoshaViewHolder viewHolder = new MiaoshaViewHolder(view);
            return viewHolder;
        }
    }

    public class GetMiaoshaProduct extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            refreshLayout.setRefreshing(true);
            saleMiaoshas.clear();
            killPriceHashMap.clear();
            miaoshaAdapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("strno", Store.getStore().getStoreNumber()));
            return NetUtil.getInstance().postWithoutCookie(API.API_SALE_MIAOSHA, valuePairs, useCache, true);
        }

        @Override
        protected void onPostExecute(String result) {
            refreshLayout.setRefreshing(false);
            if (!TextUtils.isEmpty(result)) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONArray array = object.optJSONArray("dataList");
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                saleMiaoshas.add(new ModelSaleMiaosha(array.optJSONObject(i)));
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
                loadingEmpty();
            }
        }

    }


    class GetSalePrice extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

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
            hideLoading();
            if (!TextUtils.isEmpty(result)) {
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
