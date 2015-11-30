package yitgogo.consumer.product.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tiger
 * @JsonObject { "message": "ok", "state": "SUCCESS", "cacheKey": null,
 * "dataList": [], "totalCount": 1, "dataMap": { "listzu": [ { "id":
 * "33512", "attName": "白色37码", "img":
 * "http://images.yitos.net/images/public/20150608/52991433747828259.png"
 * , "number": "YT81764199692" }, { "id": "33513", "attName":
 * "白色37码", "img":
 * "http://images.yitos.net/images/public/20150608/52991433747828259.png"
 * , "number": "YT81764243379" } ], "imgs": [
 * "http://images.yitos.net/images/public/20150608/4441433747842430.png"
 * ], "cuxiaodate": "2015-07-21 00:00:00.0n2015-07-31 00:00:00.0",
 * "img":
 * "http://images.yitos.net/images/public/20150608/52991433747828259.png"
 * , "state": "2", "number": "YT81764243379", "brandName": "长虹",
 * "id": "33513", "unit": "1", "num": 10, "price": 6, "yuanjia":
 * 444, "pmname": "测试", "xiangqing":
 * "saddfasfsdddddddddddddddddddddddddddddddddd", "addDate":
 * "2015-06-08", "place": "11", "productName": "测试促销分类1 " },
 * "object": null }
 */
public class ModelProduct {

    String cuxiaodate = "", supplierName = "", supplierId = "", img = "", state = "", number = "", brandName = "",
            id = "", unit = "", attName = "", pmname = "", xiangqing = "", addDate = "", place = "", productName = "";
    long num = 0;
    double price = 0, yuanjia = 0;
    List<String> images = new ArrayList<>();
    List<ModelProductRelation> productRelations = new ArrayList<>();
    JSONObject jsonObject = new JSONObject();

    public ModelProduct() {
    }

    public ModelProduct(JSONObject object) throws JSONException {
        if (object != null) {
            jsonObject = object;
            JSONArray listzu = object.optJSONArray("listzu");
            if (listzu != null) {
                for (int i = 0; i < listzu.length(); i++) {
                    productRelations.add(new ModelProductRelation(listzu
                            .optJSONObject(i)));
                }
            }
            if (object.has("cuxiaodate")) {
                if (!object.getString("cuxiaodate").equalsIgnoreCase("null")) {
                    cuxiaodate = object.optString("cuxiaodate");
                }
            }
            if (object.has("supplierName")) {
                if (!object.getString("supplierName").equalsIgnoreCase("null")) {
                    supplierName = object.optString("supplierName");
                }
            }
            if (object.has("supplierId")) {
                if (!object.getString("supplierId").equalsIgnoreCase("null")) {
                    supplierId = object.optString("supplierId");
                }
            }
            if (object.has("img")) {
                if (!object.getString("img").equalsIgnoreCase("null")) {
                    img = object.optString("img");
                }
            }
            if (object.has("number")) {
                if (!object.getString("number").equalsIgnoreCase("null")) {
                    number = object.optString("number");
                }
            }
            if (object.has("brandName")) {
                if (!object.getString("brandName").equalsIgnoreCase("null")) {
                    brandName = object.optString("brandName");
                }
            }
            if (object.has("id")) {
                if (!object.getString("id").equalsIgnoreCase("null")) {
                    id = object.optString("id");
                }
            }
            if (object.has("unit")) {
                if (!object.getString("unit").equalsIgnoreCase("null")) {
                    unit = object.optString("unit");
                }
            }
            if (object.has("attName")) {
                if (!object.getString("attName").equalsIgnoreCase("null")) {
                    attName = object.optString("attName");
                }
            }
            if (object.has("pmname")) {
                if (!object.getString("pmname").equalsIgnoreCase("null")) {
                    pmname = object.optString("pmname");
                }
            }
            if (object.has("xiangqing")) {
                if (!object.getString("xiangqing").equalsIgnoreCase("null")) {
                    xiangqing = object.optString("xiangqing");
                }
            }
            if (object.has("addDate")) {
                if (!object.getString("addDate").equalsIgnoreCase("null")) {
                    addDate = object.optString("addDate");
                }
            }
            if (object.has("place")) {
                if (!object.getString("place").equalsIgnoreCase("null")) {
                    place = object.optString("place");
                }
            }
            if (object.has("productName")) {
                if (!object.getString("productName").equalsIgnoreCase("null")) {
                    productName = object.optString("productName");
                }
            }
            if (object.has("state")) {
                if (!object.getString("state").equalsIgnoreCase("null")) {
                    state = object.optString("state");
                }
            }
            if (object.has("num")) {
                if (!object.getString("num").equalsIgnoreCase("null")) {
                    num = object.optLong("num");
                }
            }
            if (object.has("price")) {
                if (!object.getString("price").equalsIgnoreCase("null")) {
                    price = object.optDouble("price");
                }
            }
            if (object.has("yuanjia")) {
                if (!object.getString("yuanjia").equalsIgnoreCase("null")) {
                    yuanjia = object.optDouble("yuanjia");
                }
            }
            if (object.has("imgs")) {
                if (!object.getString("imgs").equalsIgnoreCase("null")) {
                    JSONArray imageArray = object.getJSONArray("imgs");
                    for (int i = 0; i < imageArray.length(); i++) {
                        images.add(imageArray.getString(i));
                    }
                }
            }
        }
    }

    public String getCuxiaodate() {
        return cuxiaodate;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getImg() {
        return img;
    }

    public String getNumber() {
        return number;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getId() {
        return id;
    }

    public String getUnit() {
        return unit;
    }

    public String getAttName() {
        return attName;
    }

    public String getPmname() {
        return pmname;
    }

    public String getXiangqing() {
        return xiangqing;
    }

    public String getAddDate() {
        return addDate;
    }

    public String getPlace() {
        return place;
    }

    public String getProductName() {
        return productName;
    }

    public long getNum() {
        return num;
    }

    public double getPrice() {
        return price;
    }

    public double getYuanjia() {
        return yuanjia;
    }

    public List<String> getImages() {
        return images;
    }

    public List<ModelProductRelation> getProductRelations() {
        return productRelations;
    }

    public String getState() {
        return state;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    @Override
    public String toString() {
        return "ModelProduct [cuxiaodate=" + cuxiaodate + ", img=" + img
                + ", state=" + state + ", number=" + number + ", brandName="
                + brandName + ", id=" + id + ", unit=" + unit + ", attName="
                + attName + ", pmname=" + pmname + ", xiangqing=" + xiangqing
                + ", addDate=" + addDate + ", place=" + place
                + ", productName=" + productName + ", num=" + num + ", price="
                + price + ", yuanjia=" + yuanjia + ", images=" + images
                + ", productRelations=" + productRelations + "]";
    }

}
