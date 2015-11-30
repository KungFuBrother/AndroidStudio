package com.smartown.controller.mission;

/**
 * Created by Smartown on 15/11/29.
 */
public class RequestParam {

    String key = "", value = "";

    public RequestParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}