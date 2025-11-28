//package com.infy.pintrest.service;
//
//public interface BoardService {
//
//}
package com.infy.pintrest.service;





import java.util.List;



import org.springframework.web.multipart.MultipartFile;



import com.infy.pintrest.dto.BoardDTO;

import com.infy.pintrest.dto.BoardUpdateDTO;

import com.infy.pintrest.exception.InfyPintrestException;



public interface BoardService {

BoardDTO createBoard(BoardDTO boardDto, MultipartFile file) throws InfyPintrestException;

List<BoardDTO> getBoardForUser(Integer userId) throws InfyPintrestException;

BoardDTO getBoardById(Integer boardId) throws InfyPintrestException;

BoardDTO updateBoard(Integer boardId, BoardUpdateDTO boardUpdateDTO) throws InfyPintrestException;

void deleteBoard(Integer boardId) throws InfyPintrestException;



}
