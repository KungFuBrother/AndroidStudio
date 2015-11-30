package yitgogo.consumer.user.model;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import android.content.Context;

/**
 * @author Tiger
 * 
 * @Description 用户对象
 * @JsonObject {"message":"ok","state"
 *             :"SUCCESS","cacheKey":"b1a48a96fbf9e1f6b849fa221cefca18"
 *             ,"dataList"
 *             :[],"totalCount":1,"dataMap":{},"object":{"id":470,"useraccount"
 *             :"13032889558"
 *             ,"realname":"Tiger","phone":"13032889558","area":null
 *             ,"address":"凤凰大厦"
 *             ,"uImg":null,"addtime":"2014-11-10 16:43:09","email"
 *             :"1076192306@qq.com"
 *             ,"sex":"男","age":"21","birthday":"1993-12-16 00:00:00"
 *             ,"idcard":"513030199311056012"
 *             ,"spid":"0","memtype":"手机","myRecommend"
 *             :"yt162411","otherRecommend"
 *             :null,"grade":null,"isopenCosmo":false,"getBouns":false}}
 */
public class User {

	public static User user;

	private String cacheKey = "", id = "", useraccount = "", realname = "",
			phone = "", area = "", address = "", uImg = "", addtime = "",
			email = "", sex = "", age = "", idcard = "", spid = "",
			memtype = "", myRecommend = "", otherRecommend = "", birthday = "";
	private boolean isopenCosmo = false, update = false;
	boolean isLogin = false;

	ModelGrade grade = new ModelGrade();

	public static void init(Context context) {
		user = new User();
		String userJsonString = Content.getStringContent(
				Parameters.CACHE_KEY_USER_JSON, "");
		if (userJsonString.length() > 0) {
			try {
				user = new User(new JSONObject(userJsonString));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public User() {
	}

	public User(JSONObject object) {
		if (object != null) {
			isLogin = true;
			cacheKey = Content.getStringContent(Parameters.CACHE_KEY_MONEY_SN,
					"");
			if (object.has("id")) {
				if (!object.optString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("useraccount")) {
				if (!object.optString("useraccount").equalsIgnoreCase("null")) {
					useraccount = object.optString("useraccount");
				}
			}
			if (object.has("realname")) {
				if (!object.optString("realname").equalsIgnoreCase("null")) {
					realname = object.optString("realname");
				}
			}
			if (object.has("phone")) {
				if (!object.optString("phone").equalsIgnoreCase("null")) {
					phone = object.optString("phone");
				}
			}
			if (object.has("area")) {
				if (!object.optString("area").equalsIgnoreCase("null")) {
					area = object.optString("area");
				}
			}
			if (object.has("address")) {
				if (!object.optString("address").equalsIgnoreCase("null")) {
					address = object.optString("address");
				}
			}
			if (object.has("uImg")) {
				if (!object.optString("uImg").equalsIgnoreCase("null")) {
					uImg = object.optString("uImg");
				}
			}
			if (object.has("addtime")) {
				if (!object.optString("addtime").equalsIgnoreCase("null")) {
					addtime = object.optString("addtime");
				}
			}
			if (object.has("email")) {
				if (!object.optString("email").equalsIgnoreCase("null")) {
					email = object.optString("email");
				}
			}
			if (object.has("sex")) {
				if (!object.optString("sex").equalsIgnoreCase("null")) {
					sex = object.optString("sex");
				}
			}
			if (object.has("age")) {
				if (!object.optString("age").equalsIgnoreCase("null")) {
					age = object.optString("age");
				}
			}
			if (object.has("birthday")) {
				if (!object.optString("birthday").equalsIgnoreCase("null")) {
					birthday = object.optString("birthday");
				}
			}
			if (object.has("idcard")) {
				if (!object.optString("idcard").equalsIgnoreCase("null")) {
					idcard = object.optString("idcard");
				}
			}
			if (object.has("spid")) {
				if (!object.optString("spid").equalsIgnoreCase("null")) {
					spid = object.optString("spid");
				}
			}
			if (object.has("memtype")) {
				if (!object.optString("memtype").equalsIgnoreCase("null")) {
					memtype = object.optString("memtype");
				}
			}
			if (object.has("myRecommend")) {
				if (!object.optString("myRecommend").equalsIgnoreCase("null")) {
					myRecommend = object.optString("myRecommend");
				}
			}
			if (object.has("otherRecommend")) {
				if (!object.optString("otherRecommend")
						.equalsIgnoreCase("null")) {
					otherRecommend = object.optString("otherRecommend");
				}
			}
			JSONObject gradeObject = object.optJSONObject("grade");
			grade = new ModelGrade(gradeObject);
			isopenCosmo = object.optBoolean("isopenCosmo");
			update = object.optBoolean("update");
		}
	}

	public static User getUser() {
		return user;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public String getId() {
		return id;
	}

	public String getUseraccount() {
		return useraccount;
		// return "13032889558";
	}

	public String getRealname() {
		return realname;
	}

	public String getPhone() {
		return phone;
	}

	public String getArea() {
		return area;
	}

	public String getAddress() {
		return address;
	}

	public String getuImg() {
		return uImg;
	}

	public String getAddtime() {
		return addtime;
	}

	public String getEmail() {
		return email;
	}

	public String getSex() {
		return sex;
	}

	public String getAge() {
		return age;
	}

	public String getBirthday() {
		return birthday;
	}

	public String getIdcard() {
		return idcard;
	}

	public String getSpid() {
		return spid;
	}

	public String getMemtype() {
		return memtype;
	}

	public boolean isLogin() {
		// return true;
		return isLogin;
	}

	public String getMyRecommend() {
		return myRecommend;
	}

	public String getOtherRecommend() {
		return otherRecommend;
	}

	public ModelGrade getGrade() {
		return grade;
	}

	public boolean isIsopenCosmo() {
		return isopenCosmo;
	}

	public boolean isUpdate() {
		return update;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", useraccount=" + useraccount
				+ ", realname=" + realname + ", phone=" + phone + ", area="
				+ area + ", address=" + address + ", uImg=" + uImg
				+ ", addtime=" + addtime + ", email=" + email + ", sex=" + sex
				+ ", age=" + age + ", idcard=" + idcard + ", spid=" + spid
				+ ", memtype=" + memtype + ", myRecommend=" + myRecommend
				+ ", otherRecommend=" + otherRecommend + ", grade=" + grade
				+ ", isopenCosmo=" + isopenCosmo + ", update=" + update
				+ ", birthday=" + birthday + ", isLogin=" + isLogin + "]";
	}

}
