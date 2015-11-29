package yitgogo.smart.bianmin.game.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.bianmin.game.model.ModelGame;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.view.Notify;
import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

public class GameFilterFragment extends BaseNotifyFragment {

	TextView nameTextView, areaTextView, serverTextView;
	TextView searchButton, hideButton;
	LinearLayout fragmentLayout;

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
			getGames();
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
		searchButton = (TextView) contentView
				.findViewById(R.id.game_filter_search);
		hideButton = (TextView) contentView.findViewById(R.id.game_filter_hide);
		fragmentLayout = (LinearLayout) contentView
				.findViewById(R.id.game_filter_content);
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
		hideButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fragmentLayout.setVisibility(View.GONE);
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
			GameChargeFragment gameChargeFragment = new GameChargeFragment();
			gameChargeFragment.setArguments(bundle);
			getFragmentManager().beginTransaction()
					.replace(R.id.game_filter_fragment, gameChargeFragment)
					.commit();
			fragmentLayout.setVisibility(View.VISIBLE);
		}
	}

	class GameDialog extends DialogFragment {

		View dialogView;
		ListView listView;
		TextView titleTextView;
		ImageView closeButton;

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
			dialog.setContentView(dialogView, new LayoutParams(360, 480));
			return dialog;
		}

		private void findViews() {
			dialogView = layoutInflater.inflate(R.layout.dialog_list, null);
			titleTextView = (TextView) dialogView
					.findViewById(R.id.dialog_title);
			closeButton = (ImageView) dialogView
					.findViewById(R.id.dialog_close);
			listView = (ListView) dialogView.findViewById(R.id.dialog_list);
			initViews();
		}

		private void initViews() {
			titleTextView.setText("选择游戏名称");
			listView.setAdapter(gameAdapter);
			closeButton.setOnClickListener(new OnClickListener() {

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
					getGameArea();
					dismiss();
				}
			});
		}
	}

	class GameAreaDialog extends DialogFragment {

		View dialogView;
		ListView listView;
		TextView titleTextView;
		ImageView closeButton;

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
			dialog.setContentView(dialogView, new LayoutParams(360, 480));
			return dialog;
		}

		private void findViews() {
			dialogView = layoutInflater.inflate(R.layout.dialog_list, null);
			titleTextView = (TextView) dialogView
					.findViewById(R.id.dialog_title);
			closeButton = (ImageView) dialogView
					.findViewById(R.id.dialog_close);
			listView = (ListView) dialogView.findViewById(R.id.dialog_list);
			initViews();
		}

		private void initViews() {
			titleTextView.setText("选择游戏区域");
			listView.setAdapter(gameAreaAdapter);
			closeButton.setOnClickListener(new OnClickListener() {

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
					getGameServer();
					dismiss();
				}
			});
		}
	}

	class GameServerDialog extends DialogFragment {

		View dialogView;
		ListView listView;
		TextView titleTextView;
		ImageView closeButton;

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
			dialog.setContentView(dialogView, new LayoutParams(360, 480));
			return dialog;
		}

		private void findViews() {
			dialogView = layoutInflater.inflate(R.layout.dialog_list, null);
			titleTextView = (TextView) dialogView
					.findViewById(R.id.dialog_title);
			closeButton = (ImageView) dialogView
					.findViewById(R.id.dialog_close);
			listView = (ListView) dialogView.findViewById(R.id.dialog_list);
			initViews();
		}

		private void initViews() {
			titleTextView.setText("选择游戏服");
			listView.setAdapter(gameServerAdapter);
			closeButton.setOnClickListener(new OnClickListener() {

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
				convertView = layoutInflater.inflate(R.layout.item_simple_text,
						null);
				holder.textView = (TextView) convertView
						.findViewById(R.id.simple_text);
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
				convertView = layoutInflater.inflate(R.layout.item_simple_text,
						null);
				holder.textView = (TextView) convertView
						.findViewById(R.id.simple_text);
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
				convertView = layoutInflater.inflate(R.layout.item_simple_text,
						null);
				holder.textView = (TextView) convertView
						.findViewById(R.id.simple_text);
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

	private void getGames() {
		games.clear();
		gameAdapter.notifyDataSetChanged();
		NetworkContent networkContent = new NetworkContent(
				API.API_BIANMIN_GAME_LIST);
		MissionController.startNetworkMission(getActivity(), networkContent,
				new OnNetworkListener() {
					@Override
					public void onStart() {
						super.onStart();
						showLoading();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						hideLoading();
					}

					@Override
					public void onSuccess(NetworkMissionMessage message) {
						super.onSuccess(message);
						if (!TextUtils.isEmpty(message.getResult())) {
							try {
								JSONObject object = new JSONObject(message
										.getResult());
								if (object.optString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONArray array = object
											.optJSONArray("dataList");
									if (array != null) {
										for (int i = 0; i < array.length(); i++) {
											games.add(new ModelGame(array
													.optJSONObject(i)));
										}
										if (games.size() > 0) {
											gameAdapter.notifyDataSetChanged();
											game = games.get(0);
											nameTextView.setText(game.getName());
											getGameArea();
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

	private void getGameArea() {
		gameAreas.clear();
		gameAreaAdapter.notifyDataSetChanged();
		gameArea = new ModelGame();
		areaTextView.setText("");
		NetworkContent networkContent = new NetworkContent(
				API.API_BIANMIN_GAME_AREA);
		networkContent.addParameters("gameid", game.getId());
		MissionController.startNetworkMission(getActivity(), networkContent,
				new OnNetworkListener() {
					@Override
					public void onStart() {
						super.onStart();
						showLoading();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						hideLoading();
					}

					@Override
					public void onSuccess(NetworkMissionMessage message) {
						super.onSuccess(message);
						if (!TextUtils.isEmpty(message.getResult())) {
							try {
								JSONObject object = new JSONObject(message
										.getResult());
								if (object.optString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONArray array = object
											.optJSONArray("dataList");
									if (array != null) {
										for (int i = 0; i < array.length(); i++) {
											ModelGame area = new ModelGame(
													array.optJSONObject(i));
											if (!TextUtils.isEmpty(area
													.getArea())) {
												gameAreas.add(area);
											}
										}
										if (gameAreas.size() > 0) {
											gameAreaAdapter
													.notifyDataSetChanged();
											gameArea = gameAreas.get(0);
											areaTextView.setText(gameArea
													.getArea());
											getGameServer();
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

	private void getGameServer() {
		gameServers.clear();
		gameServerAdapter.notifyDataSetChanged();
		gameServer = new ModelGame();
		serverTextView.setText("");
		serverTextView.setHint("该游戏区域无游戏服");
		NetworkContent networkContent = new NetworkContent(
				API.API_BIANMIN_GAME_SERVER);
		networkContent.addParameters("gameid", game.getId());
		networkContent.addParameters("area", gameArea.getArea());
		MissionController.startNetworkMission(getActivity(), networkContent,
				new OnNetworkListener() {
					@Override
					public void onStart() {
						super.onStart();
						showLoading();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						hideLoading();
					}

					@Override
					public void onSuccess(NetworkMissionMessage message) {
						super.onSuccess(message);
						if (!TextUtils.isEmpty(message.getResult())) {
							try {
								JSONObject object = new JSONObject(message
										.getResult());
								if (object.optString("state").equalsIgnoreCase(
										"SUCCESS")) {
									JSONArray array = object
											.optJSONArray("dataList");
									if (array != null) {
										for (int i = 0; i < array.length(); i++) {
											ModelGame server = new ModelGame(
													array.optJSONObject(i));
											if (!TextUtils.isEmpty(server
													.getServer())) {
												gameServers.add(server);
											}
										}
										if (gameServers.size() > 0) {
											gameServerAdapter
													.notifyDataSetChanged();
											gameServer = gameServers.get(0);
											serverTextView.setText(gameServer
													.getServer());
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
}
