package yitgogo.consumer.suning.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tiger on 2015-10-19.
 */
public class ModelSuningCar {

    ModelProductDetail productDetail = new ModelProductDetail();
    int productCount = 0;
    int productSelection = 0;

    public ModelSuningCar() {
    }

    public ModelSuningCar(String object, int productCount, int productSelection) {
        this.productCount = productCount;
        this.productSelection = productSelection;
        try {
            productDetail = new ModelProductDetail(new JSONObject(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ModelProductDetail getProductDetail() {
        return productDetail;
    }

    public int getProductCount() {
        return productCount;
    }

    public int getProductSelection() {
        return productSelection;
    }

    public boolean isSelected() {
        return productSelection == 1;
    }

    @Override
    public String toString() {
        return "ModelSuningCar{" +
                "productDetail=" + productDetail +
                ", productCount=" + productCount +
                ", productSelection=" + productSelection +
                '}';
    }
}
