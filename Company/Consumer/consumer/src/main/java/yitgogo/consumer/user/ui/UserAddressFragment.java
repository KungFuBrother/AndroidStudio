package yitgogo.consumer.user.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

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
import yitgogo.consumer.view.NormalAskDialog;

public class UserAddressFragment extends BaseNetworkFragment {

    ListView addressListView;
    List<ModelAddress> addresses;
    AddressAdapter addressAdapter;
    String addressId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_address);
        init();
        findViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UserAddressFragment.class.getName());
    }

    @Override
    protected void init() {
        addresses = new ArrayList<>();
        addressAdapter = new AddressAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UserAddressFragment.class.getName());
        getAddresses();
    }

    @Override
    protected void findViews() {
        addressListView = (ListView) contentView
                .findViewById(R.id.address_list);
        addImageButton(R.drawable.address_add, "添加收货地址", new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(UserAddressEditFragment.class.getName(), "添加收货地址");
            }
        });
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        addressListView.setAdapter(addressAdapter);
    }

    @Override
    protected void registerViews() {
    }

    private void delete(final String id) {
        NormalAskDialog askDialog = new NormalAskDialog("确定要删除这个收货地址吗？", "删除",
                "取消") {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (makeSure) {
                    deleteAddress(id);
                }
                super.onDismiss(dialog);
            }
        };
        askDialog.show(getFragmentManager(), null);
    }

    private void getAddresses() {
        addresses.clear();
        addressAdapter.notifyDataSetChanged();
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("memberAccount", User.getUser().getUseraccount()));
        post(API.API_USER_ADDRESS_LIST, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    addresses.add(new ModelAddress(array.optJSONObject(i)));
                                }
                                if (addresses.size() > 0) {
                                    addressAdapter.notifyDataSetChanged();
                                    return;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (addresses.size() == 0) {
                        missionNodata();
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
        public Object getItem(int position) {
            return addresses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.list_address_edit, null);
                holder = new ViewHolder();
                holder.nameTextView = (TextView) convertView
                        .findViewById(R.id.list_address_username);
                holder.phoneTextView = (TextView) convertView
                        .findViewById(R.id.list_address_phone);
                holder.areaTextView = (TextView) convertView
                        .findViewById(R.id.list_address_area);
                holder.detailTextView = (TextView) convertView
                        .findViewById(R.id.list_address_detail);
                holder.checkBox = (CheckBox) convertView
                        .findViewById(R.id.list_address_default);
                holder.deleteButton = (ImageView) convertView
                        .findViewById(R.id.list_address_delete);
                holder.editButton = (ImageView) convertView
                        .findViewById(R.id.list_address_edit);
                holder.setDefault = (FrameLayout) convertView
                        .findViewById(R.id.list_address_set_default);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ModelAddress address = addresses.get(position);
            holder.nameTextView.setText(address.getPersonName());
            holder.phoneTextView.setText(address.getPhone());
            holder.areaTextView.setText(address.getAreaAddress());
            holder.detailTextView.setText(address.getDetailedAddress());
            holder.checkBox.setChecked(address.isDefault());
            holder.setDefault.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!address.isDefault()) {
                        setDefaultAddress(address.getId());
                    }
                }
            });
            holder.deleteButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    delete(address.getId());
                }
            });
            holder.editButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("addressId", address.getId());
                    jump(UserAddressEditFragment.class.getName(), "修改收货地址",
                            bundle);
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView nameTextView, phoneTextView, areaTextView, detailTextView;
            ImageView deleteButton, editButton;
            CheckBox checkBox;
            FrameLayout setDefault;
        }

    }

    private void setDefaultAddress(String id) {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("id", id));
        post(API.API_USER_ADDRESS_SET_DEAFULT, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            ApplicationTool.showToast("设置默认地址成功");
                            getAddresses();
                        } else {
                            ApplicationTool.showToast(object.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void deleteAddress(String id) {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("id", id));
        post(API.API_USER_ADDRESS_DELETE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            ApplicationTool.showToast("删除成功");
                            getAddresses();
                        } else {
                            ApplicationTool.showToast(object.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

}
