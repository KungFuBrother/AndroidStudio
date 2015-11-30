package yitgogo.consumer.product.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    RecyclerView recyclerView;

    List<ModelSaleMiaosha> saleMiaoshas;
    HashMap<String, Double> prices;
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
        prices = new HashMap<>();
        miaoshaAdapter = new MiaoshaAdapter();
    }

    @Override
    protected void findViews() {
        recyclerView = (RecyclerView) contentView.findViewById(R.id.miaosha_list);
        initViews();
    }

    @Override
    protected void initViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(miaoshaAdapter);
    }

    class MiaoshaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        class MiaoshaViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView nameTextView, timeTextView, priceTextView;

            public MiaoshaViewHolder(View view) {
                super(view);
                imageView = (ImageView) view
                        .findViewById(R.id.local_sale_miaosha_image);
                nameTextView = (TextView) view
                        .findViewById(R.id.local_sale_miaosha_name);
                timeTextView = (TextView) view
                        .findViewById(R.id.local_sale_miaosha_time);
                priceTextView = (TextView) view
                        .findViewById(R.id.local_sale_miaosha_price);
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
            ImageLoader.getInstance()
                    .displayImage(
                            getSmallImageUrl(saleMiaoshas.get(position)
                                    .getSeckillImg()), holder.imageView);
            holder.nameTextView.setText(saleMiaoshas.get(position)
                    .getProductName());
            if (prices.containsKey(saleMiaoshas.get(position).getProdutId())) {
                holder.priceTextView.setText(Parameters.CONSTANT_RMB
                        + decimalFormat.format(prices.get(saleMiaoshas.get(
                        position).getProdutId())));
            }
            Date currentTime = Calendar.getInstance().getTime();
            Date startTime = new Date(saleMiaoshas.get(position)
                    .getSeckillTime());
            if (startTime.before(currentTime)) {
                if (saleMiaoshas.get(position).getSeckillNumber() <= 0) {
                    holder.timeTextView.setText("抢购结束");
                } else {
                    holder.timeTextView.setText("抢购中");
                }
            } else {
                holder.timeTextView.setText("开始时间：" + simpleDateFormat.format(startTime));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Date currentTime = Calendar.getInstance().getTime();
                    Date startTime = new Date(saleMiaoshas.get(index)
                            .getSeckillTime());
                    if (startTime.before(currentTime)) {
                        if (saleMiaoshas.get(index).getSeckillNumber() <= 0) {
                            Notify.show("秒杀已经结束");
                        } else {
                            showProductDetail(saleMiaoshas.get(index)
                                            .getProdutId(), saleMiaoshas.get(index)
                                            .getProductName(),
                                    CaptureActivity.SALE_TYPE_MIAOSHA);
                        }
                    } else {
                        Notify.show("秒杀还未开始，开始时间\n"
                                + simpleDateFormat.format(startTime));
                    }
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
            View view = layoutInflater.inflate(R.layout.list_local_sale_miaosha,
                    null);
            MiaoshaViewHolder viewHolder = new MiaoshaViewHolder(view);
            return viewHolder;
        }
    }

    public class GetMiaoshaProduct extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
            saleMiaoshas.clear();
            prices.clear();
            miaoshaAdapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("strno", Store.getStore()
                    .getStoreNumber()));
            return NetUtil.getInstance().postWithoutCookie(API.API_SALE_MIAOSHA,
                    valuePairs, useCache, true);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (!TextUtils.isEmpty(result)) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONArray array = object.optJSONArray("dataList");
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                saleMiaoshas.add(new ModelSaleMiaosha(array
                                        .optJSONObject(i)));
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
            nameValuePairs.add(new BasicNameValuePair("productId", builder
                    .toString()));
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
                                    prices.put(jsonObject.optString("id"),
                                            jsonObject.optDouble("price"));
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
