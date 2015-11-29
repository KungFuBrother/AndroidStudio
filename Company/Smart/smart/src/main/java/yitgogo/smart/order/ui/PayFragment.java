package yitgogo.smart.order.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.OnQrEncodeListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.tools.QrEncodeMissonMessage;

public class PayFragment extends DialogFragment {

    LayoutInflater layoutInflater;
    String orderNumbers = "";
    String orderNumbersShow = "";
    double totalMoney = 0;
    int orderType = 1;

    public static final int ORDER_TYPE_YY = 1;
    public static final int ORDER_TYPE_YD = 2;
    public static final int ORDER_TYPE_LP = 3;
    public static final int ORDER_TYPE_LS = 4;
    public static final int ORDER_TYPE_BM = 5;
    public static final int ORDER_TYPE_SN = 6;

    View dialogView;
    TextView orderNumberTextView, payMoneyTextView, notifyTextView;
    ImageView qrCodeImageView;
    ImageView closeButton;

    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(PayFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(PayFragment.class.getName());
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView, new LayoutParams(800,
                LayoutParams.WRAP_CONTENT));
        return dialog;
    }

    private void init() {
        setCancelable(false);
        layoutInflater = LayoutInflater.from(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("orderNumbers")) {
                if (bundle.containsKey("orderNumbers")) {
                    List<String> orderNumbers = bundle
                            .getStringArrayList("orderNumbers");
                    for (int i = 0; i < orderNumbers.size(); i++) {
                        if (i > 0) {
                            this.orderNumbers += ",";
                            orderNumbersShow += "\n";
                        }
                        this.orderNumbers += orderNumbers.get(i);
                        orderNumbersShow += orderNumbers.get(i);
                    }
                }
            }
            if (bundle.containsKey("totalMoney")) {
                totalMoney = bundle.getDouble("totalMoney");
            }
            if (bundle.containsKey("orderType")) {
                orderType = bundle.getInt("orderType");
            }
        }
    }

    protected void findViews() {
        dialogView = layoutInflater.inflate(R.layout.dialog_fragment_pay, null);
        orderNumberTextView = (TextView) dialogView
                .findViewById(R.id.pay_order_number);
        payMoneyTextView = (TextView) dialogView.findViewById(R.id.pay_money);
        qrCodeImageView = (ImageView) dialogView.findViewById(R.id.pay_qrcode);
        closeButton = (ImageView) dialogView.findViewById(R.id.pay_close);
        notifyTextView = (TextView) dialogView.findViewById(R.id.pay_type_notify);
        initViews();
    }

    protected void initViews() {
        qrEncodeOrder(orderNumbers, totalMoney, orderType,
                new OnQrEncodeListener() {

                    @Override
                    public void onSuccess(QrEncodeMissonMessage message) {
                        super.onSuccess(message);
                        if (message.getBitmap() != null) {
                            qrCodeImageView.setImageBitmap(message.getBitmap());
                        }
                    }

                });
        orderNumberTextView.setText(orderNumbersShow);
        payMoneyTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(totalMoney));
        if (orderType == ORDER_TYPE_BM) {
            notifyTextView.setText("请使用易田购购扫描二维码付款。");
        } else {
            notifyTextView.setText("请到收银台支付现金，或使用易田购购扫描二维码付款。");
        }
        closeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    protected void qrEncodeOrder(String orderNumbers, double totalMoney,
                                 int orderType, OnQrEncodeListener onQrEncodeListener) {
        JSONObject object = new JSONObject();
        try {
            object.put("codeType", 2);
            JSONObject dataObject = new JSONObject();
            dataObject.put("orderNumbers", orderNumbers);
            dataObject.put("totalMoney", totalMoney);
            dataObject.put("orderType", orderType);
            object.put("data", dataObject);
            MissionController.startQrEncodeMission(getActivity(), Base64
                    .encodeToString(object.toString().getBytes(),
                            Base64.DEFAULT), 240, onQrEncodeListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
