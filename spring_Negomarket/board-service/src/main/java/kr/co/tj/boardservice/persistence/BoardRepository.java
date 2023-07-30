package kr.co.tj.boardservice.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity, Long>{



	void deleteByUsername(String username);

	List<BoardEntity> findByUsername(String username);

	Page<BoardEntity> findByTitleContainingOrHtmlStringContaining(String keyword, String keyword2, Pageable pageable);

	Optional<BoardEntity> findBysellId(String sellid);



	

}
