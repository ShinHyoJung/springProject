package com.project.service;

import com.project.dto.BoardDTO;
import com.project.dto.Criteria;
import com.project.dto.PagingDTO;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * Created with IntellliJ IDEA.
 * User: nandsoft
 * Date: 2022-02-21
 * Time: 오전 8:36
 * Comments:
 */
public interface BoardService {

    void insertBoard(BoardDTO board, MultipartHttpServletRequest mpRequest) throws Exception;

    List<BoardDTO> viewBoard(Criteria cri) throws Exception;

    BoardDTO selectBoard(int bno) throws Exception;

    BoardDTO modifyBoard(int bno) throws Exception;

    void updateBoard(BoardDTO board) throws Exception;

    void deleteBoard(int bno) throws Exception;

    int countBoard(Criteria cri) throws Exception;

    List<BoardDTO> searchBoard(String bwriter) throws Exception;

    BoardDTO heartBoard(int bno) throws Exception;

    void upBoard(int bno) throws Exception;

    void downBoard(int bno) throws Exception;


}
