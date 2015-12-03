package yitgogo.smart.tools;

public interface API {

    /**
     * 检查更新
     */
    public String API_UPDATE = "http://updatePhone.yitos.net/android/version_smart.js";
    public String API_DOWNLOAD = "http://updatePhone.yitos.net/android/yitgogo_smart.apk";

    public String API_IP = "http://yitos.net";
//    public String API_IP = "http://192.168.8.8:8050";

    /**
     * 获取服务器系统时间
     */
    public String API_GET_SYSTEM_TIME = API_IP + "/api/agency/mechanismManage/equipment/findSystemTime";

    /**
     * 说明：根据手机号码和面值（为慢充时还须到账时间）查询充值信息 参数： phoneno 手机号码 必须 pervalue 面值 必须
     * 快充可选面值（1、2、5、10、20、30、50、100、300、500） 慢充可选面值（30、50、100）
     * <p>
     * mctype 慢充到账时间 慢充才传 0.5（半小时到账）、4（4小时到账）、12（12小时到账）、
     * 24（24小时到账）、48（48小时到账）、72（72小时到账） 默认为24
     */
    public String API_BIANMIN_PHONE_CHARGE_INFO = API_IP
            + "/api/facilitate/recharge/findPhoneInfo";
    /**
     * 说明：生成手机充值订单 参数： phoneno 手机号码 必须 pervalue 面值 必须 mctype 慢充到账时间 慢充才传
     */
    public String API_BIANMIN_PHONE_CHARGE = API_IP
            + "/api/facilitate/recharge/addOrder/addPhoneOrder";

    /**
     * 说明：根据固话号码，面值，运营商，充值类型查询充值信息 参数： phoneno 固话/宽带号码 必须 格式：021-88888888
     * <p>
     * pervalue 面值 必须 电信：10、20、30、50、100、300 联通：50、100
     * <p>
     * teltype 运营商 必须 1、电信 2、联通（默认选中1）
     * <p>
     * chargeType 充值类型 必须 1：固话；2：宽带（默认选中1）
     */
    public String API_BIANMIN_TELEPHONE_CHARGE_INFO = API_IP
            + "/api/facilitate/recharge/fixtelquery";
    /**
     * 说明：生成固话/宽带充值订单 参数： phoneno 固话/宽带号码 必须 pervalue 面值 必须 teltype 运营商编号 必须
     * chargeType 充值类型编号 必须 memberAccount 会员账号 登录才传 返回结果：map orderNumber 订单编号
     */
    public String API_BIANMIN_TELEPHONE_CHARGE = API_IP
            + "/api/facilitate/recharge/addOrder/addFixtelOrder";

    /**
     * 说明：根据手机号码和面值（为慢充时还须到账时间）查询充值信息 参数： game_userid 加油卡卡号 必须 chargeType 加油卡类型
     * 必须 1:中石化、2:中石油（默认选中1） 返回结果：充值卡号对象 username 卡主姓名
     */
    public String API_BIANMIN_OIL_CARD_INFO = API_IP
            + "/api/facilitate/recharge/queryCardInfo";

    public String API_BIANMIN_TRAFFIC_PROVINCE = API_IP
            + "/api/facilitate/recharge/trafficFines/getProvinceList";

    public String API_BIANMIN_TRAFFIC_CITY = API_IP
            + "/api/facilitate/recharge/trafficFines/getCityList";

    public String API_BIANMIN_TRAFFIC_CARTYPE = API_IP
            + "/api/facilitate/recharge/trafficFines/getVehicleType";

    public String API_BIANMIN_TRAFFIC_HISTORY = API_IP
            + "/api/facilitate/recharge/trafficFines/queryViolationInfo";

