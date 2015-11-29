package yitgogo.smart.suning.model;

/**
 * Created by Tiger on 2015-10-21.
 */
public interface API_SUNING {

//    String IP_SUNING = "http://gcapi.suning.com";
    String IP_SUNING = "http://58.240.86.161";

    /**
     * 获取品类(目录)接口
     *
     * @URL http://58.240.86.161/esbadapter/GEProductMgmt_GCMS/getProductCategory?data={"accessToken":"ae26377a628b0d2710f91257b67a1db0","appKey":"ZJZF","v":"2.0"}
     * @RESULT {"result":[{"name":"美上美A4打印纸","categoryId":"6040653c-7b47-4ed5-bd96-fe75b110b909"},{"name":"派克圆珠笔","categoryId":"f632ec4a-5797-4203-a7d0-101006a74efb"},{"name":"罗技键盘","categoryId":"0501599f-ef25-49ce-b853-88f3ca99828c"},{"name":"苏宁笔记本","categoryId":"6e557100-f2db-4d9c-8a3c-8c93505c29d6"},{"name":"罗技鼠标","categoryId":"f77a9987-5645-4ed0-bbf5-38b4c3485b3a"},{"name":"光明纸箱","categoryId":"8b0615e6-76ba-4c7c-ab57-19b64115e3ec"},{"name":"英雄铅笔","categoryId":"fe0520a9-bf6c-4218-b96a-af657ce6bdc6"},{"name":"中华钢笔","categoryId":"e60f58e6-f6bb-430d-a636-21bc226440d6"},{"name":"好用墨盒","categoryId":"a4368141-3bab-4cfd-9ce1-9f542f294d1c"},{"name":"蚌埠商品1","categoryId":"9dae3d16-e322-4796-93bb-a3dea425d14b"},{"name":"蚌埠商品2","categoryId":"da203a0c-0651-4afd-975c-1c35368b7cc6"},{"name":"四级页面新寻源","categoryId":"a069eba9-446a-44be-8b47-4842c05d418f"}],"isSuccess":true,"returnMsg":"success"}
     */
    String API_PRODUCT_CALSSES = IP_SUNING + "/esbadapter/GEProductMgmt_GCMS/getProductCategory";

    String API_PRODUCT_LIST = IP_SUNING + "/esbadapter/GEProductMgmt_GCMS/getProductPool";

    String API_PRODUCT_DETAIL = IP_SUNING + "/esbadapter/GEProductMgmt_GCMS/getProductDetail";

    String API_PRODUCT_IMAGES = IP_SUNING + "/esbadapter/GEProductMgmt_GCMS/getProductImage";

    String API_PRODUCT_STOCK = IP_SUNING + "/esbadapter/GEProductMgmt_GCMS/getProductInventory";

    String API_PRODUCT_STOCK_STATE = IP_SUNING + "/esbadapter/GEProductMgmt_GCMS/bathQueryInventoryStatus";

    String API_PRODUCT_STATE = IP_SUNING + "/esbadapter/GEProductMgmt_GCMS/getProductOnShelvesInfo";

    String API_PRODUCT_PRICE = IP_SUNING + "/esbadapter/GEProductMgmt_GCMS/getProductSNPrice";

    String API_AREA_PROVINCE = IP_SUNING + "/esbadapter/GEBaseInfoMgmt_GCMS/getProvinceInfo";

    String API_AREA_CITY = IP_SUNING + "/esbadapter/GEBaseInfoMgmt_GCMS/getCityInfo";

    String API_AREA_DISTRICT = IP_SUNING + "/esbadapter/GEBaseInfoMgmt_GCMS/getDistrictInfo";

    String API_AREA_TOWN = IP_SUNING + "/esbadapter/GEBaseInfoMgmt_GCMS/getTownInfo";

}
