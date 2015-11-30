package yitgogo.consumer.local.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.store.model.ModelStoreSelected;

/**
 * 
 * @author Tiger
 * 
 * @JsonObject { "id": 272, "retailOrderNumber": "YT3978893852", "customerName":
 *             "雷小武", "customerPhone": "13032889558", "deliveryAddress": null,
 *             "orderDate": "2015-09-21 18:23:39", "retailOrderType": "",
 *             "retailOrderPrice": 73, "retailOrderProductInfoSet": [ { "id":
 *             335, "retailProductName": "重庆市潼南区特产天府牌糖水黄桃罐头正品包邮8罐装",
 *             "retailProductNumber": "YT31950453586", "retailProductType":
 *             "普通产品", "productQuantity": 1, "retailProductUnitPrice": 73,
 *             "retailProductTypeValue": { "id": 396670,
 *             "retailProdTypeValueName": "黄桃罐头", "retailClassTypeBean": { "id":
 *             2, "retailProductType": "小类" }, "retailClassValueParentBean": {
 *             "id": 193288, "retailProdTypeValueName": "食用特产",
 *             "retailClassTypeBean": { "id": 1, "retailProductType": "大类" },
 *             "retailClassValueParentBean": null, "retailBrandSet": [],
 *             "retailProductTypeValueSet": [], "img": "1", "providerBean": {
 *             "id": 48, "no": "YT201254129006", "brevitycode": "zqtnxczlfwzx",
 *             "servicename": "重庆潼南县陈祖伦服务中心", "businessno": "530124578895632",
 *             "contacts": "陈祖伦", "cardnumber": "654126196106232530",
 *             "serviceaddress": "重庆市潼南县柏梓镇金盆路262号", "contactphone":
 *             "13101392816", "contacttelephone": "13101392816", "email":
 *             "3210262459@qq.com", "reva": { "id": 3385, "valuename": "柏梓镇",
 *             "valuetype": { "id": 5, "typename": "镇" }, "onid": 2395,
 *             "onname": null, "brevitycode": null }, "contractno": "49120",
 *             "contractannex": "", "onservice": { "id": 1476, "no":
 *             "YT452293682649", "brevitycode": "zqtnxyyzx", "servicename":
 *             "重庆潼南县运营中心", "businessno": "500223008234904  ", "contacts": "龙帅",
 *             "cardnumber": "500223198604230035 ", "serviceaddress":
 *             "重庆市潼南区桂林街道办事处夏露街247号", "contactphone": "18166473331   ",
 *             "contacttelephone": "18166473331   ", "email":
 *             "975151517@qq.com", "reva": { "id": 2395, "valuename": "潼南县",
 *             "valuetype": { "id": 4, "typename": "区县" }, "onid": 268,
 *             "onname": null, "brevitycode": null }, "contractno": "6543123",
 *             "contractannex": "", "onservice": { "id": 64, "no":
 *             "YT201257570339", "brevitycode": "zqsfws", "servicename":
 *             "重庆市运营中心", "businessno": "5002272100123", "contacts": "徐刚",
 *             "cardnumber": "510232196502230017", "serviceaddress":
 *             "重庆市渝中区新华路220号雅兰电子城18-1", "contactphone": "13320337477",
 *             "contacttelephone": "023-86362727", "email": "123456798@qq.com",
 *             "reva": { "id": 22, "valuename": "重庆市", "valuetype": { "id": 2,
 *             "typename": "省" }, "onid": 3253, "onname": null, "brevitycode":
 *             null }, "contractno": "984152", "contractannex": "", "onservice":
 *             { "id": 1, "no": "YT613630259926", "brevitycode": "scytsmyxgs",
 *             "servicename": "易田总运营中心", "businessno": "VB11122220000",
 *             "contacts": "易田", "cardnumber": "111111111111111111",
 *             "serviceaddress": "成都市金牛区", "contactphone": "13076063079",
 *             "contacttelephone": "028-83222680", "email": "qqqqq@qq.com",
 *             "reva": { "id": 3253, "valuename": "中国", "valuetype": { "id": 1,
 *             "typename": "国" }, "onid": 0, "onname": null, "brevitycode": null
 *             }, "contractno": "SC11111100000", "contractannex": "",
 *             "onservice": null, "state": "启用", "addtime":
 *             "2014-09-04 16:01:36", "starttime": 1409760000000, "sptype": "1",
 *             "endtime": 1457712000000, "supply": true, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2014-10-21 16:26:24", "starttime": 1413820800000, "sptype": "1",
 *             "endtime": 1508428800000, "supply": false, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2015-09-16 12:12:22", "starttime": 1442332800000, "sptype": "1",
 *             "endtime": 1537027200000, "supply": false, "imghead": "",
 *             "longitude": "105.84619991868", "latitude": "30.195062649868" },
 *             "state": "启用", "addtime": "2014-10-21 14:31:38", "starttime":
 *             1413820800000, "sptype": "2", "endtime": 1508428800000, "supply":
 *             false, "imghead": "", "longitude": "105.72370240607", "latitude":
 *             "30.102609026591" } }, "retailBrandSet": [],
 *             "retailProductTypeValueSet": [], "img": "undefined",
 *             "providerBean": { "id": 48, "no": "YT201254129006",
 *             "brevitycode": "zqtnxczlfwzx", "servicename": "重庆潼南县陈祖伦服务中心",
 *             "businessno": "530124578895632", "contacts": "陈祖伦", "cardnumber":
 *             "654126196106232530", "serviceaddress": "重庆市潼南县柏梓镇金盆路262号",
 *             "contactphone": "13101392816", "contacttelephone": "13101392816",
 *             "email": "3210262459@qq.com", "reva": { "id": 3385, "valuename":
 *             "柏梓镇", "valuetype": { "id": 5, "typename": "镇" }, "onid": 2395,
 *             "onname": null, "brevitycode": null }, "contractno": "49120",
 *             "contractannex": "", "onservice": { "id": 1476, "no":
 *             "YT452293682649", "brevitycode": "zqtnxyyzx", "servicename":
 *             "重庆潼南县运营中心", "businessno": "500223008234904  ", "contacts": "龙帅",
 *             "cardnumber": "500223198604230035 ", "serviceaddress":
 *             "重庆市潼南区桂林街道办事处夏露街247号", "contactphone": "18166473331   ",
 *             "contacttelephone": "18166473331   ", "email":
 *             "975151517@qq.com", "reva": { "id": 2395, "valuename": "潼南县",
 *             "valuetype": { "id": 4, "typename": "区县" }, "onid": 268,
 *             "onname": null, "brevitycode": null }, "contractno": "6543123",
 *             "contractannex": "", "onservice": { "id": 64, "no":
 *             "YT201257570339", "brevitycode": "zqsfws", "servicename":
 *             "重庆市运营中心", "businessno": "5002272100123", "contacts": "徐刚",
 *             "cardnumber": "510232196502230017", "serviceaddress":
 *             "重庆市渝中区新华路220号雅兰电子城18-1", "contactphone": "13320337477",
 *             "contacttelephone": "023-86362727", "email": "123456798@qq.com",
 *             "reva": { "id": 22, "valuename": "重庆市", "valuetype": { "id": 2,
 *             "typename": "省" }, "onid": 3253, "onname": null, "brevitycode":
 *             null }, "contractno": "984152", "contractannex": "", "onservice":
 *             { "id": 1, "no": "YT613630259926", "brevitycode": "scytsmyxgs",
 *             "servicename": "易田总运营中心", "businessno": "VB11122220000",
 *             "contacts": "易田", "cardnumber": "111111111111111111",
 *             "serviceaddress": "成都市金牛区", "contactphone": "13076063079",
 *             "contacttelephone": "028-83222680", "email": "qqqqq@qq.com",
 *             "reva": { "id": 3253, "valuename": "中国", "valuetype": { "id": 1,
 *             "typename": "国" }, "onid": 0, "onname": null, "brevitycode": null
 *             }, "contractno": "SC11111100000", "contractannex": "",
 *             "onservice": null, "state": "启用", "addtime":
 *             "2014-09-04 16:01:36", "starttime": 1409760000000, "sptype": "1",
 *             "endtime": 1457712000000, "supply": true, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2014-10-21 16:26:24", "starttime": 1413820800000, "sptype": "1",
 *             "endtime": 1508428800000, "supply": false, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2015-09-16 12:12:22", "starttime": 1442332800000, "sptype": "1",
 *             "endtime": 1537027200000, "supply": false, "imghead": "",
 *             "longitude": "105.84619991868", "latitude": "30.195062649868" },
 *             "state": "启用", "addtime": "2014-10-21 14:31:38", "starttime":
 *             1413820800000, "sptype": "2", "endtime": 1508428800000, "supply":
 *             false, "imghead": "", "longitude": "105.72370240607", "latitude":
 *             "30.102609026591" } }, "consumptionInfo": "", "createDate":
 *             "2015-09-21 18:23:39", "state": "努力发货中", "content": "",
 *             "sourceProviderBean": { "id": 48, "no": "YT201254129006",
 *             "brevitycode": "zqtnxczlfwzx", "servicename": "重庆潼南县陈祖伦服务中心",
 *             "businessno": "530124578895632", "contacts": "陈祖伦", "cardnumber":
 *             "654126196106232530", "serviceaddress": "重庆市潼南县柏梓镇金盆路262号",
 *             "contactphone": "13101392816", "contacttelephone": "13101392816",
 *             "email": "3210262459@qq.com", "reva": { "id": 3385, "valuename":
 *             "柏梓镇", "valuetype": { "id": 5, "typename": "镇" }, "onid": 2395,
 *             "onname": null, "brevitycode": null }, "contractno": "49120",
 *             "contractannex": "", "onservice": { "id": 1476, "no":
 *             "YT452293682649", "brevitycode": "zqtnxyyzx", "servicename":
 *             "重庆潼南县运营中心", "businessno": "500223008234904  ", "contacts": "龙帅",
 *             "cardnumber": "500223198604230035 ", "serviceaddress":
 *             "重庆市潼南区桂林街道办事处夏露街247号", "contactphone": "18166473331   ",
 *             "contacttelephone": "18166473331   ", "email":
 *             "975151517@qq.com", "reva": { "id": 2395, "valuename": "潼南县",
 *             "valuetype": { "id": 4, "typename": "区县" }, "onid": 268,
 *             "onname": null, "brevitycode": null }, "contractno": "6543123",
 *             "contractannex": "", "onservice": { "id": 64, "no":
 *             "YT201257570339", "brevitycode": "zqsfws", "servicename":
 *             "重庆市运营中心", "businessno": "5002272100123", "contacts": "徐刚",
 *             "cardnumber": "510232196502230017", "serviceaddress":
 *             "重庆市渝中区新华路220号雅兰电子城18-1", "contactphone": "13320337477",
 *             "contacttelephone": "023-86362727", "email": "123456798@qq.com",
 *             "reva": { "id": 22, "valuename": "重庆市", "valuetype": { "id": 2,
 *             "typename": "省" }, "onid": 3253, "onname": null, "brevitycode":
 *             null }, "contractno": "984152", "contractannex": "", "onservice":
 *             { "id": 1, "no": "YT613630259926", "brevitycode": "scytsmyxgs",
 *             "servicename": "易田总运营中心", "businessno": "VB11122220000",
 *             "contacts": "易田", "cardnumber": "111111111111111111",
 *             "serviceaddress": "成都市金牛区", "contactphone": "13076063079",
 *             "contacttelephone": "028-83222680", "email": "qqqqq@qq.com",
 *             "reva": { "id": 3253, "valuename": "中国", "valuetype": { "id": 1,
 *             "typename": "国" }, "onid": 0, "onname": null, "brevitycode": null
 *             }, "contractno": "SC11111100000", "contractannex": "",
 *             "onservice": null, "state": "启用", "addtime":
 *             "2014-09-04 16:01:36", "starttime": 1409760000000, "sptype": "1",
 *             "endtime": 1457712000000, "supply": true, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2014-10-21 16:26:24", "starttime": 1413820800000, "sptype": "1",
 *             "endtime": 1508428800000, "supply": false, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2015-09-16 12:12:22", "starttime": 1442332800000, "sptype": "1",
 *             "endtime": 1537027200000, "supply": false, "imghead": "",
 *             "longitude": "105.84619991868", "latitude": "30.195062649868" },
 *             "state": "启用", "addtime": "2014-10-21 14:31:38", "starttime":
 *             1413820800000, "sptype": "2", "endtime": 1508428800000, "supply":
 *             false, "imghead": "", "longitude": "105.72370240607", "latitude":
 *             "30.102609026591" }, "img":
 *             "http://images.yitos.net/images/public/20150916/53921442412376767.jpg"
 *             , "attName": null } ], "retailOrderStatus": "新订单", "paymentType":
 *             "2", "payment": "未付款", "providerBean": { "id": 48, "no":
 *             "YT201254129006", "brevitycode": "zqtnxczlfwzx", "servicename":
 *             "重庆潼南县陈祖伦服务中心", "businessno": "530124578895632", "contacts":
 *             "陈祖伦", "cardnumber": "654126196106232530", "serviceaddress":
 *             "重庆市潼南县柏梓镇金盆路262号", "contactphone": "13101392816",
 *             "contacttelephone": "13101392816", "email": "3210262459@qq.com",
 *             "reva": { "id": 3385, "valuename": "柏梓镇", "valuetype": { "id": 5,
 *             "typename": "镇" }, "onid": 2395, "onname": null, "brevitycode":
 *             null }, "contractno": "49120", "contractannex": "", "onservice":
 *             { "id": 1476, "no": "YT452293682649", "brevitycode": "zqtnxyyzx",
 *             "servicename": "重庆潼南县运营中心", "businessno": "500223008234904  ",
 *             "contacts": "龙帅", "cardnumber": "500223198604230035 ",
 *             "serviceaddress": "重庆市潼南区桂林街道办事处夏露街247号", "contactphone":
 *             "18166473331   ", "contacttelephone": "18166473331   ", "email":
 *             "975151517@qq.com", "reva": { "id": 2395, "valuename": "潼南县",
 *             "valuetype": { "id": 4, "typename": "区县" }, "onid": 268,
 *             "onname": null, "brevitycode": null }, "contractno": "6543123",
 *             "contractannex": "", "onservice": { "id": 64, "no":
 *             "YT201257570339", "brevitycode": "zqsfws", "servicename":
 *             "重庆市运营中心", "businessno": "5002272100123", "contacts": "徐刚",
 *             "cardnumber": "510232196502230017", "serviceaddress":
 *             "重庆市渝中区新华路220号雅兰电子城18-1", "contactphone": "13320337477",
 *             "contacttelephone": "023-86362727", "email": "123456798@qq.com",
 *             "reva": { "id": 22, "valuename": "重庆市", "valuetype": { "id": 2,
 *             "typename": "省" }, "onid": 3253, "onname": null, "brevitycode":
 *             null }, "contractno": "984152", "contractannex": "", "onservice":
 *             { "id": 1, "no": "YT613630259926", "brevitycode": "scytsmyxgs",
 *             "servicename": "易田总运营中心", "businessno": "VB11122220000",
 *             "contacts": "易田", "cardnumber": "111111111111111111",
 *             "serviceaddress": "成都市金牛区", "contactphone": "13076063079",
 *             "contacttelephone": "028-83222680", "email": "qqqqq@qq.com",
 *             "reva": { "id": 3253, "valuename": "中国", "valuetype": { "id": 1,
 *             "typename": "国" }, "onid": 0, "onname": null, "brevitycode": null
 *             }, "contractno": "SC11111100000", "contractannex": "",
 *             "onservice": null, "state": "启用", "addtime":
 *             "2014-09-04 16:01:36", "starttime": 1409760000000, "sptype": "1",
 *             "endtime": 1457712000000, "supply": true, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2014-10-21 16:26:24", "starttime": 1413820800000, "sptype": "1",
 *             "endtime": 1508428800000, "supply": false, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2015-09-16 12:12:22", "starttime": 1442332800000, "sptype": "1",
 *             "endtime": 1537027200000, "supply": false, "imghead": "",
 *             "longitude": "105.84619991868", "latitude": "30.195062649868" },
 *             "state": "启用", "addtime": "2014-10-21 14:31:38", "starttime":
 *             1413820800000, "sptype": "2", "endtime": 1508428800000, "supply":
 *             false, "imghead": "", "longitude": "105.72370240607", "latitude":
 *             "30.102609026591" }, "sourceProviderBean": { "id": 48, "no":
 *             "YT201254129006", "brevitycode": "zqtnxczlfwzx", "servicename":
 *             "重庆潼南县陈祖伦服务中心", "businessno": "530124578895632", "contacts":
 *             "陈祖伦", "cardnumber": "654126196106232530", "serviceaddress":
 *             "重庆市潼南县柏梓镇金盆路262号", "contactphone": "13101392816",
 *             "contacttelephone": "13101392816", "email": "3210262459@qq.com",
 *             "reva": { "id": 3385, "valuename": "柏梓镇", "valuetype": { "id": 5,
 *             "typename": "镇" }, "onid": 2395, "onname": null, "brevitycode":
 *             null }, "contractno": "49120", "contractannex": "", "onservice":
 *             { "id": 1476, "no": "YT452293682649", "brevitycode": "zqtnxyyzx",
 *             "servicename": "重庆潼南县运营中心", "businessno": "500223008234904  ",
 *             "contacts": "龙帅", "cardnumber": "500223198604230035 ",
 *             "serviceaddress": "重庆市潼南区桂林街道办事处夏露街247号", "contactphone":
 *             "18166473331   ", "contacttelephone": "18166473331   ", "email":
 *             "975151517@qq.com", "reva": { "id": 2395, "valuename": "潼南县",
 *             "valuetype": { "id": 4, "typename": "区县" }, "onid": 268,
 *             "onname": null, "brevitycode": null }, "contractno": "6543123",
 *             "contractannex": "", "onservice": { "id": 64, "no":
 *             "YT201257570339", "brevitycode": "zqsfws", "servicename":
 *             "重庆市运营中心", "businessno": "5002272100123", "contacts": "徐刚",
 *             "cardnumber": "510232196502230017", "serviceaddress":
 *             "重庆市渝中区新华路220号雅兰电子城18-1", "contactphone": "13320337477",
 *             "contacttelephone": "023-86362727", "email": "123456798@qq.com",
 *             "reva": { "id": 22, "valuename": "重庆市", "valuetype": { "id": 2,
 *             "typename": "省" }, "onid": 3253, "onname": null, "brevitycode":
 *             null }, "contractno": "984152", "contractannex": "", "onservice":
 *             { "id": 1, "no": "YT613630259926", "brevitycode": "scytsmyxgs",
 *             "servicename": "易田总运营中心", "businessno": "VB11122220000",
 *             "contacts": "易田", "cardnumber": "111111111111111111",
 *             "serviceaddress": "成都市金牛区", "contactphone": "13076063079",
 *             "contacttelephone": "028-83222680", "email": "qqqqq@qq.com",
 *             "reva": { "id": 3253, "valuename": "中国", "valuetype": { "id": 1,
 *             "typename": "国" }, "onid": 0, "onname": null, "brevitycode": null
 *             }, "contractno": "SC11111100000", "contractannex": "",
 *             "onservice": null, "state": "启用", "addtime":
 *             "2014-09-04 16:01:36", "starttime": 1409760000000, "sptype": "1",
 *             "endtime": 1457712000000, "supply": true, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2014-10-21 16:26:24", "starttime": 1413820800000, "sptype": "1",
 *             "endtime": 1508428800000, "supply": false, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2015-09-16 12:12:22", "starttime": 1442332800000, "sptype": "1",
 *             "endtime": 1537027200000, "supply": false, "imghead": "",
 *             "longitude": "105.84619991868", "latitude": "30.195062649868" },
 *             "state": "启用", "addtime": "2014-10-21 14:31:38", "starttime":
 *             1413820800000, "sptype": "2", "endtime": 1508428800000, "supply":
 *             false, "imghead": "", "longitude": "105.72370240607", "latitude":
 *             "30.102609026591" }, "memberAccount": "13032889558",
 *             "parentOrderNumber": null, "deliveryType": "自取", "mustAddress":
 *             "重庆市潼南县柏梓镇剧场路", "fahuoDate": 0, "jiqima": null }
 * 
 */

