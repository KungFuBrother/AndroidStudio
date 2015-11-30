package yitgogo.consumer.local.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.store.model.ModelStoreSelected;

/**
 * 本地商品
 * 
 * @author Tiger
 * 
 * @JsonObject { "id": 12011, "retailProdManagerName": "产品组-测试产品",
 *             "retailProdManagerNumber": "YT42207519862", "providerBean": {
 *             "id": 1445, "no": "YT205646883534", "brevitycode": "ytcsfwz",
 *             "servicename": "易田测试服务站", "businessno": "455114656563",
 *             "contacts": "蒋丰裕", "cardnumber": "510522199308169272",
 *             "serviceaddress": "四川省成都市金牛区解放路二段六号", "contactphone":
 *             "18227754003", "contacttelephone": "028-235669165465", "email":
 *             "46464@qq.com", "reva": { "id": 2421, "valuename": "金牛区",
 *             "valuetype": { "id": 4, "typename": "区县" }, "onid": 269,
 *             "onname": null, "brevitycode": null }, "contractno":
 *             "564646546443", "contractannex": "", "onservice": { "id": 6,
 *             "no": "YT445571890374", "brevitycode": "ytcsjmds", "servicename":
 *             "易田测试加盟店四", "businessno": "sadfsfsf", "contacts": "阿斯顿发送到",
 *             "cardnumber": "123456854585632514", "serviceaddress":
 *             "解放路二段六号凤凰大厦", "contactphone": "13228116626", "contacttelephone":
 *             "028-12345678", "email": "saaa@qqq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
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
 *             "longitude": "104.086087", "latitude": "30.727658" }, "state":
 *             "启用", "addtime": "2015-09-14 12:00:56", "starttime":
 *             1442160000000, "sptype": "3", "endtime": 1557072000000, "supply":
 *             false, "imghead": "", "longitude": "104.08669607219", "latitude":
 *             "30.684018810049" }, "retailProTypeValue": { "id": 342339,
 *             "retailProdTypeValueName": "电视", "retailClassTypeBean": { "id":
 *             2, "retailProductType": "小类" }, "retailClassValueParentBean": {
 *             "id": 342338, "retailProdTypeValueName": "大家电",
 *             "retailClassTypeBean": { "id": 1, "retailProductType": "大类" },
 *             "retailClassValueParentBean": null, "retailBrandSet": [],
 *             "retailProductTypeValueSet": [], "img": "1", "providerBean": {
 *             "id": 6, "no": "YT445571890374", "brevitycode": "ytcsjmds",
 *             "servicename": "易田测试加盟店四", "businessno": "sadfsfsf", "contacts":
 *             "阿斯顿发送到", "cardnumber": "123456854585632514", "serviceaddress":
 *             "解放路二段六号凤凰大厦", "contactphone": "13228116626", "contacttelephone":
 *             "028-12345678", "email": "saaa@qqq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
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
 *             "longitude": "104.086087", "latitude": "30.727658" } },
 *             "retailBrandSet": [], "retailProductTypeValueSet": [], "img":
 *             "1", "providerBean": { "id": 6, "no": "YT445571890374",
 *             "brevitycode": "ytcsjmds", "servicename": "易田测试加盟店四",
 *             "businessno": "sadfsfsf", "contacts": "阿斯顿发送到", "cardnumber":
 *             "123456854585632514", "serviceaddress": "解放路二段六号凤凰大厦",
 *             "contactphone": "13228116626", "contacttelephone":
 *             "028-12345678", "email": "saaa@qqq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
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
 *             "longitude": "104.086087", "latitude": "30.727658" } },
 *             "retailBrandBean": { "id": 557911, "retailBrandName": "卡卡",
 *             "retailBrandNameQP": "kaka", "retailBrandNameJP": "kk",
 *             "serviceProviderID": 6, "retailClassValueSet": [ { "id": 342339,
 *             "retailProdTypeValueName": "电视", "retailClassTypeBean": { "id":
 *             2, "retailProductType": "小类" }, "retailClassValueParentBean": {
 *             "id": 342338, "retailProdTypeValueName": "大家电",
 *             "retailClassTypeBean": { "id": 1, "retailProductType": "大类" },
 *             "retailClassValueParentBean": null, "retailBrandSet": [],
 *             "retailProductTypeValueSet": [], "img": "1", "providerBean": {
 *             "id": 6, "no": "YT445571890374", "brevitycode": "ytcsjmds",
 *             "servicename": "易田测试加盟店四", "businessno": "sadfsfsf", "contacts":
 *             "阿斯顿发送到", "cardnumber": "123456854585632514", "serviceaddress":
 *             "解放路二段六号凤凰大厦", "contactphone": "13228116626", "contacttelephone":
 *             "028-12345678", "email": "saaa@qqq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
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
 *             "longitude": "104.086087", "latitude": "30.727658" } },
 *             "retailBrandSet": [], "retailProductTypeValueSet": [], "img":
 *             "1", "providerBean": { "id": 6, "no": "YT445571890374",
 *             "brevitycode": "ytcsjmds", "servicename": "易田测试加盟店四",
 *             "businessno": "sadfsfsf", "contacts": "阿斯顿发送到", "cardnumber":
 *             "123456854585632514", "serviceaddress": "解放路二段六号凤凰大厦",
 *             "contactphone": "13228116626", "contacttelephone":
 *             "028-12345678", "email": "saaa@qqq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
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
 *             "longitude": "104.086087", "latitude": "30.727658" } }, { "id":
 *             342340, "retailProdTypeValueName": "洗衣机", "retailClassTypeBean":
 *             { "id": 2, "retailProductType": "小类" },
 *             "retailClassValueParentBean": { "id": 342338,
 *             "retailProdTypeValueName": "大家电", "retailClassTypeBean": { "id":
 *             1, "retailProductType": "大类" }, "retailClassValueParentBean":
 *             null, "retailBrandSet": [], "retailProductTypeValueSet": [],
 *             "img": "1", "providerBean": { "id": 6, "no": "YT445571890374",
 *             "brevitycode": "ytcsjmds", "servicename": "易田测试加盟店四",
 *             "businessno": "sadfsfsf", "contacts": "阿斯顿发送到", "cardnumber":
 *             "123456854585632514", "serviceaddress": "解放路二段六号凤凰大厦",
 *             "contactphone": "13228116626", "contacttelephone":
 *             "028-12345678", "email": "saaa@qqq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
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
 *             "longitude": "104.086087", "latitude": "30.727658" } },
 *             "retailBrandSet": [], "retailProductTypeValueSet": [], "img":
 *             "1", "providerBean": { "id": 6, "no": "YT445571890374",
 *             "brevitycode": "ytcsjmds", "servicename": "易田测试加盟店四",
 *             "businessno": "sadfsfsf", "contacts": "阿斯顿发送到", "cardnumber":
 *             "123456854585632514", "serviceaddress": "解放路二段六号凤凰大厦",
 *             "contactphone": "13228116626", "contacttelephone":
 *             "028-12345678", "email": "saaa@qqq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
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
 *             "longitude": "104.086087", "latitude": "30.727658" } }, { "id":
 *             342341, "retailProdTypeValueName": "电冰箱", "retailClassTypeBean":
 *             { "id": 2, "retailProductType": "小类" },
 *             "retailClassValueParentBean": { "id": 342338,
 *             "retailProdTypeValueName": "大家电", "retailClassTypeBean": { "id":
 *             1, "retailProductType": "大类" }, "retailClassValueParentBean":
 *             null, "retailBrandSet": [], "retailProductTypeValueSet": [],
 *             "img": "1", "providerBean": { "id": 6, "no": "YT445571890374",
 *             "brevitycode": "ytcsjmds", "servicename": "易田测试加盟店四",
 *             "businessno": "sadfsfsf", "contacts": "阿斯顿发送到", "cardnumber":
 *             "123456854585632514", "serviceaddress": "解放路二段六号凤凰大厦",
 *             "contactphone": "13228116626", "contacttelephone":
 *             "028-12345678", "email": "saaa@qqq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
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
 *             "longitude": "104.086087", "latitude": "30.727658" } },
 *             "retailBrandSet": [], "retailProductTypeValueSet": [], "img":
 *             "1", "providerBean": { "id": 6, "no": "YT445571890374",
 *             "brevitycode": "ytcsjmds", "servicename": "易田测试加盟店四",
 *             "businessno": "sadfsfsf", "contacts": "阿斯顿发送到", "cardnumber":
 *             "123456854585632514", "serviceaddress": "解放路二段六号凤凰大厦",
 *             "contactphone": "13228116626", "contacttelephone":
 *             "028-12345678", "email": "saaa@qqq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
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
 *             "longitude": "104.086087", "latitude": "30.727658" } }, { "id":
 *             342342, "retailProdTypeValueName": "空调", "retailClassTypeBean": {
 *             "id": 2, "retailProductType": "小类" },
 *             "retailClassValueParentBean": { "id": 342338,
 *             "retailProdTypeValueName": "大家电", "retailClassTypeBean": { "id":
 *             1, "retailProductType": "大类" }, "retailClassValueParentBean":
 *             null, "retailBrandSet": [], "retailProductTypeValueSet": [],
 *             "img": "1", "providerBean": { "id": 6, "no": "YT445571890374",
 *             "brevitycode": "ytcsjmds", "servicename": "易田测试加盟店四",
 *             "businessno": "sadfsfsf", "contacts": "阿斯顿发送到", "cardnumber":
 *             "123456854585632514", "serviceaddress": "解放路二段六号凤凰大厦",
 *             "contactphone": "13228116626", "contacttelephone":
 *             "028-12345678", "email": "saaa@qqq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
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
 *             "longitude": "104.086087", "latitude": "30.727658" } },
 *             "retailBrandSet": [], "retailProductTypeValueSet": [], "img":
 *             "1", "providerBean": { "id": 6, "no": "YT445571890374",
 *             "brevitycode": "ytcsjmds", "servicename": "易田测试加盟店四",
 *             "businessno": "sadfsfsf", "contacts": "阿斯顿发送到", "cardnumber":
 *             "123456854585632514", "serviceaddress": "解放路二段六号凤凰大厦",
 *             "contactphone": "13228116626", "contacttelephone":
 *             "028-12345678", "email": "saaa@qqq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
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
 *             "longitude": "104.086087", "latitude": "30.727658" } }, { "id":
 *             342343, "retailProdTypeValueName": "数字终端", "retailClassTypeBean":
 *             { "id": 2, "retailProductType": "小类" },
 *             "retailClassValueParentBean": { "id": 342338,
 *             "retailProdTypeValueName": "大家电", "retailClassTypeBean": { "id":
 *             1, "retailProductType": "大类" }, "retailClassValueParentBean":
 *             null, "retailBrandSet": [], "retailProductTypeValueSet": [],
 *             "img": "1", "providerBean": { "id": 6, "no": "YT445571890374",
 *             "brevitycode": "ytcsjmds", "servicename": "易田测试加盟店四",
 *             "businessno": "sadfsfsf", "contacts": "阿斯顿发送到", "cardnumber":
 *             "123456854585632514", "serviceaddress": "解放路二段六号凤凰大厦",
 *             "contactphone": "13228116626", "contacttelephone":
 *             "028-12345678", "email": "saaa@qqq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
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
 *             "longitude": "104.086087", "latitude": "30.727658" } },
 *             "retailBrandSet": [], "retailProductTypeValueSet": [], "img":
 *             "1", "providerBean": { "id": 6, "no": "YT445571890374",
 *             "brevitycode": "ytcsjmds", "servicename": "易田测试加盟店四",
 *             "businessno": "sadfsfsf", "contacts": "阿斯顿发送到", "cardnumber":
 *             "123456854585632514", "serviceaddress": "解放路二段六号凤凰大厦",
 *             "contactphone": "13228116626", "contacttelephone":
 *             "028-12345678", "email": "saaa@qqq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "四川易田商贸有限公司", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
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
 *             "longitude": "104.086087", "latitude": "30.727658" } } ] },
 *             "retailProdDescribe": "擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦擦",
 *             "retailPrice": 20, "unit": "123", "place": "312", "inventory":
 *             122, "bigImgUrl":
 *             "http://imageprocess.yitos.net/images/public/20151012/14901444639321863.jpg"
 *             , "retailProductImgList": [ { "id": 32696, "retailProductImgUrl":
 *             "http://imageprocess.yitos.net/images/public/20151012/98161444639328091.jpg"
 *             } ], "createDate": 1444639340000, "updateDate": 1444639340000,
 *             "regionalValueBean": { "id": 2421, "valuename": "金牛区",
 *             "valuetype": { "id": 4, "typename": "区县" }, "onid": 269,
 *             "onname": null, "brevitycode": null }, "userName": "ceshi4",
 *             "state": "2", "sort": 0, "bigRetailClassValueID": 342338,
 *             "littleRetailClassValueId": 342339, "brieFcode": "cpzcscp",
 *             "attName": "红色", "showYN": 0, "huohao": "123444" }
 */
