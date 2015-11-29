package yitgogo.smart.home.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelSaleTejia {

	List<ModelSaleTejiaProduct> tejiaProducts = new ArrayList<ModelSaleTejiaProduct>();
	ModelSaleTejiaGroup groupA = new ModelSaleTejiaGroup(1);
	ModelSaleTejiaGroup groupB = new ModelSaleTejiaGroup(2);
	ModelSaleTejiaGroup groupC = new ModelSaleTejiaGroup(2);

	public ModelSaleTejia() {
	}

	public ModelSaleTejia(JSONObject object) throws JSONException {
		if (object != null) {
			groupA = new ModelSaleTejiaGroup(object.optJSONArray("oneGroup"), 1);
			groupB = new ModelSaleTejiaGroup(object.optJSONArray("twoGroup"), 2);
			groupC = new ModelSaleTejiaGroup(object.optJSONArray("threeGroup"),
					2);
		}
	}

	public List<ModelSaleTejiaProduct> getTejiaProducts() {
		tejiaProducts.clear();
		tejiaProducts.addAll(groupA.getTejiaProducts());
		tejiaProducts.addAll(groupB.getTejiaProducts());
		tejiaProducts.addAll(groupC.getTejiaProducts());
		return tejiaProducts;
	}

	public ModelSaleTejiaGroup getGroupA() {
		return groupA;
	}

	public ModelSaleTejiaGroup getGroupB() {
		return groupB;
	}

	public ModelSaleTejiaGroup getGroupC() {
		return groupC;
	}

	@Override
	public String toString() {
		return "ModelSaleTejia [groupA=" + groupA + ", groupB=" + groupB
				+ ", groupC=" + groupC + "]";
	}

}
