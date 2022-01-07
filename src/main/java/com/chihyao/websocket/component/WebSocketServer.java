package com.chihyao.websocket.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Slf4j
@ServerEndpoint("/api/websocket/{sid}")
public class WebSocketServer {

    private static int onlineCount = 0;
    private static CopyOnWriteArraySet<WebSocketServer> webSocketServers = new CopyOnWriteArraySet<>();

    private Session session;
    private String sid = "";

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        webSocketServers.add(this);     //加入set中
        this.sid = sid;
        addOnlineCount();           //線上數加1
        try {
            sendMessage("conn_success");
            log.info("有新視窗開始監聽:" + sid + ",當前線上人數為:" + getOnlineCount());
        } catch (IOException e) {
            log.error("websocket IO Exception");
        }
    }

    @OnClose
    public void onClose() {
        webSocketServers.remove(this);  //從set中刪除
        subOnlineCount();           //線上數減1
        //斷開連線情況下，更新主機板佔用情況為釋放
        log.info("釋放的sid為："+sid);
        //這裡寫你 釋放的時候，要處理的業務
        log.info("有一連線關閉！當前線上人數為" + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到來自視窗" + sid + "的資訊:" + message);
        //群發訊息
        for (WebSocketServer item : webSocketServers) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("發生錯誤");
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static void sendInfo(String message, @PathParam("sid") String sid) throws IOException {
        log.info("推播訊息到視窗" + sid + "，推播內容:" + message);

        for (WebSocketServer item : webSocketServers) {
            try {
                //這裡可以設定只推播給這個sid的，為null則全部推播
                if (sid == null) {
                    item.sendMessage(message);
                } else if (item.sid.equals(sid)) {
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    public static CopyOnWriteArraySet<WebSocketServer> getWebSocketServers() {
        return webSocketServers;
    }
}
