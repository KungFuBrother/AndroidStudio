package yitgogo.consumer.tools;

/**
 * Created by Tiger on 2015-11-20.
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
