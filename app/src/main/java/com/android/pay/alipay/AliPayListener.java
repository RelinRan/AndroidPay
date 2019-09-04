package com.android.pay.alipay;

/**
 * Created by Ice on 2016/2/18.
 * 支付宝支付回调
 */
public interface AliPayListener {

    /**
     * 支付成功
     *
     * @param status      是结果码(类型为字符串)。
     *                    9000	订单支付成功
     *                    8000	正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
     *                    4000	订单支付失败
     *                    5000	重复请求
     *                    6001	用户中途取消
     *                    6002	网络连接出错
     *                    6004	支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
     *                    其它	其它支付错误
     * @param json        是处理结果(类型为json结构字符串)
     *                    out_trade_no	String	是	64	商户网站唯一订单号	70501111111S001111119
     *                    trade_no	String	是	64	该交易在支付宝系统中的交易流水号。最长64位。	2014112400001000340011111118
     *                    app_id	String	是	32	支付宝分配给开发者的应用Id。	2014072300007148
     *                    total_amount	Price	是	9	该笔订单的资金总额，单位为RMB-Yuan。取值范围为[0.01,100000000.00]，精确到小数点后两位。	9.00
     *                    seller_id	String	是	16	收款支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字	2088111111116894
     *                    msg	String	是	16	处理结果的描述，信息来自于code返回结果的描述	success
     *                    charset	String	是	16	编码格式	utf-8
     *                    timestamp	String	是	32	时间	2016-10-11 17:43:36
     *                    code	String	是	16	结果码	具体见
     * @param description description是描述信息(类型为字符串)
     */
    public void aliPaySuccess(String status, String json, String description);

    /**
     * 支付失败
     *
     * @param status      是结果码(类型为字符串)。
     *                    9000	订单支付成功
     *                    8000	正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
     *                    4000	订单支付失败
     *                    5000	重复请求
     *                    6001	用户中途取消
     *                    6002	网络连接出错
     *                    6004	支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
     *                    其它	其它支付错误
     * @param json        是处理结果(类型为json结构字符串)
     *                    out_trade_no	String	是	64	商户网站唯一订单号	70501111111S001111119
     *                    trade_no	String	是	64	该交易在支付宝系统中的交易流水号。最长64位。	2014112400001000340011111118
     *                    app_id	String	是	32	支付宝分配给开发者的应用Id。	2014072300007148
     *                    total_amount	Price	是	9	该笔订单的资金总额，单位为RMB-Yuan。取值范围为[0.01,100000000.00]，精确到小数点后两位。	9.00
     *                    seller_id	String	是	16	收款支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字	2088111111116894
     *                    msg	String	是	16	处理结果的描述，信息来自于code返回结果的描述	success
     *                    charset	String	是	16	编码格式	utf-8
     *                    timestamp	String	是	32	时间	2016-10-11 17:43:36
     *                    code	String	是	16	结果码	具体见
     * @param description description是描述信息(类型为字符串)
     */
    public void aliPayFail(String status, String json, String description);

    /**
     * 支付中
     *
     * @param status      是结果码(类型为字符串)。
     *                    9000	订单支付成功
     *                    8000	正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
     *                    4000	订单支付失败
     *                    5000	重复请求
     *                    6001	用户中途取消
     *                    6002	网络连接出错
     *                    6004	支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
     *                    其它	其它支付错误
     * @param json        是处理结果(类型为json结构字符串)
     *                    out_trade_no	String	是	64	商户网站唯一订单号	70501111111S001111119
     *                    trade_no	String	是	64	该交易在支付宝系统中的交易流水号。最长64位。	2014112400001000340011111118
     *                    app_id	String	是	32	支付宝分配给开发者的应用Id。	2014072300007148
     *                    total_amount	Price	是	9	该笔订单的资金总额，单位为RMB-Yuan。取值范围为[0.01,100000000.00]，精确到小数点后两位。	9.00
     *                    seller_id	String	是	16	收款支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字	2088111111116894
     *                    msg	String	是	16	处理结果的描述，信息来自于code返回结果的描述	success
     *                    charset	String	是	16	编码格式	utf-8
     *                    timestamp	String	是	32	时间	2016-10-11 17:43:36
     *                    code	String	是	16	结果码	具体见
     * @param description description是描述信息(类型为字符串)
     */
    public void aliPaying(String status, String json, String description);
}
