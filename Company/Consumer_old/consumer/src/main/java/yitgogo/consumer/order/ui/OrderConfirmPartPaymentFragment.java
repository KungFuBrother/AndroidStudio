package yitgogo.consumer.order.ui;

import yitgogo.consumer.BaseNormalFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;

public class OrderConfirmPartPaymentFragment extends BaseNormalFragment {

    RadioGroup paymentGroup;
    TextView payOnlineInfo, payCashInfo, paySendInfo;

    public final static int PAY_TYPE_CODE_ONLINE = 2;
    public final static int PAY_TYPE_CODE_SEND = 1;
    public final static int PAY_TYPE_CODE_CASH = 3;

    public final static String PAY_TYPE_NAME_ONLINE = "在线支付";
    public final static String PAY_TYPE_NAME_SEND = "货到付款";
    public final static String PAY_TYPE_NAME_CASH = "现金支付";

    boolean usePayTypeOnline = true;
    boolean usePayTypeCash = false;
    boolean canPaySend = true;

    /**
     * @param usePayTypeCash
     * @param canPaySend
     */
    public OrderConfirmPartPaymentFragment(boolean usePayTypeCash,
                                           boolean canPaySend) {
        this.usePayTypeCash = usePayTypeCash;
        this.canPaySend = canPaySend;
    }

    public OrderConfirmPartPaymentFragment(boolean usePayTypeCash) {
        this.usePayTypeCash = usePayTypeCash;
    }


    public OrderConfirmPartPaymentFragment(boolean usePayTypeOnline, boolean usePayTypeCash,
                                           boolean canPaySend) {
        this.usePayTypeOnline = usePayTypeOnline;
        this.usePayTypeCash = usePayTypeCash;
        this.canPaySend = canPaySend;
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.part_confirm_order_payment, null);
        findViews(view);
        return view;
    }

    @Override
    protected void findViews(View view) {
        paymentGroup = (RadioGroup) view.findViewById(R.id.payment_type);
        payOnlineInfo = (TextView) view.findViewById(R.id.payment_online);
        payCashInfo = (TextView) view.findViewById(R.id.payment_cash);
        paySendInfo = (TextView) view.findViewById(R.id.payment_send);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        if (!usePayTypeCash) {
            paymentGroup.removeViewAt(2);
        }
        if (!canPaySend) {
            paymentGroup.removeViewAt(1);
        }
        if (!usePayTypeOnline) {
            paymentGroup.removeViewAt(0);
        }
        paymentGroup.check(paymentGroup.getChildAt(0).getId());
        selectPayment();
    }

    private void selectPayment() {
        switch (paymentGroup.getCheckedRadioButtonId()) {
            case R.id.payment_type_online:
                payOnlineInfo.setVisibility(View.VISIBLE);
                payCashInfo.setVisibility(View.GONE);
                paySendInfo.setVisibility(View.GONE);
                break;

            case R.id.payment_type_cash:
                payOnlineInfo.setVisibility(View.GONE);
                payCashInfo.setVisibility(View.VISIBLE);
                paySendInfo.setVisibility(View.GONE);
                break;

            case R.id.payment_type_send:
                payOnlineInfo.setVisibility(View.GONE);
                payCashInfo.setVisibility(View.GONE);
                paySendInfo.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }

    @Override
    protected void registerViews() {
        paymentGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectPayment();
            }
        });
    }

    public void setCanPaySend(boolean canPaySend) {
        if (!canPaySend) {
            paymentGroup.removeViewAt(1);
        }
    }

    public String getPaymentName() {
        switch (paymentGroup.getCheckedRadioButtonId()) {
            case R.id.payment_type_online:
                return PAY_TYPE_NAME_ONLINE;

            case R.id.payment_type_send:
                return PAY_TYPE_NAME_SEND;

            case R.id.payment_type_cash:
                return PAY_TYPE_NAME_CASH;

            default:
                return PAY_TYPE_NAME_ONLINE;
        }
    }

    public int getPaymentType() {
        switch (paymentGroup.getCheckedRadioButtonId()) {
            case R.id.payment_type_online:
                return PAY_TYPE_CODE_ONLINE;

            case R.id.payment_type_send:
                return PAY_TYPE_CODE_SEND;

            case R.id.payment_type_cash:
                return PAY_TYPE_CODE_CASH;

            default:
                return PAY_TYPE_CODE_ONLINE;
        }
    }
}