    public String API_BIANMIN_GAME_LIST = API_IP
            + "/api/facilitate/recharge/findAllGameNames";
    public String API_BIANMIN_GAME_AREA = API_IP
            + "/api/facilitate/recharge/findAllAreas";
    public String API_BIANMIN_GAME_SERVER = API_IP
            + "/api/facilitate/recharge/findAllServers";
    public String API_BIANMIN_GAME_QQ_CHARGE = API_IP
            + "/api/facilitate/recharge/addOrder/addGameOrder";
    public String API_BIANMIN_QQ_INFO = API_IP
            + "/api/facilitate/recharge/findCoinInfo";

    public String API_BIANMIN_CARD_INFO = API_IP
            + "/api/facilitate/recharge/querycardinfo";

    /**
     * 判断是否已经签到
     */
    public String API_USER_SIGN_STATE = API_IP
            + "/member/memberManage/Sign/findSign";

    /**
     * 签到送积分
     */
    public String API_USER_SIGN = API_IP + "/member/memberManage/Sign/addSign";

    /**
     * 获取会员等级
     */
    public String API_MEMBER_GRADE = API_IP
            + "/api/member/memberManage/member/getGrade";

    /**
     * 获取所有收货地址
     */
    public String API_USER_ADDRESS_LIST = API_IP
            + "/member/addressManage/MemberAddress/findAddressAll";
    /**
     * 添加收货地址
     */
    public String API_USER_ADDRESS_ADD = API_IP
            + "/member/addressManage/MemberAddress/addAddress";
    /**
     * 删除收货地址
     */
    public String API_USER_ADDRESS_DELETE = API_IP
            + "/member/addressManage/MemberAddress/deleteAddress";
    /**
     * 设为默认地址
     */
    public String API_USER_ADDRESS_SET_DEAFULT = API_IP
            + "/member/addressManage/MemberAddress/setDefaultAddress";
    /**
     * 收货地址详情
     */
    public String API_USER_ADDRESS_DETAIL = API_IP
            + "/member/addressManage/MemberAddress/getUpdateAddress";
    /**
     * 修改收货地址
     */
    public String API_USER_ADDRESS_MODIFY = API_IP
            + "/member/addressManage/MemberAddress/updateAddress";

    /**
     * 说明：根据区域id查询下级区域 授权访问：否 参数：aid：区域id（如果不传，则查询所有省份）
     * 返回结果：regionalValue（区域对象）list集合
     */
    public String API_STORE_AREA = API_IP
            + "/api/agency/mechanismManage/regional/findArea";
    /**
     * 说明：查询区域下面所有加盟商 授权访问：否 参数：areaId，区域ID 返回结果：serviceProviders(机构对象)的list集合
     */
    public String API_STORE_LIST = API_IP
            + "/api/agency/mechanismManage/mechanism/findStoreByArea";

    /**
     * 广告
     */
    public String API_ADS = API_IP
            + "/api/product/productManage/advertisement/findAdver";

    /**
     * 查询所有上级区域
     */
    public String API_AREA = API_IP
            + "/api/agency/mechanismManage/regional/findAllOnRegional";

    /*
     * 消费者手机端查询所有促销分类
     */
    public String API_SALE_CLASS = API_IP
            + "/api/product/promotionManage/promotionTheme/findPromotionclass";
    /**
     * 获取全部的品牌信息
     * <p>
     * 参数：无
     * <p>
     * 返回结果：{brandLogo:LoGo图片,brandName:品牌名称,brandId:品牌ID}
     */
    public String API_HOME_BRAND = API_IP
            + "/api/MobileArea/MobileManager/brand/MobileAreaBrandInit";
    /**
     * 获取全部的推荐分类信息
     * <p>
     * 参数：无
     * <p>
     * 返回结果：{classLogo:LoGo图片,className:分类名称,ClassId:品牌ID}
     */
    public String API_HOME_CLASS = API_IP
            + "/api/MobileArea/MobileManager/class/MobileAreaClassInit";

