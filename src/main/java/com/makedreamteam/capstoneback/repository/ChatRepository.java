package com.makedreamteam.capstoneback.repository;

import com.makedreamteam.capstoneback.domain.Chat;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Long> {

    @Query("select msg from Chat where room=:roomId and to =:to")
    List<String> findMsgByRoomAndToOrderByDate(@Param("roomId") UUID roomId,@Param("to") UUID to);


    List<Chat> findAllByRoomOrderByDate(UUID roomId);

    void deleteAllByRoom(UUID roomId);
}
