/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.misterchangray.libs.bitmex.client;

import com.github.misterchangray.libs.bitmex.data.SumZeroException;
import com.github.misterchangray.libs.bitmex.listener.IPongListener;
import com.github.misterchangray.libs.bitmex.listener.WebsocketDisconnectListener;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 *
 * @author RobTerpilowski
 */
@WebSocket(maxTextMessageSize = (64 * 1024 * 100), maxBinaryMessageSize = -1)
//@WebSocket
public class JettySocket implements IPongListener {

    protected final long MAX_PONG_TIME_SECONDS = 20;
    
    protected Logger logger = Logger.getLogger(JettySocket.class);
    protected final CountDownLatch closeLatch;
    protected boolean connected = false;
    protected volatile boolean shouldRun = true;
    protected Gson gson = new Gson();
    protected JsonParser parser = new JsonParser();
    protected IMessageProcessor messageProcessor;
    protected WebsocketDisconnectListener disconnectListener = null;
    protected Timer pingPongTimer;
    protected long lastPongTime = 0;
    


    @SuppressWarnings("unused")
    protected Session session;
    
    //Used by unit tests
    protected JettySocket() {
        this.closeLatch = null;
    }

    public JettySocket(CountDownLatch latch, IMessageProcessor messageProcessor) {
        this(latch, messageProcessor, null);
    }
    
    public JettySocket(CountDownLatch latch, IMessageProcessor messageProcessor, WebsocketDisconnectListener disconnectListener) {
        this.closeLatch = latch;
        this.messageProcessor = messageProcessor;
        this.disconnectListener = disconnectListener;
        pingPongTimer = getPingPongTimer();
        pingPongTimer.scheduleAtFixedRate(getPingPongTimerTask(), 0, 10000);
    }    

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return this.closeLatch.await(duration, unit);
    }
    
    
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        logger.error("Connection Closed: code: " + statusCode + " reason: " + reason);
        shouldRun = false;
        this.session = null;
        connected = false;
        if( disconnectListener != null ) {
            disconnectListener.socketDisconnectDetected();
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        connected = true;
        closeLatch.countDown();
        startPing();
    }

    public void startPing() {
        final String pingCommand = "ping";
        Thread thread = new Thread(() -> {
            while (shouldRun) {
                try {
                    if (session != null) {
                        Future<Void> fut = session.getRemote().sendStringByFuture(pingCommand);
                        logger.info("Sending ping");
                    }
                    Thread.sleep(10000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        logger.info("msg: " + msg);
        messageProcessor.processMessage(msg);
    }

    @Override
    public void pongReceived() {
        lastPongTime = getCurrentTime();
    }

    
    
    public boolean isConnected() {
        return connected;
    }

    public void subscribe(String message) {
        logger.info("Sending command: " + message);
        Future<Void> fut = session.getRemote().sendStringByFuture(message);
        try {
            fut.get(2, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new SumZeroException(ex);
        }
    }
    
    protected Timer getPingPongTimer() {
        return new Timer("PingPongTimer", true);
    }
    
    protected TimerTask getPingPongTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    checkLastPongTime();
                } catch( Exception ex ) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        };
    }

    
    protected void checkLastPongTime() {
        if( lastPongTime > 0 ) {
            long responseDelay = getCurrentTime() - lastPongTime;
            if( responseDelay >= (MAX_PONG_TIME_SECONDS * 1000) ) {
                logger.error("Pong not detected in " + responseDelay + "ms");
                disconnectListener.socketDisconnectDetected();
            }
        } else {
            logger.info("Last pong time has not been set");
        }
    }
    
    protected long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
