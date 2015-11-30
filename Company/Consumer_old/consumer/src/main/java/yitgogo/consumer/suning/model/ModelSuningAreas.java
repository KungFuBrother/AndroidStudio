package yitgogo.consumer.suning.model;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;

/**
 * Created by Tiger on 2015-10-19.
 * <p>
 * {
 * "code": "04",
 * "name": "武侯区"
 * }
 */
public class ModelSuningAreas {

    ModelSuningArea province = new ModelSuningArea();
    ModelSuningArea city = new ModelSuningArea();
    ModelSuningArea district = new ModelSuningArea();
    ModelSuningArea town = new ModelSuningArea();


    public ModelSuningAreas(JSONObject object) {
        if (object != null) {
            if (object.length() > 0) {
                province = new ModelSuningArea(object.optJSONObject("province"));
                city = new ModelSuningArea(object.optJSONObject("city"));
                district = new ModelSuningArea(object.optJSONObject("district"));
                town = new ModelSuningArea(object.optJSONObject("town"));
            }
        }
    }

    public ModelSuningArea getProvince() {
        return province;
    }

    public ModelSuningArea getCity() {
        return city;
    }

    public ModelSuningArea getDistrict() {
        return district;
    }

    public ModelSuningArea getTown() {
        return town;
    }

    public void setProvince(ModelSuningArea province) {
        this.province = province;
    }

    public void setCity(ModelSuningArea city) {
        this.city = city;
    }

    public void setDistrict(ModelSuningArea district) {
        this.district = district;
    }

    public void setTown(ModelSuningArea town) {
        this.town = town;
    }

    public void save() {
        JSONObject object = new JSONObject();
        try {
            object.put("province", province.toJsonObject());
            object.put("city", city.toJsonObject());
            object.put("district", district.toJsonObject());
            object.put("town", town.toJsonObject());
            Content.saveStringContent(Parameters.CACHE_KEY_SUNING_AREAS, object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