    /**
     * 查询商品列表
     */
    public String API_PRODUCT_LIST = API_IP
            + "/api/product/productManage/product/findByItemOpenProduct";
    /**
     * 查询商品列表
     */
    public String API_PRODUCT_DETAIL = API_IP
            + "/api/product/productManage/product/findByOpenProductId";
    /**
     * 添加商品浏览记录
     */
    public String API_PRODUCT_BROWSE_HISTORY = API_IP
            + "/api/product/productManage/browse/addBrowse";
    /**
     * 查询所有大分类
     */
    public String API_PRODUCT_CLASS_MAIN = API_IP
            + "/api/product/productManage/classValue/findAllClass";
    /**
     * 根据上级分类ID查询对应的分类
     */
    public String API_PRODUCT_CLASS_MID = API_IP
            + "/api/product/productManage/classValue/findBySuperiorClassId";

    /**
     * 根据最新分类id查询分类属性
     */
    public String API_PRODUCT_CLASS_ATTR = API_IP
            + "/api/product/productManage/classValue/findByMinClassId";

    /**
     * 商品热搜
     */
    public String API_PRODUCT_SEARCH_HOT = API_IP
            + "/api/product/productManage/topsearch/findTopSearchQueries";

    /**
     * 查询商品列表价格
     */
    public String API_PRICE_LIST = API_IP
            + "/api/product/productManage/product/findByOpenPorudctLXPrice";
    /**
     * 查询商品属性价格
     */
    public String API_PRICE_BY_ATTR = API_IP
            + "/api/product/productManage/product/findOpenSelectedAttPro";

    /**
     * 查询商品属性价格
     */
    public String API_PRICE_CAR = API_IP
            + "/api/product/productManage/product/findSelectedAttPriceInTF";

    /**
     * 登录
     */
    public String API_USER_LOGIN = API_IP
            + "/api/member/memberManage/member/loginMember";
    /**
     * 注册
     *
     * @Parameters String phone 电话号码 String password 密码 String smsCode 验证码
     * String equipMachine 机器码 String spNo 机构编号 refereeCode
     * 推荐我的人的推荐码
     */
    public String API_USER_REGISTER = API_IP
            + "/api/member/memberManage/member/findOrAddMember";
    /**
     * 注册时获取短信
     */
    public String API_USER_REGISTER_SMSCODE = API_IP
            + "/api/member/memberManage/member/getSmsCode";
    /**
     * 找回密码
     */
    public String API_USER_FIND_PASSWORD = API_IP
            + "/api/member/memberManage/member/getPassword";

    /**
     * 找回密码时获取短信验证码
     */
    public String API_USER_FIND_PASSWORD_SMSCODE = API_IP
            + "/api/member/memberManage/member/getSmsCodes";

    /**
     * 获取用户信息
     */
    public String API_USER_INFO_GET = API_IP
            + "/api/member/memberManage/member/findMemberByPhone";
    /**
     * 修改用户资料
     */
    public String API_USER_INFO_UPDATE = API_IP
            + "/member/memberManage/member/updateMember";

    /**
     * 修改密码参数：useraccount 账号，oldpassword旧密码，newpassword新密码
     */
    public String API_USER_MODIFY_SECRET = API_IP
            + "/member/memberManage/member/updateMemberPassword";
    /**
     * 修改手机号 参数：account账号，newphone新手机号
     */
    public String API_USER_MODIFY_PHONE = API_IP
            + "/member/memberManage/member/updateMemberPhone";
    /**
     * 修改手机号获取验证码
     */
    public String API_USER_MODIFY_PHONE_SMSCODE = API_IP
            + "/member/memberManage/member/VerificationCode";
    /**
     * 修改身份证号 参数：account账号，idcard身份证号
     */
    public String API_USER_MODIFY_IDCARD = API_IP
            + "/member/memberManage/member/upadteMemberIdcard";

    /**
     * 查询用户积分余额
     */
    public String API_USER_JIFEN = API_IP
            + "/member/bonusManage/MemberBonus/findByAccount";

