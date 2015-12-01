package yitgogo.consumer.local.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
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
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.local.model.ModelLocalSaleMiaosha;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.NetUtil;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.view.InnerListView;
import yitgogo.consumer.view.Notify;

/**
 * 本地秒杀
 */
public class LocalSaleMiaoshaFragment extends BaseNotifyFragment {

    PullToRefreshScrollView refreshScrollView;
    InnerListView serviceList;

    List<ModelLocalSaleMiaosha> localSaleMiaoshas;
    ProductAdapter productAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_local_business_nongfu);
        init();
        findViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(LocalSaleMiaoshaFragment.class.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(LocalSaleMiaoshaFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetLocalMiaosha().execute();
    }

    private void init() {
        measureScreen();
        localSaleMiaoshas = new ArrayList<>();
        productAdapter = new ProductAdapter();
    }

    @Override
    protected void findViews() {
        refreshScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.local_business_content_refresh);
        serviceList = (InnerListView) contentView
                .findViewById(R.id.local_business_content_list);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        refreshScrollView.setMode(Mode.DISABLED);
        serviceList.setAdapter(productAdapter);
    }

    @Override
    protected void registerViews() {
        serviceList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> paramAdapterView,
                                    View paramView, int paramInt, long paramLong) {
                Date currentTime = Calendar.getInstance().getTime();
                Date startTime = new Date(localSaleMiaoshas.get(paramInt).getSpikeTime());
                if (startTime.before(currentTime)) {
                    if (localSaleMiaoshas.get(paramInt).getNumbers() > 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", localSaleMiaoshas.get(paramInt).getProductId());
                        jump(LocalSaleMiaoshaDetailFragment.class.getName(), localSaleMiaoshas.get(paramInt).getProductName(), bundle);
                    } else {
                        Notify.show("抢购已结束");
                    }
                } else {
                    Notify.show("抢购还没开始");
                }
            }
        });
    }

    class ProductAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return localSaleMiaoshas.size();
        }

        @Override
        public Object getItem(int position) {
            return localSaleMiaoshas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(
                        R.layout.list_local_sale_miaosha, null);
                holder.imageView = (ImageView) convertView
                        .findViewById(R.id.local_sale_miaosha_image);
                holder.nameTextView = (TextView) convertView
                        .findViewById(R.id.local_sale_miaosha_name);
                holder.timeTextView = (TextView) convertView
                        .findViewById(R.id.local_sale_miaosha_time);
                holder.priceTextView = (TextView) convertView
                        .findViewById(R.id.local_sale_miaosha_price);
                holder.originalPriceTextView = (TextView) convertView
                        .findViewById(R.id.local_sale_miaosha_original_price);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ImageLoader.getInstance().displayImage(
                    getSmallImageUrl(localSaleMiaoshas.get(position)
                            .getPromotionImg()), holder.imageView);
            Date currentTime = Calendar.getInstance().getTime();
            Date startTime = new Date(localSaleMiaoshas.get(position)
                    .getSpikeTime());
            if (startTime.before(currentTime)) {
                if (localSaleMiaoshas.get(position).getNumbers() <= 0) {
                    holder.timeTextView.setText("抢购结束");
                } else {
                    holder.timeTextView.setText("抢购中");
                }
            } else {
                holder.timeTextView.setText("开始时间:"
                        + simpleDateFormat.format(startTime));
            }
            holder.nameTextView.setText(localSaleMiaoshas.get(position)
                    .getProductName());
            holder.priceTextView.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(localSaleMiaoshas.get(position)
                    .getPromotionalPrice()));
            holder.originalPriceTextView.setText("原价:"
                    + Parameters.CONSTANT_RMB
                    + decimalFormat.format(localSaleMiaoshas.get(position)
                    .getProductPrice()));
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView nameTextView, timeTextView, originalPriceTextView,
                    priceTextView;
        }
    }

    public class GetLocalMiaosha extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
            localSaleMiaoshas.clear();
            productAdapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("jgbh", Store.getStore()
                    .getStoreNumber()));
            return NetUtil.getInstance().postWithoutCookie(
                    API.API_LOCAL_SALE_MIAOSHA, nameValuePairs, false, false);
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
                                localSaleMiaoshas.add(new ModelLocalSaleMiaosha(
                                        array.optJSONObject(i)));
                            }
                            productAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (localSaleMiaoshas.isEmpty()) {
                loadingEmpty();
            }
        }

    }


}
