/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.misterchangray.libs.bitmex.client;

import com.github.misterchangray.libs.bitmex.data.SumZeroException;
import com.github.misterchangray.libs.bitmex.data.Ticker;
import com.github.misterchangray.libs.bitmex.entity.BitmexQuote;
import com.github.misterchangray.libs.bitmex.listener.*;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author RobTerpilowski
 */
public class BitmexWebsocketClient implements IBitmexWebsocketClient, WebsocketDisconnectListener {

    protected String productionApiUrl = "wss://www.bitmex.com/realtime";
    protected String testnetApiUrl = "wss://testnet.bitmex.com/realtime";
    protected String websocketUrl = "";
    protected CountDownLatch latch = new CountDownLatch(1);
    protected Logger logger = Logger.getLogger(BitmexWebsocketClient.class);
    protected IMessageProcessor messageProcessor;
    protected Set<String> subscribedQuoteTickers = new HashSet<>();
    protected Set<String> subscribedTradeTickers = new HashSet<>();
    protected String apiKey = "";
    protected String apiSecret = "";
    protected List<String> subscribeCommandList = new ArrayList<>();
    protected boolean shouldReconnect = true;

    WebSocketClient client = new WebSocketClient();
    JettySocket socket;

    protected boolean isStarted = false;
    protected boolean connected = false;
    protected boolean subscribedPositions = false;
    protected boolean subscribedOrders = false;
    protected boolean subscribedExecutions = false;

    //for unit tests
    protected BitmexWebsocketClient() {
    }

    public BitmexWebsocketClient(boolean useProduction) {
        if (useProduction) {
            websocketUrl = productionApiUrl;
        } else {
            websocketUrl = testnetApiUrl;
        }
        init();
    }
    
    protected void init() {
        messageProcessor = buildMessageProcessor();
        messageProcessor.startProcessor();
        socket = buildJettySocket();
        messageProcessor.addPongListener(socket);        
    }

    @Override
    public boolean connect() {
        return connect("", "");
    }

    @Override
    public boolean connect(String apiKey, String apiSecret) {
        try {
            this.apiKey = apiKey;
            this.apiSecret = apiSecret;
            logger.info("Starting connection");
            client.start();
            URI echoUri = new URI(websocketUrl);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket, echoUri, request);
            

            logger.info("Connecting to : " + echoUri);
            latch.await(15, TimeUnit.SECONDS);
            isStarted = socket.isConnected();
            connected = socket.isConnected();
            logger.info("Connected: " + connected);
            if (!Strings.isNullOrEmpty(apiKey)) {
                long nonce = System.currentTimeMillis();
                String signature = getApiSignature(apiSecret, nonce);
                authenticate(apiKey, nonce, signature);
            }
            //socket.startPing();
        } catch (Exception ex) {
            throw new SumZeroException(ex);
        } finally {
            return connected;
        }
    }

    @Override
    public void socketDisconnectDetected() {
        logger.error("Disconnect detected, should reconnect: " + shouldReconnect );
        if (shouldReconnect) {
            logger.error("Disconnect detected....reconnecting");
            connect(apiKey, apiSecret);
            for (String command : subscribeCommandList) {
                logger.error("Resubmitting subscribe command: " + command);
                socket.subscribe(command);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ex) {
                }
            }
        } else {
            logger.info("Disconnect detected, but will not reconnect");
        }
    }

    @Override
    public void disconnect() {
        try {
            shouldReconnect = false;
            client.stop();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void subscribeExecutions(IExecutionListener listener) {
        messageProcessor.addExecutionListener(listener);
        if( !subscribedExecutions ) {
            subscribedExecutions = true;
            socket.subscribe(buildSubscribeCommand("execution"));
        }
    }

    @Override
    public void subscribeOrders(IOrderListener listener) {
        messageProcessor.addOrderListener(listener);
        if (!subscribedOrders) {
            subscribedOrders = true;
            socket.subscribe(buildSubscribeCommand("order"));
        }
    }

    @Override
    public void subscribePositions(IPositionListener listener) {
        messageProcessor.addPositionListener(listener);
        if (!subscribedPositions) {
            socket.subscribe(buildSubscribeCommand("position"));
            subscribedPositions = true;
        }

    }

    @Override
    public void subscribeInstrument(Ticker ticker) {
        socket.subscribe(buildSubscribeCommand("instrument:" + ticker.getSymbol()));
    }

    @Override
    public void subscribeFunding(Ticker ticker) {
        socket.subscribe(buildSubscribeCommand("funding:" + ticker.getSymbol()));
    }

    @Override
    public void subscribeQuotes(Ticker ticker, IQuoteListener listener) {
        messageProcessor.addQuoteListener(listener);
        if (!subscribedQuoteTickers.contains(ticker.getSymbol())) {
            subscribedQuoteTickers.add(ticker.getSymbol());
            socket.subscribe(buildSubscribeCommand("quote:" + ticker.getSymbol()));
        }

    }

    @Override
    public void subscribeOrderBook(Ticker ticker) {
        socket.subscribe(buildSubscribeCommand("orderBookL2:" + ticker.getSymbol()));
    }

    @Override
    public void subscribeTrades(Ticker ticker, ITradeListener listener) {
        messageProcessor.addTradeListener(listener);
        if (!subscribedTradeTickers.contains(ticker.getSymbol())) {
            subscribedTradeTickers.add(ticker.getSymbol());
            socket.subscribe(buildSubscribeCommand("trade:" + ticker.getSymbol()));
        }
    }

    protected void authenticate(String apiKey, long nonce, String signature) {
        socket.subscribe(buildAuthKeyCommand(apiKey, nonce, signature));
    }

    protected String buildAuthKeyCommand(String apiKey, long nonce, String signature) {
        return buildCommandJson("authKey", apiKey, nonce, signature);
    }

    protected String buildSubscribeCommand(String... args) {
        String command = buildCommandJson("subscribe", args);
        subscribeCommandList.add(command);
        return command;
    }

    protected String buildCommandJson(String command, Object... args) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"op\": \"")
                .append(command)
                .append("\", \"args\": [");
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                sb.append("\"");
            }
            sb.append(args[i]);
            if (args[i] instanceof String) {
                sb.append("\"");
            }
            if (i == args.length - 1) {
                sb.append("]");
            } else {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void quoteUpdated(BitmexQuote quoteData) {
    }

    @Override
    public String getApiSignature(String apiKey, long nonce) {

        String keyString = "GET" + "/realtime" + nonce;

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(apiKey.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            String hash = DatatypeConverter.printHexBinary(sha256_HMAC.doFinal(keyString.getBytes()));
            return hash;
        } catch (Exception e) {
            throw new SumZeroException(e);
        }
    }
    
    public int getMessageProcessorCount() {
        return messageProcessor.getQueueSize();
               
    }

    
    protected IMessageProcessor buildMessageProcessor() {
        return  new WebsocketMessageProcessor();
    }
    
    protected JettySocket buildJettySocket() {
        return new JettySocket(latch, messageProcessor, this);
    }
    
}
