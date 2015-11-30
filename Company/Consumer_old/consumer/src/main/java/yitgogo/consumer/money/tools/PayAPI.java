package yitgogo.consumer.money.tools;

public interface PayAPI {

    public String API_IP = "http://yitos.net";
//    public String API_IP = "http://192.168.8.8:8050";
//    public String API_IP = "http://192.168.8.36:8080";

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
    public String API_PAY_BIND = API_IP
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
    public String API_PAY_FIRST_TIME = API_IP
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

    public String API_PAY_SECOND_TIME = API_IP
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
    public String API_PAY_BALANCE = API_IP
            + "/api/settlement/member/payMemberAccount";
}
