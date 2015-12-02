package yitgogo.smart.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.util.Log;

public class MissionController {

    static ExecutorService executorService = Executors.newFixedThreadPool(5);
    static Map<Context, List<Mission>> missions = Collections
            .synchronizedMap(new WeakHashMap<Context, List<Mission>>());

    /**
     * 启动网络任务
     *
     * @param context
     * @param networkContent
     * @param onNetworkListener
     */
    public static void startNetworkMission(Context context,
                                           NetworkContent networkContent, OnNetworkListener onNetworkListener) {
        NetworkMission networkMission = new NetworkMission(networkContent,
                onNetworkListener);
        executorService.submit(networkMission);
        syncMissions(context, networkMission);
    }

    public static void startQrEncodeMission(Context context, String content,
                                            int imageWidth, OnQrEncodeListener onQrEncodeListener) {
        QrEncodeMission qrEncodeMission = new QrEncodeMission(content,
                imageWidth, onQrEncodeListener);
        executorService.submit(qrEncodeMission);
        syncMissions(context, qrEncodeMission);
    }

    public static void cancelMissions(Context context) {
        List<Mission> contextMissions = missions.get(context);
        missions.remove(context);
        if (contextMissions != null) {
            for (Mission mission : contextMissions) {
                if (mission != null) {
                    mission.cacelMission();
                }
            }
        }
        LogUtil.logInfo("MissionController", "getActiveCount---"
                + ((ThreadPoolExecutor) executorService).getActiveCount());
    }

    private static void syncMissions(Context context, Mission mission) {
        List<Mission> contextMissions;
        synchronized (missions) {
            contextMissions = missions.get(context);
            if (contextMissions == null) {
                contextMissions = Collections
                        .synchronizedList(new LinkedList<Mission>());
                missions.put(context, contextMissions);
            }
        }
        contextMissions.add(mission);

        Iterator<Mission> iterator = contextMissions.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isCanceled()) {
                iterator.remove();
            }
        }
        LogUtil.logInfo("MissionController", "getActiveCount---"
                + ((ThreadPoolExecutor) executorService).getActiveCount());
    }

    public static String post(String url, List<NameValuePair> nameValuePairs) {
        LogUtil.logInfo("Request url", url);
        StringBuilder stringBuilder = new StringBuilder();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("version", PackageTool.getVersionName());
        try {
            if (nameValuePairs != null) {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                LogUtil.logInfo("Request nameValuePairs", nameValuePairs.toString());
            }
            HttpClient client = new DefaultHttpClient();
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
        LogUtil.logInfo("Request Result", stringBuilder.toString());
        return stringBuilder.toString();
    }

}
