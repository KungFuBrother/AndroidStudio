package yitgogo.consumer.tools;

public interface API {

    /**
     * 检查更新
     */

    String API_LBS_NEARBY = "http://api.map.baidu.com/geosearch/v3/nearby";

    String URL_DOWNLOAD = "http://updatephone.yitos.net/";

    String API_UPDATE = "http://updatePhone.yitos.net/android/version_consumer.js";

    String IP_PUBLIC = "http://yitos.net";
    String IP_SUNING = "http://gcapi.suning.com";
    String IP_MONEY = "http://pay.yitos.net";

//    String IP_PUBLIC = "http://42.96.249.111";
//    String IP_SUNING = "http://gcapi.suning.com";
//    String IP_MONEY = "http://115.28.252.7";

    /**
     * 说明：根据手机号码和面值（为慢充时还须到账时间）查询充值信息 参数： phoneno 手机号码 必须 pervalue 面值 必须
     * 快充可选面值（1、2、5、10、20、30、50、100、300、500） 慢充可选面值（30、50、100）
     * <p/>
     * mctype 慢充到账时间 慢充才传 0.5（半小时到账）、4（4小时到账）、12（12小时到账）、
     * 24（24小时到账）、48（48小时到账）、72（72小时到账） 默认为24
     */
    String API_BIANMIN_PHONE_CHARGE_INFO = IP_PUBLIC
            + "/api/facilitate/recharge/findPhoneInfo";
    /**
     * 说明：生成手机充值订单 参数： phoneno 手机号码 必须 pervalue 面值 必须 mctype 慢充到账时间 慢充才传
     */
    String API_BIANMIN_PHONE_CHARGE = IP_PUBLIC
            + "/api/facilitate/recharge/addOrder/addPhoneOrder";

    /**
     * 说明：根据固话号码，面值，运营商，充值类型查询充值信息 参数： phoneno 固话/宽带号码 必须 格式：021-88888888
     * <p/>
     * pervalue 面值 必须 电信：10、20、30、50、100、300 联通：50、100
     * <p/>
     * teltype 运营商 必须 1、电信 2、联通（默认选中1）
     * <p/>
     * chargeType 充值类型 必须 1：固话；2：宽带（默认选中1）
     */
    String API_BIANMIN_TELEPHONE_CHARGE_INFO = IP_PUBLIC
            + "/api/facilitate/recharge/fixtelquery";
    /**
     * 说明：生成固话/宽带充值订单 参数： phoneno 固话/宽带号码 必须 pervalue 面值 必须 teltype 运营商编号 必须
     * chargeType 充值类型编号 必须 memberAccount 会员账号 登录才传 返回结果：map orderNumber 订单编号
     */
    String API_BIANMIN_TELEPHONE_CHARGE = IP_PUBLIC
            + "/api/facilitate/recharge/addOrder/addFixtelOrder";

    /**
     * 说明：根据手机号码和面值（为慢充时还须到账时间）查询充值信息 参数： game_userid 加油卡卡号 必须 chargeType 加油卡类型
     * 必须 1:中石化、2:中石油（默认选中1） 返回结果：充值卡号对象 username 卡主姓名
     */
    String API_BIANMIN_OIL_CARD_INFO = IP_PUBLIC
            + "/api/facilitate/recharge/queryCardInfo";

    String API_BIANMIN_TRAFFIC_PROVINCE = IP_PUBLIC
            + "/api/facilitate/recharge/trafficFines/getProvinceList";

    String API_BIANMIN_TRAFFIC_CITY = IP_PUBLIC
            + "/api/facilitate/recharge/trafficFines/getCityList";

    String API_BIANMIN_TRAFFIC_CARTYPE = IP_PUBLIC
            + "/api/facilitate/recharge/trafficFines/getVehicleType";

    String API_BIANMIN_TRAFFIC_HISTORY = IP_PUBLIC
            + "/api/facilitate/recharge/trafficFines/queryViolationInfo";

    String API_BIANMIN_GAME_LIST = IP_PUBLIC
            + "/api/facilitate/recharge/findAllGameNames";
    String API_BIANMIN_GAME_AREA = IP_PUBLIC
            + "/api/facilitate/recharge/findAllAreas";
    String API_BIANMIN_GAME_SERVER = IP_PUBLIC
            + "/api/facilitate/recharge/findAllServers";
    String API_BIANMIN_GAME_QQ_CHARGE = IP_PUBLIC
            + "/api/facilitate/recharge/addOrder/addGameOrder";
    String API_BIANMIN_QQ_INFO = IP_PUBLIC
            + "/api/facilitate/recharge/findCoinInfo";

    String API_BIANMIN_CARD_INFO = IP_PUBLIC
            + "/api/facilitate/recharge/querycardinfo";

