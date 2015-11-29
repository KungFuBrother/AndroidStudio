package yitgogo.smart.tools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class NetworkMission extends Mission {

    HttpPost httpPost;

    NetworkContent networkContent;
    OnNetworkListener onNetworkListener;

    public NetworkMission(NetworkContent networkContent,
                          OnNetworkListener onNetworkListener) {
        this.networkContent = networkContent;
        this.onNetworkListener = onNetworkListener;
        httpPost = new HttpPost(networkContent.getUrl());
    }

    @Override
    public void cacelMission() {
        super.cacelMission();
        httpPost.abort();
    }

    private void requset() {
        showRequestContent();
        StringBuilder stringBuilder = new StringBuilder();
        // if (CacheDatabase.getInstance().containData(networkContent)) {
        // String cacheString = CacheDatabase.getInstance().getResultData(
        // networkContent);
        // if (!TextUtils.isEmpty(cacheString)) {
        // if (isCanceled) {
        // return;
        // }
        // onNetworkListener.sendMessage(
        // OnMissionListener.PROGRESS_CACHE, "获取到缓存数据",
        // cacheString);
        // }
        // }
        try {
            httpPost.setHeader("version", PackageTool.getVersionName());
            httpPost.setEntity(new UrlEncodedFormEntity(networkContent
                    .getNameValuePairs(), HTTP.UTF_8));
            HttpClient client = getHttpClient();
            client.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
            HttpResponse httpResponse = client.execute(httpPost);
            int statue = httpResponse.getStatusLine().getStatusCode();
            if (statue == 200) {
                HttpEntity entity = httpResponse.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
                inputStream.close();
                // if (TextUtils.isEmpty(stringBuilder.toString())) {
                // saveCache(stringBuilder.toString());
                // }
                if (isCanceled()) {
                    return;
                }
                onNetworkListener.sendMessage(new NetworkMissionMessage(
                        OnMissionListener.PROGRESS_SUCCESS, "访问服务器返回数据成功",
                        stringBuilder.toString()));
            } else {
                if (isCanceled()) {
                    return;
                }
                onNetworkListener.sendMessage(new NetworkMissionMessage(
                        OnMissionListener.PROGRESS_FAILED, "访问服务器失败，状态"
                        + statue, ""));
            }
        } catch (ConnectTimeoutException e) {
            if (isCanceled()) {
                return;
            }
            onNetworkListener.sendMessage(new NetworkMissionMessage(
                    OnMissionListener.PROGRESS_FAILED, "访问服务器失败，连接超时", ""));
            e.printStackTrace();
        } catch (IllegalStateException e) {
            if (isCanceled()) {
                return;
            }
            onNetworkListener.sendMessage(new NetworkMissionMessage(
                    OnMissionListener.PROGRESS_FAILED, e.getMessage(), ""));
            e.printStackTrace();
        } catch (IOException e) {
            if (isCanceled()) {
                return;
            }
            onNetworkListener.sendMessage(new NetworkMissionMessage(
                    OnMissionListener.PROGRESS_FAILED, e.getMessage(), ""));
            e.printStackTrace();
        }
        if (isCanceled()) {
            return;
        }
    }

    /**
     * 显示网络请求参数
     */
    private void showRequestContent() {
        LogUtil.logInfo("RequestTask---PARAMS", networkContent.toString());
    }

    // private void saveCache(String result) {
    // LogUtil.logInfo("RequestTask---SAVE_CACHE", result);
    // ContentValues values = new ContentValues();
    // values.put(CacheDatabase.column_url, networkContent.getUrl());
    // values.put(CacheDatabase.column_parameters, networkContent
    // .getNameValuePairs().toString());
    // values.put(CacheDatabase.column_result, result);
    // if (CacheDatabase.getInstance().containData(networkContent)) {
    // CacheDatabase.getInstance().updateData(values, networkContent);
    // } else {
    // CacheDatabase.getInstance().insertData(values);
    // }
    // }

    private HttpClient getHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                    params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    @Override
    public void missionStart() {
        onNetworkListener.sendMessage(new NetworkMissionMessage(
                OnMissionListener.PROGRESS_START, "任务开始", ""));
        requset();
        onNetworkListener.sendMessage(new NetworkMissionMessage(
                OnMissionListener.PROGRESS_FINISH, "任务结束", ""));
    }
}
