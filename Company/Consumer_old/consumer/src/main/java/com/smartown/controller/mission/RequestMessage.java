package com.smartown.controller.mission;

public class RequestMessage extends MissionMessage {

    String result = "";

    public RequestMessage(int progressStatus, String message, String result) {
        super(progressStatus, message);
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "RequestMessage [progressStatus=" + progressStatus
                + ", message=" + message + ", result=" + result + "]";
    }

}
