# websocket 簡介

在WebSocket API中，瀏覽器和伺服器只需要做一個握手的動作，然後瀏覽器和伺服器之間就形成了一條快速通道，兩者之間就直接可以資料互相傳送。

瀏覽器通過JavaScript向伺服器發出建立WebSocket連接的請求，連接建立以後，客戶端和伺服器端就可以通過TCP連接直接交換資料。

當獲取Web Socket連接後，你可以通過send()方法來向伺服器發送資料，並通過onmessage事件來接收伺服器返回的資料。

WebSocketConfig 為啟用配置

WebSocket 事件分為

@OnOpen	  連接建立時觸發

@OnMessage	客戶端接收服務端資料時觸發

@OnError	通信發生錯誤時觸發

@OnClose	連接關閉時觸發

因為WebSocket是類似使用者端伺服器端的形式(採用ws協定)，那麼這裡的WebSocketServer其實就相當於一個ws協定的Controller。

@ ServerEndpoint 註解是一個類層次的註解，它的功能主要是將目前的類定義成一個websocket伺服器端，

註解的值將被用於監聽使用者連線的終端存取URL地址，使用者端可以通過這個URL來連線到WebSocket伺服器端。

新建一個CopyOnWriteArraySet(或者ConcurrentHashMap) webSocketServers 用於接收當前user session的WebSocket，方便傳遞之間對user進行推播訊息。

sid為group的label，推播可針對各群或全體，使用session.getBasicRemote().sendText(message)即可主動發訊息給client端。

WebSocket線上測試工具
http://www.websocket-test.com/
