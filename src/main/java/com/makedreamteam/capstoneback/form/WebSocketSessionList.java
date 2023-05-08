package com.makedreamteam.capstoneback.form;

import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebSocketSessionList {
    private WebSocketSession session;
    private List<WebSocketSession> roomSessions=new ArrayList<>();
    private UUID userId;
    public void addSession(WebSocketSession session){
        roomSessions.add(session);
    }
    public boolean isContains(WebSocketSession session){
        if(this.session!=null && this.session.equals(session))
            return true;
        else
            return roomSessions.contains(session);
    }
    public void deleteSession(WebSocketSession session){
        for(WebSocketSession s : roomSessions){
            if(s.equals(session)) {
                roomSessions.remove(s);
                return;
            }
        }
        setSession(null);
    }


}
