package com.websocket.websocketchat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.websocket.websocketchat.dto.ChatRoom;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/*
    ChatService가 하는일
    - 채팅방 생성 (생성해서 Map에 추가)
    - Id로 조회
    - 모두 조회
    - 지정한 Websocket 세션에 메세지 전송
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ObjectMapper objectMapper;
    private Map<String, ChatRoom> chatRooms;

    @PostConstruct
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }

    public List<ChatRoom> findAllRoom() {
        return new ArrayList<>(chatRooms.values());
    }

    public ChatRoom findRoomById(String roomId) {
        return chatRooms.get(roomId);
    }

    public ChatRoom createRoom(String name) {
        String randomId = UUID.randomUUID().toString();
        ChatRoom chatRoom = ChatRoom.builder()
            .roomId(randomId)
            .name(name)
            .build();
        chatRooms.put(randomId, chatRoom);
        return chatRoom;
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            // ChatMessage형으로 들어오면 {"type":"TALK","roomId":"XXX","sender":"XXX","message":"XXX"}
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
