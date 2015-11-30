package yitgogo.consumer.user.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.local.model.ModelAddress;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.NormalAskDialog;
import yitgogo.consumer.view.Notify;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class UserAddressFragment extends BaseNotifyFragment {

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

	private void init() {
		addresses = new ArrayList<ModelAddress>();
		addressAdapter = new AddressAdapter();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(UserAddressFragment.class.getName());
		new GetAddresses().execute();
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

	private void delete(String id) {
		addressId = id;
		NormalAskDialog askDialog = new NormalAskDialog("确定要删除这个收货地址吗？", "删除",
				"取消") {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (makeSure) {
					new DeteleAddress().execute();
				}
				super.onDismiss(dialog);
			}
		};
		askDialog.show(getFragmentManager(), null);
	}

	class GetAddresses extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
			addresses.clear();
			addressAdapter.notifyDataSetChanged();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("memberAccount", User
					.getUser().getUseraccount()));
			return netUtil.postWithCookie(API.API_USER_ADDRESS_LIST,
					nameValuePairs);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						JSONArray array = object.optJSONArray("dataList");
						if (array != null) {
							for (int i = 0; i < array.length(); i++) {
								addresses.add(new ModelAddress(array
										.optJSONObject(i)));
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
					loadingEmpty();
				}
			} else {
				loadingFailed();
			}
		}
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
						new SetDefaultAddress().execute(address.getId());
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

	class SetDefaultAddress extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("id", params[0]));
			return netUtil.postWithCookie(API.API_USER_ADDRESS_SET_DEAFULT,
					pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
						Notify.show("设置默认地址成功");
						new GetAddresses().execute();
					} else {
						Notify.show(object.optString("message"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}
	}

	class DeteleAddress extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("id", addressId));
			return netUtil.postWithCookie(API.API_USER_ADDRESS_DELETE, pairs);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
						Notify.show("删除成功");
						new GetAddresses().execute();
					} else {
						Notify.show(object.optString("message"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}
	}

}
