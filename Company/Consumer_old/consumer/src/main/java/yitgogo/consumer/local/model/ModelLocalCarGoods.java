package yitgogo.consumer.local.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 本地商品购物车对象
 * 
 * @author Tiger
 * 
 */
public class ModelLocalCarGoods {

	private int goodsCount = 1;
	private boolean isSelected = true;
	private ModelLocalGoods goods = new ModelLocalGoods();

	public ModelLocalCarGoods(JSONObject object) throws JSONException {
		if (object != null) {
			goodsCount = object.optInt("goodsCount");
			isSelected = object.optBoolean("isSelected");
			JSONObject jsonObject = object.optJSONObject("object");
			if (jsonObject != null) {
				goods = new ModelLocalGoods(jsonObject);
			}
		}
	}

	public ModelLocalCarGoods(ModelLocalGoods localGoods) {
		goods = localGoods;
	}

	public ModelLocalGoods getGoods() {
		return goods;
	}

	public void deleteGoodsCount() {
		if (goodsCount > 1) {
			goodsCount--;
		}
	}

	public void addGoodsCount() {
		goodsCount++;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void select() {
		isSelected = !isSelected;
	}

	public int getGoodsCount() {
		return goodsCount;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public JSONObject toJsonObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("goodsCount", goodsCount);
		jsonObject.put("isSelected", isSelected);
		jsonObject.put("object", goods.getJsonObject());
		return jsonObject;
	}

	@Override
	public String toString() {
		return "ModelLocalCarGoods [goodsCount=" + goodsCount + ", isSelected="
				+ isSelected + ", goods=" + goods + "]";
	}

}
