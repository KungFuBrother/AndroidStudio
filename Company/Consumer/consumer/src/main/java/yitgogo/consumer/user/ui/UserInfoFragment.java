package yitgogo.consumer.user.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;

public class UserInfoFragment extends BaseNetworkFragment {

    RelativeLayout modifyPasswd, modifyPhone, modifyIdcard;
    TextView accountText, idCardText, registrDateText, phoneText, sexText,
            birthText, ageText;
    EditText addressText, emailText, nameText;
    SimpleDateFormat dateFormat;
    Button modify;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_info);
        init();
        findViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UserInfoFragment.class.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UserInfoFragment.class.getName());
        getUserInfo();
    }

    @Override
    protected void init() {
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void registerViews() {

    }

    protected void findViews() {
        modifyPasswd = (RelativeLayout) contentView
                .findViewById(R.id.user_info_modify_password);
        modifyPhone = (RelativeLayout) contentView
                .findViewById(R.id.user_info_modify_phone);
        modifyIdcard = (RelativeLayout) contentView
                .findViewById(R.id.user_info_modify_idcard);
        accountText = (TextView) contentView
                .findViewById(R.id.user_info_accout);
        idCardText = (TextView) contentView.findViewById(R.id.user_info_idcard);
        registrDateText = (TextView) contentView
                .findViewById(R.id.user_info_register_date);
        phoneText = (TextView) contentView.findViewById(R.id.user_info_phone);
        sexText = (TextView) contentView.findViewById(R.id.user_info_sex);
        birthText = (TextView) contentView
                .findViewById(R.id.user_info_birthday);
        ageText = (TextView) contentView.findViewById(R.id.user_info_age);
        addressText = (EditText) contentView
                .findViewById(R.id.user_info_address);
        emailText = (EditText) contentView.findViewById(R.id.user_info_email);
        nameText = (EditText) contentView.findViewById(R.id.user_info_name);
        modify = (Button) contentView.findViewById(R.id.user_info_modify);
        registerListener();
    }

    private void showUserInfo() {
        accountText.setText(User.getUser().getUseraccount());
        idCardText.setText(User.getUser().getIdcard());
        registrDateText.setText(User.getUser().getAddtime());
        phoneText.setText(User.getUser().getPhone());
        addressText.setText(User.getUser().getAddress());
        emailText.setText(User.getUser().getEmail());
        nameText.setText(User.getUser().getRealname());
        sexText.setText(User.getUser().getSex());
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(User.getUser().getBirthday());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        birthText.setText(dateFormat.format(date));
        ageText.setText(User.getUser().getAge());
    }

    private void registerListener() {
        modifyPasswd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(ModifySecret.class.getName(), "修改密码");
            }
        });
        modifyIdcard.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(ModifyIdCard.class.getName(), "修改身份证号");
            }
        });
        modifyPhone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(ModifyPhone.class.getName(), "修改手机号");
            }
        });
        sexText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new SexPicker().show(getFragmentManager(), null);
            }
        });
        birthText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new BirthDayPicker().show(getFragmentManager(), null);
            }
        });
        ageText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new BirthDayPicker().show(getFragmentManager(), null);
            }
        });
        modify.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                modifyUserInfo();
            }
        });
    }

    class SexPicker extends DialogFragment {
        String[] sex = {"男", "女"};

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(STYLE_NO_TITLE, 0);
            setCancelable(false);
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = new AlertDialog.Builder(getActivity())
                    .setSingleChoiceItems(sex, -1,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    sexText.setText(sex[which]);
                                    dismiss();
                                }
                            }).create();
            return dialog;
        }
    }

    class BirthDayPicker extends DialogFragment {

        Date date;
        Calendar calendar, today;
        OnDateSetListener onDateSetListener;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setCancelable(false);
            date = new Date();
            if (birthText.length() > 0) {
                try {
                    date = dateFormat.parse(birthText.getText().toString()
                            .trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            today = Calendar.getInstance();
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            onDateSetListener = new OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2,
                                      int arg3) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(arg1, arg2, arg3);
                    if (calendar.after(today)) {
                        Toast.makeText(getActivity(), "请重新选择",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        countAge(calendar);
                    }
                }
            };
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = new DatePickerDialog(getActivity(),
                    onDateSetListener, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            return dialog;
        }

        private void countAge(Calendar birthDay) {
            birthText.setText(dateFormat.format(birthDay.getTime()));
            int birthYear = birthDay.get(Calendar.YEAR);
            int birthMonth = birthDay.get(Calendar.MONTH);
            int birthDate = birthDay.get(Calendar.DAY_OF_MONTH);
            int todayYear = today.get(Calendar.YEAR);
            int todayMonth = today.get(Calendar.MONTH);
            int todayDate = today.get(Calendar.DAY_OF_MONTH);
            int age = todayYear - birthYear;
            if (todayMonth > birthMonth) {
                ageText.setText(age + "");
            } else if (todayMonth == birthMonth) {
                if (todayDate >= birthDate) {
                    ageText.setText(age + "");
                } else {
                    ageText.setText(age - 1 + "");
                }
            } else {
                ageText.setText(age - 1 + "");
            }
            dismiss();
        }
    }

    private void modifyUserInfo() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("useraccount", accountText.getText().toString()));
        // requestParams.add(new RequestParam("phone", User.getUser()
        // .getPhone()));
        requestParams.add(new RequestParam("uImg", User.getUser().getuImg()));
        requestParams.add(new RequestParam("idcard", User.getUser().getIdcard()));
        requestParams.add(new RequestParam("birthday", birthText.getText().toString()));
        requestParams.add(new RequestParam("sex", sexText.getText().toString()));
        requestParams.add(new RequestParam("realname", nameText.getText().toString()));
        requestParams.add(new RequestParam("age", ageText.getText().toString()));
        requestParams.add(new RequestParam("email", emailText.getText().toString()));
        requestParams.add(new RequestParam("address", addressText.getText().toString()));
        post(API.API_USER_INFO_UPDATE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        ApplicationTool.showToast("修改成功");
                        getUserInfo();
                    } else {
                        ApplicationTool.showToast(object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getUserInfo() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("username", User.getUser().getUseraccount()));
        post(API.API_USER_INFO_GET, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject userObject = object.optJSONObject("object");
                            if (userObject != null) {
                                Content.saveStringContent(Parameters.CACHE_KEY_USER_JSON, userObject.toString());
                                User.init(getActivity());
                            }
                        } else {
                            ApplicationTool.showToast(object.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                showUserInfo();
            }
        });
    }

}
