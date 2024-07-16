package kr.co.tj.chatservice.room.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long>{

	Optional<ChatRoomEntity> findByTitle(String title);

	List<ChatRoomEntity> findByUsername1ContainingOrUsername2Containing(String username1, String username2);

}