    String API_BIANMIN_ORDER = IP_PUBLIC
            + "/api/facilitate/recharge/rechargeOrder/findOrderByMember";
    /**
     * 运费计算
     */
    String API_PRODUCT_FREIGHT = IP_PUBLIC + "/api/order/orderManage/freightTemplate/calculationOfFreight";
    /**
     * orderNumber
     */
    String API_BIANMIN_ORDER_DELETE = IP_PUBLIC
            + "/api/facilitate/recharge/rechargeOrder/deleteOrder";

    /**
     * 判断是否已经签到
     */
    String API_USER_SIGN_STATE = IP_PUBLIC
            + "/member/memberManage/Sign/findSign";

    /**
     * 签到送积分
     */
    String API_USER_SIGN = IP_PUBLIC + "/member/memberManage/Sign/addSign";

    /**
     * 获取会员等级
     */
    String API_MEMBER_GRADE = IP_PUBLIC
            + "/api/member/memberManage/member/getGrade";

    /**
     * 获取所有收货地址
     */
    String API_USER_ADDRESS_LIST = IP_PUBLIC
            + "/member/addressManage/MemberAddress/findAddressAll";
    /**
     * 添加收货地址
     */
    String API_USER_ADDRESS_ADD = IP_PUBLIC
            + "/member/addressManage/MemberAddress/addAddress";
    /**
     * 删除收货地址
     */
    String API_USER_ADDRESS_DELETE = IP_PUBLIC
            + "/member/addressManage/MemberAddress/deleteAddress";
    /**
     * 设为默认地址
     */
    String API_USER_ADDRESS_SET_DEAFULT = IP_PUBLIC
            + "/member/addressManage/MemberAddress/setDefaultAddress";
    /**
     * 收货地址详情
     */
    String API_USER_ADDRESS_DETAIL = IP_PUBLIC
            + "/member/addressManage/MemberAddress/getUpdateAddress";
    /**
     * 修改收货地址
     */
    String API_USER_ADDRESS_MODIFY = IP_PUBLIC
            + "/member/addressManage/MemberAddress/updateAddress";

    /**
     * 说明：根据区域id查询下级区域 授权访问：否 参数：aid：区域id（如果不传，则查询所有省份）
     * 返回结果：regionalValue（区域对象）list集合
     */
    String API_STORE_AREA = IP_PUBLIC
            + "/api/agency/mechanismManage/regional/findArea";
    /**
     * 说明：查询区域下面所有加盟商 授权访问：否 参数：areaId，区域ID 返回结果：serviceProviders(机构对象)的list集合
     */
    String API_STORE_LIST = IP_PUBLIC
            + "/api/agency/mechanismManage/mechanism/findStoreByArea";

    String API_STORE_SELECTED_AREA = IP_PUBLIC
            + "/api/agency/mechanismManage/mechanism/findStoreAllOnRegional";

    /**
     * 广告
     */
    String API_ADS = IP_PUBLIC
            + "/api/product/productManage/adver/findAdverPhoneX";

    /*
     * 消费者手机端查询所有促销分类
     */
    String API_SALE_CLASS = IP_PUBLIC
            + "/api/product/promotionManage/promotionTheme/findPromotionclass";
    /**
     * 获取全部的品牌信息
     * <p/>
     * 参数：无
     * <p/>
     * 返回结果：{brandLogo:LoGo图片,brandName:品牌名称,brandId:品牌ID}
     */
    String API_HOME_BRAND = IP_PUBLIC
            + "/api/MobileArea/MobileManager/brand/MobileAreaBrandInit";
    /**
     * 获取全部的推荐分类信息
     * <p/>
     * 参数：无
     * <p/>
     * 返回结果：{classLogo:LoGo图片,className:分类名称,ClassId:品牌ID}
     */
    String API_HOME_CLASS = IP_PUBLIC
            + "/api/MobileArea/MobileManager/class/MobileAreaClassInit";

    /**
     * 查询商品列表
     */
    String API_PRODUCT_LIST = IP_PUBLIC
            + "/api/product/productManage/product/findByItemOpenProduct";
    /**
     * 查询商品列表
     */
    String API_PRODUCT_DETAIL = IP_PUBLIC
            + "/api/product/productManage/product/findByOpenProductId";
    /**
     * 查询所有大分类
     */
    String API_PRODUCT_CLASS_MAIN = IP_PUBLIC
            + "/api/product/productManage/classValue/findAllClass";
    /**
     * 根据上级分类ID查询对应的分类
     */
    String API_PRODUCT_CLASS_MID = IP_PUBLIC
            + "/api/product/productManage/classValue/findBySuperiorClassId";

