package com.makedreamteam.capstoneback.service;


import com.makedreamteam.capstoneback.JwtTokenProvider;
import com.makedreamteam.capstoneback.domain.Chat;
import com.makedreamteam.capstoneback.form.ResponseForm;
import com.makedreamteam.capstoneback.repository.ChatRepository;
import com.makedreamteam.capstoneback.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    public ResponseForm getAllChat(UUID roomId, String accessToken, String refreshToken) {
        if(jwtTokenProvider.isValidAccessToken(accessToken)){
            UUID userId=jwtTokenProvider.getUserId(accessToken);
            List<String> allByRoomAndTo = chatRepository.findMsgByRoomAndToOrderByDate(roomId,userId);
            return ResponseForm.builder().data(allByRoomAndTo).message("대화내용을 반환").build();
        }else
            return jwtTokenProvider.checkRefreshToken(refreshToken);
    }
}
