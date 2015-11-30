package yitgogo.consumer.money.tools;

public interface MoneyAPI {

    public String IP = "http://pay.yitos.net";
//    public String IP = "http://192.168.8.8:82";

    /**
     * 用户授权登录支付系统
     * <p/>
     * 参数： sn 授权码 必须
     * <p/>
     * 成功返回 : {payaccount:会员支付系统账号，balance：账户余额，seckey：授权码}
     */
    public String MONEY_LOGIN = IP + "/api/member/login";
    /**
     * 查询现金交易明细
     * <p/>
     * 参数： bdatetime 开始时间 选填 edatetime 结束时间 选填 pageindex 页码 必须 pagecount 页大小 必须
     */
    public String MONEY_TRADE_DETAIL = IP + "/member/cash/listdetail";
    /**
     * 查询是否设置了支付密码
     * <p/>
     * 参数：无
     * <p/>
     * 返回：{pwd：true|false}
     */
    public String MONEY_PAY_PASSWORD_STATE = IP + "/member/account/valipaypwd";
    /**
     * 设置支付密码
     * <p/>
     * 参数：paypwd 支付密码 必须 payaccount 会员支付系统账号 必须 seckey 授权码 必须
     * <p/>
     * 返回：{setpwd：ok}
     */
    public String MONEY_PAY_PASSWORD_SET = IP + "/member/account/setpaypwd";
    /**
     * 修改支付密码
     * <p/>
     * 参数：paypwd 支付密码 newpaypwd 新支付密码 mcode 短信验证码
     * <p/>
     * 返回：{modpwd：ok}
     */
    public String MONEY_PAY_PASSWORD_MODIFY = IP + "/member/account/modpaypwd";
    /**
     * 找回支付密码
     * <p/>
     * 参数：seckey 授权码 必须 cardid 身份证号码 必须 mcode 手机验证码 必须 newpaypwd 新支付密码
     * <p/>
     * 成功返回：{paypwd：ok}
     */
    public String MONEY_PAY_PASSWORD_FIND = IP
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
    public String MONEY_PAY_PASSWORD_VALIDATE = IP
            + "/api/member/account/validatepaypwd";
    /**
     * 查询银行卡类型
     * <p/>
     * 参数：无
     * <p/>
     * 返回：[{id：编号，name：类型名}...]
     */
    public String MONEY_BANK_TYPE = IP + "/member/bank/banktype";
    /**
     * 查询银行信息
     * <p/>
     * 参数：无
     * <p/>
     * 返回：列表
     */
    public String MONEY_BANK_LIST = IP + "/member/bank/listbank";
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
    public String MONEY_BANK_BIND = IP + "/member/bank/bindbankcard";
    /**
     * 查询绑定的银行卡信息
     * <p/>
     * 参数： sn 授权码 必须 memberid 会员编号 必须
     * <p/>
     * 成功返回：用户卡信息对象
     */
    public String MONEY_BANK_BINDED = IP + "/member/bank/listbindbankcard";
    /**
     * 解绑银行卡信息
     * <p/>
     * 参数：bankcardid 绑定的银行编号 必须 paypassword 支付密码
     * <p/>
     * 返回：{unbind：true|false}
     */
    public String MONEY_BANK_UNBIND = IP + "/member/bank/unbindbankcard";
    /**
     * 提现处理
     * <p/>
     * 参数：amount 提现金额 bankcardid 银行卡编号 paypassword 支付密码 area 银行区域 desc 备注
     * <p/>
     * 返回：{deposit：ok}
     * <p/>
     * 说明：区域是省加上城市的名字中间有中杠连接
     */
    public String MONEY_BANK_TAKEOUT = IP + "/member/account/deposit";
    /**
     * 查询提现记录
     * <p/>
     * 参数： bankcardid银行卡编号 选填 bdatetime 开始时间 选填 edatetime 结束时间 选填 pageindex 页码
     * 必须 pagecount 页大小 必须
     * <p/>
     * 返回：提现分页对象数组 说明：分页显示，时间格式 ：2015-05-08 包含年月日既可
     */
    public String MONEY_BANK_TAKEOUT_HISTORY = IP
            + "/member/account/depositlist";
    /**
     * 发操作验证短信
     * <p/>
     * 参数：无
     * <p/>
     * 返回：{send：ok，mobile：发送短信的手机号码后四位}
     */
    public String MONEY_SMS_CODE = IP + "/member/account/sendsms";
    /**
     * 查询支付区域 省
     * <p/>
     * 参数：无
     */
    public String MONEY_PAY_AREA_PROVINCE = IP + "/member/bank/getpaypro";
    /**
     * 查询支付区域 城市
     * <p/>
     * 参数：proid 省编号 必须
     */
    public String MONEY_PAY_AREA_CITY = IP + "/member/bank/getpaycity";
}
