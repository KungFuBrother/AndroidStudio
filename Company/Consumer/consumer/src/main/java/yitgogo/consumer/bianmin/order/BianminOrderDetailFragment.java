package yitgogo.consumer.bianmin.order;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.view.InnerListView;
import yitgogo.consumer.view.NormalAskDialog;

public class BianminOrderDetailFragment extends BaseNetworkFragment {

    SwipeRefreshLayout refreshLayout;
    TextView orderNumberText, orderStateText, orderDateText, moneyText,
            typeTextView, accountTextView, detailTextView;
    TextView payButton;
    InnerListView productList;
    ModelBianminOrder bianminOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_order_bianmin_detail);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(BianminOrderDetailFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(BianminOrderDetailFragment.class.getName());
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("object")) {
                try {
                    bianminOrder = new ModelBianminOrder(new JSONObject(
                            bundle.getString("object")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void findViews() {
        refreshLayout = (SwipeRefreshLayout) contentView
                .findViewById(R.id.order_detail_refresh);
        orderNumberText = (TextView) contentView
                .findViewById(R.id.order_detail_number);
        orderStateText = (TextView) contentView
                .findViewById(R.id.order_detail_state);
        orderDateText = (TextView) contentView
                .findViewById(R.id.order_detail_date);
        orderDateText = (TextView) contentView
                .findViewById(R.id.order_detail_date);
        moneyText = (TextView) contentView
                .findViewById(R.id.order_detail_total_money);
        typeTextView = (TextView) contentView
                .findViewById(R.id.list_order_type);
        accountTextView = (TextView) contentView
                .findViewById(R.id.list_order_account);
        detailTextView = (TextView) contentView
                .findViewById(R.id.list_order_detail);
        payButton = (TextView) contentView
                .findViewById(R.id.order_detail_action_pay);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        if (bianminOrder.getOrderState().equalsIgnoreCase("未付款")) {
            payButton.setVisibility(View.VISIBLE);
        } else {
            payButton.setVisibility(View.GONE);
        }
        orderNumberText.setText("订单号：" + bianminOrder.getOrderNumber());
        orderStateText.setText(bianminOrder.getOrderState());
        orderDateText.setText(bianminOrder.getOrderTime());
        moneyText.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(bianminOrder.getSellprice()));
        typeTextView.setText(bianminOrder.getRechargeType());
        accountTextView.setText(bianminOrder.getRechargeAccount());
        if (bianminOrder.getRechargeType().equals("手机")) {
            detailTextView.setText("为" + bianminOrder.getRechargeAccount()
                    + "充值"
                    + decimalFormat.format(bianminOrder.getRechargeMoney())
                    + "元");
        } else if (bianminOrder.getRechargeType().equals("固话/宽带")) {
            detailTextView.setText("为" + bianminOrder.getRechargeAccount()
                    + "充值"
                    + decimalFormat.format(bianminOrder.getRechargeMoney())
                    + "元");
        } else if (bianminOrder.getRechargeType().equals("游戏/Q币")) {
            if (TextUtils.isEmpty(bianminOrder.getGame_area())) {
                detailTextView.setText("为" + bianminOrder.getRechargeAccount()
                        + "充值" + bianminOrder.getRechargeNum() + "Q币");
            } else {
                detailTextView.setText(bianminOrder.getGame_area() + "/"
                        + bianminOrder.getGame_server() + "(单价:"
                        + bianminOrder.getRechargeMoney() + "/数量:"
                        + bianminOrder.getRechargeNum() + ")");
            }
        } else {

        }
        addImageButton(R.drawable.get_goods_delete, "删除订单",
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        NormalAskDialog askDialog = new NormalAskDialog(
                                "确定要删除此订单吗？", "删除", "不删除") {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                if (makeSure) {
                                    deleteBianminOrder();
                                }
                                super.onDismiss(dialog);
                            }
                        };
                        askDialog.show(getFragmentManager(), null);
                    }
                });
    }

    @Override
    protected void registerViews() {
        payButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                payMoney(bianminOrder.getOrderNumber(),
                        bianminOrder.getSellprice(), PayFragment.ORDER_TYPE_BM);
            }
        });
    }

    private void deleteBianminOrder() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("orderNumber", bianminOrder.getOrderNumber()));
        post(API.API_BIANMIN_ORDER_DELETE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            ApplicationTool.showToast("删除成功");
                            BianminOrderFragment.removeOrder();
                            getActivity().finish();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ApplicationTool.showToast("删除失败");
            }
        });
    }

}
