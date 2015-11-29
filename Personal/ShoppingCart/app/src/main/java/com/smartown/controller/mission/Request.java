package com.smartown.controller.mission;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class Request {

    boolean useCookie = false;
    String url = "";
    List<RequestParam> requestParams = new ArrayList<>();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUseCookie() {
        return useCookie;
    }

    public void setUseCookie(boolean useCookie) {
        this.useCookie = useCookie;
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
