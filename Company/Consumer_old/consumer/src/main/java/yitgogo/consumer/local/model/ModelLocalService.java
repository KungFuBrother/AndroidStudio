package yitgogo.consumer.local.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.store.model.ModelStoreSelected;

/**
 * @author Tiger
 * 
 * @JsonObject { "id": 7, "productNumber": "YT2342424", "productName":
 *             "测试产品本地团购6666666", "classValueBean": { "id": 4, "classValueName":
 *             "休闲", "classImg": null, "organizationId": "1068" },
 *             "productDescribe": "111111111", "productPrice": 2500, "img":
 *             "http://images.yitos.net/images/public/20140907/29391410098513531.png"
 *             , "imgs": [ { "id": 7, "imgName":
 *             "http://images.yitos.net/images/public/20140907/29391410098513531.png"
 *             } ], "privilege": "优惠信息6666666666", "detailedAddress":
 *             "666666666666", "productType": "服务", "productNumDateType": "时间",
 *             "areaId": "2421", "addDate": "2015-07-14", "sortDate": 12312123,
 *             "productDate": "2015-07-14", "productNum": 20,
 *             "deliveredToPaidYN": "是", "deliverYN": "是", "deliverNum": "20",
 *             "state": "上架", "providerBean": { "id": 1068, "no":
 *             "YT117351516493", "brevitycode": "csyyzxy", "servicename":
 *             "测试运营中心一", "businessno": "SB789456", "contacts": "测试运营中心一",
 *             "cardnumber": "123456789789789789", "serviceaddress":
 *             "四川省成都市金牛区解放路二段", "contactphone": "15878978945",
 *             "contacttelephone": "028-2356895623", "email":
 *             "45645645656@qq.com", "reva": { "id": 2421, "valuename": "金牛区",
 *             "valuetype": { "id": 4, "typename": "区县" }, "onid": 269,
 *             "onname": null, "brevitycode": null }, "contractno":
 *             "SB15614616", "contractannex": "", "onservice": { "id": 1064,
 *             "no": "YT117351165326", "brevitycode": "csfwsy", "servicename":
 *             "测试服务商一", "businessno": "SB123456", "contacts": "测试服务商一",
 *             "cardnumber": "513012121212121212", "serviceaddress":
 *             "四川省成都市金牛区解放路二段六号", "contactphone": "15823565656",
 *             "contacttelephone": "028-23562356", "email": "145615616@qq.com",
 *             "reva": { "id": 3253, "valuename": "中国", "valuetype": { "id": 1,
 *             "typename": "国" }, "onid": 0, "onname": null, "brevitycode": null
 *             }, "contractno": "SB123115321531", "contractannex": "",
 *             "onservice": null, "state": "启用", "addtime":
 *             "2015-05-30 11:03:07", "starttime": 1432915200000, "sptype": "1",
 *             "endtime": 1564416000000, "supply": false, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2015-05-30 11:22:27", "starttime": 1432915200000, "sptype": "2",
 *             "endtime": 1577462400000, "supply": false, "imghead": "",
 *             "longitude": "104.08902889614", "latitude": "30.686814874166" },
 *             "remark": "备注", "aixinxianYN": "是" }
 * 
 */
public class ModelLocalService {

	String id = "", productNumber = "", productName = "", productDescribe = "",
			img = "", privilege = "", detailedAddress = "", productType = "",
			productNumDateType = "", areaId = "", addDate = "", sortDate = "",
			productDate = "", state = "", remark = "", aixinxianYN = "";
	double productPrice = 0;
	long productNum = 0;
	boolean deliveredToPaidYN = false, deliverYN = false;
	int deliverNum = 0;
	List<ModelLocalServiceImage> images = new ArrayList<ModelLocalServiceImage>();
	ModelStoreSelected providerBean = new ModelStoreSelected();
	ModelLocalServiceClass serviceClass = new ModelLocalServiceClass();

	public ModelLocalService() {
	}

