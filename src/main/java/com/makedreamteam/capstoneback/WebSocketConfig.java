package com.makedreamteam.capstoneback;

import com.google.gson.Gson;
import com.makedreamteam.capstoneback.domain.TeamMember;
import com.makedreamteam.capstoneback.domain.WaitingListOfMatchingTeamToUser;
import com.makedreamteam.capstoneback.form.WebSocketSessionList;
import com.makedreamteam.capstoneback.repository.TeamMemberRepository;
import com.makedreamteam.capstoneback.repository.WaitingListTeamToUserRepository;
import com.makedreamteam.capstoneback.repository.WaitingListUserToTeamRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ansi.AnsiOutput;
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
    private static final Map<UUID, WebSocketSessionList> sessions = new HashMap<>();

    private static final Map<UUID, List<WebSocketSession>> isEnterd = new HashMap<>();

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private WaitingListTeamToUserRepository waitingListTeamToUserRepository;
    @Autowired
    private WaitingListUserToTeamRepository waitingListUserToTeamRepository;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(), "/sock").setAllowedOrigins("*");
    }


    public class MyWebSocketHandler implements WebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {

            String uuid = UUID.randomUUID().toString();
            session.sendMessage(new TextMessage("{\"type\":\"uuid\",\"uuid\":\"" + uuid + "\",\"welcome\" : \"어서오세요\"}"));


        }

        @Override
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
            if (message.getPayload() instanceof String) {
                String payload = (String) message.getPayload();


                // Check if the payload is an authentication message
                if (payload.startsWith("AUTH:")) {
                    String token = payload.substring(5);
                    Claims userInfo = jwtTokenProvider.getClaimsToken(token);
                    UUID userId = UUID.fromString((String) userInfo.get("userId"));

                    //sessions 에 session을 추가
                    if (sessions.get(userId) == null || !sessions.get(userId).isContains(session)) {
                        WebSocketSessionList webSocketSession = new WebSocketSessionList();
                        webSocketSession.setUserId(userId);
                        webSocketSession.setSession(session);
                        sessions.put(userId, webSocketSession);
                    } else {
                        sessions.get(userId).setSession(session);
                    }


                } else if (payload.startsWith("ROOM:")) {
                    UUID where = UUID.fromString(payload.substring(5).split("##")[0]);
                    UUID to = UUID.fromString(payload.substring(5).split("##")[1]);
                    sendTeamChatToAnother(session, payload.substring(5).split("##")[2], where, to);
                    System.out.println(payload.substring(5));
                } else if (payload.startsWith("enterRoom:")) {
                    UUID waitingId = UUID.fromString(payload.substring(10).split("##")[0]);
                    String token = payload.substring(10).split("##")[1];
                    UUID userId = jwtTokenProvider.getUserId(token);
                    //isEnterd에 session 추가
                    if (sessions.get(userId) == null) {
                        WebSocketSessionList webSocketSession = new WebSocketSessionList();
                        webSocketSession.setUserId(userId);
                        webSocketSession.addSession(session);
                        sessions.put(userId, webSocketSession);
                    } else {
                        System.out.println("cnrncrnkcnrkcnrknckr");
                        sessions.get(userId).addSession(session);
                    }
                    if (isEnterd.get(waitingId) == null) {
                        List<WebSocketSession> sessionList = new ArrayList<>();
                        sessionList.add(session);
                        isEnterd.put(waitingId, sessionList);
                    } else {
                        isEnterd.get(waitingId).add(session);
                    }
                } else {

                }
            } else {

            }

        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            System.err.println("WebSocket transport error: " + exception.getMessage());
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
            System.out.println("세션을 삭제합니다.");

            System.out.println("closeStatus : " + closeStatus.getCode());
            UUID userId = getUserIdFromSession(session);

            if (userId != null) {

                sessions.get(userId).deleteSession(session);
                deleteUserFromChatRoom(session);

            } else {
                System.out.println("문제문제문제문제문제문제문제문제문제문제문제문제");
            }

        }


        public static void sendNotificationToUser(UUID userId,String msg) throws IOException {
            if(sessions.get(userId)!=null) {
                WebSocketSession session = sessions.get(userId).getSession();
                if (session != null) {
                    Gson gson = new Gson();
                    Map<String, String> payloadMap = new HashMap<>();
                    payloadMap.put("type", "notification");
                    payloadMap.put("message", msg);
                    String payload = gson.toJson(payloadMap);

                    TextMessage message = new TextMessage(payload);
                    session.sendMessage(message);
                } else {
                    System.out.println("오프라인");
                }
            }
        }

        public static void sendTeamChatToAnother(WebSocketSession sendUser, String msg, UUID where, UUID to) throws IOException {
            List<WebSocketSession> userList = isEnterd.get(where);
            if (isEnterd.get(where) != null && sessions.get(to) != null && sessions.get(to).getRoomSessions() != null) {
                boolean isContained = false;
                for (WebSocketSession user : userList) {
                    System.out.println("for문 들어왔다");
                    if (user != sendUser) {
                        System.out.println("user != sendUser");
                        if (isEnterd.get(where).contains(user) || sessions.get(to).getRoomSessions().contains(user)) {
                            System.out.println("isEnterd.get(where).contains(user) || sessions.get(to).getRoomSessions().contains(user)");
                            Gson gson = new Gson();
                            Map<String, String> payloadMap = new HashMap<>();
                            payloadMap.put("type", "message");
                            payloadMap.put("message", "a:" + msg);
                            String payload = gson.toJson(payloadMap);
                            TextMessage message = new TextMessage(payload);
                            user.sendMessage(message);
                            isContained = true;
                        }
                    }
                }
                if (!isContained) {
                    if (sessions.get(to) != null && sessions.get(to).getSession() != null) {
                        Gson gson = new Gson();
                        Map<String, String> payloadMap = new HashMap<>();
                        payloadMap.put("type", "notificationFromChat");
                        payloadMap.put("message", "신청 팀으로부터 채팅이 왔습니다");
                        payloadMap.put("waitingId", where.toString());
                        payloadMap.put("teamLeader", getUserIdFromSession(sendUser).toString());
                        String payload = gson.toJson(payloadMap);
                        TextMessage message = new TextMessage(payload);
                        sessions.get(to).getSession().sendMessage(message);
                    }
                }
            } else {
                if (sessions.get(to) != null && sessions.get(to).getSession() != null) {
                    Gson gson = new Gson();
                    Map<String, String> payloadMap = new HashMap<>();
                    payloadMap.put("type", "notificationFromChat");
                    payloadMap.put("message", "신청 팀으로부터 채팅이 왔습니다");
                    payloadMap.put("waitingId", where.toString());
                    payloadMap.put("teamLeader", getUserIdFromSession(sendUser).toString());
                    String payload = gson.toJson(payloadMap);
                    TextMessage message = new TextMessage(payload);
                    sessions.get(to).getSession().sendMessage(message);
                }
            }


        }


        private static UUID getUserIdFromSession(WebSocketSession session) {
            for (WebSocketSessionList entry : sessions.values()) {
                if (entry.isContains(session)) {
                    System.out.println("해당하는 세션있습니다,유저id를 반환합니다. 삭제합니다");
                    return entry.getUserId();
                }
            }
            System.out.println("해당하는 세션이 없습니다");
            return null;
        }


        @Override
        public boolean supportsPartialMessages() {
            return false;
        }

        public void deleteUserFromChatRoom(WebSocketSession session) {
            for (List<WebSocketSession> list : isEnterd.values()) {
                if (list.contains(session))
                    list.remove(session);
            }
        }
    }
}
