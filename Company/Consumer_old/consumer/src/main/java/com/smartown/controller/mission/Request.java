package com.smartown.controller.mission;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class Request {

    boolean saveCookie = false;
    boolean useCookie = false;

    String host = "";
    String url = "";

    List<RequestParam> requestParams = new ArrayList<>();

    public String getUrl() {
        return url;
    }

    public String getHost() {
        return host;
    }

    public void setUrl(String host, String url) {
        this.host = host;
        this.url = url;
    }

    public boolean isUseCookie() {
        return useCookie;
    }

    public void setUseCookie(boolean useCookie) {
        this.useCookie = useCookie;
    }

    public boolean isSaveCookie() {
        return saveCookie;
    }

    public void setSaveCookie(boolean saveCookie) {
        this.saveCookie = saveCookie;
    }

    public List<RequestParam> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(List<RequestParam> requestParams) {
        this.requestParams = requestParams;
    }

    public void addRequestParam(String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }
        requestParams.add(new RequestParam(key, value));
    }

}
