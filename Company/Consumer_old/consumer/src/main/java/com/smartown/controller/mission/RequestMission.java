package com.smartown.controller.mission;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class RequestMission extends Mission {


    HttpURLConnection httpURLConnection;

    Request request;
    RequestListener requestListener;

    public RequestMission(Request request, RequestListener requestListener) {
        this.request = request;
        this.requestListener = requestListener;
    }

    private void request() {
        if (isCanceled()) {
            return;
        }
        try {
            Log.i("Request", "url:" + request.getHost() + request.getUrl());
            URL url = new URL(request.getHost() + request.getUrl());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false;
            httpURLConnection.setDoInput(true);// 设置是否从httpUrlConnection读入，默认情况下是true;
            httpURLConnection.setUseCaches(false); // Post 请求不能使用缓存
            httpURLConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");// 设定传送的内容类型是可序列化的java对象(如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
            httpURLConnection.setRequestMethod("POST");// 设定请求的方法为"POST"，默认是GET
            httpURLConnection.setConnectTimeout(5000);//连接超时 单位毫秒
            httpURLConnection.setReadTimeout(5000);//读取超时 单位毫秒
            if (request.isUseCookie()) {
                httpURLConnection.setRequestProperty("Cookie", CookieController.getCookie(request.getHost()));
            }
            if (!request.getRequestParams().isEmpty()) {
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < request.getRequestParams().size(); i++) {
                    if (i > 0) {
                        stringBuffer.append("&");
                    }
                    stringBuffer.append(request.getRequestParams().get(i).getKey());
                    stringBuffer.append("=");
                    stringBuffer.append(request.getRequestParams().get(i).getValue());
                }
                if (isCanceled()) {
                    return;
                }
                Log.i("Request", "parameters:" + stringBuffer);
                OutputStream outputStream = httpURLConnection.getOutputStream();// 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，所以在开发中不调用上述的connect()也可以)。
                outputStream.write(stringBuffer.toString().getBytes());
            }
            if (isCanceled()) {
                return;
            }
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (request.isSaveCookie()) {
                    List<String> cookies = httpURLConnection.getHeaderFields().get("Set-Cookie");
                    if (cookies != null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < cookies.size(); i++) {
                            if (i > 0) {
                                stringBuilder.append(";");
                            }
                            stringBuilder.append(cookies.get(i));
                        }
                    }
                }
                StringBuilder stringBuilder = new StringBuilder();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                inputStream.close();
                if (isCanceled()) {
                    return;
                }
                if (!TextUtils.isEmpty(stringBuilder.toString())) {
                    requestListener.sendMessage(new RequestMessage(MissionListener.PROGRESS_SUCCESS, "PROGRESS_SUCCESS", stringBuilder.toString()));
                }
            } else {
                if (isCanceled()) {
                    return;
                }
                requestListener.sendMessage(new MissionMessage(MissionListener.PROGRESS_FAILED, "PROGRESS_FAILED" + " " + String.valueOf(responseCode)));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            if (isCanceled()) {
                return;
            }
            requestListener.sendMessage(new MissionMessage(MissionListener.PROGRESS_FAILED, e.getMessage()));
        } catch (IOException e) {
            e.printStackTrace();
            if (isCanceled()) {
                return;
            }
            requestListener.sendMessage(new MissionMessage(MissionListener.PROGRESS_FAILED, e.getMessage()));
        }
    }

    @Override
    public void start() {
        requestListener.sendMessage(new MissionMessage(MissionListener.PROGRESS_START, "PROGRESS_START"));
        request();
        cacel();
        requestListener.sendMessage(new MissionMessage(MissionListener.PROGRESS_FINISH, "PROGRESS_FINISH"));
    }

}