    /**
     * 根据最新分类id查询分类属性
     */
    String API_PRODUCT_CLASS_ATTR = IP_PUBLIC
            + "/api/product/productManage/classValue/findByMinClassId";

    /**
     * 商品热搜
     */
    String API_PRODUCT_SEARCH_HOT = IP_PUBLIC
            + "/api/product/productManage/topsearch/findTopSearchQueries";

    /**
     * 查询商品列表价格
     */
    String API_PRICE_LIST = IP_PUBLIC
            + "/api/product/productManage/product/findByOpenPorudctLXPrice";
    /**
     * 查询商品属性价格
     */
    String API_PRICE_BY_ATTR = IP_PUBLIC
            + "/api/product/productManage/product/findOpenSelectedAttPro";

    /**
     * 查询商品属性价格
     */
    String API_PRICE_CAR = IP_PUBLIC
            + "/api/product/productManage/product/findSelectedAttPriceInTF";

    /**
     * 登录
     */
    String API_USER_LOGIN = IP_PUBLIC
            + "/api/member/memberManage/member/loginMember";
    /**
     * 注册
     *
     * @Parameters String phone 电话号码 String password 密码 String smsCode 验证码
     * String equipMachine 机器码 String spNo 机构编号 refereeCode
     * 推荐我的人的推荐码
     */
    String API_USER_REGISTER = IP_PUBLIC
            + "/api/member/memberManage/member/addMember";
    /**
     * 注册时获取短信
     */
    String API_USER_REGISTER_SMSCODE = IP_PUBLIC
            + "/api/member/memberManage/member/getSmsCode";
    /**
     * 找回密码
     */
    String API_USER_FIND_PASSWORD = IP_PUBLIC
            + "/api/member/memberManage/member/getPassword";

    /**
     * 找回密码时获取短信验证码
     */
    String API_USER_FIND_PASSWORD_SMSCODE = IP_PUBLIC
            + "/api/member/memberManage/member/getSmsCodes";

    /**
     * 获取用户信息
     */
    String API_USER_INFO_GET = IP_PUBLIC
            + "/member/memberManage/member/getMember";
    /**
     * 修改用户资料
     */
    String API_USER_INFO_UPDATE = IP_PUBLIC
            + "/member/memberManage/member/updateMember";

    /**
     * 修改密码参数：useraccount 账号，oldpassword旧密码，newpassword新密码
     */
    String API_USER_MODIFY_SECRET = IP_PUBLIC
            + "/member/memberManage/member/updateMemberPassword";
    /**
     * 修改手机号 参数：account账号，newphone新手机号
     */
    String API_USER_MODIFY_PHONE = IP_PUBLIC
            + "/member/memberManage/member/updateMemberPhone";
    /**
     * 修改手机号获取验证码
     */
    String API_USER_MODIFY_PHONE_SMSCODE = IP_PUBLIC
            + "/member/memberManage/member/VerificationCode";
    /**
     * 修改身份证号 参数：account账号，idcard身份证号
     */
    String API_USER_MODIFY_IDCARD = IP_PUBLIC
            + "/member/memberManage/member/upadteMemberIdcard";

    /**
     * 查询用户积分余额
     */
    String API_USER_JIFEN = IP_PUBLIC
            + "/member/bonusManage/MemberBonus/findByAccount";

    // 查询积分详情
    String API_USER_JIFEN_DETAIL = IP_PUBLIC
            + "/member/bonusDetailManage/bonusDetail/MemberBonusDetail/findByItem";
    /**
     * 上传用户位置
     */
    String API_USER_UPDATE_LOCATION = IP_PUBLIC
            + "/member/locationManage/MemberLocation/addMemberLocation";
    /**
     * 获取我推荐的会员列表
     */
    String API_USER_RECOMMEND_LIST = IP_PUBLIC
            + "/member/memberManage/member/findRecPersons";
    /**
     * 统计所推荐会员的数据
     */
    String API_USER_RECOMMEND_STATISTICS = IP_PUBLIC
            + "/member/memberManage/member/recommendData";
    /**
     * 下单
     */
    String API_ORDER_ADD_CENTER = IP_PUBLIC
            + "/api/order/unified/orderManage/addYiShopOrOCOrder";

    /**
     * 查询订单列表
     */
    String API_ORDER_LIST = IP_PUBLIC
            + "/api/order/unified/orderManage/findByYiShopOrOCOrderUserNumber";
    /**
     * 查询订单详情
     * orderId
     */
    String API_ORDER_DETAIL = IP_PUBLIC
            + "/api/order/unified/orderManage/findOCOrderId";
    /**
     * 查询订单详情
     */
    String API_ORDER_WULIU = IP_PUBLIC
            + "/api/mobilePhone/deliveryRecord/logisticsManage/findDeliveryRecords";