	public ModelLocalService(JSONObject object) throws JSONException {
		if (object != null) {
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("productNumber")) {
				if (!object.getString("productNumber").equalsIgnoreCase("null")) {
					productNumber = object.optString("productNumber");
				}
			}
			if (object.has("productName")) {
				if (!object.getString("productName").equalsIgnoreCase("null")) {
					productName = object.optString("productName");
				}
			}
			if (object.has("productDescribe")) {
				if (!object.getString("productDescribe").equalsIgnoreCase(
						"null")) {
					productDescribe = object.optString("productDescribe");
				}
			}
			if (object.has("img")) {
				if (!object.getString("img").equalsIgnoreCase("null")) {
					img = object.optString("img");
				}
			}
			if (object.has("privilege")) {
				if (!object.getString("privilege").equalsIgnoreCase("null")) {
					privilege = object.optString("privilege");
				}
			}
			if (object.has("detailedAddress")) {
				if (!object.getString("detailedAddress").equalsIgnoreCase(
						"null")) {
					detailedAddress = object.optString("detailedAddress");
				}
			}
			if (object.has("productType")) {
				if (!object.getString("productType").equalsIgnoreCase("null")) {
					productType = object.optString("productType");
				}
			}
			if (object.has("productNumDateType")) {
				if (!object.getString("productNumDateType").equalsIgnoreCase(
						"null")) {
					productNumDateType = object.optString("productNumDateType");
				}
			}
			if (object.has("areaId")) {
				if (!object.getString("areaId").equalsIgnoreCase("null")) {
					areaId = object.optString("areaId");
				}
			}
			if (object.has("addDate")) {
				if (!object.getString("addDate").equalsIgnoreCase("null")) {
					addDate = object.optString("addDate");
				}
			}
			if (object.has("sortDate")) {
				if (!object.getString("sortDate").equalsIgnoreCase("null")) {
					sortDate = object.optString("sortDate");
				}
			}
			if (object.has("productDate")) {
				if (!object.getString("productDate").equalsIgnoreCase("null")) {
					productDate = object.optString("productDate");
				}
			}
			deliveredToPaidYN = object.optString("deliveredToPaidYN")
					.equalsIgnoreCase("是");
			deliverYN = object.optString("deliverYN").equalsIgnoreCase("是");
			deliverNum = object.optInt("deliverNum");
			if (object.has("state")) {
				if (!object.getString("state").equalsIgnoreCase("null")) {
					state = object.optString("state");
				}
			}
			if (object.has("remark")) {
				if (!object.getString("remark").equalsIgnoreCase("null")) {
					remark = object.optString("remark");
				}
			}
			if (object.has("aixinxianYN")) {
				if (!object.getString("aixinxianYN").equalsIgnoreCase("null")) {
					aixinxianYN = object.optString("aixinxianYN");
				}
			}
			if (object.has("productPrice")) {
				if (!object.getString("productPrice").equalsIgnoreCase("null")) {
					productPrice = object.optDouble("productPrice");
				}
			}
			if (object.has("productNum")) {
				if (!object.getString("productNum").equalsIgnoreCase("null")) {
					productNum = object.optLong("productNum");
				}
			}
			JSONObject providerBeanObject = object
					.optJSONObject("providerBean");
			if (providerBeanObject != null) {
				providerBean = new ModelStoreSelected(providerBeanObject);
			}
			JSONObject classValueBean = object.optJSONObject("classValueBean");
			if (classValueBean != null) {
				serviceClass = new ModelLocalServiceClass(classValueBean);
			}
			JSONArray imgs = object.optJSONArray("imgs");
			if (imgs != null) {
				for (int i = 0; i < imgs.length(); i++) {
					images.add(new ModelLocalServiceImage(imgs.optJSONObject(i)));
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public String getProductName() {
		return productName;
	}

	public String getProductDescribe() {
		return productDescribe;
	}

	public String getImg() {
		return img;
	}

	public String getPrivilege() {
		return privilege;
	}

	public String getDetailedAddress() {
		return detailedAddress;
	}

	public String getProductType() {
		return productType;
	}

	public String getProductNumDateType() {
		return productNumDateType;
	}

	public String getAreaId() {
		return areaId;
	}

	public String getAddDate() {
		return addDate;
	}

	public String getSortDate() {
		return sortDate;
	}

	public String getProductDate() {
		return productDate;
	}

	public boolean isDeliveredToPaidYN() {
		return deliveredToPaidYN;
	}

	public boolean isDeliverYN() {
		return deliverYN;
	}

	public int getDeliverNum() {
		return deliverNum;
	}

	public String getState() {
		return state;
	}

	public String getRemark() {
		return remark;
	}

	public String getAixinxianYN() {
		return aixinxianYN;
	}

	public double getProductPrice() {
		return productPrice;
	}

	public long getProductNum() {
		return productNum;
	}

	public List<ModelLocalServiceImage> getImages() {
		return images;
	}

	public ModelStoreSelected getProviderBean() {
		return providerBean;
	}

	public ModelLocalServiceClass getServiceClass() {
		return serviceClass;
	}

	@Override
	public String toString() {
		return "ModelLocalService [id=" + id + ", productNumber="
				+ productNumber + ", productName=" + productName
				+ ", productDescribe=" + productDescribe + ", img=" + img
				+ ", privilege=" + privilege + ", detailedAddress="
				+ detailedAddress + ", productType=" + productType
				+ ", productNumDateType=" + productNumDateType + ", areaId="
				+ areaId + ", addDate=" + addDate + ", sortDate=" + sortDate
				+ ", productDate=" + productDate + ", deliveredToPaidYN="
				+ deliveredToPaidYN + ", deliverYN=" + deliverYN
				+ ", deliverNum=" + deliverNum + ", state=" + state
				+ ", remark=" + remark + ", aixinxianYN=" + aixinxianYN
				+ ", productPrice=" + productPrice + ", productNum="
				+ productNum + ", images=" + images + ", providerBean="
				+ providerBean + ", serviceClass=" + serviceClass + "]";
	}

}
