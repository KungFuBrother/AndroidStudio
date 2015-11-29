package yitgogo.consumer.tools;

import android.content.Context;

import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by Tiger on 2015-11-13.
 */
public class MissionController {

    private static OkHttpClient okHttpClient;
//    private static Map<Context, List<Call>> calls = Collections
//            .synchronizedMap(new WeakHashMap<Context, List<Call>>());

    public static void init(Context context) {
        okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(5, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(5, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(5, TimeUnit.SECONDS);
        okHttpClient.setConnectionPool(new ConnectionPool(5, 15 * 1000));
    }

    public static void cancelNetworkMission(Context context) {
        okHttpClient.cancel(context);
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    //    private static void syncCalls(Context context, Call call) {
//        List<Call> contextCalls = new ArrayList<>();
//        synchronized (calls) {
//            contextCalls = calls.get(context);
//            if (contextCalls == null) {
//                contextCalls = Collections
//                        .synchronizedList(new LinkedList<Call>());
//                calls.put(context, contextCalls);
//            }
//        }
//        contextCalls.add(call);
//        Iterator<Call> iterator = contextCalls.iterator();
//        while (iterator.hasNext()) {
//            if (iterator.next().isCanceled()) {
//                iterator.remove();
//            }
//        }
//    }
//
//    public static void cancelMissions(Context context) {
//        List<Call> contextCalls = new ArrayList<>();
//        if (calls.containsKey(context)) {
//            contextCalls = calls.get(context);
//            calls.remove(context);
//            if (contextCalls != null) {
//                for (Call call : contextCalls) {
//                    if (call != null) {
//                        if (!call.isCanceled()) {
//                            call.cancel();
//                        }
//                    }
//                }
//            }
//        }
//    }

}
