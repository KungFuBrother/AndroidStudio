package yitgogo.consumer.order.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.local.model.ModelAddress;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserAddressEditFragment;
import yitgogo.consumer.user.ui.UserAddressFragment;

public class OrderConfirmPartAddressFragment extends BaseNetworkFragment {

    TextView userNameTextView, userPhoneTextView, userAreaTextView,
            userAddressTextView;
    LinearLayout receiverLayout, addAddressButton;

    List<ModelAddress> addresses;
    AddressAdapter addressAdapter;
    ModelAddress address;
    String mustAddress = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.part_confirm_order_address);
        init();
        findViews();
    }

    @Override
    protected void init() {
        addresses = new ArrayList<>();
        addressAdapter = new AddressAdapter();
    }

    @Override
    protected void findViews() {
        userNameTextView = (TextView) contentView
                .findViewById(R.id.order_address_username);
        userAddressTextView = (TextView) contentView
                .findViewById(R.id.order_address_detail);
        userAreaTextView = (TextView) contentView
                .findViewById(R.id.order_address_area);
        userPhoneTextView = (TextView) contentView
                .findViewById(R.id.order_address_phone);
        receiverLayout = (LinearLayout) contentView
                .findViewById(R.id.order_address_change);
        addAddressButton = (LinearLayout) contentView
                .findViewById(R.id.order_address_add);
        initViews();
        registerViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserAddress();
    }

    @Override
    protected void initViews() {
    }

    @Override
    protected void registerViews() {
        receiverLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new AddressDialog().show(getFragmentManager(), null);
            }
        });
        addAddressButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(UserAddressEditFragment.class.getName(), "添加收货地址");
            }
        });
    }

    private void showAddressInfo() {
        if (address != null) {
            userNameTextView.setText(address.getPersonName());
            userPhoneTextView.setText(address.getPhone());
            userAreaTextView.setText(address.getAreaAddress());
            userAddressTextView.setText(address.getDetailedAddress());
        }
    }

    private void initDefaultAddress() {
        address = new ModelAddress();
        if (addresses.size() > 0) {
            receiverLayout.setVisibility(View.VISIBLE);
            addAddressButton.setVisibility(View.GONE);
            for (int i = 0; i < addresses.size(); i++) {
                if (addresses.get(i).isDefault()) {
                    address = addresses.get(i);
                    return;
                }
            }
            address = addresses.get(0);
        } else {
            receiverLayout.setVisibility(View.GONE);
            addAddressButton.setVisibility(View.VISIBLE);
        }
    }

    public ModelAddress getAddress() {
        return address;
    }

    private void getUserAddress() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("memberAccount", User.getUser().getUseraccount()));
        post(API.API_USER_ADDRESS_LIST, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (result.length() > 0) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    addresses.add(new ModelAddress(array
                                            .getJSONObject(i)));
                                }
                                initDefaultAddress();
                                showAddressInfo();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class AddressAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return addresses.size();
        }

        @Override
        public Object getItem(int arg0) {
            return addresses.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            ViewHolder holder;
            if (arg1 == null) {
                arg1 = layoutInflater.inflate(R.layout.list_address, null);
                holder = new ViewHolder();
                holder.areaTextView = (TextView) arg1
                        .findViewById(R.id.order_address_area);
                holder.addressTextView = (TextView) arg1
                        .findViewById(R.id.order_address_detail);
                holder.nameTextView = (TextView) arg1
                        .findViewById(R.id.order_address_username);
                holder.phoneTextView = (TextView) arg1
                        .findViewById(R.id.order_address_phone);
                arg1.setTag(holder);
            } else {
                holder = (ViewHolder) arg1.getTag();
            }
            holder.areaTextView.setText(addresses.get(arg0).getAreaAddress());
            holder.addressTextView.setText(addresses.get(arg0)
                    .getDetailedAddress());
            holder.nameTextView.setText(addresses.get(arg0).getPersonName());
            holder.phoneTextView.setText(addresses.get(arg0).getPhone());
            return arg1;
        }

        class ViewHolder {
            TextView nameTextView, phoneTextView, areaTextView,
                    addressTextView;
        }
    }

    class AddressDialog extends DialogFragment {

        View dialogView;
        LinearLayout addButton;
        ListView addressListView;
        TextView manageButton;

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
            dialogView = layoutInflater.inflate(R.layout.dialog_address, null);
            addButton = (LinearLayout) dialogView
                    .findViewById(R.id.address_dialog_add);
            addressListView = (ListView) dialogView
                    .findViewById(R.id.address_dialog_list);
            manageButton = (TextView) dialogView
                    .findViewById(R.id.address_dialog_manage);
            initViews();
        }

        private void initViews() {
            addressListView.setAdapter(addressAdapter);
            addButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    jump(UserAddressEditFragment.class.getName(), "添加收货地址");
                    dismiss();
                }
            });
            manageButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    jump(UserAddressFragment.class.getName(), "收货地址管理");
                    dismiss();
                }
            });
            addressListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    address = addresses.get(arg2);
                    showAddressInfo();
                    dismiss();
                }
            });
        }
    }
}
