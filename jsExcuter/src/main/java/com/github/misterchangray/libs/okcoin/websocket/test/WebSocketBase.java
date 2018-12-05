package com.github.misterchangray.libs.okcoin.websocket.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

public abstract class WebSocketBase {

	private Logger log = Logger.getLogger(WebSocketBase.class);
	private WebSocketService service = null;
	private Timer timerTask = null;
	private MoniterTask moniter = null;
	private EventLoopGroup group = null;
	private Bootstrap bootstrap = null;
	private Channel channel = null;
	private String url = null;
	private ChannelFuture future = null;
	private boolean isAlive = false;
	/** 国内站siteFlag=0,国际站siteFlag=1 */
	private int siteFlag = 0;
	private Set<String> subscribChannel = new HashSet<String>();

	public WebSocketBase(String url, WebSocketService serivce) {
		this.url = url;
		this.service = serivce;
	}

	public void start() {
		if (url == null) {
			log.info("WebSocketClient start error  url can not be null");
			return;
		}
		if (service == null) {
			log.info("WebSocketClient start error  WebSocketService can not be null");
			return;
		}
		moniter = new MoniterTask(this);
		this.connect();
		timerTask = new Timer();
		timerTask.schedule(moniter, 1000, 5000);
	}

	public void setStatus(boolean flag) {
		this.isAlive = flag;
	}

	public void addChannel(String channel) {
		if (channel == null) {
			return;
		}
		String dataMsg = "{'event':'addChannel','channel':'" + channel
				+ "','binary':'true'}";
		this.sendMessage(dataMsg);
		subscribChannel.add(channel);
	}

	public void removeChannel(String channel) {
		if (channel == null) {
			return;
		}
		String dataMsg = "{'event':'removeChannel','channel':'" + channel
				+ "'}";
		this.sendMessage(dataMsg);
		subscribChannel.remove(channel);
	}

	/**
	 * 取消合约订单
	 * 
	 * @param apiKey
	 * @param secretKey
	 * @param symbol
	 * @param orderId
	 * @param contractType
	 */
	public void cancelFutureOrder(String apiKey, String secretKey,
			String symbol, long orderId, String contractType) {
		log.debug("apiKey=" + apiKey + ", secretKey=" + secretKey + ", symbol="
				+ symbol + ", orderId=" + orderId + ", contractType="
				+ contractType);
		Map<String, String> preMap = new HashMap<String, String>();
		preMap.put("api_key", apiKey);
		preMap.put("symbol", symbol);
		preMap.put("order_id", String.valueOf(orderId));
		preMap.put("contract_type", contractType);
		String preStr = MD5Util.createLinkString(preMap);
		preStr = preStr + "&secret_key=" + secretKey;
		String signStr = MD5Util.getMD5String(preStr);
		preMap.put("sign", signStr);
		String params = MD5Util.getParams(preMap);
		StringBuilder tradeStr = new StringBuilder(
				"{'event': 'addChannel','channel': 'ok_futuresusd_cancel_order','parameters': ")
				.append(params).append("}");
		this.sendMessage(tradeStr.toString());
	}

	/**
	 * 取消现货交易
	 * 
	 * @param apiKey
	 * @param secretKey
	 * @param symbol
	 * @param orderId
	 */
	public void cancelOrder(String apiKey, String secretKey, String symbol,
			Long orderId) {
		log.debug("apiKey=" + apiKey + ", secretKey=" + secretKey + ", symbol="
				+ symbol + ", orderId=" + orderId);
		Map<String, String> preMap = new HashMap<String, String>();
		preMap.put("api_key", apiKey);
		preMap.put("symbol", symbol);
		preMap.put("order_id", orderId.toString());
		String preStr = MD5Util.createLinkString(preMap);
		StringBuilder preBuilder = new StringBuilder(preStr);
		preBuilder.append("&secret_key=").append(secretKey);
		String signStr = MD5Util.getMD5String(preBuilder.toString());
		preMap.put("sign", signStr);
		String params = MD5Util.getParams(preMap);
		String channel = "ok_spotcny_cancel_order";
		if (siteFlag == 1) {
			channel = "ok_spotusd_cancel_order";
		}
		StringBuilder tradeStr = new StringBuilder(
				"{'event':'addChannel', 'channel':'" + channel
						+ "', 'parameters':").append(params).append("}");
		this.sendMessage(tradeStr.toString());
	}

