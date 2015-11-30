package yitgogo.consumer.money.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.money.model.ModelBankCard;
import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.money.task.GetBankCards;
import yitgogo.consumer.money.task.HavePayPasswordTask;
import yitgogo.consumer.view.InnerListView;
import yitgogo.consumer.view.Notify;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class BankCardFragment extends BaseNotifyFragment {

	LinearLayout addButton;
	InnerListView cardListView;

	List<ModelBankCard> bankCards;
	BandCardAdapter bandCardAdapter;

	HavePayPasswordTask havePayPasswordTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_money_backcard_list);
		init();
		findViews();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(BankCardFragment.class.getName());
	}

	private void init() {
		bankCards = new ArrayList<ModelBankCard>();
		bandCardAdapter = new BandCardAdapter();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(BankCardFragment.class.getName());
		GetBankCards getBankCards = new GetBankCards() {
			@Override
			protected void onPreExecute() {
				showLoading();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String result) {
				hideLoading();
				super.onPostExecute(result);
				bankCards = MoneyAccount.getMoneyAccount().getBankCards();
				bandCardAdapter.notifyDataSetChanged();
			}
		};
		getBankCards.execute();
	}

	@Override
	protected void findViews() {
		cardListView = (InnerListView) contentView
				.findViewById(R.id.bank_card_list);
		addButton = (LinearLayout) contentView.findViewById(R.id.bank_card_add);
		addImageButton(R.drawable.address_add, "添加银行卡", new OnClickListener() {

			@Override
			public void onClick(View v) {
				havePayPassword();
			}
		});
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		cardListView.setAdapter(bandCardAdapter);
	}

	@Override
	protected void registerViews() {
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				havePayPassword();
			}
		});
		cardListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bundle bundle = new Bundle();
				bundle.putString("bankCard", bankCards.get(arg2)
						.getJsonObject().toString());
				jump(BankCardDetailFragment.class.getName(), "我的银行卡", bundle);
			}
		});
	}

	class BandCardAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return bankCards.size();
		}

		@Override
		public Object getItem(int position) {
			return bankCards.get(position);
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
						R.layout.list_money_bank_card, null);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.bank_card_bank_image);
				viewHolder.cardNumberTextView = (TextView) convertView
						.findViewById(R.id.bank_card_number);
				viewHolder.cardTypeTextView = (TextView) convertView
						.findViewById(R.id.bank_card_type);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			ImageLoader.getInstance().displayImage(
					bankCards.get(position).getBank().getIcon(),
					viewHolder.imageView);
			viewHolder.cardNumberTextView.setText(getSecretCardNuber(bankCards
					.get(position).getBanknumber()));
			viewHolder.cardTypeTextView.setText(bankCards.get(position)
					.getBank().getName()
					+ "  " + bankCards.get(position).getCardType());
			return convertView;
		}

		class ViewHolder {
			ImageView imageView;
			TextView cardNumberTextView, cardTypeTextView;
		}
	}

	private void havePayPassword() {
		if (havePayPasswordTask != null) {
			if (havePayPasswordTask.getStatus() == Status.RUNNING) {
				return;
			}
		}
		havePayPasswordTask = new HavePayPasswordTask() {

			@Override
			protected void onPreExecute() {
				showLoading();
			}

			@Override
			protected void onPostExecute(String result) {
				hideLoading();
				if (!TextUtils.isEmpty(result)) {
					try {
						JSONObject object = new JSONObject(result);
						if (object.optString("state").equalsIgnoreCase(
								"success")) {
							JSONObject jsonObject = object
									.optJSONObject("databody");
							if (jsonObject != null) {
								if (jsonObject.optBoolean("pwd")) {
									// 已设置支付密码
									jump(BankCardBindFragment.class.getName(),
											"添加银行卡");
								} else {
									// 未设置支付密码
									Notify.show("请先设置支付密码");
									jump(PayPasswordSetFragment.class.getName(),
											"设置支付密码");
								}
								return;
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				Notify.show("访问服务器失败，请稍候再试！");
			}
		};
		havePayPasswordTask.execute();
	}

}
