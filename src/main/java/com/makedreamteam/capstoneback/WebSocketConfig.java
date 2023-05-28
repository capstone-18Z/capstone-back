package com.makedreamteam.capstoneback;

import com.google.gson.Gson;
import com.makedreamteam.capstoneback.domain.Chat;
import com.makedreamteam.capstoneback.domain.Notification;
import com.makedreamteam.capstoneback.domain.TeamMember;
import com.makedreamteam.capstoneback.domain.WaitingListOfMatchingTeamToUser;
import com.makedreamteam.capstoneback.form.WebSocketSessionList;
import com.makedreamteam.capstoneback.repository.*;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;
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

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private NotificationRepository notificationRepository;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(), "/sock").setAllowedOrigins("*");
    }


    @Component
    public class MyWebSocketHandler implements WebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {

            String uuid = UUID.randomUUID().toString();
            session.sendMessage(new TextMessage("{\"type\":\"uuid\",\"uuid\":\"" + uuid + "\",\"welcome\" : \"어서오세요\"}"));
            System.out.println("웹소켓 접속 : "+session.toString());

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
                    System.out.println("아아 : " + payload.substring(5).split("##")[1]);
                    UUID to = UUID.fromString(payload.substring(5).split("##")[1]);
                    String nickName = payload.substring(5).split("##")[3];
                    String mode = payload.substring(5).split("##")[4];
                    sendTeamChatToAnother(session, payload.substring(5).split("##")[2], where, to, nickName, mode);
                    System.out.println(payload.substring(5));
                } else if (payload.startsWith("TEAM:")) {
                    UUID where = UUID.fromString(payload.substring(5).split("##")[0]);
                    String msg = payload.substring(5).split("##")[1];
                    String nickName = payload.substring(5).split("##")[2];
                    String mode = payload.substring(5).split("##")[3];
                    sendTeamChatToAnother(session,msg,where,nickName,mode);
                } else if (payload.startsWith("enterRoom:")) {
                    UUID waitingId = UUID.fromString(payload.substring(10).split("##")[0]);
                    String token = payload.substring(10).split("##")[1];
                    String nickname = payload.substring(10).split("##")[2];
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
                    sendEnterNotifocationTeamChat(nickname + "님이 입장했습니다", nickname, waitingId, userId);
                } else if (payload.startsWith("exitRoom:")) {
                    UUID waitingId = UUID.fromString(payload.substring(9).split("##")[0]);
                    String token = payload.substring(9).split("##")[1];
                    String nickname = payload.substring(9).split("##")[2];
                    UUID userId = jwtTokenProvider.getUserId(token);
                    sendExitNotifocationTeamChat(nickname + "님이 퇴장했습니다", nickname, waitingId, userId);
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


        public void sendNotificationToUser(UUID userId, String msg) throws IOException {
            if (sessions.get(userId) != null) {
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
                    Gson gson = new Gson();
                    Map<String, String> payloadMap = new HashMap<>();
                    payloadMap.put("type", "notification");
                    payloadMap.put("message", msg);
                    String payload = gson.toJson(payloadMap);
                    Notification notification = new Notification();
                    notification.setMsg(gson.toJson(payloadMap));
                    notification.setUserId(userId);
                    notificationRepository.save(notification);
                }
            } else {
                Gson gson = new Gson();
                Map<String, String> payloadMap = new HashMap<>();
                payloadMap.put("type", "notification");
                payloadMap.put("message", msg);
                String payload = gson.toJson(payloadMap);
                Notification notification = new Notification();
                notification.setMsg(gson.toJson(payloadMap));
                notification.setUserId(userId);
                notificationRepository.save(notification);
            }
        }

        public void sendTeamChatToAnother(WebSocketSession sendUser, String msg, UUID where, UUID to, String nickName, String mode) throws IOException {
            System.out.println("into sendTeamChat");
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
                            payloadMap.put("message", "a:" + " " + nickName + " " + msg);
                            payloadMap.put("nickname", nickName);
                            String payload = gson.toJson(payloadMap);
                            TextMessage message = new TextMessage(payload);
                            user.sendMessage(message);
                            Chat chat = Chat.builder().from(getUserIdFromSession(sendUser)).mode("chat").to(to).room(where).date(new Date()).msg("a:" + " " + nickName + " " + msg).build();
                            chatRepository.save(chat);
                            isContained = true;
                        }
                    } else if (userList.size() == 1 && user == sendUser) {
                        System.out.println("userList.size()==1 && user == sendUser");
                        Chat chat = Chat.builder().from(getUserIdFromSession(sendUser)).mode("chat").to(getUserIdFromSession(sendUser)).room(where).date(new Date()).msg("m:" + " " + nickName + " " + msg).build();
                        Chat chat2 = Chat.builder().from(getUserIdFromSession(sendUser)).mode("chat").to(to).room(where).date(new Date()).msg("a:" + " " + nickName + " " + msg).build();
                        chatRepository.save(chat);
                        chatRepository.save(chat2);
                    } else if (userList.size() > 1 && user == sendUser) {
                        System.out.println("userList.size()>1 && user == sendUser");
                        Chat chat = Chat.builder().from(getUserIdFromSession(sendUser)).mode("chat").to(getUserIdFromSession(sendUser)).room(where).date(new Date()).msg("m:" + " " + nickName + " " + msg).build();
                        chatRepository.save(chat);
                    }
                }
                if (!isContained) {
                    if (sessions.get(to) != null && sessions.get(to).getSession() != null) {
                        System.out.println("위에꺼");
                        Gson gson = new Gson();
                        Map<String, String> payloadMap = new HashMap<>();
                        payloadMap.put("type", "notificationFromChat");
                        payloadMap.put("userId", getUserIdFromSession(sendUser).toString());
                        payloadMap.put("mode", mode);
                        payloadMap.put("waitingId", where.toString());
                        payloadMap.put("teamLeader", getUserIdFromSession(sendUser).toString());
                        if (mode.equals("team")) {
                            payloadMap.put("message", "신청 팀으로부터 채팅이 왔습니다");
                            Notification notification = new Notification();
                            notification.setMsg(gson.toJson(payloadMap));
                            notification.setUserId(to);
                            notificationRepository.save(notification);
                        }
                        if (mode.equals("user")) {
                            payloadMap.put("message", "유저로부터 채팅이 왔습니다");
                            Notification notification = new Notification();
                            notification.setMsg(gson.toJson(payloadMap));
                            notification.setUserId(to);
                            notificationRepository.save(notification);
                        }
                        String payload = gson.toJson(payloadMap);
                        TextMessage message = new TextMessage(payload);
                        sessions.get(to).getSession().sendMessage(message);
                    }
                }
            } else {
                if (sessions.get(to) != null && sessions.get(to).getSession() != null) {
                    System.out.println("아래꺼 ");
                    Gson gson = new Gson();
                    Map<String, String> payloadMap = new HashMap<>();
                    payloadMap.put("type", "notificationFromChat");
                    payloadMap.put("mode", mode);
                    payloadMap.put("waitingId", where.toString());
                    payloadMap.put("teamLeader", getUserIdFromSession(sendUser).toString());
                    if (mode.equals("team")) {
                        payloadMap.put("message", "신청 팀으로부터 채팅이 왔습니다");
                        Notification notification = new Notification();
                        notification.setMsg(gson.toJson(payloadMap));
                        notification.setUserId(to);
                        notificationRepository.save(notification);
                    }
                    if (mode.equals("user")) {
                        payloadMap.put("message", "유저로부터 채팅이 왔습니다");
                        Notification notification = new Notification();
                        notification.setMsg(gson.toJson(payloadMap));
                        notification.setUserId(to);
                        notificationRepository.save(notification);
                    }
                    String payload = gson.toJson(payloadMap);
                    TextMessage message = new TextMessage(payload);
                    sessions.get(to).getSession().sendMessage(message);
                    Chat chat = Chat.builder().from(getUserIdFromSession(sendUser)).mode("chat").to(getUserIdFromSession(sendUser)).room(where).date(new Date()).msg("m:" + " " + nickName + " " + msg).build();
                    Chat chat2 = Chat.builder().from(getUserIdFromSession(sendUser)).mode("chat").to(to).room(where).date(new Date()).msg("a:" + " " + nickName + " " + msg).build();
                    chatRepository.save(chat);
                    chatRepository.save(chat2);

                }
                else{
                    Chat chat = Chat.builder().from(getUserIdFromSession(sendUser)).mode("chat").to(getUserIdFromSession(sendUser)).room(where).date(new Date()).msg("m:" + " " + nickName + " " + msg).build();
                    Chat chat2 = Chat.builder().from(getUserIdFromSession(sendUser)).mode("chat").to(to).room(where).date(new Date()).msg("a:" + " " + nickName + " " + msg).build();
                    chatRepository.save(chat);
                    chatRepository.save(chat2);
                }
            }
        }

        public void sendTeamChatToAnother(WebSocketSession sendUser, String msg, UUID where, String nickName, String mode) throws IOException {
            List<WebSocketSession> userList = isEnterd.get(where);
            if (userList != null) {
                boolean isContained = false;
                Chat chat = Chat.builder().from(getUserIdFromSession(sendUser)).mode("chat").room(where).date(new Date()).msg(nickName + " " + msg).build();
                chatRepository.save(chat);
                for (WebSocketSession user : userList) {
                    if (user != sendUser) {
                        Gson gson = new Gson();
                        Map<String, String> payloadMap = new HashMap<>();
                        payloadMap.put("type", "message");
                        payloadMap.put("message", "a: "+nickName + " " + msg);
                        payloadMap.put("nickname", nickName);
                        String payload = gson.toJson(payloadMap);
                        TextMessage message = new TextMessage(payload);
                        user.sendMessage(message);
                        isContained = true;
                    }
                }

            }

        }

        public void sendEnterNotifocationTeamChat(String msg, String nickname, UUID where, UUID userId) throws IOException {
            List<WebSocketSession> userList = isEnterd.get(where);
            if (userList != null) {
                Chat chat = Chat.builder().mode("noti").room(where).date(new Date()).msg("n:" + " " + nickname + " " + msg).build();
                chatRepository.save(chat);
                for (WebSocketSession user : userList) {
                    System.out.println("isEnterd.get(where).contains(user) || sessions.get(to).getRoomSessions().contains(user)");
                    Gson gson = new Gson();
                    Map<String, String> payloadMap = new HashMap<>();
                    payloadMap.put("type", "enter");
                    payloadMap.put("message", "n:" + " " + nickname + " " + msg);
                    payloadMap.put("checkUser", userId.toString());
                    String payload = gson.toJson(payloadMap);

                    TextMessage message = new TextMessage(payload);
                    user.sendMessage(message);
                }
            }
        }

        public void sendExitNotifocationTeamChat(String msg, String nickname, UUID where, UUID userId) throws IOException {
            System.out.println("exit");
            List<WebSocketSession> userList = isEnterd.get(where);
            if (userList != null) {
                Chat chat = Chat.builder().mode("noti").room(where).date(new Date()).msg("n:" + " " + nickname + " " + msg).build();
                chatRepository.save(chat);
                for (WebSocketSession user : userList) {

                    Gson gson = new Gson();
                    Map<String, String> payloadMap = new HashMap<>();
                    payloadMap.put("type", "enter");
                    payloadMap.put("message", "n:" + " " + nickname + " " + msg);
                    payloadMap.put("checkUser", userId.toString());
                    String payload = gson.toJson(payloadMap);
                    TextMessage message = new TextMessage(payload);
                    user.sendMessage(message);
                }
            } else {
                System.out.println("userList==null");
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
