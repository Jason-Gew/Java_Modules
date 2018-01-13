package gew.server.websocket.controller;

import gew.server.websocket.entity.ChatMessage;
import gew.server.websocket.entity.ClientInfo;
import gew.server.websocket.service.WebSocketStorage;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.time.ZonedDateTime;
import java.util.Map;


@RestController
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage)
    {
        return chatMessage;
    }


    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor)
    {
        ZonedDateTime now = ZonedDateTime.now();
        WebSocketStorage socketStorage = WebSocketStorage.getInstance();

        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();
        if(attributes.containsKey("username"))
            logger.info("--> User [{}] Connected... ",  attributes.get("username"));
        else
            logger.info("--> Get WebSocket Message: ",  attributes);

        ClientInfo newClient = new ClientInfo(chatMessage.getSender(), headerAccessor.getSessionId(), now.toString());
        boolean addStatus = socketStorage.addClient(headerAccessor.getSessionId(), newClient);
        return chatMessage;
    }

}
