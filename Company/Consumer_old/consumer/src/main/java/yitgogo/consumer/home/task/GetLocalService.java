package yitgogo.consumer.home.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.NetUtil;
import android.os.AsyncTask;

public class GetLocalService extends AsyncTask<Boolean, Void, String> {

	@Override
	protected String doInBackground(Boolean... params) {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("pageNo", "1"));
		parameters.add(new BasicNameValuePair("pageSize", "3"));
		parameters.add(new BasicNameValuePair("organizationId", Store
				.getStore().getStoreId()));
		return NetUtil.getInstance().postWithoutCookie(
				API.API_LOCAL_BUSINESS_SERVICE, parameters, params[0], true);
	}

}
