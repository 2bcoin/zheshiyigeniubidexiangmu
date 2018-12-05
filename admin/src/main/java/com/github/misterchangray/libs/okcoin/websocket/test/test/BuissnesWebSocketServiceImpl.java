package com.github.misterchangray.libs.okcoin.websocket.test.test;

import com.github.misterchangray.libs.okcoin.websocket.test.WebSocketBase;
import com.github.misterchangray.libs.okcoin.websocket.test.WebSocketService;
import org.apache.log4j.Logger;
/**
 * 订阅信息处理类需要实现WebSocketService接口
 * @author okcoin
 *
 */
public class BuissnesWebSocketServiceImpl implements WebSocketService {
	private Logger log = Logger.getLogger(WebSocketBase.class);
	@Override
	public void onReceive(String msg){
		
		log.info("WebSocket Client received message: " + msg);
	
	}
}