    // 查询积分详情
    public String API_USER_JIFEN_DETAIL = API_IP
            + "/member/bonusDetailManage/bonusDetail/MemberBonusDetail/findByItem";
    /**
     * 上传用户位置
     */
    public String API_USER_UPDATE_LOCATION = API_IP
            + "/member/locationManage/MemberLocation/addMemberLocation";
    /**
     * 获取我推荐的会员列表
     */
    public String API_USER_RECOMMEND_LIST = API_IP
            + "/member/memberManage/member/findRecPersons";
    /**
     * 统计所推荐会员的数据
     */
    public String API_USER_RECOMMEND_STATISTICS = API_IP
            + "/member/memberManage/member/recommendData";

    /**
     * 下单
     */
    public String API_ORDER_ADD = API_IP
            + "/api/order/unified/orderManage/addYiShopOrOCOrder";

    /**
     * 下单至运营中心
     */
    public String API_ORDER_ADD_CENTER = API_IP
            + "/api/order/franchiseeOrder/orderManage/addOrder";

    /**
     * 下单至易店
     */
    public String API_ORDER_ADD_YISTORE = API_IP
            + "/api/order/yiShopOrder/orderYS/addOrderYS";

    /**
     * 查询订单列表
     */
    public String API_ORDER_LIST = API_IP
            + "/api/order/franchiseeOrder/orderManage/findByOrderUserNumber";
    /**
     * 查询订单详情
     */
    public String API_ORDER_DETAIL = API_IP
            + "/api/order/franchiseeOrder/orderManage/findByOrderNumber";
    /**
     * 查询订单详情
     */
    public String API_ORDER_WULIU = API_IP
            + "/api/mobilePhone/deliveryRecord/logisticsManage/findDeliveryRecords";

    /**
     * 查询店铺街的店铺列表及信息 参数：加盟店编号（storeId）
     */
    public String API_LOCAL_STORE_LIST = API_IP
            + "/api/agency/mechanismManage/mechanism/findShopByStore";

    /**
     * 本地产品列表
     */
    public String API_LOCAL_BUSINESS_GOODS = API_IP
            + "/api/localEBusiness/retail/productManager/queryRPManager";

    /**
     * 本地产品详情
     * <p>
     * String retailProductManagerID
     */
    public String API_LOCAL_BUSINESS_GOODS_DETAIL = API_IP
            + "/api/localEBusiness/retail/productManager/viewRPManager";

    /**
     * 本地产品大分类
     * <p>
     * serviceProviderID
     */
    public String API_LOCAL_BUSINESS_GOODS_CLASS_PRIMARY = API_IP
            + "/api/localEBusiness/retail/productClassType/queryRPBigClassType";
    /**
     * 本地产品小分类
     * <p>
     * productTypeValueID
     */
    public String API_LOCAL_BUSINESS_GOODS_CLASS_SECOND = API_IP
            + "/api/localEBusiness/retail/productClassType/queryRPNextClassType";
    /**
     * 本地产品订单添加
     *
     * @test http://192.168.8.80:8081/api/localEBusiness/retail/orderManager/
     * addRetailOrderMan
     * ?customerName=累夏鸥&customerPhone=13032889558&deliveryAddress
     * =四川省成都市金牛区解放路二段六号
     * &retailOrderPrice=300&paymentType=1&serviceProvidID
     * =1068&memberAccount
     * =13032889558&data=[{"retailProductManagerID":"1",
     * "shopNum":"3","productPrice" :"300"}]
     */
    public String API_LOCAL_BUSINESS_GOODS_ORDER_ADD = API_IP
            + "/api/localEBusiness/retail/orderManager/addRetailOrderMan";
    /**
     * 本地产品订单查询
     */
    public String API_LOCAL_BUSINESS_GOODS_ORDER_LIST = API_IP
            + "/api/localEBusiness/retail/orderManager/queryRetailOrderMan";
    /**
     * 本地服务分类
     */
    public String API_LOCAL_BUSINESS_SERVICE_CLASS = API_IP
            + "/api/localService/productManage/localClassValue/findAllMyLocalClassValue";