    /**
     * 退货申请
     */
    String API_ORDER_RETURN = IP_PUBLIC + "/member/order/returnManage/applyReturn";

    /**
     * 退货申请结果
     */
    String API_ORDER_RETURN_RESULT = IP_PUBLIC + "/member/order/returnManage/getResult";

    /**
     * 查询店铺街的店铺列表及信息 参数：加盟店编号（storeId）
     */
    String API_LOCAL_STORE_LIST = IP_PUBLIC
            + "/api/agency/mechanismManage/mechanism/findShopByStore";

    /**
     * 易商圈状态
     */
    String API_LOCAL_BUSINESS_STATE = IP_PUBLIC
            + "/api/localEBusiness/retail/productManager/findLocalServiceAndLocalProduct";

    /**
     * 本地产品列表
     */
    String API_LOCAL_BUSINESS_GOODS = IP_PUBLIC
            + "/api/localEBusiness/retail/productManager/queryRPManager";

    /**
     * 本地产品详情
     * <p/>
     * String retailProductManagerID
     */
    String API_LOCAL_BUSINESS_GOODS_DETAIL = IP_PUBLIC
            + "/api/localEBusiness/retail/productManager/viewRPManager";

    /**
     * 本地产品大分类
     * <p/>
     * serviceProviderID
     */
    String API_LOCAL_BUSINESS_GOODS_CLASS_PRIMARY = IP_PUBLIC
            + "/api/localEBusiness/retail/productClassType/queryRPBigClassType";
    /**
     * 本地产品小分类
     * <p/>
     * productTypeValueID
     */
    String API_LOCAL_BUSINESS_GOODS_CLASS_SECOND = IP_PUBLIC
            + "/api/localEBusiness/retail/productClassType/queryRPNextClassType";
    /**
     * 本地产品品牌
     */
    String API_LOCAL_BUSINESS_GOODS_BRAND = IP_PUBLIC
            + "/api/localEBusiness/retail/productClassBrand/queryRpBrand";
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
    String API_LOCAL_BUSINESS_GOODS_ORDER_ADD = IP_PUBLIC
            + "/api/localEBusiness/retail/orderManager/addRetailOrderMan";
    /**
     * 本地产品订单查询
     */
    String API_LOCAL_BUSINESS_GOODS_ORDER_LIST = IP_PUBLIC
            + "/api/localEBusiness/retail/orderManager/queryRetailOrderMan";

    /**
     * 本地服务订单-可修改状态
     */
    String API_LOCAL_BUSINESS_SERVICE_ORDER_STATE = IP_PUBLIC
            + "/api/localService/orderManage/localOrder/findAllStatus";

    /**
     * 本地服务订单-修改状态
     */
    String API_LOCAL_BUSINESS_SERVICE_ORDER_STATE_UPDATE = IP_PUBLIC
            + "/api/localService/orderManage/localOrder/updateLocalOrder";
    /**
     * 本地产品订单-可修改状态
     */
    String API_LOCAL_BUSINESS_GOODS_ORDER_STATE = IP_PUBLIC
            + "/api/localEBusiness/retail/orderManager/findAllStatus";

    /**
     * 本地产品订单-修改状态
     */
    String API_LOCAL_BUSINESS_GOODS_ORDER_STATE_UPDATE = IP_PUBLIC
            + "/api/localEBusiness/retail/orderManager/updateRetailOMState";
    /**
     * 本地服务分类
     */
    String API_LOCAL_BUSINESS_SERVICE_CLASS = IP_PUBLIC
            + "/api/localService/productManage/localClassValue/findAllMyLocalClassValue";

    /**
     * 本地服务列表
     */
    String API_LOCAL_BUSINESS_SERVICE = IP_PUBLIC
            + "/api/localService/productManage/localProduct/findByItemOpenLocalProduct";

    /**
     * 农副产品
     */
    String API_LOCAL_BUSINESS_SERVICE_NONGFU = IP_PUBLIC
            + "/api/localService/productManage/reverseRelease/findByItemOpenRRlocalProduct";

    /**
     * 查询爱新鲜产品
     */
    String API_LOCAL_BUSINESS_SERVICE_FRESH = IP_PUBLIC
            + "/api/localService/productManage/localProduct/findByAixinxianProduct";

