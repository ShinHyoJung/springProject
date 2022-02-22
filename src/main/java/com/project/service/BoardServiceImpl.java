package com.project.service;

import com.project.dao.BoardDAO;
import com.project.dto.BoardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntellliJ IDEA.
 * User: nandsoft
 * Date: 2022-02-21
 * Time: 오전 8:37
 * Comments:
 */

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    BoardDAO boardDAO;

    @Override
    public void writeBoard(BoardDTO board) throws  Exception {
        boardDAO.writeBoard(board);
    }

    @Override
    public List<BoardDTO> viewBoard() throws Exception {
        return boardDAO.viewBoard();
    }

    @Override
    public BoardDTO readBoard(int bno) throws Exception {
        return boardDAO.readBoard(bno);
    }
}
