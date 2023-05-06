package com.makedreamteam.capstoneback;

import com.google.gson.Gson;
import com.makedreamteam.capstoneback.domain.TeamMember;
import com.makedreamteam.capstoneback.repository.TeamMemberRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.io.IOException;
import java.util.*;

@Configuration
@EnableWebSocket
@CrossOrigin
@EnableAutoConfiguration
@ComponentScan("com.makedreamteam.capstoneback")
public class WebSocketConfig implements WebSocketConfigurer {
    private static final Map<UUID, WebSocketSession> sessions = new HashMap<>();
    private static final Map<UUID, List<UUID>> teamChatRooms = new HashMap<>();
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(), "/sock").setAllowedOrigins("*");
    }


    public class MyWebSocketHandler implements WebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            System.out.println("WebSocket connection established");
            String uuid = UUID.randomUUID().toString();
            session.sendMessage(new TextMessage("{\"type\":\"uuid\",\"uuid\":\"" + uuid + "\",\"message\" : \"어서오세요\"}"));


        }

        @Override
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
            if (message.getPayload() instanceof String) {
                String payload = (String) message.getPayload();
                System.out.println("Received message: " + payload);

                // Check if the payload is an authentication message
                if (payload.startsWith("AUTH:")) {
                    String token = payload.substring(5);
                    Claims userInfo = jwtTokenProvider.getClaimsToken(token);
                    UUID userId = UUID.fromString((String) userInfo.get("userId"));
                    System.out.println("Received authentication message for user " + userId);
                    if (!sessions.containsValue(session))
                        sessions.put(userId, session);
                    System.out.println("-----Add session---- ");
                    System.out.println("key : " + userId);
                    System.out.println("sessionId : " + session.getId());
                    //팀 채팅 추가
                    List<UUID> teams = teamMemberRepository.getTeams(userId);
                    if (teams.size() > 0)
                        for (UUID teamId : teams) {
                            if (teamChatRooms.get(teamId)==null) {
                                List<UUID> userIds = new ArrayList<>();
                                userIds.add(userId);
                                teamChatRooms.put(teamId, userIds);
                            } else {
                                List<UUID> userIds = teamChatRooms.get(teamId);
                                userIds.add(userId);
                                teamChatRooms.put(teamId, userIds);
                            }
                        }


                } else if (payload.startsWith("ROOM:")) {
                    UUID teamId = UUID.fromString(payload.substring(5, payload.indexOf(" ")).trim());
                    String msg = payload.substring(payload.indexOf(" ") + 1);
                    List<UUID> uuids = teamChatRooms.get(teamId);
                    for (UUID userId : uuids) {
                        sendNotificationToUser(userId, msg);
                    }
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
            UUID userId = getUserIdFromSession(session);
            if (userId != null) {
                sessions.remove(userId);
                deleteUserFromChatRoom(userId);
                System.out.println("Removed session for user " + userId);
            } else {
                System.out.println("Could not find user ID for session " + session.getId());
            }
        }

        public static void sendNotificationToUser(UUID userId) throws IOException {
            System.out.println(userId + "에게 알람을 보냅니다");
            WebSocketSession session = sessions.get(userId);
            if (session != null) {
                Gson gson = new Gson();
                Map<String, String> payloadMap = new HashMap<>();
                payloadMap.put("type", "notification");
                payloadMap.put("message", "팀원 신청이 왔습니다.");
                String payload = gson.toJson(payloadMap);
                //String payload = "{\"type\":\"notification\",\"message\" : \"팀원신청이 들어왔습니다.\"}";
                TextMessage message = new TextMessage(payload);
                session.sendMessage(message);
            } else {
                System.out.println("Could not find session for user " + userId);
            }
        }

        public static void sendNotificationToUser(UUID userId, String msg) throws IOException {
            System.out.println(userId + "에게 알람을 보냅니다");
            WebSocketSession session = sessions.get(userId);
            if (session != null) {
                Gson gson = new Gson();
                Map<String, String> payloadMap = new HashMap<>();
                payloadMap.put("type", "message");
                payloadMap.put("message", msg);
                String payload = gson.toJson(payloadMap);
                //String payload = "{\"type\":\"notification\",\"message\" : \"팀원신청이 들어왔습니다.\"}";
                TextMessage message = new TextMessage(payload);
                session.sendMessage(message);
            } else {
                System.out.println("Could not find session for user " + userId);
            }
        }

        private UUID getUserIdFromSession(WebSocketSession session) {
            for (Map.Entry<UUID, WebSocketSession> entry : sessions.entrySet()) {
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

        public void deleteUserFromChatRoom(UUID userId) {
            for (List<UUID> uuidList : teamChatRooms.values()) {
                uuidList.remove(userId);
            }
        }
    }
}
