package com.project.service;

import com.project.dao.CommentDAO;
import com.project.dto.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntellliJ IDEA.
 * User: nandsoft
 * Date: 2022-02-24
 * Time: 오후 4:26
 * Comments:
 */

@Service
public class CommentServiceImpl implements CommentService{

    @Autowired
    CommentDAO commentDAO;

    @Override
    public void insertComment(CommentDTO comment) {
        commentDAO.insertComment(comment);
    }

    @Override
    public List<CommentDTO> selectComment(CommentDTO comment) {
        return commentDAO.selectComment(comment);
    }

    @Override
    public void deleteComment(CommentDTO comment)  {
        commentDAO.deleteComment(comment);
    }

    @Override
    public void updateComment(CommentDTO comment) {
        commentDAO.updateComment(comment);
    }
}
