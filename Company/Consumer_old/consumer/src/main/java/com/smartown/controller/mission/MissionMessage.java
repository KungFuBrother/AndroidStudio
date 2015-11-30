package com.smartown.controller.mission;

public class MissionMessage {

    int progressStatus = MissionListener.PROGRESS_FAILED;
    String message = "";

    public MissionMessage(int progressStatus, String message) {
        this.message = message;
        this.progressStatus = progressStatus;
    }

    public int getProgressStatus() {
        return progressStatus;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "MissionMessage [progressStatus=" + progressStatus
                + ", message=" + message + "]";
    }

}
