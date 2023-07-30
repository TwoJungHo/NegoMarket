package kr.co.tj.boardservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.tj.boardservice.dto.BoardDTO;
import kr.co.tj.boardservice.dto.BoardResponse;

public interface BoardService {
	
	
	BoardDTO insert(BoardDTO boardDTO);

	BoardDTO update(BoardDTO boardDTO);

	BoardDTO findBySellId(String sellId);

	void delete(String sellId);
	
	
		
//	BoardDTO createBoard(BoardDTO boardDTO);
//	BoardDTO getDate(BoardDTO boardDTO);
//	BoardDTO findById(Long id);
//	List<BoardDTO> findAll();
//	BoardDTO update(BoardResponse boardResponse);
//	Page<BoardDTO> findAll(Integer page);
//	Page<BoardDTO> search(int pageNum, String keyword);
//	BoardDTO readCntUpdate(BoardDTO dto);
//	List<BoardResponse> getPage(Pageable pageable);
//	void delete(Long id);
	
	
	
}
