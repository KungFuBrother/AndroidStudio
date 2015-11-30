package yitgogo.consumer.local.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.order.model.ModelDiliver;
import yitgogo.consumer.order.model.ModelPayment;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;

public class LocalCarController {

	public static List<ModelLocalCar> getLocalCars() {
		List<ModelLocalCar> localCars = new ArrayList<ModelLocalCar>();
		try {
			JSONArray array = new JSONArray(Content.getStringContent(
					Parameters.CACHE_KEY_CAR_LOCAL, "[]"));
			for (int i = 0; i < array.length(); i++) {
				localCars.add(new ModelLocalCar(array.optJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return localCars;
	}

	public static boolean containGoods(String storeNumber, String goodsId) {
		List<ModelLocalCar> localCars = getLocalCars();
		for (int i = 0; i < localCars.size(); i++) {
			if (localCars.get(i).getStore().getNo().equals(storeNumber)) {
				for (int j = 0; j < localCars.get(i).getCarGoods().size(); j++) {
					if (localCars.get(i).getCarGoods().get(j).getGoods()
							.getId().equals(goodsId)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static int addGoods(ModelLocalGoods localGoods) {
		List<ModelLocalCar> localCars = getLocalCars();
		for (int i = 0; i < localCars.size(); i++) {
			if (localCars.get(i).getStore().getNo()
					.equals(localGoods.getProviderBean().getNo())) {
				for (int j = 0; j < localCars.get(i).getCarGoods().size(); j++) {
					if (localCars.get(i).getCarGoods().get(j).getGoods()
							.getId().equals(localGoods.getId())) {
						// 已经添加，不重复添加
						return 1;
					}
				}
				// 已存在相同店铺，添加到此店铺下
				localCars.get(i).getCarGoods()
						.add(new ModelLocalCarGoods(localGoods));
				return save(localCars);
			}
		}
		localCars.add(new ModelLocalCar(localGoods));
		return save(localCars);
	}

	public static int addCount(ModelLocalCarGoods goods) {
		List<ModelLocalCar> localCars = getLocalCars();
		for (int i = 0; i < localCars.size(); i++) {
			if (localCars.get(i).getStore().getNo()
					.equals(goods.getGoods().getProviderBean().getNo())) {
				for (int j = 0; j < localCars.get(i).getCarGoods().size(); j++) {
					if (localCars.get(i).getCarGoods().get(j).getGoods()
							.getId().equals(goods.getGoods().getId())) {
						// 定位到此产品，增加数量
						localCars.get(i).getCarGoods().get(j).addGoodsCount();
						break;
					}
				}
			}
		}
		return save(localCars);
	}

	public static int deleteCount(ModelLocalCarGoods goods) {
		List<ModelLocalCar> localCars = getLocalCars();
		for (int i = 0; i < localCars.size(); i++) {
			if (localCars.get(i).getStore().getNo()
					.equals(goods.getGoods().getProviderBean().getNo())) {
				for (int j = 0; j < localCars.get(i).getCarGoods().size(); j++) {
					if (localCars.get(i).getCarGoods().get(j).getGoods()
							.getId().equals(goods.getGoods().getId())) {
						// 定位到此产品，减少数量
						localCars.get(i).getCarGoods().get(j)
								.deleteGoodsCount();
						break;
					}
				}
			}
		}
		return save(localCars);
	}

	public static int select(ModelLocalCarGoods goods) {
		List<ModelLocalCar> localCars = getLocalCars();
		for (int i = 0; i < localCars.size(); i++) {
			if (localCars.get(i).getStore().getNo()
					.equals(goods.getGoods().getProviderBean().getNo())) {
				for (int j = 0; j < localCars.get(i).getCarGoods().size(); j++) {
					if (localCars.get(i).getCarGoods().get(j).getGoods()
							.getId().equals(goods.getGoods().getId())) {
						// 定位到此产品，修改选中状态
						localCars.get(i).getCarGoods().get(j).select();
						break;
					}
				}
			}
		}
		return save(localCars);
	}

	public static boolean isAllSelected() {
		List<ModelLocalCar> localCars = getLocalCars();
		for (int i = 0; i < localCars.size(); i++) {
			for (int j = 0; j < localCars.get(i).getCarGoods().size(); j++) {
				if (!localCars.get(i).getCarGoods().get(j).isSelected()) {
					return false;
				}
			}
		}
		return true;
	}

	public static int selectStore(ModelLocalCar localCar) {
		List<ModelLocalCar> localCars = getLocalCars();
		boolean b = !localCar.isSelected();
		for (int i = 0; i < localCars.size(); i++) {
			if (localCars.get(i).getStore().getNo()
					.equals(localCar.getStore().getNo())) {
				// 定位到此店铺，遍历店铺下所有产品，修改选择状态
				for (int j = 0; j < localCars.get(i).getCarGoods().size(); j++) {
					localCars.get(i).getCarGoods().get(j).setSelected(b);
				}
				break;
			}
		}
		return save(localCars);
	}

	public static int selectDiliver(ModelLocalCar localCar, ModelDiliver diliver) {
		List<ModelLocalCar> localCars = getLocalCars();
		for (int i = 0; i < localCars.size(); i++) {
			if (localCars.get(i).getStore().getNo()
					.equals(localCar.getStore().getNo())) {
				// 定位到此店铺，修改配送方式
				localCars.get(i).setDiliver(diliver);
				break;
			}
		}
		return save(localCars);
	}

	public static int selectPayment(ModelLocalCar localCar, ModelPayment payment) {
		List<ModelLocalCar> localCars = getLocalCars();
		for (int i = 0; i < localCars.size(); i++) {
			if (localCars.get(i).getStore().getNo()
					.equals(localCar.getStore().getNo())) {
				// 定位到此店铺，修改付款方式
				localCars.get(i).setPayment(payment);
				break;
			}
		}
		return save(localCars);
	}

	public static int selectDiliverAndPayment(ModelLocalCar localCar,
			ModelDiliver diliver, ModelPayment payment) {
		List<ModelLocalCar> localCars = getLocalCars();
		for (int i = 0; i < localCars.size(); i++) {
			if (localCars.get(i).getStore().getNo()
					.equals(localCar.getStore().getNo())) {
				// 定位到此店铺，修改配送方式
				localCars.get(i).setDiliver(diliver);
				// 定位到此店铺，修改付款方式
				localCars.get(i).setPayment(payment);
				break;
			}
		}
		return save(localCars);
	}

	public static int selectAll() {
		boolean b = !isAllSelected();
		List<ModelLocalCar> localCars = getLocalCars();
		for (int i = 0; i < localCars.size(); i++) {
			for (int j = 0; j < localCars.get(i).getCarGoods().size(); j++) {
				localCars.get(i).getCarGoods().get(j).setSelected(b);
			}
		}
		return save(localCars);
	}

	/**
	 * 删除所有选中的商品
	 * 
	 * @return 状态值 0：保存成功 1：保存失败
	 */
	public static int deleteSelectedGoods() {
		// 创建集合用于存放未选中的商品
		List<ModelLocalCar> disSelectedLocalCars = new ArrayList<ModelLocalCar>();
		try {
			// 获取所有购物车商品
			JSONArray array = new JSONArray(Content.getStringContent(
					Parameters.CACHE_KEY_CAR_LOCAL, "[]"));
			// 遍历所有店铺
			for (int i = 0; i < array.length(); i++) {
				// 创建单个店铺对象
				ModelLocalCar localCar = new ModelLocalCar(
						array.optJSONObject(i));
				// 单个店铺json数据
				JSONObject disSelectedObject = localCar.toJsonObject();
				if (disSelectedObject.has("goods")) {
					// 先去除店铺下所有的商品数据
					disSelectedObject.remove("goods");
				}
				// 用于存放选中的商品
				JSONArray disSelectedGoodsArray = new JSONArray();
				// 遍历店铺内添加的所有产品
				for (int j = 0; j < localCar.getCarGoods().size(); j++) {
					if (!localCar.getCarGoods().get(j).isSelected()) {
						// 商品未选中，则添加到新集合
						disSelectedGoodsArray.put(localCar.getCarGoods().get(j)
								.toJsonObject());
					}
				}
				disSelectedObject.put("goods", disSelectedGoodsArray);
				ModelLocalCar disSelectedLocalCar = new ModelLocalCar(
						disSelectedObject);
				if (!disSelectedLocalCar.getCarGoods().isEmpty()) {
					disSelectedLocalCars.add(disSelectedLocalCar);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return save(disSelectedLocalCars);
	}

	/**
	 * 获取所有选中的商品
	 * 
	 * @return 商品数据
	 */
	public static List<ModelLocalCar> getSelectedLocalCars() {
		// 创建集合用于存放选中的商品
		List<ModelLocalCar> selectedLocalCars = new ArrayList<ModelLocalCar>();
		try {
			// 获取所有购物车商品
			JSONArray array = new JSONArray(Content.getStringContent(
					Parameters.CACHE_KEY_CAR_LOCAL, "[]"));
			// 遍历所有店铺
			for (int i = 0; i < array.length(); i++) {
				// 创建单个店铺对象
				ModelLocalCar localCar = new ModelLocalCar(
						array.optJSONObject(i));
				// 单个店铺json数据
				JSONObject selectedObject = localCar.toJsonObject();
				if (selectedObject.has("goods")) {
					// 先去除店铺下所有的商品数据
					selectedObject.remove("goods");
				}
				// 用于存放选中的商品
				JSONArray selectedGoodsArray = new JSONArray();
				// 遍历店铺内添加的所有产品
				for (int j = 0; j < localCar.getCarGoods().size(); j++) {
					if (localCar.getCarGoods().get(j).isSelected()) {
						// 商品选中，则添加到新集合
						selectedGoodsArray.put(localCar.getCarGoods().get(j)
								.toJsonObject());
					}
				}
				selectedObject.put("goods", selectedGoodsArray);
				ModelLocalCar selectedLocalCar = new ModelLocalCar(
						selectedObject);
				if (!selectedLocalCar.getCarGoods().isEmpty()) {
					selectedLocalCars.add(selectedLocalCar);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return selectedLocalCars;
	}

	public static int save(List<ModelLocalCar> localCars) {
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < localCars.size(); i++) {
			try {
				jsonArray.put(localCars.get(i).toJsonObject());
			} catch (JSONException e) {
				e.printStackTrace();
				return 2;
			}
		}
		Content.saveStringContent(Parameters.CACHE_KEY_CAR_LOCAL,
				jsonArray.toString());
		return 0;
	}

}
