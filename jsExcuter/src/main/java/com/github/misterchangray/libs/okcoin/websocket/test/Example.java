package com.github.misterchangray.libs.okcoin.websocket.test;


import com.github.misterchangray.libs.okcoin.websocket.test.test.BuissnesWebSocketServiceImpl;
import com.github.misterchangray.libs.okcoin.websocket.test.test.WebSoketClient;

/**
 * WebSocket API使用事例
 * 
 * @author okcoin
 * 
 */
public class Example {
	private static String okex_secretKey = "092230CB2C9AE*A7092B434";
	private static String okex_api_key = "088cfef7-5a26-*-*-15c6d1115d89";

	public static void main(String[] args) {

		// apiKey 为用户申请的apiKey
		String apiKey =  okex_api_key;

		// secretKey为用户申请的secretKey
		String secretKey = okex_secretKey;



		// 国际站WebSocket地址 注意如果访问国内站 请将 real.okcoin.com 改为 real.okcoin.cn
		String url = "wss://real.okex.com:10441/websocket";

		// 订阅消息处理类,用于处理WebSocket服务返回的消息
		WebSocketService service = new BuissnesWebSocketServiceImpl();

		// WebSocket客户端
		WebSoketClient client = new WebSoketClient(url, service);
		// 启动客户端
		client.start();

		// 添加订阅
		client.addChannel("ok_sub_spotusd_btc_ticker");

		// 删除定订阅
		// client.removeChannel("ok_sub_spotusd_btc_ticker");

		// 合约下单交易
		// client.futureTrade(apiKey, secretKey, "btc_usd", "this_week", 2.3, 2,
		// 1, 0, 10);

		// 实时交易数据 apiKey
		// client.futureRealtrades(apiKey, secretKey);

		// 取消合约交易
		// client.cancelFutureOrder(apiKey, secretKey, "btc_usd", 123456L,
		// "this_week");

		// 现货下单交易
		// client.spotTrade(apiKey, secretKey, "btc_usd", 3.2, 2.3, "buy");

		// 现货交易数据
		// client.realTrades(apiKey, secretKey);

		// 现货取消订单
		// client.cancelOrder(apiKey, secretKey, "btc_usd", 123L);

		// 获取用户信息
		// client.getUserInfo(apiKey,secretKey);
	}
}
