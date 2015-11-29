package yitgogo.smart.order.model;

import org.json.JSONObject;

/**
 * @author Tiger
 * 
 * @Description 用户对象
 * @JsonObject {"message":"ok","state"
 *             :"SUCCESS","cacheKey":null,"dataList":[],"totalCount"
 *             :1,"dataMap":
 *             {},"object":{"id":44,"useraccount":"HY345595695593","realname"
 *             :"雷小武","phone":"13032889558","area":{"id":2421,"valuename":"金牛区",
 *             "valuetype"
 *             :{"id":4,"typename":"区县"},"onid":269,"onname":null,"brevitycode"
 *             :null
 *             },"address":"成都市金牛区","uImg":"","addtime":"2015-08-06 16:35:36"
 *             ,"email":"1076192306@qq.com","sex":"男","age":"21","birthday":
 *             "1993-12-17 00:00:00"
 *             ,"idcard":"","spid":"17","memtype":"手机","myRecommend"
 *             :"yt665888","otherRecommend"
 *             :"yt665888","grade":{"id":30,"firstMoney"
 *             :0.0,"lastMoney":500.0,"gradeName":"易田新人","gradeImg":
 *             "http://images.yitos.net/images/public/20150812/87761439368053133.png"
 *             },"isopenCosmo":false,"getBouns":false}}
 */
public class User {

	private String id = "", useraccount = "", realname = "", phone = "",
			area = "", address = "", uImg = "", addtime = "", email = "",
			sex = "", age = "", idcard = "", spid = "", memtype = "",
			myRecommend = "", otherRecommend = "", birthday = "";
	private boolean isopenCosmo = false, update = false;
	boolean isLogin = false;

	ModelGrade grade = new ModelGrade();

	public User() {
		isLogin = false;
	}

	public User(JSONObject object) {
		if (object != null) {
			isLogin = true;
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

	public String getId() {
		return id;
	}

	public String getUseraccount() {
		return useraccount;
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
