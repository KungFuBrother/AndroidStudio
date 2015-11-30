package yitgogo.consumer.suning.model;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tiger on 2015-10-19.
 *
 * @json {
 * "message": "ok",
 * "state": "SUCCESS",
 * "cacheKey": null,
 * "dataList": [],
 * "totalCount": 1,
 * "dataMap": {
 * "time": "2015-10-19 17:54:53",
 * "token": "6ed890ef8e4a0ca4ba2af0bd2203bd6c"
 * },
 * "object": null
 * }
 */
public class ModelSignature {

    private String token = "";

    public ModelSignature() {
    }

    public ModelSignature(JSONObject object) {
        if (object != null) {
            if (object.has("token")) {
                if (!object.optString("token").equalsIgnoreCase("null")) {
                    token = object.optString("token");
                }
            }
        }
    }


    public String getToken() {
        return token;
    }

}
