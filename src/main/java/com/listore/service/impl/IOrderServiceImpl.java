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
import com.github.miemiedev.mybatis.paginator.domain.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.listore.commen.Const;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.dao.*;
import com.listore.pojo.*;
import com.listore.pojo.Order;
import com.listore.service.IOrderService;
import com.listore.util.BigDecimalUtil;
import com.listore.util.FtpUtil;
import com.listore.util.PropertiesUtil;
import com.listore.util.TimeUtil;
import com.listore.vo.OrderItemVo;
import com.listore.vo.OrderProductVo;
import com.listore.vo.OrderVo;
import com.listore.vo.ShippingVo;
import com.mysql.jdbc.log.LogFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.maven.settings.Server;
import org.joda.time.DateTimeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

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
    @Resource
    private CartMapper cartMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ShippingMapper shippingMapper;


    //创建订单
    @Override
    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        //查看在购物车中被勾选的数据
        List<Cart> cartList = cartMapper.selectCheckedByUserId(userId);
        //封装成orderItem
        ServerResponse<List<OrderItem>> serverResponse = this.getOrderItemList(userId,cartList);
        if(!serverResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("生成订单错误");
        }
        //为了计算订单总价
        List<OrderItem> orderItemList = serverResponse.getData();
        BigDecimal totalPrice = this.getTotalPrice(orderItemList);


        //生成订单，插入数据库
        Order order = this.assembleOrder(userId,shippingId,totalPrice);
        if(order == null){
            return ServerResponse.createByErrorMessage("生成订单错误");

        }
        if(CollectionUtils.isEmpty(orderItemList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        //在orderItem进行批量插入将该订单的所有订单明细的订单号置为当前的订单号
        for(OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //mybatis批量插入
        orderItemMapper.batchInsert(orderItemList);
        //生成成功，减少产品的库存
        this.reduceProductStock(orderItemList);


        //清空购物车
        this.cleanCart(cartList);
        //返回前端信息
        OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }
    //取消订单
    @Override
    public ServerResponse<String> cancelOrder(Integer userId, Long orderNo) {
         Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
         if(order == null){
             return ServerResponse.createByErrorMessage("您没有该订单");
         }
        if(order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()){
            return ServerResponse.createByErrorMessage("您已经付款无法取消该订单");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        updateOrder.setUpdateTime(new Date());
        int rowCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
         if(rowCount > 0){
             return ServerResponse.createBySuccess("取消成功");
         }
        return ServerResponse.createByErrorMessage("取消失败");
    }
    public ServerResponse getOrderCartProduct(Integer userId){
        OrderProductVo orderProductVo = new OrderProductVo();
        //从购物车中取出被选中的商品
        List<Cart> cartList = cartMapper.selectCheckedByUserId(userId);
        //返回一个订单明细
        ServerResponse serverResponse = this.getOrderItemList(userId,cartList);
        if(!serverResponse.isSuccess()){
            return serverResponse;

        }
        //计算总价
        List<OrderItem> orderItemList = (List<OrderItem>)serverResponse.getData();
        List<OrderItemVo> orderItemVoList = this.assembleOrderItemVoList(orderItemList);
        BigDecimal totalPrice = new BigDecimal("0");
        //遍历orderItemList
        for(OrderItem orderItem : orderItemList){
            totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        orderProductVo.setProductTotalPrice(totalPrice);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

          return ServerResponse.createBySuccess(orderProductVo);

    }
    //订单详情
    @Override
    public ServerResponse<OrderVo> detail(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if((order != null) && (orderItemList!= null)){
            //封装VO对象
            OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
            return  ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("您没有该订单");
    }
    //订单列表
    @Override
    public ServerResponse<PageInfo> list(Integer userId,Integer pageNum,Integer pageSize) {
        //开始分页
        PageHelper.startPage(pageNum,pageSize);
        //订单列表
        List<Order> orderList = orderMapper.selectByUserId(userId);

        List<OrderVo> orderVoList = this.assembleOrderVoList(userId,orderList);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }




    //backend
    /*
    *
    * 后台订单list页
    *
    * */
    public ServerResponse<PageInfo> manageList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        //获得所有订单
        List<Order> orderList = orderMapper.selectAllOrder();
        List<OrderVo> orderVoList = assembleOrderVoList(null,orderList);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
    /*
    *
    * 后台订单详情
    *
    * */
    @Override
    public ServerResponse<OrderVo> manageDetail(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order != null){
            //查询订单明细列表
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
            OrderVo orderVo = assembleOrderVo(order,orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
       return ServerResponse.createByErrorMessage("您没有该订单");

    }

    /*
    *
    * 依据订单号进行搜索订单
    * 为了以后扩展：模糊查询的功能
    *
    *
    * */

    @Override
    public ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order != null){

            //查询订单明细列表
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
            OrderVo orderVo = assembleOrderVo(order,orderItemList);
            PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
            pageInfo.setList(Lists.newArrayList(orderVo));
            return ServerResponse.createBySuccess(pageInfo);
        }
        return ServerResponse.createByErrorMessage("您没有该订单");

    }
    //发货
    public ServerResponse<String> manageSendGoods(long orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        //将订单状态改为已发货
        if(order.getStatus() == Const.OrderStatusEnum.PAID.getCode()){
            //如果该订单已经支付成功那么是可以允许发货的
            order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
            order.setSendTime(new Date());
            int updateRow = orderMapper.updateByPrimaryKeySelective(order);
            if(updateRow > 0){
                return ServerResponse.createByErrorMessage("发货成功");

            }
            return ServerResponse.createByErrorMessage("发货失败");
        }
        else if(order.getStatus() == Const.OrderStatusEnum.NO_PAY.getCode()){
            return ServerResponse.createByErrorMessage("订单未付款");
        }else if(order.getStatus() == Const.OrderStatusEnum.ORDER_SUCCESS.getCode() ||
                order.getStatus() == Const.OrderStatusEnum.SHIPPED.getCode()){
            return ServerResponse.createByErrorMessage("订单已发货或者完成，请不要重复发货");
        }else if(order.getStatus() == Const.OrderStatusEnum.CANCELED.getCode()){
            return ServerResponse.createByErrorMessage("订单已经取消，不能发货");
        }else{
            return ServerResponse.createByErrorMessage("订单已经关闭，不能发货");
        }



    }

    private List<OrderVo> assembleOrderVoList(Integer userId,List<Order> orderList){
        List<OrderVo> orderVoList = Lists.newArrayList();
        //遍历订单列表
        for(Order order : orderList){
            List<OrderItem> orderItemList = Lists.newArrayList();
            OrderVo  orderVo = new OrderVo();
            if(userId == null){
                //管理员不需要userId 依据订单号获得订单明细
                orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
            }else{
                orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId,order.getOrderNo());
            }

            orderVo = this.assembleOrderVo(order,orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }
    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        orderVo.setCloseTime(TimeUtil.dateToStr(order.getCloseTime()));
        orderVo.setEndTime(TimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(TimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setPaymentTime(TimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        List<OrderItemVo> orderItemVoList = this.assembleOrderItemVoList(orderItemList);
        orderVo.setOrderItemVoList(orderItemVoList);
        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping != null){
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(this.assembleShippingVo(shipping));
        }
        return orderVo;
    }
    private ShippingVo assembleShippingVo(Shipping shipping){
                ShippingVo shippingVo = new ShippingVo();
                shippingVo.setReceiverName(shipping.getReceiverName());
                shippingVo.setReceiverAddress(shipping.getReceiverAddress());
                shippingVo.setReceiverCity(shipping.getReceiverCity());
                shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
                shippingVo.setReceiverMobile(shipping.getReceiverMobile());
                shippingVo.setReceiverPhone(shipping.getReceiverPhone());
                shippingVo.setReceiverProvince(shipping.getReceiverProvince());
                shippingVo.setReceiverZip(shipping.getReceiverZip());

               return shippingVo;
    }
    private List<OrderItemVo> assembleOrderItemVoList(List<OrderItem> orderItemList){
         List<OrderItemVo> orderItemVoList = null;
        if(!CollectionUtils.isEmpty(orderItemList)){
            orderItemVoList = Lists.newArrayList();
            for(OrderItem orderItem : orderItemList){
                OrderItemVo orderItemVo = new OrderItemVo();
                orderItemVo.setCreateTime(TimeUtil.dateToStr(orderItem.getCreateTime()));
                orderItemVo.setQuantity(orderItem.getQuantity());
                orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
                orderItemVo.setOrderNo(orderItem.getOrderNo());
                orderItemVo.setProductId(orderItem.getProductId());
                orderItemVo.setProductImage(orderItem.getProductImage());
                orderItemVo.setTotalPrice(orderItem.getTotalPrice());

                orderItemVoList.add(orderItemVo);
            }
        }
        return orderItemVoList;
    }
    private void cleanCart(List<Cart> cartList){
        for(Cart cart : cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }
    //更新库存
    private void reduceProductStock(List<OrderItem> orderItemList){
        for(OrderItem orderItem : orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }
    //生成订单的私有方法
    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal totalPrice){

        Order order = new Order();
        order.setPayment(totalPrice);
        order.setShippingId(shippingId);
        order.setUserId(userId);
        order.setOrderNo(this.generatorOrderNo());
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE.getCode());
        order.setPostage(0);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        int rowCount = orderMapper.insert(order);
        if(rowCount > 0){
            return order;
        }
        //发货时间等等
        return null;
    }
    //生成订单号的私有方法
    private long generatorOrderNo(){
          long orderNo = System.currentTimeMillis();
          //加入在高并发条件下进行测试容易出现错误
          //return orderNo + orderNo%10;
          return orderNo + new Random().nextInt(100);
    }
    private BigDecimal getTotalPrice(List<OrderItem> orderItemList){
        BigDecimal totalPrice = new BigDecimal("0");
        if(orderItemList != null){
            for(OrderItem orderItem:orderItemList){
                //totalPrice 一定要拿过来
                totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(),orderItem.getTotalPrice().doubleValue());
            }

        }
        return totalPrice;

    }
    private ServerResponse<List<OrderItem>> getOrderItemList(Integer userId,List<Cart> cartList){
        if(CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        List<OrderItem> orderItemList = Lists.newArrayList();
        //遍历list
        for(Cart cartItem:cartList){
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "不是在售状态");
            }
            if(product.getStock() < cartItem.getQuantity()){
                return ServerResponse.createByErrorMessage("产品"+ product.getName() + "超过库存");
            }
            //创建订单明细
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProducoImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(cartItem.getQuantity().doubleValue(),product.getPrice().doubleValue()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }
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
            log.info(totalAmount);
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
            log.info(sellerId);
            // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品3件共20.00元"
            String body = new StringBuilder().append("订单:").append(outTradeNo).append("商品总价：").append(totalAmount).append("元").toString();

            // 商户操作员编号，添加此参数可以为商户操作员做销售统计
            String operatorId = "test_operator_id";
            log.info(operatorId);
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
        log.info(params.toString());
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
        if(!order.getPayment().equals(tradePayment)){
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

    @Override
    public ServerResponse<Boolean> queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order != null){

            if(order.getStatus() >= Const.OrderStatusEnum.ORDER_SUCCESS.getCode()){
                return ServerResponse.createBySuccess();
            }
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createByError();
    }

}
