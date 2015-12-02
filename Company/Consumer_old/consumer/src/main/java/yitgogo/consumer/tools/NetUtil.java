package yitgogo.consumer.tools;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.user.model.User;

/**
 * 网络工具类
 *
 * @author Tiger
 */
public class NetUtil {

    static Context context;
    static CacheDatabase cacheDatabase;
    static NetUtil netUtil;

    public static void init(Context c) {
        context = c;
        netUtil = new NetUtil();
        cacheDatabase = new CacheDatabase(c);
    }

    public static NetUtil getInstance() {
        return netUtil;
    }

    /**
     * 显示网络请求参数
     *
     * @param url
     * @param nameValuePairs
     */
    private void showParameters(String url, List<NameValuePair> nameValuePairs) {
        LogUtil.logInfo("Request Url", url);
        if (nameValuePairs == null) {
            LogUtil.logInfo("Request Parameters", "No Parameters");
        } else {
            LogUtil.logInfo("Request Parameters", nameValuePairs.toString());
        }
    }

    /**
     * 主要是用于登录时的网络请求方法，登录并保存会话cookie
     *
     * @param url
     * @param nameValuePairs
     * @return
     */
    public String postAndSaveCookie(String url, List<NameValuePair> nameValuePairs) {
        showParameters(url, nameValuePairs);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("version", PackageTool.getVersionName());
        try {
            if (nameValuePairs != null) {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
                        HTTP.UTF_8));
            }
            HttpClient client = getHttpClient();
            // 请求超时
            client.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
            // // 读取超时
            // client.getParams().setParameter(
            // CoreConnectionPNames.SO_TIMEOUT, 5000);
            HttpResponse httpResponse = client.execute(httpPost);
            int statue = httpResponse.getStatusLine().getStatusCode();
            if (statue == 200) {
                // 保存cookie
                List<Cookie> cookies = ((AbstractHttpClient) client)
                        .getCookieStore().getCookies();
                if (cookies.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < cookies.size(); i++) {
                        if (i > 0) {
                            builder.append(";");
                        }
                        builder.append(cookies.get(i).getName() + "="
                                + cookies.get(i).getValue());
                    }
                    LogUtil.logInfo("Request Save_Cookie", builder.toString());
                    if (url.startsWith(API.IP_MONEY)) {
                        Content.saveStringContent(
                                Parameters.CACHE_KEY_COOKIE_MONEY,
                                builder.toString());
                    } else {
                        Content.saveStringContent(Parameters.CACHE_KEY_COOKIE,
                                builder.toString());
                    }
                }
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
            } else {
                LogUtil.logError("Request Status", statue + "");
                return "";
            }
        } catch (ConnectTimeoutException e) {
            LogUtil.logError("Request Status", "网络连接超时");
            return "";
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        LogUtil.logInfo("Request Result", stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * 需要验证权限的接口，每次请求时都要加入header cookie
     *
     * @param url
     * @param nameValuePairs
     * @return
     */
    public String postWithCookie(String url, List<NameValuePair> nameValuePairs) {
        showParameters(url, nameValuePairs);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("version", PackageTool.getVersionName());
        if (url.startsWith(API.IP_MONEY)) {
            if (Content.contain(Parameters.CACHE_KEY_COOKIE_MONEY)) {
                LogUtil.logInfo("Request Put_Cookie", Content.getStringContent(
                        Parameters.CACHE_KEY_COOKIE_MONEY, ""));
                httpPost.setHeader("Cookie", Content.getStringContent(
                        Parameters.CACHE_KEY_COOKIE_MONEY, ""));
            }
        } else {
            if (Content.contain(Parameters.CACHE_KEY_COOKIE)) {
                LogUtil.logInfo("Request Put_Cookie", Content.getStringContent(
                        Parameters.CACHE_KEY_COOKIE, ""));
                httpPost.setHeader("Cookie", Content.getStringContent(
                        Parameters.CACHE_KEY_COOKIE, ""));
            }
        }
        try {
            if (nameValuePairs != null) {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
                        HTTP.UTF_8));
            }
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
            } else {
                LogUtil.logError("Request Status", statue + "");
                return "";
            }
        } catch (ConnectTimeoutException e) {
            LogUtil.logError("Request Status", "网络连接超时");
            return "";
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        // 判断返回的的字符串是否包含以下这些会话过期的标志
        if (stringBuilder.toString().contains("NAUTH")) {
            LogUtil.logError("Request Status", "会话过期");
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("phone", User.getUser()
                    .getPhone()));
            valuePairs.add(new BasicNameValuePair("password", Content
                    .getStringContent(Parameters.CACHE_KEY_USER_PASSWORD, "")));
            String result = postAndSaveCookie(API.API_USER_LOGIN, valuePairs);
            if (result.length() > 0) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        Content.saveStringContent(
                                Parameters.CACHE_KEY_MONEY_SN,
                                object.optString("cacheKey"));
                        JSONObject userObject = object.optJSONObject("object");
                        if (userObject != null) {
                            Content.saveStringContent(
                                    Parameters.CACHE_KEY_USER_JSON,
                                    userObject.toString());
                            User.init(context);
                            return postWithCookie(url, nameValuePairs);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Content.removeContent(Parameters.CACHE_KEY_USER_JSON);
            Content.removeContent(Parameters.CACHE_KEY_USER_PASSWORD);
            Content.removeContent(Parameters.CACHE_KEY_COOKIE);
            Content.removeContent(Parameters.CACHE_KEY_MONEY_SN);
            User.init(context);
            MoneyAccount.init(null);
            return "";
        }
        LogUtil.logInfo("Request Result", stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * 普通的网络请求
     *
     * @param url
     * @param nameValuePairs
     * @return
     */
    // public String postWithoutCookie(String url,
    // List<NameValuePair> nameValuePairs) {
    // showParameters(url, nameValuePairs);
    // StringBuilder stringBuilder = new StringBuilder();
    // stringBuilder.append("");
    // HttpPost httpRequest = new HttpPost(url);
    // try {
    // if (nameValuePairs != null) {
    // httpRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs,
    // HTTP.UTF_8));
    // }
    // HttpClient client = new DefaultHttpClient();
    // client.getParams().setParameter(
    // CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
    // HttpResponse httpResponse = client.execute(httpRequest);
    // int statue = httpResponse.getStatusLine().getStatusCode();
    // if (statue == 200) {
    // HttpEntity entity = httpResponse.getEntity();
    // InputStream inputStream = entity.getContent();
    // BufferedReader reader = new BufferedReader(
    // new InputStreamReader(inputStream));
    // String line;
    // while ((line = reader.readLine()) != null) {
    // stringBuilder.append(line);
    // }
    // reader.close();
    // inputStream.close();
    // } else {
    // LogUtil.logError("Request Status", statue + "");
    // return "";
    // }
    // } catch (ConnectTimeoutException e) {
    // LogUtil.logError("Request Status", "网络连接超时");
    // return "";
    // } catch (IllegalStateException e) {
    // e.printStackTrace();
    // return "";
    // } catch (IOException e) {
    // e.printStackTrace();
    // return "";
    // }
    // LogUtil.logInfo("Request Result", stringBuilder.toString());
    // return stringBuilder.toString();
    // }

    /**
     * 普通网络请求，
     *
     * @param url            接口
     * @param nameValuePairs 参数
     * @param getCache       是否获取缓存
     * @param saveCache      是否缓存数据
     * @return 请求数据
     */
    public String postWithoutCookie(String url,
                                    List<NameValuePair> nameValuePairs, boolean getCache,
                                    boolean saveCache) {
        showParameters(url, nameValuePairs);
        if (getCache) {
            if (cacheDatabase.containData(url, nameValuePairs)) {
                ContentValues contentValues = cacheDatabase.getResultData(url,
                        nameValuePairs);
                if (contentValues != null) {
                    long saveTime = Long.parseLong(contentValues
                            .getAsString("time"));
                    long currentTime = new Date().getTime();
                    if ((currentTime - saveTime) < 21600000) {
                        String cacheString = contentValues.getAsString("data");
                        if (!TextUtils.isEmpty(cacheString)) {
                            LogUtil.logInfo("Request Get Cache", cacheString);
                            return cacheString;
                        }
                    } else {
                        LogUtil.logInfo("Request Get Cache",
                                "cache out of date");
                    }
                }
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("version", PackageTool.getVersionName());
        try {
            if (nameValuePairs != null) {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
                        HTTP.UTF_8));
            }
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
            } else {
                LogUtil.logError("Request Status", statue + "");
                return "";
            }
        } catch (ConnectTimeoutException e) {
            LogUtil.logError("Request Status", "网络连接超时");
            return "";
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        if (saveCache) {
            saveCache(url, nameValuePairs, stringBuilder.toString());
        }
        LogUtil.logInfo("Request Result", stringBuilder.toString());
        return stringBuilder.toString();
    }

    // public String postWithoutCookieSaveCache(String url,
    // List<NameValuePair> nameValuePairs) {
    // showParameters(url, nameValuePairs);
    // StringBuilder stringBuilder = new StringBuilder();
    // stringBuilder.append("");
    // HttpPost httpRequest = new HttpPost(url);
    // try {
    // if (nameValuePairs != null) {
    // httpRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs,
    // HTTP.UTF_8));
    // }
    // HttpClient client = getHttpClient();
    // client.getParams().setParameter(
    // CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
    // HttpResponse httpResponse = client.execute(httpRequest);
    // int statue = httpResponse.getStatusLine().getStatusCode();
    // if (statue == 200) {
    // HttpEntity entity = httpResponse.getEntity();
    // InputStream inputStream = entity.getContent();
    // BufferedReader reader = new BufferedReader(
    // new InputStreamReader(inputStream));
    // String line;
    // while ((line = reader.readLine()) != null) {
    // stringBuilder.append(line);
    // }
    // reader.close();
    // inputStream.close();
    // } else {
    // LogUtil.logError("Request Status", statue + "");
    // return "";
    // }
    // } catch (ConnectTimeoutException e) {
    // LogUtil.logError("Request Status", "网络连接超时");
    // return "";
    // } catch (IllegalStateException e) {
    // e.printStackTrace();
    // return "";
    // } catch (IOException e) {
    // e.printStackTrace();
    // return "";
    // }
    // saveCache(url, nameValuePairs, stringBuilder.toString());
    // LogUtil.logInfo("Request Result", stringBuilder.toString());
    // return stringBuilder.toString();
    // }

    private void saveCache(String url, List<NameValuePair> nameValuePairs,
                           String result) {
        LogUtil.logInfo("Request Save Cache", result);
        ContentValues values = new ContentValues();
        values.put(CacheDatabase.column_url, url);
        if (nameValuePairs != null) {
            values.put(CacheDatabase.column_parameters, nameValuePairs
                    .toString().trim());
        } else {
            values.put(CacheDatabase.column_parameters, "");
        }
        values.put(CacheDatabase.column_result, result);
        values.put(CacheDatabase.column_time, new Date().getTime());
        if (cacheDatabase.containData(url, nameValuePairs)) {
            cacheDatabase.updateData(values, url, nameValuePairs);
        } else {
            cacheDatabase.insertData(values);
        }
    }

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

}
