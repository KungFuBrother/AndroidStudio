package yitgogo.smart.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;

public class NetworkContent {

	String url = "";
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	public NetworkContent() {
	}

	public NetworkContent(String url) {
		this.url = url;
	}

	public void addParameters(String name, String value) {
		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value)) {
			return;
		}
		nameValuePairs.add(new BasicNameValuePair(name, value));
	}

	public void addParameters(NameValuePair nameValuePair) {
		if (nameValuePair == null) {
			return;
		}
		nameValuePairs.add(nameValuePair);
	}

	public void addParameters(List<NameValuePair> valuePairs) {
		if (valuePairs == null) {
			return;
		}
		nameValuePairs.addAll(valuePairs);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<NameValuePair> getNameValuePairs() {
		return nameValuePairs;
	}

	public void setNameValuePairs(List<NameValuePair> nameValuePairs) {
		this.nameValuePairs = nameValuePairs;
	}

	@Override
	public String toString() {
		return "NetworkContent [url=" + url + ", nameValuePairs="
				+ nameValuePairs + "]";
	}

}