    /**
     * 本地服务列表
     */
    public String API_LOCAL_BUSINESS_SERVICE = API_IP
            + "/api/localService/productManage/localProduct/findByItemOpenLocalProduct";
    /**
     * 农副产品
     */
    public String API_LOCAL_BUSINESS_NONGFU = API_IP
            + "/api/localService/productManage/reverseRelease/findByItemOpenRRlocalProduct";

    /**
     * 查询爱新鲜产品
     */
    public String API_LOCAL_BUSINESS_SERVICE_FRESH = API_IP
            + "/api/localService/productManage/localProduct/findByAixinxianProduct";

    /**
     * 本地服务详情
     */
    public String API_LOCAL_BUSINESS_SERVICE_DETAIL = API_IP
            + "/api/localService/productManage/localProduct/findByIdOpenLocalProduct";
    /**
     * 本地促销-特价
     * <p>
     * 传递参数: jqm 机器码
     */
    public String API_LOCAL_SALE_TEJIA = API_IP
            + "/api/product/promotionManage/LocalPromotion/findShopPromotionJQM";
    /**
     * 本地促销-特价详情
     * <p>
     * 传递参数 : id 促销ID
     */
    public String API_LOCAL_SALE_TEJIA_DETAIL = API_IP
            + "/api/product/promotionManage/LocalPromotion/findShopPromotionId";
    /**
     * 本地促销-秒杀
     * <p>
     * 传递参数: jqm 机器码
     */
    public String API_LOCAL_SALE_MIAOSHA = API_IP
            + "/api/product/promotionManage/LocalPromotion/findSpikePromotionJQM";
    /**
     * 本地促销-秒杀详情
     * <p>
     * 传递参数 : id 促销ID
     */
    public String API_LOCAL_SALE_MIAOSHA_DETAIL = API_IP
            + "/api/product/promotionManage/promotion/findspikePromotionDetais";
    /**
     * 本地服务订单添加
     *
     * @LocalTest http://192.168.8.80:8081/api/localService/orderManage/localOrder
     * / addLocalOrderPos
     * ?customerName=累夏鸥&customerPhone=13032889558&deliveryAddress
     * =四川省成都市金牛区解放路二段六号
     * &orderPrice=300&providerId=1068&memberNumber=13032889558
     * &deliveryType=送货上门&orderType=服务&productId=1&productNum=1
     */
    public String API_LOCAL_BUSINESS_SERVICE_ORDER_ADD = API_IP
            + "/api/localService/orderManage/localOrder/addLocalOrderPos";
    /**
     * 本地服务订单列表
     *
     * @LocalTest http://192.168.8.80:8081/api/localService/orderManage/localOrder
     * /findByItemMemberLocalOrder?memberNumber=13032889558&pageNo=1&
     * pageSize=2
     */
    public String API_LOCAL_BUSINESS_SERVICE_ORDER_LIST = API_IP
            + "/api/localService/orderManage/localOrder/findByItemMemberLocalOrder";
    /**
     * 本地服务订单详情
     */
    public String API_LOCAL_BUSINESS_SERVICE_ORDER_DETAIL = API_IP
            + "/api/localService/orderManage/localOrder/findByLocalOrderId";
    /**
     * 查询限时促销商品详情
     */
    public String API_SALE_TIME_DETAIL = API_IP
            + "/api/product/promotionManage/promotion/findPromotionDetails";
    /**
     * 查询限时促销商品列表
     */
    public String API_SALE_TIME_LIST = API_IP
            + "/api/product/promotionManage/promotion/findAllpromotion";
    /**
     * 查询限时促销商品列表
     */
    public String API_SALE_PRICE = API_IP
            + "/api/product/promotionManage/promotion/findPromotionPrice";

