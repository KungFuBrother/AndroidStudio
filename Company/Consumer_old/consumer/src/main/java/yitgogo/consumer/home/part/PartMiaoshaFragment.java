package yitgogo.consumer.home.part;

import android.os.AsyncTask;
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
    HashMap<String, Double> prices;

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
        prices = new HashMap<>();
        miaoshaAdapter = new MiaoshaAdapter();
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                screenWidth, screenWidth / 21 * 11);
        recyclerView.setLayoutParams(layoutParams);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity());
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
                jump(SaleMiaoshaFragment.class.getName(), "秒杀",bundle);
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
                        if (array.length() > 4) {
                            for (int i = 0; i < 4; i++) {
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
            TextView nameTextView, timeTextView, priceTextView;

            public MiaoshaViewHolder(View view) {
                super(view);
                LayoutParams params = new LayoutParams(screenWidth / 5 * 2,
                        screenWidth / 21 * 11);
                view.setLayoutParams(params);
                imageView = (ImageView) view
                        .findViewById(R.id.list_miaosha_image);
                nameTextView = (TextView) view
                        .findViewById(R.id.list_miaosha_name);
                timeTextView = (TextView) view
                        .findViewById(R.id.list_miaosha_time);
                priceTextView = (TextView) view
                        .findViewById(R.id.list_miaosha_price);
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
                // long remainSeconds = (startTime.getTime() - currentTime
                // .getTime()) / 1000;
                // long hour = remainSeconds / 3600;
                // long minute = (remainSeconds % 3600) / 60;
                // long second = (remainSeconds % 3600) % 60;
                // String h = "", m = "", s = "";
                // if (hour < 10) {
                // h = "0" + hour;
                // } else {
                // h = "" + hour;
                // }
                // if (minute < 10) {
                // m = "0" + minute;
                // } else {
                // m = "" + minute;
                // }
                // if (second < 10) {
                // s = "0" + second;
                // } else {
                // s = "" + second;
                // }
                holder.timeTextView.setText(simpleDateFormat.format(startTime));
            }
            holder.itemView.setOnClickListener(new OnClickListener() {

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
        public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
            View view = layoutInflater.inflate(R.layout.list_product_miaosha,
                    null);
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
            nameValuePairs.add(new BasicNameValuePair("productId", builder
                    .toString()));
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
