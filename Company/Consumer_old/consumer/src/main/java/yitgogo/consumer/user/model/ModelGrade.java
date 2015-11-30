package yitgogo.consumer.user.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json "grade":{"id":30,"firstMoney" :0.0,"lastMoney":500.0,"gradeName":"易田新人
 *       ","gradeImg": "http://images.yitos.net/images/public/20150812/
 *       87761439368053133.png" }
 */
public class ModelGrade {

	String gradeName = "", gradeImg = "";

	public ModelGrade() {
	}

	public ModelGrade(JSONObject object) {
		if (object != null) {
			if (object.has("gradeName")) {
				if (!object.optString("gradeName").equalsIgnoreCase("null")) {
					gradeName = object.optString("gradeName");
				}
			}
			if (object.has("gradeImg")) {
				if (!object.optString("gradeImg").equalsIgnoreCase("null")) {
					gradeImg = object.optString("gradeImg");
				}
			}
		}
	}

	public String getGradeName() {
		return gradeName;
	}

	public String getGradeImg() {
		return gradeImg;
	}

}
