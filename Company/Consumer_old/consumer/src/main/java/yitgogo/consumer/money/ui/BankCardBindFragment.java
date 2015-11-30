package yitgogo.consumer.money.ui;

import android.app.Dialog;
import android.os.AsyncTask;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.money.model.ModelBank;
import yitgogo.consumer.money.model.ModelBankCardType;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.view.Notify;

public class BankCardBindFragment extends BaseNotifyFragment {

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

    private void init() {
        measureScreen();
        banks = new ArrayList<ModelBank>();
        bankAdapter = new BankAdapter();
        bank = new ModelBank();

        cardType = new ModelBankCardType();
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
                bindBankCard();
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
        new GetBanks().execute();
    }

    private void selectBank(ModelBank bank) {
        this.bank = bank;
        bankNameTextView.setText(bank.getName());
    }

    private void bindBankCard() {
        if (TextUtils.isEmpty(cardType.getId())) {
            Notify.show("请选择银行卡类型");
        } else if (TextUtils.isEmpty(bank.getId())) {
            Notify.show("请选择银行");
        } else if (TextUtils.isEmpty(openBankEditText.getText().toString()
                .trim())) {
            Notify.show("请输入开户行名称");
        } else if (TextUtils.isEmpty(cardNumberEditText.getText().toString()
                .trim())) {
            Notify.show("请输入银行卡号");
        } else if (TextUtils.isEmpty(userNameEditText.getText().toString()
                .trim())) {
            Notify.show("请输入持卡人真实姓名");
        } else if (TextUtils.isEmpty(userIdeEditText.getText().toString()
                .trim())) {
            Notify.show("请输入持卡人身份证号");
        } else {
            new BindBankCard().execute();
        }
    }

