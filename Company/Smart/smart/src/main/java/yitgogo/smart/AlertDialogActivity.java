package yitgogo.smart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;

public class AlertDialogActivity extends BaseActivity {

	private TextView titleTextView;

	private ImageView closeButton;

	private TextView disconnectTextView;

	private String fragmentName = "", title = "";
	private Bundle parameters;
	private Fragment fragment;
	private Bundle bundle;

	BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		View view = LayoutInflater.from(this).inflate(
				R.layout.activity_dialog_alert, null);
		LayoutParams layoutParams = new LayoutParams(880,
				LayoutParams.WRAP_CONTENT);
		setContentView(view, layoutParams);
		init();
		findViews();
	}

	@Override
	protected void onResume() {
		initReceiver();
		super.onResume();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(broadcastReceiver);
		super.onPause();
	}

	private void init() {
		bundle = getIntent().getExtras();
		if (bundle != null) {
			if (bundle.containsKey("fragmentName")) {
				fragmentName = bundle.getString("fragmentName");
			}
			if (bundle.containsKey("title")) {
				title = bundle.getString("title");
			}
			if (bundle.containsKey("parameters")) {
				parameters = bundle.getBundle("parameters");
			}
		}
		if (fragmentName.length() > 0) {
			try {
				fragment = (Fragment) Class.forName(fragmentName).newInstance();
				if (parameters != null) {
					fragment.setArguments(parameters);
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void findViews() {
		titleTextView = (TextView) findViewById(R.id.dialog_activity_title);
		closeButton = (ImageView) findViewById(R.id.dialog_activity_close);
		disconnectTextView = (TextView) findViewById(R.id.dialog_activity_disconnect);
		initViews();
		registerViews();
	}

	@Override
	protected void initViews() {
		titleTextView.setText(title);
		if (fragment != null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.dialog_activity_content, fragment).commit();
		}
	}

	@Override
	protected void registerViews() {
		closeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		disconnectTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Settings.ACTION_SETTINGS);
				startActivity(intent);
			}
		});
	}

	private void initReceiver() {
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(
						ConnectivityManager.CONNECTIVITY_ACTION)) {
					checkConnection();
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(broadcastReceiver, intentFilter);
	}

	/**
	 * 判断是否连接网络
	 * 
	 * @return
	 */
	protected boolean isConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager.getActiveNetworkInfo() != null) {
			if (connectivityManager.getActiveNetworkInfo().isAvailable()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private void checkConnection() {
		if (isConnected()) {
			disconnectTextView.setVisibility(View.GONE);
		} else {
			disconnectTextView.setVisibility(View.VISIBLE);
		}
	}

}
