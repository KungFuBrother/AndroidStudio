package yitgogo.consumer.home.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelHomeClass {

	private String dataId = "", classLogo = "", className = "", classId = "",
			sequenceId = "";

	public ModelHomeClass(JSONObject object) {
		// TODO Auto-generated constructor stub
		try {
			if (object.has("id")) {
				if (!object.getString("id").equals("null")) {
					setDataId(object.getString("id"));
				}
			}
			if (object.has("classLogo")) {
				if (!object.getString("classLogo").equals("null")) {
					setClassLogo(object.getString("classLogo"));
				}
			}
			if (object.has("className")) {
				if (!object.getString("className").equals("null")) {
					setClassName(object.getString("className"));
				}
			}
			if (object.has("classId")) {
				if (!object.getString("classId").equals("null")) {
					setClassId(object.getString("classId"));
				}
			}
			if (object.has("sequenceId")) {
				if (!object.getString("sequenceId").equals("null")) {
					setSequenceId(object.getString("sequenceId"));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public void setClassLogo(String classLogo) {
		this.classLogo = classLogo;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	public String getClassId() {
		return classId;
	}

	public String getClassLogo() {
		return classLogo;
	}

	public String getClassName() {
		return className;
	}

	public String getDataId() {
		return dataId;
	}

	public String getSequenceId() {
		return sequenceId;
	}

}
