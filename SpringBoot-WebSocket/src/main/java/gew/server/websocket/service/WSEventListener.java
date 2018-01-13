package gew.server.websocket.service;


import gew.server.websocket.entity.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WSEventListener {


    private static final Logger logger = LoggerFactory.getLogger(WSEventListener.class);

    @Autowired
    private SimpMessageSendingOperations sendingOperations;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        WebSocketStorage socketStorage = WebSocketStorage.getInstance();
        int count = socketStorage.increaseClientAndGet();

        logger.info("-> New User Connected... Current [{}] Connected Clients!", count);
    }


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        WebSocketStorage socketStorage = WebSocketStorage.getInstance();
        int count = socketStorage.decreaseClientAndGet();

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username != null) {
            logger.info("-> User [{}] Disconnected... Current [{}] Connected Clients!", username, count);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);

            sendingOperations.convertAndSend("/topic/public", chatMessage);
            socketStorage.deleteClient(sessionId);
        }
    }
}
