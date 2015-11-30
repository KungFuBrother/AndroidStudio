package yitgogo.consumer.product.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
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
import yitgogo.consumer.home.model.ModelScoreProduct;
import yitgogo.consumer.product.ui.ScoreProductDetailFragment;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserLoginFragment;
import yitgogo.consumer.user.ui.UserScoreFragment;

public class ProductScoreFragment extends BaseNotifyFragment {

    PullToRefreshListView refreshListView;
    List<ModelScoreProduct> scoreProducts;
    ScoreProductAdapter scoreProductAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_score);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ProductScoreFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ProductScoreFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showDisconnectMargin();
        new GetScoreProduct().execute();
    }

    private void init() {
        scoreProducts = new ArrayList<ModelScoreProduct>();
        scoreProductAdapter = new ScoreProductAdapter();
    }

    @Override
    protected void findViews() {
        refreshListView = (PullToRefreshListView) contentView
                .findViewById(R.id.score_product_list);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        refreshListView.setMode(Mode.BOTH);
        refreshListView.setAdapter(scoreProductAdapter);
    }

    @Override
    protected void registerViews() {
        addTextButton("获取积分", new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!User.getUser().isLogin()) {
                    jump(UserLoginFragment.class.getName(), "会员登录");
                } else {
                    jump(UserScoreFragment.class.getName(), "赚积分");
                }
            }
        });
        refreshListView
                .setOnRefreshListener(new OnRefreshListener2<ListView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        refresh();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        new GetScoreProduct().execute();
                    }
                });
    }

    private void refresh() {
        pagenum = 0;
        scoreProducts.clear();
        scoreProductAdapter.notifyDataSetChanged();
        refreshListView.setMode(Mode.BOTH);
        new GetScoreProduct().execute();
    }

    class GetScoreProduct extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            if (pagenum == 0) {
                showLoading();
            }
            pagenum++;
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("jgbh", Store.getStore()
                    .getStoreNumber()));
            valuePairs.add(new BasicNameValuePair("pagenum", pagenum + ""));
            valuePairs.add(new BasicNameValuePair("pagesize", pagesize + ""));
            return netUtil.postWithCookie(API.API_SCORE_PRODUCT_LIST,
                    valuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            refreshListView.onRefreshComplete();
            if (result.length() > 0) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONArray array = object.optJSONArray("dataList");
                        if (array != null) {
                            if (array.length() > 0) {
                                if (array.length() < pagesize) {
                                    refreshListView
                                            .setMode(Mode.PULL_FROM_START);
                                }
                                for (int i = 0; i < array.length(); i++) {
                                    scoreProducts.add(new ModelScoreProduct(
                                            array.optJSONObject(i)));
                                }
                                scoreProductAdapter.notifyDataSetChanged();
                                return;
                            }
                        }
                    }
                } catch (JSONException e) {
                    loadingEmpty();
                    e.printStackTrace();
                }
            }
            refreshListView.setMode(Mode.PULL_FROM_START);
            if (scoreProducts.isEmpty()) {
                loadingEmpty();
            }
        }
    }

    class ScoreProductAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return scoreProducts.size();
        }

        @Override
        public Object getItem(int position) {
            return scoreProducts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int index = position;
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.list_product_score_fragment, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView
                        .findViewById(R.id.score_product_image);
                viewHolder.nameTextView = (TextView) convertView
                        .findViewById(R.id.score_product_name);
                viewHolder.scoreTextView = (TextView) convertView
                        .findViewById(R.id.score_product_score);
                viewHolder.priceTextView = (TextView) convertView
                        .findViewById(R.id.score_product_price);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ImageLoader.getInstance().displayImage(
                    getSmallImageUrl(scoreProducts.get(position).getImgs()),
                    viewHolder.imageView);
            viewHolder.nameTextView.setText(scoreProducts.get(position)
                    .getName());
            viewHolder.scoreTextView.setText("+"
                    + scoreProducts.get(position).getJifen() + "积分");
            viewHolder.priceTextView.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(scoreProducts.get(position)
                    .getJifenjia()));
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("productId", scoreProducts.get(index)
                            .getId());
                    jump(ScoreProductDetailFragment.class.getName(),
                            scoreProducts.get(index).getName(), bundle);
                }
            });
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView nameTextView, scoreTextView, priceTextView;
        }

    }

}
