package yitgogo.consumer.money.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.money.model.ModelBank;
import yitgogo.consumer.money.model.ModelBankCardType;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.RequestParam;

public class BankCardBindFragment extends BaseNetworkFragment {

    TextView bankNameTextView;
    RadioGroup cardTypeGroup;
    EditText openBankEditText, cardNumberEditText, userNameEditText,
            userIdeEditText;
    Button bindButton;

    List<ModelBank> banks;
    BankAdapter bankAdapter;
    ModelBank bank;

    ModelBankCardType cardType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_money_bankcard_bind);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(BankCardBindFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(BankCardBindFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectCardType(R.id.bind_bankcard_bank_type_chuxu);
    }

    @Override
    protected void init() {
        banks = new ArrayList<>();
        bankAdapter = new BankAdapter();
        bank = new ModelBank();

        cardType = new ModelBankCardType();
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void findViews() {
        bankNameTextView = (TextView) contentView
                .findViewById(R.id.bind_bankcard_bank_name);
        cardTypeGroup = (RadioGroup) contentView
                .findViewById(R.id.bind_bankcard_bank_type);
        userNameEditText = (EditText) contentView
                .findViewById(R.id.bind_bankcard_user_name);
        userIdeEditText = (EditText) contentView
                .findViewById(R.id.bind_bankcard_user_idcard);
        openBankEditText = (EditText) contentView
                .findViewById(R.id.bind_bankcard_bank_openname);
        cardNumberEditText = (EditText) contentView
                .findViewById(R.id.bind_bankcard_bank_number);
        bindButton = (Button) contentView.findViewById(R.id.bind_bankcard_bind);
        initViews();
        registerViews();
    }

    @Override
    protected void registerViews() {
        bankNameTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new BankDialog().show(getFragmentManager(), null);
            }
        });
        cardTypeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectCardType(checkedId);
            }
        });
        bindButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                bind();
            }
        });
    }

    private void selectCardType(int radioButtonId) {
        switch (radioButtonId) {
            case R.id.bind_bankcard_bank_type_chuxu:
                cardType = new ModelBankCardType("1", "储蓄卡");
                break;

            case R.id.bind_bankcard_bank_type_xinyong:
                cardType = new ModelBankCardType("2", "信用卡");
                break;

            default:
                break;
        }
        getBanks();
    }

    private void selectBank(ModelBank bank) {
        this.bank = bank;
        bankNameTextView.setText(bank.getName());
    }

    private void bind() {
        if (TextUtils.isEmpty(cardType.getId())) {
            ApplicationTool.showToast("请选择银行卡类型");
        } else if (TextUtils.isEmpty(bank.getId())) {
            ApplicationTool.showToast("请选择银行");
        } else if (TextUtils.isEmpty(openBankEditText.getText().toString()
                .trim())) {
            ApplicationTool.showToast("请输入开户行名称");
        } else if (TextUtils.isEmpty(cardNumberEditText.getText().toString()
                .trim())) {
            ApplicationTool.showToast("请输入银行卡号");
        } else if (TextUtils.isEmpty(userNameEditText.getText().toString()
                .trim())) {
            ApplicationTool.showToast("请输入持卡人真实姓名");
        } else if (TextUtils.isEmpty(userIdeEditText.getText().toString()
                .trim())) {
            ApplicationTool.showToast("请输入持卡人身份证号");
        } else {
            bindBankCard();
        }
    }

    private void getBanks() {
        banks.clear();
        bankAdapter.notifyDataSetChanged();
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("type", cardType.getId()));
        post(API.MONEY_BANK_LIST, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("success")) {
                            JSONArray array = object.optJSONArray("databody");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    banks.add(new ModelBank(array.optJSONObject(i)));
                                }
                                if (banks.size() > 0) {
                                    bankAdapter.notifyDataSetChanged();
                                    selectBank(banks.get(0));
                                    return;
                                }
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

    private void bindBankCard() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("bankid", bank.getId()));
        requestParams.add(new RequestParam("bankcardtype", cardType.getName()));
        requestParams.add(new RequestParam("banknumber", cardNumberEditText.getText().toString().trim()));
        requestParams.add(new RequestParam("cardname", userNameEditText.getText().toString().trim()));
        requestParams.add(new RequestParam("cardid", userIdeEditText.getText().toString().trim()));
        requestParams.add(new RequestParam("banknameadds", openBankEditText.getText().toString().trim()));
        post(API.MONEY_BANK_BIND, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("success")) {
                            JSONObject jsonObject = object
                                    .optJSONObject("databody");
                            if (jsonObject != null) {
                                if (jsonObject.optString("bind").equalsIgnoreCase(
                                        "ok")) {
                                    ApplicationTool.showToast("绑定银行卡成功");
                                    getActivity().finish();
                                    return;
                                }
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

    class BankDialog extends DialogFragment {

        View dialogView;
        ListView listView;
        TextView titleTextView, button;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            findViews();
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth()));
            return dialog;
        }

        private void findViews() {
            dialogView = layoutInflater.inflate(R.layout.dialog_list, null);
            titleTextView = (TextView) dialogView
                    .findViewById(R.id.dialog_title);
            button = (TextView) dialogView.findViewById(R.id.dialog_button);
            listView = (ListView) dialogView.findViewById(R.id.dialog_list);
            initViews();
        }

        private void initViews() {
            titleTextView.setText("选择银行");
            button.setText("取消");
            listView.setAdapter(bankAdapter);
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    selectBank(banks.get(arg2));
                    dismiss();
                }
            });
        }
    }

    class BankAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return banks.size();
        }

        @Override
        public Object getItem(int position) {
            return banks.get(position);
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
                convertView = layoutInflater.inflate(R.layout.list_money_bank,
                        null);
                holder.imageView = (ImageView) convertView
                        .findViewById(R.id.list_bank_image);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.list_bank_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ImageLoader.getInstance().displayImage(
                    banks.get(position).getIcon(), holder.imageView);
            holder.textView.setText(banks.get(position).getName());
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }

}
