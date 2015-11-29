package yitgogo.consumer.money.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.money.model.ModelBankCard;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.view.NormalAskDialog;

public class BankCardDetailFragment extends BaseNetworkFragment {

    ImageView imageView;
    TextView cardNumberTextView, cardTypeTextView, deleteButton;
    ModelBankCard bankCard = new ModelBankCard();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_money_backcard_detail);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(BankCardDetailFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(BankCardDetailFragment.class.getName());
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("bankCard")) {
                try {
                    bankCard = new ModelBankCard(new JSONObject(bundle.getString("bankCard")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void findViews() {
        imageView = (ImageView) contentView.findViewById(R.id.bank_card_detail_image);
        cardNumberTextView = (TextView) contentView.findViewById(R.id.bank_card_detail_number);
        cardTypeTextView = (TextView) contentView.findViewById(R.id.bank_card_detail_type);
        deleteButton = (TextView) contentView.findViewById(R.id.bank_card_detail_delete);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        ImageLoader.getInstance().displayImage(bankCard.getBank().getIcon(), imageView);
        cardNumberTextView.setText(getSecretCardNuber(bankCard.getBanknumber()));
        cardTypeTextView.setText(bankCard.getBank().getName() + "  " + bankCard.getCardType());
    }

    @Override
    protected void registerViews() {
        deleteButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                NormalAskDialog askDialog = new NormalAskDialog("确定要解绑这张银行卡吗？", "解绑", "取消") {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (makeSure) {
                            PayPasswordDialog payPasswordDialog = new PayPasswordDialog("请输入支付密码", false) {
                                public void onDismiss(DialogInterface dialog) {
                                    if (!TextUtils.isEmpty(payPassword)) {
                                        unBindBankCard(payPassword);
                                    }
                                    super.onDismiss(dialog);
                                }
                            };
                            payPasswordDialog.show(getFragmentManager(), null);
                        }
                        super.onDismiss(dialog);
                    }
                };
                askDialog.show(getFragmentManager(), null);
            }
        });
    }

    private void unBindBankCard(String paypassword) {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("bankcardid", bankCard.getId()));
        requestParams.add(new RequestParam("paypassword", paypassword));
        post(API.MONEY_BANK_UNBIND, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("success")) {
                            JSONObject databody = object.optJSONObject("databody");
                            if (databody.optBoolean("unbind")) {
                                ApplicationTool.showToast("解绑成功");
                                getActivity().finish();
                                return;
                            }
                        }
                        ApplicationTool.showToast(object.optString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
