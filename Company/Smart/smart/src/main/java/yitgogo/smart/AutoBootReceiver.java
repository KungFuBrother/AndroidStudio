package yitgogo.smart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent enterIntent = new Intent(context, EntranceActivity.class);
			enterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(enterIntent);
		}
	}

}
