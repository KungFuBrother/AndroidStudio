package yitgogo.consumer.money.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
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
import yitgogo.consumer.money.model.ModelBankCard;
import yitgogo.consumer.money.model.ModelTakeOutArea;
import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.RequestParam;

public class TakeOutFragment extends BaseNetworkFragment {

    TextView bankCardTextView, bankAreaTextView;
    EditText additionEditText, amountEditText;
    Button takeOutButton;

    BandCardAdapter bandCardAdapter;
    ModelBankCard bankCard = new ModelBankCard();

    List<ModelTakeOutArea> provinces;
    ProvinceAdapter provinceAdapter;
    ModelTakeOutArea province = new ModelTakeOutArea();

    List<ModelTakeOutArea> cities;
    CityAdapter cityAdapter;
    ModelTakeOutArea city = new ModelTakeOutArea();
    /**
     * 设置小数位数控制
     */
    InputFilter lengthfilter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            // 删除等特殊字符，直接返回
            if ("".equals(source.toString())) {
                return null;
            }
            String dValue = dest.toString();
            String[] splitArray = dValue.split("//.");
            if (splitArray.length > 1) {
                String dotValue = splitArray[1];
                int diff = dotValue.length() + 1 - 2;
                if (diff > 0) {
                    return source.subSequence(start, end - diff);
                }
            }
            return null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_money_takeout);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TakeOutFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TakeOutFragment.class.getName());
    }

    @Override
    protected void init() {
        bandCardAdapter = new BandCardAdapter();
        provinces = new ArrayList<>();
        provinceAdapter = new ProvinceAdapter();

        cities = new ArrayList<>();
        cityAdapter = new CityAdapter();
    }

    @Override
    protected void initViews() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void findViews() {
        bankCardTextView = (TextView) contentView
                .findViewById(R.id.takeout_bankcard);
        bankAreaTextView = (TextView) contentView
                .findViewById(R.id.takeout_bankcard_area);
        additionEditText = (EditText) contentView
                .findViewById(R.id.takeout_addition);
        amountEditText = (EditText) contentView
                .findViewById(R.id.takeout_amount);
        takeOutButton = (Button) contentView.findViewById(R.id.takeout_ok);
        addTextButton("提现记录", new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(TakeOutHistoryFragment.class.getName(), "提现记录");
            }
        });
        registerViews();
    }

    @Override
    protected void registerViews() {
        bankCardTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new BankCardDialog().show(getFragmentManager(), null);
            }
        });
        bankAreaTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getProvince();
            }
        });
        takeOutButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                takeOut();
            }
        });
        amountEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (amountEditText.length() > 0) {
                    amountEditText.setTextSize(18);
                } else {
                    amountEditText.setTextSize(14);
                }
            }
        });
        amountEditText.setFilters(new InputFilter[]{lengthfilter});
    }

    private void takeOut() {
        PayPasswordDialog payPasswordDialog = new PayPasswordDialog("请输入支付密码",
                false) {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!TextUtils.isEmpty(payPassword)) {
                    takeOut(payPassword);
                }
                super.onDismiss(dialog);
            }
        };
        payPasswordDialog.show(getFragmentManager(), null);
    }

    class BandCardAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return MoneyAccount.getMoneyAccount().getBankCards().size();
        }

        @Override
        public Object getItem(int position) {
            return MoneyAccount.getMoneyAccount().getBankCards().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(
                        R.layout.list_pay_bank_card, null);
                viewHolder.selected = (ImageView) convertView
                        .findViewById(R.id.bank_card_bank_selection);
                viewHolder.bankImageView = (ImageView) convertView
                        .findViewById(R.id.bank_card_bank_image);
                viewHolder.cardNumberTextView = (TextView) convertView
                        .findViewById(R.id.bank_card_number);
                viewHolder.cardTypeTextView = (TextView) convertView
                        .findViewById(R.id.bank_card_type);
                viewHolder.selected.setVisibility(View.GONE);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ModelBankCard bankCard = MoneyAccount.getMoneyAccount()
                    .getBankCards().get(position);
            ImageLoader.getInstance().displayImage(
                    bankCard.getBank().getIcon(), viewHolder.bankImageView);
            viewHolder.cardNumberTextView.setText(getSecretCardNuber(bankCard
                    .getBanknumber()));
            viewHolder.cardTypeTextView.setText(bankCard.getBank().getName()
                    + "  " + bankCard.getCardType());
            return convertView;
        }

        class ViewHolder {
            ImageView selected, bankImageView;
            TextView cardNumberTextView, cardTypeTextView;
        }
    }

    class BankCardDialog extends DialogFragment {

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
            dialog.setContentView(dialogView, new LayoutParams(LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth()));
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
            titleTextView.setText("选择银行卡");
            button.setText("取消");
            listView.setAdapter(bandCardAdapter);
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
                    bankCard = MoneyAccount.getMoneyAccount().getBankCards()
                            .get(arg2);
                    bankCardTextView.setText(bankCard.getBank().getName()
                            + "  "
                            + bankCard.getCardType()
                            + "(尾号"
                            + bankCard.getBanknumber().subSequence(
                            bankCard.getBanknumber().length() - 4,
                            bankCard.getBanknumber().length()) + ")");
                    dismiss();
                }
            });
        }
    }

    private void getProvince() {
        post(API.MONEY_PAY_AREA_PROVINCE, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("success")) {
                            JSONArray array = object.optJSONArray("databody");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    provinces.add(new ModelTakeOutArea(array.optJSONObject(i)));
                                }
                                if (provinces.size() > 0) {
                                    provinceAdapter.notifyDataSetChanged();
                                    new ProvinceDialog().show(getFragmentManager(), null);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class ProvinceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return provinces.size();
        }

        @Override
        public Object getItem(int position) {
            return provinces.get(position);
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
                convertView = layoutInflater.inflate(R.layout.list_class_main,
                        null);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.class_main_name);
                holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                holder.textView.setGravity(Gravity.CENTER_VERTICAL);
                holder.textView.setPadding(ApplicationTool.dip2px(24), 0, ApplicationTool.dip2px(24), 0);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ApplicationTool.dip2px(48));
                holder.textView.setLayoutParams(layoutParams);
                convertView.setBackgroundResource(R.drawable.selector_trans_divider);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(provinces.get(position).getName());
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }

    class ProvinceDialog extends DialogFragment {

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
            titleTextView.setText("选择所在省");
            button.setText("取消");
            listView.setAdapter(provinceAdapter);
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
                    province = provinces.get(arg2);
                    getCity();
                    dismiss();
                }
            });
        }
    }

    private void getCity() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("proid", province.getId()));
        post(API.MONEY_PAY_AREA_CITY, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("success")) {
                            JSONArray array = object.optJSONArray("databody");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    cities.add(new ModelTakeOutArea(array
                                            .optJSONObject(i)));
                                }
                                if (cities.size() > 0) {
                                    cityAdapter.notifyDataSetChanged();
                                    new CityDialog().show(getFragmentManager(),
                                            null);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class CityAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cities.size();
        }

        @Override
        public Object getItem(int position) {
            return cities.get(position);
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
                convertView = layoutInflater.inflate(R.layout.list_class_main,
                        null);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.class_main_name);
                holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                holder.textView.setGravity(Gravity.CENTER_VERTICAL);
                holder.textView.setPadding(ApplicationTool.dip2px(24), 0, ApplicationTool.dip2px(24), 0);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ApplicationTool.dip2px(48));
                holder.textView.setLayoutParams(layoutParams);
                convertView.setBackgroundResource(R.drawable.selector_trans_divider);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(cities.get(position).getName());
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }

    class CityDialog extends DialogFragment {

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
            dialog.setContentView(dialogView, new LayoutParams(LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth()));
            return dialog;
        }

        private void findViews() {
            dialogView = layoutInflater.inflate(R.layout.dialog_list, null);
            titleTextView = (TextView) dialogView.findViewById(R.id.dialog_title);
            button = (TextView) dialogView.findViewById(R.id.dialog_button);
            listView = (ListView) dialogView.findViewById(R.id.dialog_list);
            initViews();
        }

        private void initViews() {
            titleTextView.setText("选择所在城市");
            button.setText("取消");
            listView.setAdapter(cityAdapter);
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
                    city = cities.get(arg2);
                    bankAreaTextView.setText(province.getName() + " " + city.getName());
                    dismiss();
                }
            });
        }
    }

    private void takeOut(String paypassword) {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("amount", amountEditText.getText().toString().trim()));
        requestParams.add(new RequestParam("bankcardid", bankCard.getId()));
        requestParams.add(new RequestParam("area", province.getName() + "-" + city.getName()));
        requestParams.add(new RequestParam("desc", additionEditText.getText().toString().trim()));
        requestParams.add(new RequestParam("paypassword", paypassword));
        post(API.MONEY_BANK_TAKEOUT, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("success")) {
                            JSONObject databody = object.optJSONObject("databody");
                            if (databody != null) {
                                if (databody.optBoolean("deposit")) {
                                    ApplicationTool.showToast("提现成功");
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

}
