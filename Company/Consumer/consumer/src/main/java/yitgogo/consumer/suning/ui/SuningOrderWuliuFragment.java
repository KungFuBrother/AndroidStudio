package yitgogo.consumer.suning.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.suning.model.SuningManager;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.view.InnerListView;

public class SuningOrderWuliuFragment extends BaseNetworkFragment {

    PullToRefreshScrollView refreshScrollView;
    TextView wuliuStateText;
    List<ModelSuningWuliu> wulius;
    WuliuAdapter wuliuAdapter;
    InnerListView wuliuList;
    String orderId = "6021394830", skuId = "128410606";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_order_wuliu);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(SuningOrderWuliuFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(SuningOrderWuliuFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getSuningOrderWuliu();
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
//        if (bundle != null) {
//            if (bundle.containsKey("orderId")) {
//                orderId = bundle.getString("orderId");
//            }
//            if (bundle.containsKey("skuId")) {
//                skuId = bundle.getString("skuId");
//            }
//        }
        wulius = new ArrayList<>();
        wuliuAdapter = new WuliuAdapter();
    }

    @Override
    protected void findViews() {
        refreshScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.wuliu_refresh);
        wuliuStateText = (TextView) contentView.findViewById(R.id.wuliu_state);
        wuliuList = (InnerListView) contentView.findViewById(R.id.wuliu_list);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        wuliuList.setAdapter(wuliuAdapter);
        refreshScrollView.setMode(Mode.PULL_FROM_START);
    }

    @Override
    protected void registerViews() {
        refreshScrollView
                .setOnRefreshListener(new OnRefreshListener<ScrollView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        getSuningOrderWuliu();
                    }
                });
    }

    class WuliuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return wulius.size();
        }

        @Override
        public Object getItem(int position) {
            return wulius.get(position);
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
                        R.layout.list_wuliu_detail, null);
                holder = new ViewHolder();
                holder.detailText = (TextView) convertView
                        .findViewById(R.id.wuliu_detail_text);
                holder.timeText = (TextView) convertView
                        .findViewById(R.id.wuliu_detail_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ModelSuningWuliu wuliu = wulius.get(position);
            holder.timeText.setText(wuliu.getOperateTime());
            holder.detailText.setText(wuliu.getOperateState());
            return convertView;
        }

        class ViewHolder {
            TextView timeText, detailText;
        }
    }

    private void getSuningOrderWuliu() {
        wulius.clear();
        wuliuAdapter.notifyDataSetChanged();
        List<RequestParam> requestParams = new ArrayList<>();
        JSONObject data = new JSONObject();
        try {
            data.put("accessToken", SuningManager.getSignature().getToken());
            data.put("appKey", SuningManager.appKey);
            data.put("orderId", orderId);
            data.put("skuId", skuId);
            data.put("v", SuningManager.version);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestParams.add(new RequestParam("data", data.toString()));
        post(API.API_SUNING_ORDER_WULIU, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                refreshScrollView.onRefreshComplete();
                if (SuningManager.isSignatureOutOfDate(result)) {
                    post(API.API_SUNING_SIGNATURE, new OnNetworkListener() {
                        @Override
                        public void onSuccess(String result) {
                            if (!TextUtils.isEmpty(result)) {
                                try {
                                    JSONObject object = new JSONObject(result);
                                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                                        JSONObject dataMap = object.optJSONObject("dataMap");
                                        Content.saveStringContent(Parameters.CACHE_KEY_SUNING_SIGNATURE, dataMap.toString());
                                        getSuningOrderWuliu();
                                        return;
                                    }
                                    ApplicationTool.showToast(object.optString("message"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    return;
                }
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optBoolean("isSuccess")) {
                            JSONArray array = object.optJSONArray("orderLogisticStatus");
                            if (array != null) {
                                if (array.length() > 0) {
                                    for (int i = array.length() - 1; i >= 0; i--) {
                                        ModelSuningWuliu wuliu = new ModelSuningWuliu(array.optJSONObject(i));
                                        if (!TextUtils.isEmpty(wuliu.getOperateState())) {
                                            wulius.add(wuliu);
                                        }
                                    }
                                    wuliuStateText.setVisibility(View.GONE);
                                    wuliuAdapter.notifyDataSetChanged();
                                    return;
                                }
                            }
                            wuliuStateText.setVisibility(View.VISIBLE);
                            wuliuStateText.setText("暂无物流信息");
                            return;
                        }
                        ApplicationTool.showToast(object.optString("returnMsg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public class ModelSuningWuliu {

        String operateTime = "", operateState = "";

        public ModelSuningWuliu(JSONObject object) {
            if (object != null) {
                if (!object.optString("operateTime").equalsIgnoreCase("null")) {
                    operateTime = object.optString("operateTime");
                }
                if (!object.optString("operateState").equalsIgnoreCase("null")) {
                    operateState = object.optString("operateState");
                }
            }
        }

        public String getOperateTime() {
            return operateTime;
        }

        public String getOperateState() {
            return operateState;
        }
    }

}
