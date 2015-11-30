package yitgogo.consumer.money.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.money.model.ModelBankCard;
import yitgogo.consumer.money.model.ModelTakeOutArea;
import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ScreenUtil;
import yitgogo.consumer.view.Notify;

public class TakeOutFragment extends BaseNotifyFragment {

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

    private void init() {
        measureScreen();
        bandCardAdapter = new BandCardAdapter();
        provinces = new ArrayList<ModelTakeOutArea>();
        provinceAdapter = new ProvinceAdapter();

        cities = new ArrayList<ModelTakeOutArea>();
        cityAdapter = new CityAdapter();
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
                new GetProvince().execute();
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
                    new TakeOut().execute(payPassword);
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
            dialog.setContentView(dialogView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, screenWidth));
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

    /**
     * @author Tiger
     * @Url http://192.168.8.2:8030/member/bank/getpaypro
     * @Parameters No Parameters
     * @Put_Cookie JSESSIONID=186E8C60BA5BFC2E7CE1CE342E68B7A9
     * @Result {"state":"success","msg":"操作成功","databody":[{"id":1,"name":"湖北"},{
     * "id"
     * :2,"name":"云南"},{"id":3,"name":"江苏"},{"id":4,"name":"湖南"},{"id"
     * :5,
     * "name":"西藏"},{"id":6,"name":"河北"},{"id":7,"name":"浙江"},{"id":8,
     * "name":"广东"},{"id":9,"name":"陕西"},{"id":10,"name":"山西"},{"id":11,
     * "name"
     * :"安徽"},{"id":12,"name":"广西"},{"id":13,"name":"甘肃"},{"id":14,
     * "name"
     * :"内蒙古"},{"id":15,"name":"福建"},{"id":16,"name":"海南"},{"id":17
     * ,"name"
     * :"青海"},{"id":18,"name":"辽宁"},{"id":19,"name":"江西"},{"id":20
     * ,"name"
     * :"宁夏"},{"id":21,"name":"吉林"},{"id":22,"name":"山东"},{"id":23
     * ,"name"
     * :"四川"},{"id":24,"name":"新疆"},{"id":25,"name":"黑龙江"},{"id":26
     * ,"name"
     * :"河南"},{"id":27,"name":"贵州"},{"id":28,"name":"台湾"},{"id":29
     * ,"name"
     * :"香港"},{"id":30,"name":"澳门"},{"id":31,"name":"北京"},{"id":32
     * ,"name":"上海"},{"id":33,"name":"天津"},{"id":34,"name":"重庆"}]}
     */
    class GetProvince extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            return netUtil.postWithCookie(API.MONEY_PAY_AREA_PROVINCE,
                    null);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("success")) {
                        JSONArray array = object.optJSONArray("databody");
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                provinces.add(new ModelTakeOutArea(array
                                        .optJSONObject(i)));
                            }
                            if (provinces.size() > 0) {
                                provinceAdapter.notifyDataSetChanged();
                                new ProvinceDialog().show(getFragmentManager(),
                                        null);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
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
                holder.textView.setPadding(ScreenUtil.dip2px(24), 0,
                        ScreenUtil.dip2px(24), 0);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        ScreenUtil.dip2px(48));
                holder.textView.setLayoutParams(layoutParams);
                convertView
                        .setBackgroundResource(R.drawable.selector_trans_divider);
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
                    LayoutParams.MATCH_PARENT, screenWidth));
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
                    new GetCity().execute();
                    dismiss();
                }
            });
        }
    }

    /**
     * @author Tiger
     * @Url http://192.168.8.2:8030/member/bank/getpaycity
     * @Parameters [proid=28]
     * @Put_Cookie JSESSIONID=581227E914C5BA3F9050F0585CF9DABE
     * @Result {"state":"success","msg":"操作成功","databody":[{"id":751,"name"
     * :"台北","province":"28"},{"id":752,"name":"基隆","province":
     * "28"},{"id":753,"name":"台南","province":"28"},{"id":754, "name"
     * :"台中","province":"28"},{"id":755,"name":"高雄","province"
     * :"28"},{"id":756,"name":"屏东","province":"28"},{"id":757, "name"
     * :"南投","province":"28"},{"id":758,"name":"云林","province"
     * :"28"},{"id":759,"name":"新竹","province":"28"},{"id":760, "name"
     * :"彰化","province":"28"},{"id":761,"name":"苗栗","province"
     * :"28"},{"id":762,"name":"嘉义","province":"28"},{"id":763, "name"
     * :"花莲","province":"28"},{"id":764,"name":"桃园","province"
     * :"28"},{"id":765,"name":"宜兰","province":"28"},{"id":766, "name"
     * :"台东","province":"28"},{"id":767,"name":"金门","province"
     * :"28"},{"id":768,"name":"马祖","province":"28"},{"id":769,
     * "name":"澎湖","province":"28"}]}
     */
    class GetCity extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs
                    .add(new BasicNameValuePair("proid", province.getId()));
            return netUtil.postWithCookie(API.MONEY_PAY_AREA_CITY,
                    nameValuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
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
                holder.textView.setPadding(ScreenUtil.dip2px(24), 0,
                        ScreenUtil.dip2px(24), 0);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        ScreenUtil.dip2px(48));
                holder.textView.setLayoutParams(layoutParams);
                convertView
                        .setBackgroundResource(R.drawable.selector_trans_divider);
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
            dialog.setContentView(dialogView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, screenWidth));
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
                    bankAreaTextView.setText(province.getName() + " "
                            + city.getName());
                    dismiss();
                }
            });
        }
    }

    /**
     * @author Tiger
     * @Url http://192.168.8.2:8030/member/account/deposit
     * @Parameters [amount=1000, bankcardid=39, area=云南-大理, desc=取出来,
     * paypassword=467b617fec4d9fcb63505734ee224851]
     * @Put_Cookie JSESSIONID=29A466D7AF9A466BF00FCE8DA607DBBE
     * @Result {"state":"berror","msg":"支付密码错误!","databody":{}}
     * @Result {"state":"success","msg":"操作成功","databody":{"deposit":true}}
     */
    class TakeOut extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("amount", amountEditText
                    .getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("bankcardid", bankCard
                    .getId()));
            nameValuePairs.add(new BasicNameValuePair("area", province
                    .getName() + "-" + city.getName()));
            nameValuePairs.add(new BasicNameValuePair("desc", additionEditText
                    .getText().toString().trim()));
            nameValuePairs
                    .add(new BasicNameValuePair("paypassword", params[0]));
            return netUtil.postWithCookie(API.MONEY_BANK_TAKEOUT,
                    nameValuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("success")) {
                        JSONObject databody = object.optJSONObject("databody");
                        if (databody != null) {
                            if (databody.optBoolean("deposit")) {
                                Notify.show("提现成功");
                                return;
                            }
                        }
                    }
                    Notify.show(object.optString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