public class ModelLocalGoods {

	String id = "", retailProdManagerName = "", retailProdManagerNumber = "",
			retailProdDescribe = "", unit = "", bigImgUrl = "", attName = "";
	double retailPrice = 0;

	ModelStoreSelected providerBean = new ModelStoreSelected();
	List<ModelLocalGoodsImage> images = new ArrayList<ModelLocalGoodsImage>();

	JSONObject jsonObject = new JSONObject();

	public ModelLocalGoods() {
	}

	public ModelLocalGoods(JSONObject object) throws JSONException {

		if (object != null) {
			jsonObject = object;
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("retailProdManagerName")) {
				if (!object.getString("retailProdManagerName")
						.equalsIgnoreCase("null")) {
					retailProdManagerName = object
							.optString("retailProdManagerName");
				}
			}
			if (object.has("retailProdManagerNumber")) {
				if (!object.getString("retailProdManagerNumber")
						.equalsIgnoreCase("null")) {
					retailProdManagerNumber = object
							.optString("retailProdManagerNumber");
				}
			}
			JSONObject providerBeanObject = object
					.optJSONObject("providerBean");
			if (providerBeanObject != null) {
				providerBean = new ModelStoreSelected(providerBeanObject);
			}
			if (object.has("retailProdDescribe")) {
				if (!object.getString("retailProdDescribe").equalsIgnoreCase(
						"null")) {
					retailProdDescribe = object.optString("retailProdDescribe");
				}
			}
			if (object.has("retailPrice")) {
				if (!object.getString("retailPrice").equalsIgnoreCase("null")) {
					retailPrice = object.optDouble("retailPrice");
				}
			}
			if (object.has("unit")) {
				if (!object.getString("unit").equalsIgnoreCase("null")) {
					unit = object.optString("unit");
				}
			}
			if (object.has("bigImgUrl")) {
				if (!object.getString("bigImgUrl").equalsIgnoreCase("null")) {
					bigImgUrl = object.optString("bigImgUrl");
				}
			}
			JSONArray retailProductImgList = object
					.optJSONArray("retailProductImgList");
			if (retailProductImgList != null) {
				for (int i = 0; i < retailProductImgList.length(); i++) {
					images.add(new ModelLocalGoodsImage(retailProductImgList
							.optJSONObject(i)));
				}
			}
			if (object.has("attName")) {
				if (!object.getString("attName").equalsIgnoreCase("null")) {
					attName = object.optString("attName");
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getRetailProdManagerName() {
		return retailProdManagerName;
	}

	public String getRetailProdManagerNumber() {
		return retailProdManagerNumber;
	}

	public String getRetailProdDescribe() {
		return retailProdDescribe;
	}

	public String getUnit() {
		return unit;
	}

	public String getBigImgUrl() {
		return bigImgUrl;
	}

	public String getAttName() {
		return attName;
	}

	public double getRetailPrice() {
		return retailPrice;
	}

	public ModelStoreSelected getProviderBean() {
		return providerBean;
	}

	public List<ModelLocalGoodsImage> getImages() {
		return images;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	@Override
	public String toString() {
		return "ModelLocalGoods [id=" + id + ", retailProdManagerName="
				+ retailProdManagerName + ", retailProdManagerNumber="
				+ retailProdManagerNumber + ", retailProdDescribe="
				+ retailProdDescribe + ", unit=" + unit + ", bigImgUrl="
				+ bigImgUrl + ", attName=" + attName + ", retailPrice="
				+ retailPrice + ", providerBean=" + providerBean + ", images="
				+ images + ", jsonObject=" + jsonObject + "]";
	}

}
