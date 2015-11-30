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
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.local.model.ModelLocalSaleTejia;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.NetUtil;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.view.InnerListView;

/**
 * 本地秒杀
 */
public class LocalSaleTejiaFragment extends BaseNotifyFragment {

    PullToRefreshScrollView refreshScrollView;
    InnerListView serviceList;

    List<ModelLocalSaleTejia> localSaleTejias;
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
        MobclickAgent.onPageEnd(LocalSaleTejiaDetailFragment.class.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(LocalSaleTejiaDetailFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetLocalTejia().execute();
    }

    private void init() {
        measureScreen();
        localSaleTejias = new ArrayList<>();
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
                bundle.putString("id", localSaleTejias.get(paramInt).getProductId());
                jump(LocalSaleTejiaDetailFragment.class.getName(),
                        localSaleTejias.get(paramInt).getProductName(), bundle);

            }
        });
    }

    class ProductAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return localSaleTejias.size();
        }

        @Override
        public Object getItem(int position) {
            return localSaleTejias.get(position);
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
                        R.layout.list_local_sale_tejia, null);
                holder.imageView = (ImageView) convertView
                        .findViewById(R.id.local_sale_tejia_image);
                holder.nameTextView = (TextView) convertView
                        .findViewById(R.id.local_sale_tejia_name);
                holder.priceTextView = (TextView) convertView
                        .findViewById(R.id.local_sale_tejia_price);
                holder.originalPriceTextView = (TextView) convertView
                        .findViewById(R.id.local_sale_tejia_original_price);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ImageLoader.getInstance().displayImage(
                    getSmallImageUrl(localSaleTejias.get(position)
                            .getPromotionImg()), holder.imageView);
            holder.nameTextView.setText(localSaleTejias.get(position)
                    .getProductName());
            holder.priceTextView.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(localSaleTejias.get(position)
                    .getPromotionalPrice()));
            holder.originalPriceTextView.setText("原价:"
                    + Parameters.CONSTANT_RMB
                    + decimalFormat.format(localSaleTejias.get(position)
                    .getProductPrice()));
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView priceTextView, originalPriceTextView, nameTextView;
        }
    }

    public class GetLocalTejia extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
            localSaleTejias.clear();
            productAdapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("jgbh", Store.getStore()
                    .getStoreNumber()));
            return NetUtil.getInstance().postWithoutCookie(
                    API.API_LOCAL_SALE_TEJIA, nameValuePairs, false, false);
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
                                localSaleTejias.add(new ModelLocalSaleTejia(array
                                        .optJSONObject(i)));
                            }
                            productAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (localSaleTejias.isEmpty()) {
                loadingEmpty();
            }
        }

    }

}
