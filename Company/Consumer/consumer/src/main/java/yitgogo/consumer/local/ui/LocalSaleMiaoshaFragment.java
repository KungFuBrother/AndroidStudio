package yitgogo.consumer.local.ui;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.local.model.ModelLocalSaleMiaosha;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.view.InnerListView;

/**
 * 本地秒杀
 */
public class LocalSaleMiaoshaFragment extends BaseNetworkFragment {

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
        getLocalMiaosha();
    }

    @Override
    protected void init() {
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
                Bundle bundle = new Bundle();
                bundle.putString("id", localSaleMiaoshas.get(paramInt)
                        .getProductId());
                jump(LocalSaleMiaoshaDetailFragment.class.getName(),
                        localSaleMiaoshas.get(paramInt).getProductName(), bundle);

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

    private void getLocalMiaosha() {
        localSaleMiaoshas.clear();
        productAdapter.notifyDataSetChanged();
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("jgbh", Store.getStore().getStoreNumber()));
        post(API.API_LOCAL_SALE_MIAOSHA, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
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
                    missionNodata();
                }
            }
        });
    }

}
