package com.smartown.controller.mission;

import android.content.Context;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MissionController {

    static ExecutorService executorService = Executors.newFixedThreadPool(5);
    static Map<Context, List<Mission>> missions = Collections.synchronizedMap(new WeakHashMap<Context, List<Mission>>());

    /**
     * 启动网络任务
     *
     * @param context
     * @param request
     * @param requestListener
     */
    public static void startNetworkMission(Context context, Request request, RequestListener requestListener) {
        RequestMission requestMission = new RequestMission(request, requestListener);
        executorService.submit(requestMission);
        syncMissions(context, requestMission);
    }

    public static void cancelMissions(Context context) {
        List<Mission> contextMissions = missions.get(context);
        if (contextMissions != null) {
            for (Mission mission : contextMissions) {
                if (mission != null) {
                    mission.cacel();
                }
            }
        }
        missions.remove(context);
    }

    public static void syncMissions(Context context, Mission mission) {
        List<Mission> contextMissions;
        synchronized (missions) {
            contextMissions = missions.get(context);
            if (contextMissions == null) {
                contextMissions = Collections.synchronizedList(new LinkedList<Mission>());
                missions.put(context, contextMissions);
            }
        }
        contextMissions.add(mission);
    }

}