	/**
	 * 合约交易数据
	 * 
	 * @param apiKey
	 * @param secretKey
	 */
	public void futureRealtrades(String apiKey, String secretKey) {
		log.debug("apiKey=" + apiKey + ", secretKey=" + secretKey);
		StringBuilder preStr = new StringBuilder("api_key=");
		preStr.append(apiKey).append("&secret_key=").append(secretKey);
		String signStr = MD5Util.getMD5String(preStr.toString());
		StringBuilder tradeStr = new StringBuilder(
				"{'event':'addChannel','channel':'ok_sub_futureusd_trades','parameters':{'api_key':'")
				.append(apiKey).append("','sign':'").append(signStr)
				.append("'},'binary':'true'}");
		log.info(tradeStr.toString());
		this.sendMessage(tradeStr.toString());
	}

	/**
	 * 合约下单
	 * 
	 * @param apiKey
	 * @param secretKey
	 * @param symbol
	 * @param contractType
	 * @param price
	 * @param amount
	 * @param type
	 * @param matchPrice
	 * @param leverRate
	 */
	public void futureTrade(String apiKey, String secretKey, String symbol,
			String contractType, double price, int amount, int type,
			double matchPrice, int leverRate) {
		log.debug("apiKey=" + apiKey + ", secretKey=" + secretKey + ", symbol="
				+ symbol + ", contractType=" + contractType + ", price="
				+ price + ", amount=" + amount + ", type=" + type
				+ ", matchPrice=" + matchPrice + ", leverRate=" + leverRate);
		Map<String, String> preMap = new HashMap<String, String>();
		// 待签名字符串
		preMap.put("api_key", apiKey);
		preMap.put("symbol", symbol);
		preMap.put("contract_type", contractType);
		preMap.put("price", String.valueOf(price));
		preMap.put("amount", String.valueOf(amount));
		preMap.put("type", String.valueOf(type));
		preMap.put("match_price", String.valueOf(matchPrice));
		preMap.put("lever_rate", String.valueOf(leverRate));
		String preStr = MD5Util.createLinkString(preMap);
		preStr = preStr + "&secret_key=" + secretKey;
		// 签名
		String signStr = MD5Util.getMD5String(preStr);
		// 参数
		preMap.put("sign", signStr);
		String params = MD5Util.getParams(preMap);
		// 交易json
		StringBuilder tradeStr = new StringBuilder(
				"{'event': 'addChannel','channel':'ok_futuresusd_trade','parameters':")
				.append(params).append("}");
		log.info(tradeStr.toString());
		this.sendMessage(tradeStr.toString());

	}

	/**
	 * 现货查询账户信息
	 */
	public void getUserInfo(String apiKey, String secretKey) {
		log.debug("apiKey=" + apiKey + ", secretKey=" + secretKey);
		StringBuilder preStr = new StringBuilder("api_key=");
		preStr.append(apiKey).append("&secret_key=").append(secretKey);
		String signStr = MD5Util.getMD5String(preStr.toString());
		String channel = "ok_spotcny_userinfo";
		if (siteFlag == 1) {
			channel = "ok_spotusd_userinfo";
		}
		StringBuilder tradeStr = new StringBuilder(
				"{'event':'addChannel','channel':'").append(channel)
				.append("','parameters':{'api_key':'").append(apiKey)
				.append("','sign':'").append(signStr)
				.append("'},'binary':'true'}");
		log.info(tradeStr.toString());
		this.sendMessage(tradeStr.toString());
	}

	/**
	 * 现货交易数据
	 * 
	 * @param apiKey
	 * @param secretKey
	 */
	public void realTrades(String apiKey, String secretKey) {
		log.debug("apiKey=" + apiKey + ", secretKey=" + secretKey);
		StringBuilder preStr = new StringBuilder("api_key=");
		preStr.append(apiKey).append("&secret_key=").append(secretKey);
		String signStr = MD5Util.getMD5String(preStr.toString());
		String channel = "ok_sub_spotcny_trades";
		if (siteFlag == 1) {
			channel = "ok_sub_spotusd_trades";
		}
		StringBuilder tradeStr = new StringBuilder(
				"{'event':'addChannel','channel':'" + channel
						+ "','parameters':{'api_key':'").append(apiKey)
				.append("','sign':'").append(signStr)
				.append("'},'binary':'true'}");
		log.info(tradeStr.toString());
		this.sendMessage(tradeStr.toString());
	}

