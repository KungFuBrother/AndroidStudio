package yitgogo.consumer.local.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.order.model.ModelDiliver;
import yitgogo.consumer.order.model.ModelPayment;
import yitgogo.consumer.order.model.ModelStorePostInfo;
import yitgogo.consumer.store.model.ModelStoreSelected;

/**
 * 本地商品购物车对象
 * 
 * @author Tiger
 * 
 */
public class ModelLocalCar {

	int goodsCount = 0;
	boolean isSelected = true;
	double totalMoney = 0, goodsMoney = 0, postFee = 0;
	ModelDiliver diliver = new ModelDiliver();
	ModelPayment payment = new ModelPayment();
	ModelStoreSelected store = new ModelStoreSelected();
	ModelStorePostInfo storePostInfo = new ModelStorePostInfo();
	List<ModelLocalCarGoods> carGoods = new ArrayList<ModelLocalCarGoods>();

	List<ModelDiliver> dilivers = new ArrayList<ModelDiliver>();
	List<ModelPayment> payments = new ArrayList<ModelPayment>();

	public ModelLocalCar() {
		setStorePostInfo();
	}

	public ModelLocalCar(JSONObject object) throws JSONException {
		if (object != null) {
			store = new ModelStoreSelected(object.optJSONObject("store"));
			storePostInfo = new ModelStorePostInfo(
					object.optJSONObject("storePostInfo"));
			diliver = new ModelDiliver(object.optJSONObject("diliver"));
			payment = new ModelPayment(object.optJSONObject("payment"));
			JSONArray array = object.optJSONArray("goods");
			if (array != null) {
				goodsMoney = 0;
				postFee = 0;
				for (int i = 0; i < array.length(); i++) {
					ModelLocalCarGoods goods = new ModelLocalCarGoods(
							array.optJSONObject(i));
					carGoods.add(goods);
					if (goods.isSelected()) {
						goodsCount += goods.getGoodsCount();
						goodsMoney += goods.getGoodsCount()
								* goods.getGoods().getRetailPrice();
					} else {
						isSelected = false;
					}
				}
				if (diliver.getType() != ModelDiliver.TYPE_SELF
						& goodsMoney > 0
						& goodsMoney < storePostInfo.getHawManyPackages()) {
					// 非自取，购物金额大于0且小于免邮金额
					postFee = storePostInfo.getPostage();
				}
				totalMoney = goodsMoney + postFee;
			}
		}
		setStorePostInfo();
	}

	public ModelLocalCar(ModelLocalGoods localGoods) {
		store = localGoods.getProviderBean();
		carGoods.add(new ModelLocalCarGoods(localGoods));
	}

	public ModelLocalCar(ModelStoreSelected modelStore) {
		store = modelStore;
	}

	public int getGoodsCount() {
		return goodsCount;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public double getTotalMoney() {
		return totalMoney;
	}

	public double getGoodsMoney() {
		return goodsMoney;
	}

	public double getPostFee() {
		return postFee;
	}

	public ModelStoreSelected getStore() {
		return store;
	}

	public List<ModelLocalCarGoods> getCarGoods() {
		return carGoods;
	}

	public void setDiliver(ModelDiliver diliver) {
		this.diliver = diliver;
	}

	public void setPayment(ModelPayment payment) {
		this.payment = payment;
	}

	public List<ModelDiliver> getDilivers() {
		return dilivers;
	}

	public List<ModelPayment> getPayments() {
		return payments;
	}

	public ModelDiliver getDiliver() {
		return diliver;
	}

	public ModelPayment getPayment() {
		return payment;
	}

	public ModelStorePostInfo getStorePostInfo() {
		return storePostInfo;
	}

	public void setStorePostInfo(ModelStorePostInfo storePostInfo) {
		this.storePostInfo = storePostInfo;
		setStorePostInfo();
	}

	private void setStorePostInfo() {
		dilivers.clear();
		payments.clear();
		dilivers.add(new ModelDiliver(ModelDiliver.TYPE_SELF,
				ModelDiliver.NAME_SELF));
		dilivers.add(new ModelDiliver(ModelDiliver.TYPE_HOME,
				ModelDiliver.NAME_HOME));
		payments.add(new ModelPayment(ModelPayment.TYPE_ONLINE,
				ModelPayment.NAME_ONLINE));
		if (storePostInfo.isSupportForDelivery()) {
			payments.add(new ModelPayment(ModelPayment.TYPE_RECEIVED,
					ModelPayment.NAME_RECEIVED));
		}
	}

	public JSONObject toJsonObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("store", store.getJsonObject());
		jsonObject.put("storePostInfo", storePostInfo.getJsonObject());
		jsonObject.put("diliver", diliver.toJsonObject());
		jsonObject.put("payment", payment.toJsonObject());
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < carGoods.size(); i++) {
			jsonArray.put(carGoods.get(i).toJsonObject());
		}
		jsonObject.put("goods", jsonArray);
		return jsonObject;
	}

}
