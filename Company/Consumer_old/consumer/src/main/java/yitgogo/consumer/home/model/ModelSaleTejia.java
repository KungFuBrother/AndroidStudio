package yitgogo.consumer.home.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelSaleTejia {

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

	public ModelSaleTejiaGroup getGroupA() {
		return groupA;
	}

	public ModelSaleTejiaGroup getGroupB() {
		return groupB;
	}

	public ModelSaleTejiaGroup getGroupC() {
		return groupC;
	}

	public boolean isEmpty() {
		return groupA.getTejiaProducts().size()
				+ groupB.getTejiaProducts().size()
				+ groupC.getTejiaProducts().size() == 0;
	}

	@Override
	public String toString() {
		return "ModelSaleTejia [groupA=" + groupA + ", groupB=" + groupB
				+ ", groupC=" + groupC + "]";
	}

}
