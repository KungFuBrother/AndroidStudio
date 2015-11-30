package yitgogo.consumer.local.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.product.model.ModelProductRelation;

/**
 * 本地商品
 * 
 * @author Tiger
 * 
 * @JsonObject { "message": "ok", "state": "SUCCESS", "cacheKey": null,
 *             "dataList": [], "totalCount": 1, "dataMap": { "listzu": [ { "id":
 *             "12010", "attName": "白色", "img":
 *             "http://imageprocess.yitos.net/images/public/20151012/49431444639318719.jpg"
 *             , "number": "YT42207461500" }, { "id": "12011", "attName": "红色",
 *             "img":
 *             "http://imageprocess.yitos.net/images/public/20151012/14901444639321863.jpg"
 *             , "number": "YT42207519862" }, { "id": "12012", "attName": "黑色",
 *             "img":
 *             "http://imageprocess.yitos.net/images/public/20151012/10001444639323227.jpg"
 *             , "number": "YT42207672417" } ], "bean": { "id": 12011,
 *             "retailProdManagerName": "产品组-测试产品", "retailProdManagerNumber":
 *             "YT42207519862", "providerBean": { "id": 6, "no":
 *             "YT445571890374", "brevitycode": "ytcsjmds", "servicename":
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
 *             "longitude": "104.086087", "latitude": "30.727658" },
 *             "retailProTypeValue": { "id": 342339, "retailProdTypeValueName":
 *             "电视", "retailClassTypeBean": { "id": 2, "retailProductType": "小类"
 *             }, "retailClassValueParentBean": { "id": 342338,
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
 *             "retailPrice": 456, "unit": "123", "place": "312", "inventory":
 *             123, "bigImgUrl":
 *             "http://imageprocess.yitos.net/images/public/20151012/14901444639321863.jpg"
 *             , "retailProductImgList": [ { "id": 32696, "retailProductImgUrl":
 *             "http://imageprocess.yitos.net/images/public/20151012/98161444639328091.jpg"
 *             } ], "createDate": 1444639340000, "updateDate": 1444639340000,
 *             "regionalValueBean": { "id": 2421, "valuename": "金牛区",
 *             "valuetype": { "id": 4, "typename": "区县" }, "onid": 269,
 *             "onname": null, "brevitycode": null }, "userName": "ceshi4",
 *             "state": "2", "sort": 0, "bigRetailClassValueID": 342338,
 *             "littleRetailClassValueId": 342339, "brieFcode": "cpzcscp",
 *             "payOnDelivery": "1", "deliverYN": "1", "deliverNum": "0",
 *             "attName": "红色", "showYN": 0, "huohao": "123444" } }, "bean":
 *             null }
 */
public class ModelLocalGoodsDetail {

	List<ModelProductRelation> productRelations = new ArrayList<ModelProductRelation>();
	ModelLocalGoods localGoods = new ModelLocalGoods();

	public ModelLocalGoodsDetail() {
	}

	public ModelLocalGoodsDetail(JSONObject object) throws JSONException {

		if (object != null) {
			JSONArray listzu = object.optJSONArray("listzu");
			if (listzu != null) {
				for (int i = 0; i < listzu.length(); i++) {
					productRelations.add(new ModelProductRelation(listzu
							.optJSONObject(i)));
				}
			}
			localGoods = new ModelLocalGoods(object.optJSONObject("bean"));
		}
	}

	public List<ModelProductRelation> getProductRelations() {
		return productRelations;
	}

	public ModelLocalGoods getLocalGoods() {
		return localGoods;
	}

}