    /**
     * 本地服务详情
     */
    String API_LOCAL_BUSINESS_SERVICE_DETAIL = IP_PUBLIC
            + "/api/localService/productManage/localProduct/findByIdOpenLocalProduct";
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
    String API_LOCAL_BUSINESS_SERVICE_ORDER_ADD = IP_PUBLIC
            + "/api/localService/orderManage/localOrder/addLocalOrderPos";
    /**
     * 本地服务订单列表
     *
     * @LocalTest http://192.168.8.80:8081/api/localService/orderManage/localOrder
     * /findByItemMemberLocalOrder?memberNumber=13032889558&pageNo=1&
     * pageSize=2
     */
    String API_LOCAL_BUSINESS_SERVICE_ORDER_LIST = IP_PUBLIC
            + "/api/localService/orderManage/localOrder/findByItemMemberLocalOrder";
    /**
     * 本地服务订单详情
     */
    String API_LOCAL_BUSINESS_SERVICE_ORDER_DETAIL = IP_PUBLIC
            + "/api/localService/orderManage/localOrder/findByLocalOrderId";
    /**
     * 本地促销-特价
     * <p/>
     * 传递参数: jgbh 机构编号
     */
    String API_LOCAL_SALE_TEJIA = IP_PUBLIC
            + "/api/product/promotionManage/LocalPromotion/findShopPromotionJGBH";
    /**
     * 本地促销-特价详情
     * <p/>
     * 传递参数 : id 促销ID
     */
    String API_LOCAL_SALE_TEJIA_DETAIL = IP_PUBLIC
            + "/api/product/promotionManage/LocalPromotion/findShopPromotionId";
    /**
     * 本地促销-秒杀
     * <p/>
     * 传递参数: jgbh 机构编号
     */
    String API_LOCAL_SALE_MIAOSHA = IP_PUBLIC
            + "/api/product/promotionManage/LocalPromotion/findSpikePromotionJGBH";
    /**
     * 本地促销-秒杀详情
     * <p/>
     * 传递参数 : id 促销ID
     */
    String API_LOCAL_SALE_MIAOSHA_DETAIL = IP_PUBLIC
            + "/api/product/promotionManage/promotion/findspikePromotionDetais";

    /**
     * 本地店铺运费
     */
    String API_STORE_SEND_FEE = IP_PUBLIC
            + "/api/agency/mechanismManage/ConfigurationFile/findSpPostage";

    /**
     * 查询限时促销商品详情
     */
    String API_SALE_TIME_DETAIL = IP_PUBLIC
            + "/api/product/promotionManage/promotion/findPromotionDetails";
    /**
     * 查询限时促销商品列表
     */
    String API_SALE_TIME_LIST = IP_PUBLIC
            + "/api/product/promotionManage/promotion/findAllpromotion";

    /**
     * 查询秒杀商品
     * <p/>
     * strno
     */
    String API_SALE_MIAOSHA = IP_PUBLIC
            + "/api/product/promotionManage/seckillActivity/findSeckillActivity";

    /**
     * 查询秒杀商品详情
     */
    String API_SALE_MIAOSHA_DETAIL = IP_PUBLIC
            + "/api/product/promotionManage/promotion/findSeckillPromotionDetais";

    /**
     * 查询特价商品
     * <p/>
     * strno
     */
    String API_SALE_TEJIA = IP_PUBLIC
            + "/api/product/promotionManage/promotionTheme/findSalePromotion";
    /**
     * 查询特价促销商品详情
     */
    String API_SALE_TEJIA_DETAIL = IP_PUBLIC
            + "/api/product/promotionManage/promotion/findSalePromotionDetails";

    /**
     * 查询主题活动
     * <p/>
     * strno
     */
    String API_SALE_ACTIVITY = IP_PUBLIC
            + "/api/product/promotionManage/Theme/findAllThmem";

    /**
     * 查询促销价格
     */
    String API_SALE_PRICE = IP_PUBLIC
            + "/api/product/promotionManage/promotion/findPromotionPrice";

    /**
     * 查询积分商城列表
     */
    String API_SCORE_PRODUCT_LIST = IP_PUBLIC
            + "/api/product/integralMall/IntegralMallAction/findIntegralMallJGBH";
    /**
     * 查询积分商品详情
     */
    String API_SCORE_PRODUCT_DETAIL = IP_PUBLIC
            + "/api/product/integralMall/IntegralMallAction/findIntegrallMallIds";

    /**
     * 确认收货
     */
    String API_ORDER_RECEIVED = IP_PUBLIC
            + "/api/order/unified/orderManage/updateYiShopOrOCOOrderState";

    /**
     * 注册店铺
     */
    String API_USER_OPEN_STORE = IP_PUBLIC
            + "/api/agency/mechanismManage/mechanism/addShopStore";

    /**
     * 获取所有活动：
     * <p/>
     * 地址: /member/activityManage/memberActivity/findAllActivity
     * <p/>
     * 说明：获取所有要展示的活动 参数：无
     * <p/>
     * 返回结果：活动对象集合（list）
     */
    String API_ACTIVITY_LIST = IP_PUBLIC
            + "/api/member/activityManage/memberActivity/findAllActivity";