    /**
     * @author Tiger
     * @Url http://192.168.8.2:8030/member/bank/listbank
     * @Parameters [sn=91b3b795c848369d4b1092c984396c90, memberid=13032889558]
     * @Put_Cookie JSESSIONID=FA0F2AA6DA109D3E7B0E2BE59741B000
     * @Result {"state":"success","msg":"操作成功","databody":[{"code":"ICBC" ,
     * "icon":"1","id" :1,"name":"中国工商银行"},{"code":"CBC","icon"
     * :"2","id":2,"name": "中国建设银行"},{"code" :"ABC","icon":"3","id"
     * :3,"name":"中国农业银行"},{"code":"BC","icon" :"4","id":4,"name"
     * :"中国银行"},{"code":"CCB","icon":"5","id":5,"name":"交通银行"},
     * {"code":"CMBC","icon"
     * :"6","id":6,"name":"招商银行"},{"code":"SPDB","icon":"7",
     * "id":7,"name":"上海浦东发展银行"
     * },{"code":"CMSB","icon":"8","id":8,"name":"中国民生银行"
     * },{"code":"SDB","icon":"9"
     * ,"id":9,"name":"深圳发展银行"},{"code":"GDB","icon":"10"
     * ,"id":10,"name":"广东发展银行"
     * },{"code":"CITIC","icon":"11","id":11,"name":"中信银行"
     * },{"code":"HXB","icon":
     * "12","id":12,"name":"华夏银行"},{"code":"CIB","icon":"13"
     * ,"id":13,"name":"兴业银行"
     * },{"code":"GZRCC","icon":"14","id":14,"name":"广州市农村信用合作社"
     * },{"code":"GZCB", "icon":"15","id":15,"name":"广州市商业银行"},{"code"
     * :"SRCB","icon" :"16","id":16,"name"
     * :"上海农村商业银行"},{"code":"PSBC","icon":"17","id":17,"name"
     * :"中国邮政储蓄"},{"code":"CEB"
     * ,"icon":"18","id":18,"name":"中国光大银行"},{"code":"SHB"
     * ,"icon":"19","id":19,"name"
     * :"上海银行"},{"code":"BJB","icon":"20","id":20,"name"
     * :"北京银行"},{"code":"CBHB","icon"
     * :"21","id":21,"name":"渤海银行"},{"code":"HSB","icon"
     * :"22","id":22,"name":"徽商银行"
     * },{"code":"CSCB","icon":"23","id":23,"name":"长沙市商业银行"
     * },{"code":"HSBC","icon"
     * :"24","id":24,"name":"汇丰银行"},{"code":"PINGAN","icon"
     * :"25","id":25,"name":"深圳平安银行"
     * },{"code":"HKBEA","icon":"26","id":26,"name":
     * "东亚银行"},{"code":"HCCB","icon"
     * :"27","id":27,"name":"杭州银行"},{"code":"JSB","icon"
     * :"28","id":28,"name":"江苏银行"
     * },{"code":"NONGXINYIN","icon":"29","id":29,"name"
     * :"农信银中心"},{"code":"RCB","icon"
     * :"30","id":30,"name":"农村商业银行"},{"code":"CITYBANK"
     * ,"icon":"31","id":31,"name"
     * :"城市商业银行"},{"code":"NXS","icon":"32","id":32,"name"
     * :"农村信用合作社"},{"code":"NJCB"
     * ,"icon":"33","id":33,"name":"南京市商业银行"},{"code":"NBCB"
     * ,"icon":"34","id":34,
     * "name":"宁波银行"},{"code":"CITI","icon":"35","id":35,"name"
     * :"花旗银行"},{"code":"GNETE","icon":"36","id":36,"name": "广州银联"}]}
     */
    class GetBanks extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            banks.clear();
            bankAdapter.notifyDataSetChanged();
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs
                    .add(new BasicNameValuePair("type", cardType.getId()));
            return netUtil.postWithCookie(API.MONEY_BANK_LIST,
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
                                banks.add(new ModelBank(array.optJSONObject(i)));
                            }
                            if (banks.size() > 0) {
                                bankAdapter.notifyDataSetChanged();
                                selectBank(banks.get(0));
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

    /**
     * @author Tiger
     *
     *
     * @Url http://192.168.8.2:8030/member/bank/banktype
     * @Parameters No Parameters
     * @Put_Cookie JSESSIONID=EED19711CD0B1B808EB0C31CC15D61DF
     * @Result {"state":"success","msg":"操作成功","databody":["储蓄卡","信用卡"]}
     *
     *
     */
    // class GetBankCardTypes extends AsyncTask<Void, Void, String> {
    //
    // @Override
    // protected void onPreExecute() {
    // showLoading();
    // }
    //
    // @Override
    // protected String doInBackground(Void... params) {
    // return netUtil.postWithCookie(MoneyAPI.MONEY_BANK_TYPE, null);
    // }
    //
    // @Override
    // protected void onPostExecute(String result) {
    // hideLoading();
    // if (!TextUtils.isEmpty(result)) {
    // try {
    // JSONObject object = new JSONObject(result);
    // if (object.optString("state").equalsIgnoreCase("success")) {
    // JSONArray array = object.optJSONArray("databody");
    // if (array != null) {
    // for (int i = 0; i < array.length(); i++) {
    // cardTypes.add(array.optString(i));
    // }
    // if (cardTypes.size() > 0) {
    // cardTypeAdapter.notifyDataSetChanged();
    // return;
    // }
    // }
    // }
    // Notify.show(object.optString("msg"));
    // } catch (JSONException e) {
    // e.printStackTrace();
    // }
    // }
    // }
    //
    // }

    /**
     * @author Tiger
     * @Url http://192.168.8.2:8030/member/bank/bindbankcard
     * @Parameters [bankid=17, bankcardtype=储蓄卡, banknumber=6210986731007566422,
     * cardname=雷小武, cardid=513030199311056012, banknameadds=南充支行]
     * @Put_Cookie JSESSIONID=6D0B5EDAAA7231BF03D3D0FCEF60F3C4
     * @Result {"state":"success","msg":"操作成功","databody":{"bind":"ok"}}
     */
    class BindBankCard extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("bankid", bank.getId()));
            nameValuePairs.add(new BasicNameValuePair("bankcardtype", cardType
                    .getName()));
            nameValuePairs.add(new BasicNameValuePair("banknumber",
                    cardNumberEditText.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("cardname",
                    userNameEditText.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("cardid", userIdeEditText
                    .getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("banknameadds",
                    openBankEditText.getText().toString().trim()));
            return netUtil.postWithCookie(API.MONEY_BANK_BIND,
                    nameValuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("success")) {
                        JSONObject jsonObject = object
                                .optJSONObject("databody");
                        if (jsonObject != null) {
                            if (jsonObject.optString("bind").equalsIgnoreCase(
                                    "ok")) {
                                Notify.show("绑定银行卡成功");
                                getActivity().finish();
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
