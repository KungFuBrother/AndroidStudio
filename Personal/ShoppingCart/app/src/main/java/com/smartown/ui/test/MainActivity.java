package com.smartown.ui.test;

import android.app.Activity;
import android.os.Bundle;

import com.smartown.controller.R;
import com.smartown.controller.mission.CookieController;
import com.smartown.controller.mission.MissionController;
import com.smartown.controller.mission.MissionMessage;
import com.smartown.controller.mission.Request;
import com.smartown.controller.mission.RequestListener;
import com.smartown.controller.mission.RequestMessage;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CookieController.init(this);

        Request request = new Request();
        request.setUrl("http://www.baidu.com","");
        request.addRequestParam("test", "test");
        request.setSaveCookie(true);
        request.setUseCookie(true);
        MissionController.startNetworkMission(this, request, new RequestListener() {
            @Override
            protected void onStart() {
                System.out.println("onStart");
            }

            @Override
            protected void onFail(MissionMessage missionMessage) {
                System.out.println("onFail");
            }

            @Override
            protected void onSuccess(RequestMessage requestMessage) {
                System.out.println("onSuccess");
            }

            @Override
            protected void onFinish() {
                System.out.println("onFinish");
            }
        });
    }

}