	/**
	 * 现货交易下单
	 * 
	 * @param apiKey
	 * @param symbol
	 * @param secretKey
	 * @param price
	 * @param amount
	 * @param type
	 */
	public void spotTrade(String apiKey, String secretKey, String symbol,
			String price, String amount, String type) {
		Map<String, String> signPreMap = new HashMap<String, String>();
		signPreMap.put("api_key", apiKey);
		signPreMap.put("symbol", symbol);
		if (price != null) {
			signPreMap.put("price", price);
		}
		if (amount != null) {
			signPreMap.put("amount", amount);
		}
		signPreMap.put("type", type);
		String preStr = MD5Util.createLinkString(signPreMap);
		StringBuilder preBuilder = new StringBuilder(preStr);
		preBuilder.append("&secret_key=").append(secretKey);
		String signStr = MD5Util.getMD5String(preBuilder.toString());
		String channel = "ok_spotcny_trade";
		if (siteFlag == 1) {
			channel = "ok_spotusd_trade";
		}
		StringBuilder tradeStr = new StringBuilder(
				"{'event':'addChannel','channel':'" + channel
						+ "','parameters':");
		signPreMap.put("sign", signStr);
		String params = MD5Util.getParams(signPreMap);
		tradeStr.append(params).append("}");
		log.info(tradeStr.toString());
		this.sendMessage(tradeStr.toString());
	}

	private void connect() {
		try {
			final URI uri = new URI(url);
			if (uri == null) {
				return;
			}
			if (uri.getHost().contains("com")) {
				siteFlag = 1;
			}
			group = new NioEventLoopGroup(1);
			bootstrap = new Bootstrap();
			final SslContext sslCtx = SslContext.newClientContext();
			final WebSocketClientHandler handler = new WebSocketClientHandler(
					WebSocketClientHandshakerFactory.newHandshaker(uri,
							WebSocketVersion.V13, null, false,
							new DefaultHttpHeaders(), Integer.MAX_VALUE),
					service, moniter);
			bootstrap.group(group).option(ChannelOption.TCP_NODELAY, true)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						protected void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							if (sslCtx != null) {
								p.addLast(sslCtx.newHandler(ch.alloc(),
										uri.getHost(), uri.getPort()));
							}
							p.addLast(new HttpClientCodec(),
									new HttpObjectAggregator(8192), handler);
						}
					});

			future = bootstrap.connect(uri.getHost(), uri.getPort());
			future.addListener(new ChannelFutureListener() {
				public void operationComplete(final ChannelFuture future)
						throws Exception {
				}
			});
			channel = future.sync().channel();
			handler.handshakeFuture().sync();
			this.setStatus(true);
		} catch (Exception e) {
			log.info("WebSocketClient start error ", e);
			group.shutdownGracefully();
			this.setStatus(false);
		}
	}

	private void sendMessage(String message) {
		if (!isAlive) {
			log.info("WebSocket is not Alive addChannel error");
		}
		channel.writeAndFlush(new TextWebSocketFrame(message));
	}

	public void sentPing() {
		String dataMsg = "{'event':'ping'}";
		this.sendMessage(dataMsg);
	}

	public void reConnect() {
		try {
			this.group.shutdownGracefully();
			this.group = null;
			this.connect();
			if (future.isSuccess()) {
				this.setStatus(true);
				this.sentPing();
				Iterator<String> it = subscribChannel.iterator();
				while (it.hasNext()) {
					String channel = it.next();
					this.addChannel(channel);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

class MoniterTask extends TimerTask {

	private Logger log = Logger.getLogger(WebSocketBase.class);
	private long startTime = System.currentTimeMillis();
	private int checkTime = 5000;
	private WebSocketBase client = null;

	public void updateTime() {
		// log.info("startTime is update");
		startTime = System.currentTimeMillis();
	}

	public MoniterTask(WebSocketBase client) {
		this.client = client;
		// log.info("TimerTask is starting.... ");
	}

	public void run() {
		if (System.currentTimeMillis() - startTime > checkTime) {
			client.setStatus(false);
			// log.info("Moniter reconnect....... ");
			client.reConnect();
		}
		client.sentPing();
		// log.info("Moniter ping data sent.... ");
	}

}