    /**
     * 会员参与活动
     */
    String API_ACTIVITY_JOIN = IP_PUBLIC
            + "/member/activityManage/memberActivity/getIsJoin";
    /**
     * 判断会员是否参与过该活
     * <p/>
     * memberAccount activityId
     */
    String API_ACTIVITY_JOIN_STATE = IP_PUBLIC
            + "/member/activityManage/memberActivity/isInput";

    /**
     * 会员中奖
     */
    String API_ACTIVITY_WIN = IP_PUBLIC
            + "/member/activityManage/memberActivity/memberWin";

    /**
     * 短信验证码接口
     * <p/>
     * 参数： (第一次绑卡)：pan // 卡号 expiredDate // 卡效期// 格式是：MMYY cvv2 //
     * 安全校验值,CVV2或CVC2 // 对于银联和VISA卡，对应卡片背面的CVV2数字；对于MasterCard卡，对应卡片背面的CVC2数字
     * Amount // 交易金额 // 以元为单位，小数点后最多两位 externalRefNumber// 流水号（商家自己的订单号）
     * customerId// 客户号 cardHolderName// 客户姓名 cardHolderId// 客户身份证号 phoneNO//
     * 手机号码
     * <p/>
     * 返回数据：{responseCode=00, customerId=11999, token=1131247,
     * merchantId=104110045112012}
     */
    String API_PAY_BIND = IP_PUBLIC
            + "/api/settlement/kuaiqian/getAuthCodeApi";

    /**
     * 快捷支付(首次)
     * <p/>
     * payInfoType:订单类型（1-运营中心订单 2-易店订单 3-本地产品订单 4-本地服务订单5-便民服务） orderNumber：订单号
     * cardNo// 卡号 externalRefNumber// 流水号（商家自己的订单号） storableCardNo// 短卡号
     * expiredDate// 卡效期,格式是：MMYY cvv2// 安全校验值,CVV2或CVC2 //
     * 对于银联和VISA卡，对应卡片背面的CVV2数字；对于MasterCard卡，对应卡片背面的CVC2数字 amount//
     * 交易金额,以元为单位，小数点后最多两位 customerId// 客户号 cardHolderName// 客户姓名 cardHolderId//
     * 客户身份证号 phone// 手机号码 validCode// 手机验证码 token// 手机令牌码
     * <p/>
     * {responseCode=00, responseTextMessage=交易成功,
     * externalRefNumber=20150814110720}
     */
    String API_PAY_FIRST_TIME = IP_PUBLIC
            + "/api/settlement/kuaiqian/payDataFirstApi";

    /**
     * 快捷支付(非首次)
     * <p/>
     * payInfoType:订单类型（1-运营中心订单 2-易店订单 3-本地产品订单 4-本地服务订单） orderNumber：订单号
     * <p/>
     * storableCardNo// 短卡号 amount// 交易金额,以元为单位，小数点后最多两位 externalRefNumber//
     * 流水号（商家自己的订单号） customerId// 客户号 phone// 手机号码 validCode// 手机验证码 token//
     * 手机令牌码
     * <p/>
     * {responseCode=00, responseTextMessage=交易成功,
     * externalRefNumber=20150814110720}
     */

    String API_PAY_SECOND_TIME = IP_PUBLIC
            + "/api/settlement/kuaiqian/payDataTwiceApi";
    /**
     * 钱袋子余额支付
     * <p/>
     * String memberAccount 会员账号
     * <p/>
     * String customerName 会员姓名（可选）
     * <p/>
     * String pwd 钱袋子支付密码（需加密）
     * <p/>
     * String orderNumbers 订单编号，多个订单以”,”拼接
     * <p/>
     * String orderType 订单类型 1-运营中心订单 2-易店订单 3-本地产品订单 4-本地服务订单
     * <p/>
     * String apAmount 订单金额
     */
    String API_PAY_BALANCE = IP_PUBLIC
            + "/api/settlement/member/payMemberAccount";

