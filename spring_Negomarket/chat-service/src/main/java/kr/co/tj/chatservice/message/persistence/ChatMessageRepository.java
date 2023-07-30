package kr.co.tj.chatservice.message.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long>{

	List<ChatMessageEntity> findByRoomTitle(String title);

}
