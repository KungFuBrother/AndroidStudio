package yitgogo.consumer.home.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.NetUtil;
import android.os.AsyncTask;

public class GetAds extends AsyncTask<Boolean, Void, String> {

	@Override
	protected String doInBackground(Boolean... params) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("number", Store.getStore()
				.getStoreNumber()));
		return NetUtil.getInstance().postWithoutCookie(API.API_ADS,
				nameValuePairs, params[0], true);
	}

}
