package com.liuvenking.springintejms.websocket.handler;


import org.apache.log4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;



/**
 * Created by venking on 15/11/5.
 */
public class WebsocketEndPoint extends TextWebSocketHandler{

    private  static Logger logger=Logger.getLogger(WebsocketEndPoint.class);
    @Override
    protected void handleTextMessage(WebSocketSession webSocketSession,TextMessage message) throws Exception {
        logger.info("get a message from client:" + message);
        super.handleTextMessage(webSocketSession,message);
        TextMessage textMessage=new TextMessage(message.getPayload()+" received at server");
        webSocketSession.sendMessage(textMessage);
    }
}
