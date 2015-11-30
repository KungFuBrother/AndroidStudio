package yitgogo.consumer.home.task;

import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.NetUtil;
import android.os.AsyncTask;

public class GetBrand extends AsyncTask<Boolean, Void, String> {

	@Override
	protected String doInBackground(Boolean... params) {
		return NetUtil.getInstance().postWithoutCookie(API.API_HOME_BRAND,
				null, params[0], true);
	}

}
