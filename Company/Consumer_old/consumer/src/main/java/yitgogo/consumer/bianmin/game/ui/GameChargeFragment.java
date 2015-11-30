package yitgogo.consumer.bianmin.game.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.bianmin.ModelBianminOrderResult;
import yitgogo.consumer.bianmin.game.model.ModelGame;
import yitgogo.consumer.bianmin.game.model.ModelGameCard;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.ScreenUtil;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.Notify;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import android.widget.ListView;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class GameChargeFragment extends BaseNotifyFragment {

	TextView infoTextView, nameTextView, amountTextView, priceTextView;
	EditText accountIdEditText, accountPassEditText;
	Button chargeButton;

	List<ModelGameCard> gameCards;
	GameCardAdapetr gameCardAdapetr;

	List<Integer> amounts;
	AmountAdapetr amountAdapetr;

	ModelGame game = new ModelGame();
	ModelGameCard gameCard = new ModelGameCard();
	int amount = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_bianmin_game_charge);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(GameChargeFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(GameChargeFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		new GetGameCards().execute();
	}

	private void init() {
		measureScreen();
		Bundle bundle = getArguments();
		if (bundle != null) {
			String gameId = "", gameName = "", gameArea = "", gameServer = "";
			if (bundle.containsKey("gameId")) {
				gameId = bundle.getString("gameId");
			}
			if (bundle.containsKey("gameName")) {
				gameName = bundle.getString("gameName");
			}
			if (bundle.containsKey("gameArea")) {
				gameArea = bundle.getString("gameArea");
			}
			if (bundle.containsKey("gameServer")) {
				gameServer = bundle.getString("gameServer");
			}
			game = new ModelGame(gameId, gameArea, gameServer, gameName);
		}
		gameCards = new ArrayList<ModelGameCard>();
		gameCardAdapetr = new GameCardAdapetr();
		amounts = new ArrayList<Integer>();
		amountAdapetr = new AmountAdapetr();
	}

	@Override
	protected void findViews() {
		infoTextView = (TextView) contentView
				.findViewById(R.id.game_charge_info);
		nameTextView = (TextView) contentView
				.findViewById(R.id.game_charge_name);
		amountTextView = (TextView) contentView
				.findViewById(R.id.game_charge_amount);
		priceTextView = (TextView) contentView
				.findViewById(R.id.game_charge_price);
		accountIdEditText = (EditText) contentView
				.findViewById(R.id.game_charge_id);
		accountPassEditText = (EditText) contentView
				.findViewById(R.id.game_charge_pass);
		chargeButton = (Button) contentView
				.findViewById(R.id.game_charge_charge);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		infoTextView.setText(game.getName() + " - " + game.getArea() + " - "
				+ game.getServer());
	}

	@Override
	protected void registerViews() {
		nameTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new GameCardDialog().show(getFragmentManager(), null);
			}
		});
		amountTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AmountDialog().show(getFragmentManager(), null);
			}
		});
		chargeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				charge();
			}
		});
	}

	private void charge() {
		if (accountIdEditText.length() > 0) {
			if (gameCard.getSellprice() > 0) {
				new GameCharge().execute();
			}
		} else {
			Notify.show("请输入要充值的游戏账号");
		}
	}

	class GameCardDialog extends DialogFragment {

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
			titleTextView.setText("选择充值类型");
			button.setText("取消");
			listView.setAdapter(gameCardAdapetr);
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
					gameCard = gameCards.get(arg2);
					nameTextView.setText(gameCard.getCardname());
					amounts = gameCard.getAmounts();
					if (amounts.size() > 0) {
						amountAdapetr.notifyDataSetChanged();
						amount = amounts.get(0);
						amountTextView.setText("" + amount);
						priceTextView.setText(Parameters.CONSTANT_RMB
								+ decimalFormat.format(gameCard.getSellprice()
										* amount));
					}
					dismiss();
				}
			});
		}
	}

	class AmountDialog extends DialogFragment {

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
			titleTextView.setText("选择充值数量");
			button.setText("取消");
			listView.setAdapter(amountAdapetr);
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
					if (amounts.get(arg2) > gameCard.getInnum()) {
						Notify.show("商品仅剩" + gameCard.getInnum() + "件");
						return;
					}
					amount = amounts.get(arg2);
					amountTextView.setText("" + amount);
					priceTextView.setText(Parameters.CONSTANT_RMB
							+ decimalFormat.format(gameCard.getSellprice()
									* amount));
					dismiss();
				}
			});
		}
	}

	class GameCardAdapetr extends BaseAdapter {

		@Override
		public int getCount() {
			return gameCards.size();
		}

		@Override
		public Object getItem(int position) {
			return gameCards.get(position);
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
			holder.textView.setText(gameCards.get(position).getCardname());
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}

	class AmountAdapetr extends BaseAdapter {

		@Override
		public int getCount() {
			return amounts.size();
		}

		@Override
		public Object getItem(int position) {
			return amounts.get(position);
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
			holder.textView.setText(amounts.get(position) + "");
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}

	/**
	 * 
	 * @author Tiger
	 * 
	 * @Result {"message":"此商品暂不可用","state":"ERROR"}
	 */
	class GetGameCards extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
			gameCards.clear();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("cardid", game.getId()));
			return netUtil.postWithoutCookie(API.API_BIANMIN_CARD_INFO,
					nameValuePairs, false, false);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				try {
					JSONObject object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						JSONArray array = object.optJSONArray("dataList");
						if (array != null) {
							for (int i = 0; i < array.length(); i++) {
								gameCards.add(new ModelGameCard(array
										.optJSONObject(i)));
							}
							if (gameCards.size() > 0) {
								gameCardAdapetr.notifyDataSetChanged();
								gameCard = gameCards.get(0);
								nameTextView.setText(gameCard.getCardname());
								amounts = gameCard.getAmounts();
								if (amounts.size() > 0) {
									amountAdapetr.notifyDataSetChanged();
									amount = amounts.get(0);
									amountTextView.setText("" + amount);
									priceTextView
											.setText(Parameters.CONSTANT_RMB
													+ decimalFormat.format(gameCard
															.getSellprice()
															* amount));
								}
								return;
							}
						}
					}
					Notify.show(object.optString("message"));
					return;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			Notify.show("获取商品信息失败");
		}
	}

	/**
	 * 
	 * @author Tiger
	 * 
	 * @Url 
	 *      http://192.168.8.14:8888/api/facilitate/recharge/addOrder/addGameOrder
	 * @Parameters [cardid=229501, game_area=81w上海电信, game_srv=320w上海电信一区,
	 *             game_userid=1076192306, pass=, cardnum=10,
	 *             memberAccount=HY345595695593]
	 * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList"
	 *         :[],"totalCount":1,"dataMap":{"sellPrice":"98.9",
	 *         "orderNumber":"YT4261694182"},"object":null}
	 */
	class GameCharge extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("cardid", gameCard
					.getCardid()));
			nameValuePairs.add(new BasicNameValuePair("game_area", game
					.getArea()));
			nameValuePairs.add(new BasicNameValuePair("game_srv", game
					.getServer()));
			nameValuePairs.add(new BasicNameValuePair("game_userid",
					accountIdEditText.getText().toString().trim()));
			// nameValuePairs.add(new BasicNameValuePair("game_userpsw",
			// accountEditText.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("pass",
					accountPassEditText.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("cardnum", amount + ""));
			if (User.getUser().isLogin()) {
				nameValuePairs.add(new BasicNameValuePair("memberAccount", User
						.getUser().getUseraccount()));
			}
			return netUtil.postWithoutCookie(API.API_BIANMIN_GAME_QQ_CHARGE,
					nameValuePairs, false, false);
		}

		@Override
		protected void onPostExecute(String result) {
			hideLoading();
			if (result.length() > 0) {
				try {
					JSONObject object = new JSONObject(result);
					if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
						JSONObject dataMap = object.optJSONObject("dataMap");
						ModelBianminOrderResult orderResult = new ModelBianminOrderResult(
								dataMap);
						if (orderResult != null) {
							if (orderResult.getSellPrice() > 0) {
								payMoney(orderResult);
								getActivity().finish();
								return;
							}
						}
					}
					Notify.show(object.optString("message"));
					return;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			Notify.show("充值失败");
		}
	}

}
