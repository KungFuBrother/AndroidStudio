package com.smartown.controller.mission;

import android.util.Log;

public abstract class RequestListener extends MissionListener {

    protected abstract void onStart();

    protected void onCache(RequestMessage requestMessage) {

    }

    protected abstract void onFail(MissionMessage missionMessage);

    protected abstract void onSuccess(RequestMessage requestMessage);

    protected abstract void onFinish();


    @Override
    protected void onMissionStart() {
        Log.i("Request", "onMissionStart");
        onStart();
    }

    @Override
    protected void onMissionCache(MissionMessage missionMessage) {
        Log.i("Request", "onMissionCache " + (RequestMessage) missionMessage);
        onCache((RequestMessage) missionMessage);
    }

    @Override
    protected void onMissionFail(MissionMessage missionMessage) {
        Log.i("Request", "onMissionFail " + missionMessage);
        onFail(missionMessage);
    }

    @Override
    protected void onMissionSuccess(MissionMessage missionMessage) {
        Log.i("Request", "onMissionSuccess " + (RequestMessage) missionMessage);
        onSuccess((RequestMessage) missionMessage);
    }

    @Override
    protected void onMissionFinish() {
        Log.i("Request", "onMissionFinish");
        onFinish();
    }
}
