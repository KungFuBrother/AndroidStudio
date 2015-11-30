package yitgogo.consumer.suning.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Json {
 * "price": 399,
 * "no": 1,
 * "name": "七彩虹(COLORFLY) 音频播放器 Pocket HiFi C3(4G)",
 * "img": [
 * "http://image1.suning.cn/content/catentries/00000000012035/000000000120351177/fullimage/000000000120351177_3.jpg",
 * "http://image4.suning.cn/content/catentries/00000000012035/000000000120351177/fullimage/000000000120351177_5.jpg",
 * "http://image5.suning.cn/content/catentries/00000000012035/000000000120351177/fullimage/000000000120351177_4.jpg",
 * "http://image2.suning.cn/content/catentries/00000000012035/000000000120351177/fullimage/000000000120351177_2.jpg",
 * "http://image3.suning.cn/content/catentries/00000000012035/000000000120351177/fullimage/000000000120351177_1.jpg"
 * ],
 * "attr": "Pocket HiFi C3(4G)",
 * "number": "120351177"
 * }
 */
public class ModelSuningOrderProduct {

    int no = 0;
    double price = 0;
    String name = "", attr = "", number = "";
    List<String> images = new ArrayList<>();

    public ModelSuningOrderProduct() {
    }

    public ModelSuningOrderProduct(JSONObject object) {
        if (object != null) {
            if (object.has("no")) {
                if (!object.optString("no").equalsIgnoreCase("null")) {
                    no = object.optInt("no");
                }
            }
            if (object.has("price")) {
                if (!object.optString("price").equalsIgnoreCase("null")) {
                    price = object.optDouble("price");
                }
            }
            if (object.has("name")) {
                if (!object.optString("name").equalsIgnoreCase("null")) {
                    name = object.optString("name");
                }
            }
            if (object.has("attr")) {
                if (!object.optString("attr").equalsIgnoreCase("null")) {
                    attr = object.optString("attr");
                }
            }
            if (object.has("number")) {
                if (!object.optString("number").equalsIgnoreCase("null")) {
                    number = object.optString("number");
                }
            }
            JSONArray imgArray = object.optJSONArray("img");
            if (imgArray != null) {
                for (int i = 0; i < imgArray.length(); i++) {
                    images.add(imgArray.optString(i));
                }
            }
        }
    }

    public int getNo() {
        return no;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public List<String> getImages() {
        return images;
    }

    public String getAttr() {
        return attr;
    }
}