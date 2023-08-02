package kr.co.tj.boardservice.service;

import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.tj.boardservice.DataNotFoundException;
import kr.co.tj.boardservice.dto.BoardDTO;

import kr.co.tj.boardservice.persistence.BoardEntity;
import kr.co.tj.boardservice.persistence.BoardRepository;

@Service
public class BoardServiceImpl implements BoardService {
	

	private BoardRepository boardRepository;
	
	@Autowired
	public BoardServiceImpl(BoardRepository boardRepository) {
		super();
		this.boardRepository = boardRepository;
	}
	
	
	@Override
	public BoardDTO findBySellId(String sellId) {
		
		Optional<BoardEntity> optional = boardRepository.findBysellId(sellId);
		
		if(!optional.isPresent()) {
			throw new DataNotFoundException("게시글 없음");
		}
		
		BoardEntity boardEntity = optional.get();
		BoardDTO boardDTO = BoardDTO.builder()
				.id(boardEntity.getId())
				.sellId(boardEntity.getSellId())
				.username(boardEntity.getUsername())
				.title(boardEntity.getTitle())
				.deltaString(boardEntity.getDeltaString())
				.htmlString(boardEntity.getHtmlString())
				.build();
		
		return boardDTO;
	}
	

	@Override
	public BoardDTO insert(BoardDTO boardDTO) {
		BoardEntity entity = BoardEntity.builder()
				.sellId(boardDTO.getSellId())
				.username(boardDTO.getUsername())
				.title(boardDTO.getTitle())
				.deltaString(boardDTO.getDeltaString())
				.htmlString(boardDTO.getHtmlString())
				.build();
		
		entity = boardRepository.save(entity);
		
		boardDTO.setId(entity.getId());
		
		return boardDTO;
	}
	
	@Override
	public BoardDTO update(BoardDTO boardDTO) {
		Optional<BoardEntity> optional = boardRepository.findBysellId(boardDTO.getSellId());
		if(!optional.isPresent()) {
			throw new DataNotFoundException("거래 데이터가 확인되지 않습니다");
		}
		
		BoardEntity orgEntity = optional.get();
		
		BoardEntity entity = BoardEntity.builder()
				.id(orgEntity.getId())
				.sellId(orgEntity.getSellId())
				.username(boardDTO.getUsername())
				.title(boardDTO.getTitle())
				.deltaString(boardDTO.getDeltaString())
				.htmlString(boardDTO.getHtmlString())
				.build();
		
		entity = boardRepository.save(entity);
		
		return boardDTO;
	}
	
	@Override
	public void delete(String sellId) {
		
		Optional<BoardEntity> optional = boardRepository.findBysellId(sellId);
		if(!optional.isPresent()) {
			throw new DataNotFoundException("게시글을 찾지 못함");
		}
		
		BoardEntity entity = optional.get();
		
		boardRepository.delete(entity);
	}
}
