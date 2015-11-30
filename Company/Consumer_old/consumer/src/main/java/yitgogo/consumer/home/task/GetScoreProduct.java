package yitgogo.consumer.home.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.NetUtil;
import android.os.AsyncTask;

public class GetScoreProduct extends AsyncTask<Boolean, Void, String> {

	@Override
	protected String doInBackground(Boolean... params) {
		List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
		valuePairs.add(new BasicNameValuePair("jgbh", Store.getStore()
				.getStoreNumber()));
		valuePairs.add(new BasicNameValuePair("pagenum", "1"));
		valuePairs.add(new BasicNameValuePair("pagesize", "8"));
		return NetUtil.getInstance().postWithoutCookie(
				API.API_SCORE_PRODUCT_LIST, valuePairs, params[0], true);
	}

}
