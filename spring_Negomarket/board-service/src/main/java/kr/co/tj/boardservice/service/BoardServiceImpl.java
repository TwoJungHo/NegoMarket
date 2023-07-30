package kr.co.tj.boardservice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import kr.co.tj.boardservice.DataNotFoundException;
import kr.co.tj.boardservice.dto.BoardDTO;
import kr.co.tj.boardservice.dto.BoardResponse;

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
	
//	@Override
//	public BoardDTO createBoard(BoardDTO boardDTO) {
//		// 시간날짜 받아오기
//		boardDTO = getDate(boardDTO);
//		
//		BoardEntity boardEntity = boardDTO.toBoardEntity();
//		
//		boardEntity = boardRepository.save(boardEntity);
//		
//		
//		return boardDTO.toBoardDTO2(boardEntity);
//				
//	}
//	
//	
// 
//	@Override
//	public  BoardDTO getDate(BoardDTO boardDTO) {
//		Date now = new Date();
//		
//		if(boardDTO.getCreateDate() ==null) {
//			boardDTO.setCreateDate(now);
//		}
//		boardDTO.setUpdateDate(now);
//		
//		return boardDTO;
//	}
//	
//	
//	@Override
//	 public BoardDTO findById(Long id) {
//		
//		Optional<BoardEntity> optional = boardRepository.findById(id);
//		
//		if(!optional.isPresent()) {
//			throw new RuntimeException("잘못된 정보입니다");
//		}
//		
//		BoardEntity entity = optional.get();
//		
//		
//		return BoardDTO.toBoardDTO(entity);
//	}
//	 // 글 전체 목록 가져오는 코드
//	@Transactional
//	@Override
//	public List<BoardDTO> findAll() {
//		
//		List<BoardEntity> list_entity = boardRepository.findAll();
//		List<BoardDTO> list_dto = new ArrayList<>();
//		
//		for(BoardEntity x : list_entity) {
//			list_dto.add(BoardDTO.toBoardDTO(x));
//		}
//		
//		
//		return list_dto;
//	}
//	// 삭제
//	@Override
//	public void delete(Long id) {
//		
//	
//		
//		boardRepository.deleteById(id);	
//	}
//
//	
//	// 글 수정
//	@Transactional
//	@Override
//	public BoardDTO update(BoardResponse boardResponse) {
//		Optional<BoardEntity> optional = boardRepository.findById(boardResponse.getId());
//		
//		if(!optional.isPresent()) {
//			throw new RuntimeException("값을 입력해주세요");
//		}
//		
//		BoardEntity boardEntity = optional.get();
//		
//		Date now = new Date();
//		
//		boardEntity.setTitle(boardResponse.getTitle());
//		boardEntity.setContent(boardResponse.getContent());
//		boardEntity.setCid(boardResponse.getCid());
//		boardEntity.setUpdateDate(now);
//		
//		 boardEntity = boardRepository.save(boardEntity);
//		 
//		 BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
//		
//		return boardDTO;
//	}
//	
//	
//	@Override
//	public Page<BoardDTO> findAll(Integer page) {
//		List<Sort.Order> sortList = new ArrayList<>();
//		sortList.add(Sort.Order.desc("id"));
//		
//		Pageable pageable = PageRequest.of(page, 10, Sort.by(sortList));
//		
//		Page<BoardEntity> page_board = boardRepository.findAll(pageable);
//		
//		Page<BoardDTO> page_dto = page_board.map(
//				boardEntity ->
//				new BoardDTO(boardEntity.getId(),
//						boardEntity.getCid(),
//						boardEntity.getUsername(),
//						boardEntity.getTitle(),
//						boardEntity.getContent(),
//						boardEntity.getCreateDate(),
//						boardEntity.getUpdateDate(),
//						boardEntity.getCateName()
//						
//						
//						)
//				
//				
//				
//				);
//				
//		return page_dto;
//	}
//	// 검색 관련코드
//	@Override
//	public Page<BoardDTO> search(int page, String keyword) {
//		List<Sort.Order> sortList = new ArrayList<>();
//		sortList.add(Sort.Order.desc("id"));
//		
//		Pageable pageable = PageRequest.of(page, 10, Sort.by(sortList));
//				
//		Page<BoardEntity> page_board = boardRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
//		 
//		 Page<BoardDTO> page_dto = page_board.map(
//				 
//				 boardEntity ->
//				 new BoardDTO(
//						 boardEntity.getId(),
//						 boardEntity.getCid(),
//						 boardEntity.getUsername(),
//						 boardEntity.getTitle(),
//						 boardEntity.getContent(),
//						 boardEntity.getCreateDate(),
//						 boardEntity.getUpdateDate(),
//						 boardEntity.getReadCnt(),
//						 boardEntity.getCateName()
//						 )
//				 );
//		 return page_dto;
//		
//	}
//	// 조회수 +1씩 늘어나는코드
//	@Transactional
//	@Override
//	public BoardDTO readCntUpdate(BoardDTO dto) {
//		Optional<BoardEntity> optional =  boardRepository.findById(dto.getId());
//		
//		BoardEntity orgEntity = optional.get();
//		long readCnt = orgEntity.getReadCnt() +1 ;
//		//조회수만 1늘어나고 나머지는 기존 값 그대로
//		BoardEntity entity = BoardEntity.builder()
//				.title(orgEntity.getTitle())
//				.id(orgEntity.getId())
//				.content(orgEntity.getContent())
//				.username(orgEntity.getUsername())
//				.readCnt(readCnt)
//				.cid(orgEntity.getCid())
//				.createDate(orgEntity.getCreateDate())
//				.updateDate(orgEntity.getUpdateDate())
//				.cateName(orgEntity.getCateName())
//				.build();
//		
//		
//		entity = boardRepository.save(entity);
//		
//		return BoardDTO.toBoardDTO(entity);
//		
//		
//				}
//	@Override
//	public List<BoardResponse> getPage(Pageable pageable) {
//		Page<BoardEntity> entity = boardRepository.findAll(pageable);
//		List<BoardResponse> boardResponse = new ArrayList<>();
//		
//		entity.forEach(x -> {
//			BoardDTO dto  = new BoardDTO();
//			BoardResponse res = dto.toBoardFindResponse(x);
//			boardResponse.add(res);
//		});
//		return boardResponse;
//	}

	
	


	

	
	
	

}
