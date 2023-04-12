package com.makedreamteam.capstoneback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.*;

@Configuration
@EnableWebSocket
@CrossOrigin
@EnableAutoConfiguration
public class WebSocketConfig implements WebSocketConfigurer {
    private static final Map<String,WebSocketSession> sessions = new HashMap<>();

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(), "/sock").setAllowedOrigins("*");
    }



    public static class MyWebSocketHandler implements WebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            System.out.println("WebSocket connection established");
            String uuid = UUID.randomUUID().toString();
            session.sendMessage(new TextMessage("{\"type\":\"uuid\",\"uuid\":\"" + uuid + "\",\"message\" : \"good\"}"));


        }

        @Override
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
            if (message.getPayload() instanceof String) {
                String payload = (String) message.getPayload();
                System.out.println("Received message: " + payload);

                // Check if the payload is an authentication message
                if (payload.startsWith("AUTH:")) {
                    String userId = payload.substring(5);
                    System.out.println("Received authentication message for user " + userId);
                    if(!sessions.containsValue(session))
                        sessions.put(userId, session);
                    System.out.println("-----Add session---- ");
                    System.out.println("key : "+userId);
                    System.out.println("sessionId : "+ session.getId());
                } else {
                    // Send a response message back to the client
                    String responsePayload = "{\"text\" : \"dsfsdf\"}";
                    TextMessage response = new TextMessage(responsePayload);
                    session.sendMessage(response);
                }
            } else {
                System.out.println("Received message of unknown type: " + message);
            }

        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            System.err.println("WebSocket transport error: " + exception.getMessage());
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
            System.out.println("WebSocket connection closed with status " + closeStatus.getCode());
            String userId = getUserIdFromSession(session);
            if (userId != null) {
                sessions.remove(userId);
                System.out.println("Removed session for user " + userId);
            } else {
                System.out.println("Could not find user ID for session " + session.getId());
            }
        }

        private String getUserIdFromSession(WebSocketSession session) {
            for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
                if (entry.getValue().equals(session)) {
                    return entry.getKey();
                }
            }
            return null;
        }


        @Override
        public boolean supportsPartialMessages() {
            return false;
        }

    }
}