    /**
     * 查询秒杀商品
     * <p>
     * strno
     */
    public String API_SALE_MIAOSHA = API_IP
            + "/api/product/promotionManage/seckillActivity/findSeckillActivity";

    /**
     * 查询秒杀商品详情
     */
    public String API_SALE_MIAOSHA_DETAIL = API_IP
            + "/api/product/promotionManage/promotion/findSeckillPromotionDetais";

    /**
     * 查询特价商品
     * <p>
     * strno
     */
    public String API_SALE_TEJIA = API_IP
            + "/api/product/promotionManage/promotionTheme/findSalePromotion";
    /**
     * 查询特价促销商品详情
     */
    public String API_SALE_TEJIA_DETAIL = API_IP
            + "/api/product/promotionManage/promotion/findSalePromotionDetails";
    /**
     * 运费计算
     */
    String API_PRODUCT_FREIGHT = API_IP + "/api/order/orderManage/freightTemplate/calculationOfFreight";

    /**
     * 查询主题活动
     * <p>
     * strno
     */
    public String API_SALE_ACTIVITY = API_IP
            + "/api/product/promotionManage/Theme/findAllThmem";

    /**
     * 查询积分商城列表
     */
    public String API_SCORE_PRODUCT_LIST = API_IP
            + "/api/product/integralMall/IntegralMallAction/findIntegralMallJQM";
    /**
     * 查询积分商品详情
     */
    public String API_SCORE_PRODUCT_DETAIL = API_IP
            + "/api/product/integralMall/IntegralMallAction/findIntegrallMallIds";

    /**
     * 支付宝支付结果异步返回给服务器地址
     */
    public String API_ALIPAY_NOTIFY = API_IP
            + "/api/settlement/pay/addPayMentData";

    /**
     * 付款后修改订单状态
     */
    public String API_ORDER_STATE_UPDATE = API_IP
            + "/api/order/franchiseeOrder/orderManage/fAmessages";

    /**
     * 说明：更新设备状态 授权访问：否 参数;machine，机器码 必传,status:状态（ONLINE或者OFFLINE）
     * 必传，version，版本号 返回结果：无 注意：无
     */
    String API_MACHINE_AREA = API_IP
            + "/api/agency/mechanismManage/regional/areas";

    /**
     * 说明：绑定加盟商设备 授权访问：否 参数：activetion（激活码），machine（机器码） 返回结果：无 注意：无
     */
    String API_MACHINE_BIND = API_IP
            + "/api/agency/mechanismManage/equipment/updateMachine";

    /**
     * 说明：根据机器码查询设备状态 授权访问：否 参数：machine，机器码 返回结果：无
     * 注意：判断状态根据抛出的异常来判断，异常有noexit(表示没有查到该设备)，stop（表示设备停用）
     */
    String API_MACHINE_STATE = API_IP
            + "/api/agency/mechanismManage/equipment/findByMachine";

    /**
     * 说明：更新设备状态 授权访问：否 参数;machine，机器码 必传,status:状态（ONLINE或者OFFLINE）
     * 必传，version，版本号 返回结果：无 注意：无
     */
    String API_MACHINE_UPDATE_STATE = API_IP
            + "/api/agency/mechanismManage/equipment/updateStatus";

    /**
     * 传机构编号：no
     */
    String API_STORE_SEND_FEE = API_IP
            + "/api/agency/mechanismManage/ConfigurationFile/findSpPostage";

    public String API_SUNING_SIGNATURE = API_IP
            + "/api/order/cloudMallOrder/CloudMallAction/queryAccessToken";

    public String API_SUNING_ORDER_ADD = API_IP
            + "/api/order/cloudMallOrder/CloudMallAction/CreatSuNingOrder";

    public String API_SUNING_ORDER_PAY = API_IP
            + "/api/order/cloudMallOrder/CloudMallAction/updateCloudmallOrderYFK";

    public String API_SUNING_ORDER_LIST = API_IP
            + "/api/order/cloudMallOrder/CloudMallAction/findMenberOrder";


}
