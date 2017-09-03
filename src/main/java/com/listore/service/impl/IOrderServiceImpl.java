package com.listore.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePayRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.listore.commen.Const;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.dao.OrderItemMapper;
import com.listore.dao.OrderMapper;
import com.listore.dao.PayInfoMapper;
import com.listore.pojo.Order;
import com.listore.pojo.OrderItem;
import com.listore.pojo.PayInfo;
import com.listore.service.IOrderService;
import com.listore.util.BigDecimalUtil;
import com.listore.util.FtpUtil;
import com.listore.util.PropertiesUtil;
import com.listore.util.TimeUtil;
import com.mysql.jdbc.log.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.joda.time.DateTimeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by HP on 2017/9/2.
 */
@Service("iOrderService")
public class IOrderServiceImpl implements IOrderService {
    private static Log log = org.apache.commons.logging.LogFactory.getLog(IOrderServiceImpl.class);
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private PayInfoMapper payInfoMapper;


    @Override
    public ServerResponse pay(Integer userId, Long orderNo, String path) {

        Map<String, String> resultMap = Maps.newHashMap();
        if (orderNo != null) {
            Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
            if (order == null) {
                return ServerResponse.createByErrorMessage("您没有该订单");
            }
            resultMap.put("orderNo", String.valueOf(orderNo));
            //如果存在该订单进行支付
            // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
            // 需保证商户系统端不能重复，建议通过数据库sequence生成，
            String outTradeNo = order.getOrderNo().toString();

            // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店消费”
            String subject = new StringBuilder().append("listore mall 订单号：").append(outTradeNo).toString();

            // (必填) 订单总金额，单位为元，不能超过1亿元
            // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
            String totalAmount = order.getPayment().toString();

            // (必填) 付款条码，用户支付宝钱包手机app点击“付款”产生的付款条码
            //String authCode = "用户自己的支付宝付款码"; // 条码示例，286648048691290423
            // (可选，根据需要决定是否使用) 订单可打折金额，可以配合商家平台配置折扣活动，如果订单部分商品参与打折，可以将部分商品总价填写至此字段，默认全部商品可打折
            // 如果该值未传入,但传入了【订单总金额】,【不可打折金额】 则该值默认为【订单总金额】- 【不可打折金额】
            //        String discountableAmount = "1.00"; //

            // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
            // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
            String undiscountableAmount = "0.0";

            // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
            // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
            String sellerId = "";

            // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品3件共20.00元"
            String body = new StringBuilder().append("订单:").append(outTradeNo).append("商品总价：").append(totalAmount).append("元").toString();

            // 商户操作员编号，添加此参数可以为商户操作员做销售统计
            String operatorId = "test_operator_id";

            // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
            String storeId = "test_store_id";

            // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
            String providerId = "2088100200300400500";
            ExtendParams extendParams = new ExtendParams();
            extendParams.setSysServiceProviderId(providerId);

            // 支付超时，线下扫码交易定义为5分钟
            String timeoutExpress = "120m";

            // 商品明细列表，需填写购买商品详细信息，
            List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
            //获得订单元素列表
            List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId, order.getOrderNo());
            //遍历订单列表
            for (OrderItem orderItem : orderItemList) {
                //为什么参数类型是Long呢，原因是它以分为单位，又因为数据库中的价格是保留两位小数的是精确到分的，不会出现99.999这样的数字所以直接可以用long类型了
                GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                        BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(), orderItem.getQuantity());
                     goodsDetailList.add(goods1);
            }
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            /*
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods1);

            // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
            GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
            goodsDetailList.add(goods2);
            */
            AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                    .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                    .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                    .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                    .setTimeoutExpress(timeoutExpress)
                    .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                    .setGoodsDetailList(goodsDetailList);
/** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
 *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
 */
            Configs.init("zfbinfo.properties");
            AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
            AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
            switch (result.getTradeStatus()) {
                case SUCCESS:
                    log.info("支付宝预下单成功: )");

                    AlipayTradePrecreateResponse response = result.getResponse();
                    dumpResponse(response);
                    //将二维码文件上传到ftp服务器上
                    File folder = new File(path);
                    //如果文件夹不存在就创建它
                    if(!folder.exists()){
                        folder.setWritable(true);
                        folder.mkdirs();
                    }
                    String qrPath = String.format(path+"/qr-%s.png",response.getOutTradeNo());
                    String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
                    //获得二维码图片
                    ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                    //目标文件
                    File targetFile = new File(path,qrFileName);
                    try {
                        FtpUtil.uploadFile(Lists.newArrayList(targetFile));
                    } catch (IOException e) {
                        log.error("上传二维码异常");
                        e.printStackTrace();
                    }
                    log.info("qrPath:" + qrPath);

                    String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
                    resultMap.put("qrUrl",qrUrl);
                    return ServerResponse.createBySuccess(resultMap);
                case FAILED:
                    log.error("支付宝预下单失败!!!");
                    return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

                case UNKNOWN:
                    log.error("系统异常，预下单状态未知!!!");
                    return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

                default:
                    log.error("不支持的交易状态，交易返回异常!!!");
                    return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
            }

        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(), ResponseCode.ILLEGALE_ARGUMENT.getDesc());
    }
    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
    //支付宝回调成功更新数据库中的内容
    public ServerResponse aliCallback(Map<String,String> params){
        //交易订单号
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        //支付宝交易号
        String tradeNo = params.get("trade_no");
        //交易状态
        String tradeStatus = params.get("trade_status");
        //交易金额
        BigDecimal tradePayment =new BigDecimal(params.get("total_amount"));

        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("不是本商城的订单号，回调忽略");
        }
        //验证通知的订单金额是否为商户系统订单的订单金额
        if(order.getPayment() != tradePayment){
            return ServerResponse.createByErrorMessage("不是本商城的订单号，回调忽略");
        }
        //验证通知中的seller_id或者seller_emial是否为
        if(order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccessMsg("支付宝重复调用");
        }
        if(Const.AlipayCallback.TARDE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            order.setPaymentTime(TimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
              orderMapper.updateByPrimaryKeySelective(order);
        }
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNo(orderNo);
        payInfo.setUserId(order.getUserId());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        //交易号
        payInfo.setPlatformNumber(tradeNo);
        //平台交易状态
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);
        //为什么不把支付信息返回回去
        return ServerResponse.createBySuccess();
    }
}