public class ModelLocalGoodsOrder {

	String id = "", retailOrderNumber = "", customerName = "",
			customerPhone = "", deliveryAddress = "", orderDate = "",
			retailOrderType = "", retailOrderStatus = "", paymentType = "",
			memberAccount = "", parentOrderNumber = "", deliveryType = "",
			mustAddress = "";
	double retailOrderPrice = 0;
	List<ModelLocalGoodsOrderGoods> orderGoods = new ArrayList<ModelLocalGoodsOrderGoods>();
	ModelStoreSelected sourceProviderBean = new ModelStoreSelected();
	JSONObject jsonObject = new JSONObject();

	public ModelLocalGoodsOrder() {
	}

	public ModelLocalGoodsOrder(JSONObject object) throws JSONException {

		if (object != null) {
			this.jsonObject = object;
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("retailOrderNumber")) {
				if (!object.getString("retailOrderNumber").equalsIgnoreCase(
						"null")) {
					retailOrderNumber = object.optString("retailOrderNumber");
				}
			}
			if (object.has("customerName")) {
				if (!object.getString("customerName").equalsIgnoreCase("null")) {
					customerName = object.optString("customerName");
				}
			}
			if (object.has("customerPhone")) {
				if (!object.getString("customerPhone").equalsIgnoreCase("null")) {
					customerPhone = object.optString("customerPhone");
				}
			}
			if (object.has("deliveryAddress")) {
				if (!object.getString("deliveryAddress").equalsIgnoreCase(
						"null")) {
					deliveryAddress = object.optString("deliveryAddress");
				}
			}
			if (object.has("orderDate")) {
				if (!object.getString("orderDate").equalsIgnoreCase("null")) {
					orderDate = object.optString("orderDate");
				}
			}
			if (object.has("retailOrderType")) {
				if (!object.getString("retailOrderType").equalsIgnoreCase(
						"null")) {
					retailOrderType = object.optString("retailOrderType");
				}
			}
			if (object.has("retailOrderPrice")) {
				if (!object.getString("retailOrderPrice").equalsIgnoreCase(
						"null")) {
					retailOrderPrice = object.optDouble("retailOrderPrice");
				}
			}
			if (object.has("retailOrderStatus")) {
				if (!object.getString("retailOrderStatus").equalsIgnoreCase(
						"null")) {
					retailOrderStatus = object.optString("retailOrderStatus");
				}
			}
			if (object.has("paymentType")) {
				if (!object.getString("paymentType").equalsIgnoreCase("null")) {
					paymentType = object.optString("paymentType");
				}
			}
			if (object.has("memberAccount")) {
				if (!object.getString("memberAccount").equalsIgnoreCase("null")) {
					memberAccount = object.optString("memberAccount");
				}
			}
			if (object.has("parentOrderNumber")) {
				if (!object.getString("parentOrderNumber").equalsIgnoreCase(
						"null")) {
					parentOrderNumber = object.optString("parentOrderNumber");
				}
			}
			if (object.has("deliveryType")) {
				if (!object.getString("deliveryType").equalsIgnoreCase("null")) {
					deliveryType = object.optString("deliveryType");
				}
			}
			if (object.has("mustAddress")) {
				if (!object.getString("mustAddress").equalsIgnoreCase("null")) {
					mustAddress = object.optString("mustAddress");
				}
			}
			JSONObject sourceProviderBeanJsonObject = object
					.optJSONObject("sourceProviderBean");
			if (sourceProviderBean != null) {
				sourceProviderBean = new ModelStoreSelected(
						sourceProviderBeanJsonObject);
			}
			JSONArray retailOrderProductInfoSet = object
					.optJSONArray("retailOrderProductInfoSet");
			if (retailOrderProductInfoSet != null) {
				for (int i = 0; i < retailOrderProductInfoSet.length(); i++) {
					orderGoods.add(new ModelLocalGoodsOrderGoods(
							retailOrderProductInfoSet.optJSONObject(i)));
				}
			}
		}

	}

	public String getId() {
		return id;
	}

	public String getRetailOrderNumber() {
		return retailOrderNumber;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public String getRetailOrderType() {
		return retailOrderType;
	}

	public String getRetailOrderStatus() {
		return retailOrderStatus;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public String getMemberAccount() {
		return memberAccount;
	}

	public String getParentOrderNumber() {
		return parentOrderNumber;
	}

	public double getRetailOrderPrice() {
		return retailOrderPrice;
	}

	public List<ModelLocalGoodsOrderGoods> getOrderGoods() {
		return orderGoods;
	}

	public ModelStoreSelected getSourceProviderBean() {
		return sourceProviderBean;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public String getMustAddress() {
		return mustAddress;
	}

	@Override
	public String toString() {
		return "ModelLocalGoodsOrder [id=" + id + ", retailOrderNumber="
				+ retailOrderNumber + ", customerName=" + customerName
				+ ", customerPhone=" + customerPhone + ", deliveryAddress="
				+ deliveryAddress + ", orderDate=" + orderDate
				+ ", retailOrderType=" + retailOrderType
				+ ", retailOrderStatus=" + retailOrderStatus + ", paymentType="
				+ paymentType + ", memberAccount=" + memberAccount
				+ ", parentOrderNumber=" + parentOrderNumber
				+ ", deliveryType=" + deliveryType + ", mustAddress="
				+ mustAddress + ", retailOrderPrice=" + retailOrderPrice
				+ ", orderGoods=" + orderGoods + ", sourceProviderBean="
				+ sourceProviderBean + ", jsonObject=" + jsonObject + "]";
	}

}
