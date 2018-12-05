package com.github.misterchangray.libs.okcoin.websocket.test.test;


import com.github.misterchangray.libs.okcoin.websocket.test.WebSocketBase;
import com.github.misterchangray.libs.okcoin.websocket.test.WebSocketService;
/**
 * 通过继承WebSocketBase创建WebSocket客户端
 * @author okcoin
 *
 */
public class WebSoketClient extends WebSocketBase {
	public WebSoketClient(String url,WebSocketService service){
		super(url,service);
	}
}