    /**
     * 苏宁
     * <p/>
     * 获取品类(目录)接口
     *
     * @URL http://58.240.86.161/esbadapter/GEProductMgmt_GCMS/getProductCategory?data={"accessToken":"ae26377a628b0d2710f91257b67a1db0","appKey":"ZJZF","v":"2.0"}
     * @RESULT {"result":[{"name":"美上美A4打印纸","categoryId":"6040653c-7b47-4ed5-bd96-fe75b110b909"},{"name":"派克圆珠笔","categoryId":"f632ec4a-5797-4203-a7d0-101006a74efb"},{"name":"罗技键盘","categoryId":"0501599f-ef25-49ce-b853-88f3ca99828c"},{"name":"苏宁笔记本","categoryId":"6e557100-f2db-4d9c-8a3c-8c93505c29d6"},{"name":"罗技鼠标","categoryId":"f77a9987-5645-4ed0-bbf5-38b4c3485b3a"},{"name":"光明纸箱","categoryId":"8b0615e6-76ba-4c7c-ab57-19b64115e3ec"},{"name":"英雄铅笔","categoryId":"fe0520a9-bf6c-4218-b96a-af657ce6bdc6"},{"name":"中华钢笔","categoryId":"e60f58e6-f6bb-430d-a636-21bc226440d6"},{"name":"好用墨盒","categoryId":"a4368141-3bab-4cfd-9ce1-9f542f294d1c"},{"name":"蚌埠商品1","categoryId":"9dae3d16-e322-4796-93bb-a3dea425d14b"},{"name":"蚌埠商品2","categoryId":"da203a0c-0651-4afd-975c-1c35368b7cc6"},{"name":"四级页面新寻源","categoryId":"a069eba9-446a-44be-8b47-4842c05d418f"}],"isSuccess":true,"returnMsg":"success"}
     */
    String API_SUNING_PRODUCT_CALSSES = IP_SUNING + "/esbadapter/GEProductMgmt_GCMS/getProductCategory";

    String API_SUNING_PRODUCT_LIST = IP_SUNING + "/esbadapter/GEProductMgmt_GCMS/getProductPool";

    String API_SUNING_PRODUCT_DETAIL = IP_SUNING + "/esbadapter/GEProductMgmt_GCMS/getProductDetail";

    String API_SUNING_PRODUCT_IMAGES = IP_SUNING + "/esbadapter/GEProductMgmt_GCMS/getProductImage";

    String API_SUNING_PRODUCT_STOCK = IP_SUNING + "/esbadapter/GEProductMgmt_GCMS/getProductInventory";

    String API_SUNING_PRODUCT_PRICE = IP_SUNING + "/esbadapter/GEProductMgmt_GCMS/getProductSNPrice";

    String API_SUNING_AREA_PROVINCE = IP_SUNING + "/esbadapter/GEBaseInfoMgmt_GCMS/getProvinceInfo";

    String API_SUNING_AREA_CITY = IP_SUNING + "/esbadapter/GEBaseInfoMgmt_GCMS/getCityInfo";

    String API_SUNING_AREA_DISTRICT = IP_SUNING + "/esbadapter/GEBaseInfoMgmt_GCMS/getDistrictInfo";

    String API_SUNING_AREA_TOWN = IP_SUNING + "/esbadapter/GEBaseInfoMgmt_GCMS/getTownInfo";

    String API_SUNING_ORDER_WULIU = IP_SUNING + "/esbadapter/GEOrderBaseMgmt_GCMS/getOrderLogisticsStatus";

    String API_SUNING_SIGNATURE = IP_PUBLIC
            + "/api/order/cloudMallOrder/CloudMallAction/queryAccessToken";

    String API_SUNING_ORDER_ADD = IP_PUBLIC
            + "/api/order/cloudMallOrder/CloudMallAction/CreatSuNingOrder";

    String API_SUNING_ORDER_PAY = IP_PUBLIC
            + "/api/order/cloudMallOrder/CloudMallAction/updateCloudmallOrderYFK";

    String API_SUNING_ORDER_LIST = IP_PUBLIC
            + "/api/order/cloudMallOrder/CloudMallAction/findMenberOrder";

    String API_SUNING_ORDER_RECEIVE_RETURN = IP_PUBLIC
            + "/api/order/cloudMallOrder/CloudMallAction/updateOrderComplete";

