package com.smartown.controller.mission;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class MissionListener {

    public static final int PROGRESS_START = 0;
    public static final int PROGRESS_CACHE = 1;
    public static final int PROGRESS_FAILED = 2;
    public static final int PROGRESS_SUCCESS = 3;
    public static final int PROGRESS_FINISH = 4;

    Handler handler = new Handler(Looper.myLooper()) {
        public void handleMessage(Message msg) {
            operateMessage((MissionMessage) msg.obj);
        }
    };

    private void operateMessage(MissionMessage missionMessage) {
        switch (missionMessage.getProgressStatus()) {

            case PROGRESS_START:
                onMissionStart();
                break;

            case PROGRESS_CACHE:
                onMissionCache(missionMessage);
                break;

            case PROGRESS_FAILED:
                onMissionFail(missionMessage);
                break;

            case PROGRESS_SUCCESS:
                onMissionSuccess(missionMessage);
                break;

            case PROGRESS_FINISH:
                onMissionFinish();
                break;
            default:
                break;
        }
    }

    public void sendMessage(MissionMessage missionMessage) {
        handler.sendMessage(Message.obtain(handler, 0, missionMessage));
    }

    protected abstract void onMissionStart();

    protected abstract void onMissionCache(MissionMessage missionMessage);

    protected abstract void onMissionFail(MissionMessage missionMessage);

    protected abstract void onMissionSuccess(MissionMessage missionMessage);

    protected abstract void onMissionFinish();

}