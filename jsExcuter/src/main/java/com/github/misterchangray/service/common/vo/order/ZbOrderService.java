package com.github.misterchangray.service.common.vo.order;


import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.libs.zbapi.EncryDigestUtil;
import com.github.misterchangray.libs.zbapi.HttpUtilManager;
import com.github.misterchangray.libs.zbapi.MapSort;
import com.github.misterchangray.service.common.vo.order.response.OrderBean;
import com.github.misterchangray.service.common.vo.order.response.ZbOrderBean;
import com.github.misterchangray.service.common.vo.order.response.ZbOrderIdBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZbOrderService extends BaseApiQuery implements OrderQueryInterface {

    private static Logger log = Logger.getLogger(ZbOrderService.class);

    public final String URL_PREFIX = "https://trade.zb.com/api/";

    @Override
    public ResultSet<String> placeOrder(String ak, String sk, String symbol, String type, Double price, Double amount) {
        //将double类型转为String类型
        DecimalFormat df = new DecimalFormat("#.########");
        Map<String, String> params = new HashMap<String, String>();
        params.put("method", "order");
        params.put("price", df.format(price));
        params.put("amount", df.format(amount));
        if (type.equals("buy")) {
            params.put("tradeType", "1");
        } else if (type.equals("sell")) {
            params.put("tradeType", "0");
        }
        params.put("currency", symbol);
        // 请求测试
        String json = this.getJsonPost(ak, sk, params);
        ZbOrderIdBean zbOrderIdBean = JSONUtils.json2obj(json, ZbOrderIdBean.class);
        if (StringUtils.isNotBlank(zbOrderIdBean.getId())) {
            return ResultSet.build().setCode(0).setData(zbOrderIdBean.getId());
        }
        return ResultSet.build().setCode(1).setData(false);
    }

    @Override
    public OrderBean getOrder(String ak, String sk, String symbol, String order_id) {
        String orderId = order_id;
        OrderBean orderBean = new OrderBean();
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", "getOrder");
            params.put("id", orderId);
            params.put("currency", symbol);
            String json = this.getJsonPost(ak, sk, params);
            ZbOrderBean zbOrderBean = JSONUtils.json2obj(json, ZbOrderBean.class);

            orderBean.setSymbol(zbOrderBean.getCurrency());
            orderBean.setAmount(String.valueOf(zbOrderBean.getTotal_amount()));
            orderBean.setOrder_id(zbOrderBean.getId());
            orderBean.setFinishe_at(String.valueOf(zbOrderBean.getTrade_date()));
            orderBean.setPrice(String.valueOf(zbOrderBean.getPrice()));
            if (zbOrderBean.getStatus().equals(1)) {
                orderBean.setStatus("-1");
            } else if (zbOrderBean.getStatus().equals(2)) {
                orderBean.setStatus("2");
            } else if (zbOrderBean.getStatus().equals(0)) {
                orderBean.setStatus("1");
            } else if (zbOrderBean.getStatus().equals(3)) {
                orderBean.setStatus("0");
            }
            orderBean.setField_cash_amount(String.valueOf(zbOrderBean.getTrade_money()));
            orderBean.setField_amount(String.valueOf(zbOrderBean.getTrade_amount()));
            if (zbOrderBean.getType().equals(1)) {
                orderBean.setType("buy");
            } else if (zbOrderBean.getType().equals(0)) {
                orderBean.setType("sell");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return orderBean;
    }

    @Override
    public ResultSet<String> cancelOrder(String ak, String sk, String symbol, String order_id) {
        String orderId = order_id;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", "cancelOrder");
            params.put("id", orderId);
            params.put("currency", symbol);

            String json = this.getJsonPost(ak, sk, params);
            ZbOrderIdBean zbOrderIdBean = JSONUtils.json2obj(json, ZbOrderIdBean.class);
            System.out.println(zbOrderIdBean);
            OrderBean orderBean = getOrder(ak, sk, symbol, order_id);
            System.out.println(orderBean);
            if (orderBean.getStatus().equals("-1")) {
                return ResultSet.build().setData("true").setCode(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ResultSet.build().setCode(1);
    }

    @Override
    public List<OrderBean> getAllOrder(String ak, String sk, String symbol) {
        List<OrderBean> orderBeanList = new ArrayList<>();
        try {
            String currency = symbol;
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", "getOrdersIgnoreTradeType");
            params.put("currency", currency);
            params.put("pageIndex", "1");
            params.put("pageSize", "100");
            String json = this.getJsonPost(ak, sk, params);
            List<ZbOrderBean> list = JSONUtils.json2list(json, ZbOrderBean.class);
            for (ZbOrderBean zbOrderBean : list) {
                OrderBean orderBean = new OrderBean();
                orderBean.setSymbol(zbOrderBean.getCurrency());
                orderBean.setAmount(String.valueOf(zbOrderBean.getTotal_amount()));
                orderBean.setOrder_id(zbOrderBean.getId());
                orderBean.setFinishe_at(String.valueOf(zbOrderBean.getTrade_date()));
                orderBean.setPrice(String.valueOf(zbOrderBean.getPrice()));
                if (zbOrderBean.getStatus().equals(1)) {
                    orderBean.setStatus("-1");
                } else if (zbOrderBean.getStatus().equals(2)) {
                    orderBean.setStatus("2");
                } else if (zbOrderBean.getStatus().equals(0)) {
                    orderBean.setStatus("1");
                } else if (zbOrderBean.getStatus().equals(3)) {
                    orderBean.setStatus("0");
                }
                orderBean.setField_cash_amount(String.valueOf(zbOrderBean.getTrade_money()));
                orderBean.setField_amount(String.valueOf(zbOrderBean.getTrade_amount()));
                if (zbOrderBean.getType().equals(1)) {
                    orderBean.setType("buy");
                } else if (zbOrderBean.getType().equals(0)) {
                    orderBean.setType("sell");
                }
                orderBeanList.add(orderBean);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return orderBeanList;
    }


    /**
     * 获取json内容(统一加密)
     *
     * @param params
     * @return
     */
    private String getJsonPost(String ak, String sk, Map<String, String> params) {
        params.put("accesskey", ak);// 这个需要加入签名,放前面
        String digest = EncryDigestUtil.digest(sk);

        String sign = EncryDigestUtil.hmacSign(MapSort.toStringMap(params), digest); // 参数执行加密
        String method = params.get("method");

        // 加入验证
        params.put("sign", sign);
        params.put("reqTime", System.currentTimeMillis() + "");
        String url = "请求地址:" + URL_PREFIX + method + " 参数:" + params;
        //System.out.println(url);
        String json = "";
        try {
            json = HttpUtilManager.getInstance().requestHttpPost(URL_PREFIX, method, params);
        } catch (Exception e) {
            log.error("获取交易json异常", e);
        }
        return json;
    }

}
