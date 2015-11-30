package yitgogo.consumer.bianmin.game.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.bianmin.game.model.ModelGame;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ScreenUtil;
import yitgogo.consumer.view.Notify;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class GameFilterFragment extends BaseNotifyFragment {

	TextView nameTextView, areaTextView, serverTextView;
	Button searchButton;

	List<ModelGame> games;
	GameAdapter gameAdapter;

	List<ModelGame> gameAreas;
	GameAreaAdapter gameAreaAdapter;

	List<ModelGame> gameServers;
	GameServerAdapter gameServerAdapter;

	ModelGame game = new ModelGame();
	ModelGame gameArea = new ModelGame();
	ModelGame gameServer = new ModelGame();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_bianmin_game_filter);
		init();
		findViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(GameFilterFragment.class.getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(GameFilterFragment.class.getName());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (games.size() <= 0) {
			new GetGames().execute();
		}
	}

	private void init() {
		measureScreen();
		games = new ArrayList<ModelGame>();
		gameAdapter = new GameAdapter();

		gameAreas = new ArrayList<ModelGame>();
		gameAreaAdapter = new GameAreaAdapter();

		gameServers = new ArrayList<ModelGame>();
		gameServerAdapter = new GameServerAdapter();
	}

	@Override
	protected void findViews() {
		nameTextView = (TextView) contentView
				.findViewById(R.id.game_filter_name);
		areaTextView = (TextView) contentView
				.findViewById(R.id.game_filter_area);
		serverTextView = (TextView) contentView
				.findViewById(R.id.game_filter_server);
		searchButton = (Button) contentView
				.findViewById(R.id.game_filter_search);
		initViews();
		registerViews();
	}

	@Override
	protected void registerViews() {
		nameTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new GameDialog().show(getFragmentManager(), null);
			}
		});
		areaTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new GameAreaDialog().show(getFragmentManager(), null);
			}
		});
		serverTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new GameServerDialog().show(getFragmentManager(), null);
			}
		});
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search();
			}
		});
	}

	private void search() {
		if (TextUtils.isEmpty(game.getId())) {
			Notify.show("请选择游戏名称");
		} else {
			Bundle bundle = new Bundle();
			bundle.putString("gameId", game.getId());
			bundle.putString("gameName", game.getName());
			bundle.putString("gameArea", gameArea.getArea());
			bundle.putString("gameServer", gameServer.getServer());
			jump(GameChargeFragment.class.getName(), "游戏充值", bundle);
		}
	}

	class GameDialog extends DialogFragment {

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
			titleTextView.setText("选择游戏名称");
			button.setText("取消");
			listView.setAdapter(gameAdapter);
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
					game = games.get(arg2);
					nameTextView.setText(game.getName());
					new GetGameArea().execute();
					dismiss();
				}
			});
		}
	}

	class GameAreaDialog extends DialogFragment {

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
			titleTextView.setText("选择游戏区域");
			button.setText("取消");
			listView.setAdapter(gameAreaAdapter);
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
					gameArea = gameAreas.get(arg2);
					areaTextView.setText(gameArea.getArea());
					new GetGameServer().execute();
					dismiss();
				}
			});
		}
	}

	class GameServerDialog extends DialogFragment {

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
			titleTextView.setText("选择游戏服");
			button.setText("取消");
			listView.setAdapter(gameServerAdapter);
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
					gameServer = gameServers.get(arg2);
					serverTextView.setText(gameServer.getServer());
					dismiss();
				}
			});
		}
	}

	class GameAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return games.size();
		}

		@Override
		public Object getItem(int position) {
			return games.get(position);
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
			holder.textView.setText(games.get(position).getName());
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}

	class GameAreaAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return gameAreas.size();
		}

		@Override
		public Object getItem(int position) {
			return gameAreas.get(position);
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
			holder.textView.setText(gameAreas.get(position).getArea());
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}

	class GameServerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return gameServers.size();
		}

		@Override
		public Object getItem(int position) {
			return gameServers.get(position);
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
			holder.textView.setText(gameServers.get(position).getServer());
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}
	}

	class GetGames extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
			games.clear();
			gameAdapter.notifyDataSetChanged();
		}

		@Override
		protected String doInBackground(Void... params) {
			return netUtil.postWithoutCookie(API.API_BIANMIN_GAME_LIST, null,
					true, true);
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
								games.add(new ModelGame(array.optJSONObject(i)));
							}
							if (games.size() > 0) {
								gameAdapter.notifyDataSetChanged();
								game = games.get(0);
								nameTextView.setText(game.getName());
								new GetGameArea().execute();
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class GetGameArea extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
			gameAreas.clear();
			gameAreaAdapter.notifyDataSetChanged();
			gameArea = new ModelGame();
			areaTextView.setText("");
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("gameid", game.getId()));
			return netUtil.postWithoutCookie(API.API_BIANMIN_GAME_AREA,
					nameValuePairs, true, true);
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
								ModelGame area = new ModelGame(
										array.optJSONObject(i));
								if (!TextUtils.isEmpty(area.getArea())) {
									gameAreas.add(area);
								}
							}
							if (gameAreas.size() > 0) {
								gameAreaAdapter.notifyDataSetChanged();
								gameArea = gameAreas.get(0);
								areaTextView.setText(gameArea.getArea());
								new GetGameServer().execute();
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class GetGameServer extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoading();
			gameServers.clear();
			gameServerAdapter.notifyDataSetChanged();
			gameServer = new ModelGame();
			serverTextView.setText("");
			serverTextView.setHint("该游戏区域无游戏服");
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("gameid", game.getId()));
			nameValuePairs.add(new BasicNameValuePair("area", gameArea
					.getArea()));
			return netUtil.postWithoutCookie(API.API_BIANMIN_GAME_SERVER,
					nameValuePairs, true, true);
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
								ModelGame server = new ModelGame(
										array.optJSONObject(i));
								if (!TextUtils.isEmpty(server.getServer())) {
									gameServers.add(server);
								}
							}
							if (gameServers.size() > 0) {
								gameServerAdapter.notifyDataSetChanged();
								gameServer = gameServers.get(0);
								serverTextView.setText(gameServer.getServer());
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
