package yitgogo.consumer.product.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ModelAttrType {

	String id = "", name = "";
	List<ModelAttrValue> attrValues = new ArrayList<ModelAttrValue>();
	int selection = 0;
	int type = 0;
	public final static int TYPE_BRAND = 0;
	public final static int TYPE_ATTR = 1;
	public final static int TYPE_ATTR_EXTEND = 2;

	/**
	 * 
	 * @param object
	 *            json数据
	 * @param type
	 *            数据类型 0:属性/1:扩展属性
	 * @throws JSONException
	 */
	public ModelAttrType(JSONObject object, JSONArray array, int type)
			throws JSONException {
		attrValues.add(new ModelAttrValue());
		this.type = type;
		switch (type) {
		case TYPE_BRAND:
			name = "品牌";
			if (array != null) {
				if (array.length() > 0) {
					for (int i = 0; i < array.length(); i++) {
						attrValues.add(new ModelAttrValue(array
								.getJSONObject(i), type));
					}
				}
			}
			break;

		case TYPE_ATTR:
			id = object.optString("id");
			name = object.optString("attributeName");
			JSONArray attrArray = object.optJSONArray("attributeValueSet");
			if (attrArray != null) {
				if (attrArray.length() > 0) {
					for (int i = 0; i < attrArray.length(); i++) {
						attrValues.add(new ModelAttrValue(attrArray
								.getJSONObject(i), type));
					}
				}
			}
			break;

		case TYPE_ATTR_EXTEND:
			id = object.optString("id");
			name = object.optString("attributeExtendName");
			JSONArray attrExtendArray = object
					.optJSONArray("attributeValueExtendSet");
			if (attrExtendArray != null) {
				if (attrExtendArray.length() > 0) {
					for (int i = 0; i < attrExtendArray.length(); i++) {
						attrValues.add(new ModelAttrValue(attrExtendArray
								.getJSONObject(i), type));
					}
				}
			}
			break;

		default:
			break;
		}
	}

	public int getType() {
		return type;
	}

	public int getSelection() {
		return selection;
	}

	public void setSelection(int selection) {
		this.selection = selection;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<ModelAttrValue> getAttrValues() {
		return attrValues;
	}

	@Override
	public String toString() {
		return "ModelAttrType [id=" + id + ", name=" + name + ", attrValues="
				+ attrValues + ", selection=" + selection + ", type=" + type
				+ "]";
	}

}
