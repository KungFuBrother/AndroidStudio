package yitgogo.consumer.local.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @JsonObject { "id": 2, "providerBean": { "id": 6, "no": "YT445571890374",
 *             "brevitycode": "ytcsjmds", "servicename": "易田测试加盟店四",
 *             "businessno": "sadfsfsf", "contacts": "黄小川", "cardnumber":
 *             "510603198806090657", "serviceaddress": "解放路二段六号凤凰大厦",
 *             "contactphone": "13004766493", "contacttelephone":
 *             "028-12345678", "email": "1076192306@qq.com", "reva": { "id":
 *             2421, "valuename": "金牛区", "valuetype": { "id": 4, "typename":
 *             "区县" }, "onid": 269, "onname": null, "brevitycode": null },
 *             "contractno": "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "510623199108227118", "serviceaddress": "成都市金牛区", "contactphone":
 *             "13076063079", "contacttelephone": "028-83222680", "email":
 *             "qqqqq@qq.com", "reva": { "id": 3253, "valuename": "中国",
 *             "valuetype": { "id": 1, "typename": "国" }, "onid": 0, "onname":
 *             null, "brevitycode": null }, "contractno": "SC11111100000",
 *             "contractannex": "", "onservice": null, "state": "启用", "addtime":
 *             "2014-09-04 16:01:36", "starttime": 1409760000000, "sptype": "1",
 *             "endtime": 1457712000000, "supply": true, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2014-09-16 20:00:02", "starttime": 1410796800000, "sptype": "2",
 *             "endtime": 1411747200000, "supply": false, "imghead": "",
 *             "longitude": "104.08660070628", "latitude": "30.683978160118" },
 *             "localProductBean": { "id": 26, "productNumber": "YT33837667475",
 *             "productName": "[演示商品]松桂坊 五花腊肉500gx2袋 湖南湘西土特产烟熏肉-演示商品",
 *             "classValueBean": { "id": 10, "classValueName": "土特产",
 *             "classImg": null, "organizationId": "6" }, "productDescribe":
 *             "          ", "productPrice": 88, "img":
 *             "http://images.yitos.net/images/public/20150821/20591440147228107.jpg"
 *             , "imgs": [ { "id": 85, "imgName":
 *             "http://images.yitos.net/images/public/20150821/4391440147237558.jpg"
 *             }, { "id": 84, "imgName":
 *             "http://images.yitos.net/images/public/20150821/18741440147233799.jpg"
 *             } ], "privilege": "无", "detailedAddress": null, "productType":
 *             "产品", "productNumDateType": "数量", "areaId": "2421", "addDate":
 *             "2015-08-21", "sortDate": 20150821165408, "productDate": null,
 *             "productNum": 200, "deliveredToPaidYN": "是", "deliverYN": "否",
 *             "deliverNum": "0", "state": "上架", "providerBean": { "id": 6,
 *             "no": "YT445571890374", "brevitycode": "ytcsjmds", "servicename":
 *             "易田测试加盟店四", "businessno": "sadfsfsf", "contacts": "黄小川",
 *             "cardnumber": "510603198806090657", "serviceaddress":
 *             "解放路二段六号凤凰大厦", "contactphone": "13004766493", "contacttelephone":
 *             "028-12345678", "email": "1076192306@qq.com", "reva": { "id":
 *             2421, "valuename": "金牛区", "valuetype": { "id": 4, "typename":
 *             "区县" }, "onid": 269, "onname": null, "brevitycode": null },
 *             "contractno": "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "510623199108227118", "serviceaddress": "成都市金牛区", "contactphone":
 *             "13076063079", "contacttelephone": "028-83222680", "email":
 *             "qqqqq@qq.com", "reva": { "id": 3253, "valuename": "中国",
 *             "valuetype": { "id": 1, "typename": "国" }, "onid": 0, "onname":
 *             null, "brevitycode": null }, "contractno": "SC11111100000",
 *             "contractannex": "", "onservice": null, "state": "启用", "addtime":
 *             "2014-09-04 16:01:36", "starttime": 1409760000000, "sptype": "1",
 *             "endtime": 1457712000000, "supply": true, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2014-09-16 20:00:02", "starttime": 1410796800000, "sptype": "2",
 *             "endtime": 1411747200000, "supply": false, "imghead": "",
 *             "longitude": "104.08660070628", "latitude": "30.683978160118" },
 *             "remark": null, "aixinxianYN": "是" }, "regionalValueSet": [ {
 *             "id": 3253, "valuename": "中国", "valuetype": { "id": 1,
 *             "typename": "国" }, "onid": 0, "onname": null, "brevitycode": null
 *             } ], "startDate": "2015-08-21", "closeDate": "2015-08-31",
 *             "remark": "", "status": "2", "yes": "1", "gonghuoPrice": 55,
 *             "tradePrice": 69, "supplyPrice": 66 }
 * 
 */
public class ModelNongfu {

	ModelLocalService localService = new ModelLocalService();

	public ModelNongfu() {
	}

	public ModelNongfu(JSONObject object) throws JSONException {
		if (object != null) {
			JSONObject jsonObject = object.optJSONObject("localProductBean");
			if (jsonObject != null) {
				localService = new ModelLocalService(jsonObject);
			}
		}
	}

	public ModelLocalService getLocalService() {
		return localService;
	}

}
