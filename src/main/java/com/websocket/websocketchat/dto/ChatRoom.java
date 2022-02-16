package com.websocket.websocketchat.dto;

import com.websocket.websocketchat.dto.ChatMessage.MessageType;
import com.websocket.websocketchat.service.ChatService;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Getter
/*
    ChatRoom이 하는일
    - 입장, 대화에 대한 핸들링(구분)
    - 채팅방 내의 모든 연결된 세션에게 메세지 전송
 */
public class ChatRoom {

    private String roomId;
    private String name;
    private Set<WebSocketSession> sessions = new HashSet<>();

    @Builder
    public ChatRoom(String roomId, String name) {
        this.roomId = roomId;
        this.name = name;
    }

    public void handleActions(WebSocketSession session, ChatMessage chatMessage, ChatService chatService) {
        // 입장한거라면, ~님이 입장했다고 보냄.
        if (chatMessage.getType().equals(ChatMessage.MessageType.ENTER)) {
            sessions.add(session); // websocket 세션을 채팅룸에 저장.
            chatMessage.setMessage(chatMessage.getSender() + "님이 입장했습니다.");
        }
        // 대화라면 그냥 메세지만 전달.
        sendMessage(chatMessage, chatService);
    }

    public <T> void sendMessage(T message, ChatService chatService) {
        // 채팅방 내의 모든 클라이언트 session에 메시지를 발송.
        sessions.parallelStream().forEach(session -> chatService.sendMessage(session, message));
    }
}
