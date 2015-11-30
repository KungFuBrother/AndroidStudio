package yitgogo.consumer.local.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
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
import yitgogo.consumer.local.model.ModelLocalService;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.view.InnerListView;

public class LoveFreshFragment extends BaseNotifyFragment {

    PullToRefreshScrollView refreshScrollView;
    InnerListView freshListView;
    FreshAdapter freshAdapter;
    List<ModelLocalService> freshProducts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fresh);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(LoveFreshFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(LoveFreshFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetFresh().execute();
    }

    private void init() {
        measureScreen();
        freshProducts = new ArrayList<ModelLocalService>();
        freshAdapter = new FreshAdapter();
    }

    @Override
    protected void findViews() {
        refreshScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.fresh_refresh);
        freshListView = (InnerListView) contentView
                .findViewById(R.id.fresh_list);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        refreshScrollView.setMode(Mode.BOTH);
        freshListView.setAdapter(freshAdapter);
    }

    @Override
    protected void registerViews() {
        refreshScrollView
                .setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        refreshScrollView.setMode(Mode.BOTH);
                        freshProducts.clear();
                        freshAdapter.notifyDataSetChanged();
                        pagenum = 0;
                        new GetFresh().execute();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        new GetFresh().execute();
                    }
                });
        freshListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Bundle bundle = new Bundle();
                bundle.putString("productId", freshProducts.get(arg2).getId());
                jump(LocalServiceDetailFragment.class.getName(), freshProducts
                        .get(arg2).getProductName(), bundle);
            }
        });
    }

    class FreshAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return freshProducts.size();
        }

        @Override
        public Object getItem(int position) {
            return freshProducts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.list_fresh_product, null);
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView
                        .findViewById(R.id.fresh_product_image);
                holder.nameTextView = (TextView) convertView
                        .findViewById(R.id.fresh_product_name);
                holder.priceTextView = (TextView) convertView
                        .findViewById(R.id.fresh_product_price);
                holder.unitTextView = (TextView) convertView
                        .findViewById(R.id.fresh_product_unit);
                LayoutParams params = new LayoutParams(
                        LayoutParams.MATCH_PARENT, screenWidth / 10 * 9);
                holder.imageView.setLayoutParams(params);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ModelLocalService freshProduct = freshProducts.get(position);
            ImageLoader.getInstance().displayImage(freshProduct.getImg(),
                    holder.imageView);
            holder.nameTextView.setText(freshProduct.getProductName());
            holder.priceTextView.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(freshProduct.getProductPrice()));
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView nameTextView, priceTextView, unitTextView;
        }

    }

    class GetFresh extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            if (pagenum == 0) {
                showLoading();
            }
            pagenum++;
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("pageNo", pagenum + ""));
            parameters.add(new BasicNameValuePair("pageSize", pagesize + ""));
            parameters.add(new BasicNameValuePair("organizationId", Store
                    .getStore().getStoreId()));
            return netUtil.postWithoutCookie(
                    API.API_LOCAL_BUSINESS_SERVICE_FRESH, parameters, false,
                    false);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            refreshScrollView.onRefreshComplete();
            if (result.length() > 0) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONArray array = object.optJSONArray("dataList");
                        if (array != null) {
                            if (array.length() > 0) {
                                if (array.length() < pagesize) {
                                    refreshScrollView
                                            .setMode(Mode.PULL_FROM_START);
                                }
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jsonObject = array
                                            .optJSONObject(i);
                                    if (jsonObject != null) {
                                        freshProducts
                                                .add(new ModelLocalService(
                                                        jsonObject));
                                    }
                                }
                                freshAdapter.notifyDataSetChanged();
                                return;
                            } else {
                                refreshScrollView.setMode(Mode.PULL_FROM_START);
                                if (freshProducts.size() > 0) {
                                }
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (freshProducts.size() == 0) {
                    loadingEmpty();
                }
            } else {
                loadingFailed();
            }
        }

    }

}
