package yitgogo.consumer.home.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.NetUtil;
import android.os.AsyncTask;

public class GetSaleTimes extends AsyncTask<Boolean, Void, String> {

	@Override
	protected String doInBackground(Boolean... params) {
		List<NameValuePair> activity = new ArrayList<NameValuePair>();
		activity.add(new BasicNameValuePair("strno", Store.getStore()
				.getStoreNumber()));
		activity.add(new BasicNameValuePair("flag", "1"));
		return NetUtil.getInstance().postWithoutCookie(API.API_SALE_CLASS,
				activity, params[0], true);
	}

}
