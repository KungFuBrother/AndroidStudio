package yitgogo.consumer.suning.model;

/**
 * Created by Tiger on 2015-10-19.
 */
public class ModelProduct {

    ModelProductPool productPool=new ModelProductPool();
    ModelProductDetail productDetail=new ModelProductDetail();

    public ModelProductPool getProductPool() {
        return productPool;
    }

    public void setProductPool(ModelProductPool productPool) {
        this.productPool = productPool;
    }

    public ModelProductDetail getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(ModelProductDetail productDetail) {
        this.productDetail = productDetail;
    }
}