    /**
     * 用户授权登录支付系统
     * <p/>
     * 参数： sn 授权码 必须
     * <p/>
     * 成功返回 : {payaccount:会员支付系统账号，balance：账户余额，seckey：授权码}
     */
    String MONEY_LOGIN = IP_MONEY + "/api/member/login";
    /**
     * 查询现金交易明细
     * <p/>
     * 参数： bdatetime 开始时间 选填 edatetime 结束时间 选填 pageindex 页码 必须 pagecount 页大小 必须
     */
    String MONEY_TRADE_DETAIL = IP_MONEY + "/member/cash/listdetail";
    /**
     * 查询是否设置了支付密码
     * <p/>
     * 参数：无
     * <p/>
     * 返回：{pwd：true|false}
     */
    String MONEY_PAY_PASSWORD_STATE = IP_MONEY + "/member/account/valipaypwd";
    /**
     * 设置支付密码
     * <p/>
     * 参数：paypwd 支付密码 必须 payaccount 会员支付系统账号 必须 seckey 授权码 必须
     * <p/>
     * 返回：{setpwd：ok}
     */
    String MONEY_PAY_PASSWORD_SET = IP_MONEY + "/member/account/setpaypwd";
    /**
     * 修改支付密码
     * <p/>
     * 参数：paypwd 支付密码 newpaypwd 新支付密码 mcode 短信验证码
     * <p/>
     * 返回：{modpwd：ok}
     */
    String MONEY_PAY_PASSWORD_MODIFY = IP_MONEY + "/member/account/modpaypwd";
    /**
     * 找回支付密码
     * <p/>
     * 参数：seckey 授权码 必须 cardid 身份证号码 必须 mcode 手机验证码 必须 newpaypwd 新支付密码
     * <p/>
     * 成功返回：{paypwd：ok}
     */
    String MONEY_PAY_PASSWORD_FIND = IP_MONEY
            + "/member/account/retrievepaypwd";
    /**
     * 验证支付密码支付正确
     * <p/>
     * 参数： sn 授权码 必须 payaccount 会员支付系统账号 必须 paypwd 支付密码 必须
     * <p/>
     * 返回：{vli：true|false}
     * <p/>
     * 说明：正确支付密码，返回true，错误返回false
     */
    String MONEY_PAY_PASSWORD_VALIDATE = IP_MONEY
            + "/api/member/account/validatepaypwd";
    /**
     * 查询银行卡类型
     * <p/>
     * 参数：无
     * <p/>
     * 返回：[{id：编号，name：类型名}...]
     */
    String MONEY_BANK_TYPE = IP_MONEY + "/member/bank/banktype";
    /**
     * 查询银行信息
     * <p/>
     * 参数：无
     * <p/>
     * 返回：列表
     */
    String MONEY_BANK_LIST = IP_MONEY + "/member/bank/listbank";
    /**
     * 绑定银行卡信息
     * <p/>
     * 参数： bankid 银行编号 必须 bankcardtype 银行卡类型名称 banknumber 银行卡号 必须 cardname 持卡人姓名
     * 必须 cardid 身份证号码 必须 banknameadds 开户行 必须
     * <p/>
     * 成功返回：{bind：ok}
     * <p/>
     * 说明：明显提醒客户，只能绑定自己的卡，并且手机号码必须和会员注册时的号码一样，且手机号码是银行半卡时预留的
     */
    String MONEY_BANK_BIND = IP_MONEY + "/member/bank/bindbankcard";
    /**
     * 查询绑定的银行卡信息
     * <p/>
     * 参数： sn 授权码 必须 memberid 会员编号 必须
     * <p/>
     * 成功返回：用户卡信息对象
     */
    String MONEY_BANK_BINDED = IP_MONEY + "/member/bank/listbindbankcard";
    /**
     * 解绑银行卡信息
     * <p/>
     * 参数：bankcardid 绑定的银行编号 必须 paypassword 支付密码
     * <p/>
     * 返回：{unbind：true|false}
     */
    String MONEY_BANK_UNBIND = IP_MONEY + "/member/bank/unbindbankcard";
    /**
     * 提现处理
     * <p/>
     * 参数：amount 提现金额 bankcardid 银行卡编号 paypassword 支付密码 area 银行区域 desc 备注
     * <p/>
     * 返回：{deposit：ok}
     * <p/>
     * 说明：区域是省加上城市的名字中间有中杠连接
     */
    String MONEY_BANK_TAKEOUT = IP_MONEY + "/member/account/deposit";
    /**
     * 查询提现记录
     * <p/>
     * 参数： bankcardid银行卡编号 选填 bdatetime 开始时间 选填 edatetime 结束时间 选填 pageindex 页码
     * 必须 pagecount 页大小 必须
     * <p/>
     * 返回：提现分页对象数组 说明：分页显示，时间格式 ：2015-05-08 包含年月日既可
     */
    String MONEY_BANK_TAKEOUT_HISTORY = IP_MONEY
            + "/member/account/depositlist";
    /**
     * 发操作验证短信
     * <p/>
     * 参数：无
     * <p/>
     * 返回：{send：ok，mobile：发送短信的手机号码后四位}
     */
    String MONEY_SMS_CODE = IP_MONEY + "/member/account/sendsms";
    /**
     * 查询支付区域 省
     * <p/>
     * 参数：无
     */
    String MONEY_PAY_AREA_PROVINCE = IP_MONEY + "/member/bank/getpaypro";
    /**
     * 查询支付区域 城市
     * <p/>
     * 参数：proid 省编号 必须
     */
    String MONEY_PAY_AREA_CITY = IP_MONEY + "/member/bank/getpaycity";

}
