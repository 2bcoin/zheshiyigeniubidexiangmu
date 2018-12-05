package com.github.misterchangray.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.controller.common.vo.SocketSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@ServerEndpoint("/v1/ws")
public class SocketController {
    Logger logger = LoggerFactory.getLogger(SocketController.class);
    //线程安全的静态变量，表示在线连接数
    private static AtomicLong onlineCount = new AtomicLong(0);

    //若要实现服务端与指定客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    public static ConcurrentHashMap<String, CopyOnWriteArrayList<SocketSession>> subscribeSession = new ConcurrentHashMap<String, CopyOnWriteArrayList<SocketSession>>();


    public static void clear() {
        for(String sub : subscribeSession.keySet()) {
            List<SocketSession> socketSessions = subscribeSession.get(sub);
            if(null == socketSessions || socketSessions.size() == 0) continue;

            for(SocketSession socketSession : socketSessions) {
                if(null != socketSession && null != socketSession.getSession() &&
                        false == socketSession.getSession().isOpen()) socketSessions.remove(socketSession);
            }
        }
    }
    /**
     * 连接建立成功调用的方法
     *
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session){
        onlineCount.incrementAndGet();    //在线数加1
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session closeSession) {
        onlineCount.decrementAndGet();          //在线数减1
        clear();
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param mySession 可选的参数
     * @throws Exception
     */
    @OnMessage
    public void onMessage(String message,Session mySession) throws Exception {
        JsonNode jsonNode = JSONUtils.buildJsonNode(message);
        JsonNode sub = jsonNode.get("subscribe");
        if(null != sub) {
            String substr = jsonNode.get("subscribe").asText();
            if(null != substr && !"".equals(substr)) {
                CopyOnWriteArrayList<SocketSession> tmp = subscribeSession.get(substr);
                if(null == tmp) {
                    tmp = new CopyOnWriteArrayList<>();
                }
                tmp.add( new SocketSession(substr, mySession));
                subscribeSession.put(substr,tmp);
            }
        }
    }


    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        // error.printStackTrace();
    }


    //给所有客户端发送信息
    public void sendAllMessage(String message) throws IOException {

    }

    //定向发送信息
    public void sendMessage(Session mySession,String message) throws IOException {
        synchronized(this) {try {
            if(mySession.isOpen()){//该session如果已被删除，则不执行发送请求，防止报错
                //this.session.getBasicRemote().sendText(message);
                mySession.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        }

    }

}
