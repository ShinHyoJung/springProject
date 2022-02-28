package com.project.controller;

import com.project.dto.CommentDTO;
import com.project.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created with IntellliJ IDEA.
 * User: nandsoft
 * Date: 2022-02-24
 * Time: 오후 4:29
 * Comments:
 */

@Controller
public class CommentController {

    @Autowired
    CommentService commentService;

    @RequestMapping("/writeComment")
    public String writeComment(CommentDTO comment) throws Exception { // 댓글 쓰기

        commentService.insertComment(comment);
        int bno = comment.getBno();
        return "redirect:read/"+bno;
    }

    @RequestMapping("/deleteComment")
    public String deleteComment(CommentDTO comment) throws Exception { // 댓글 삭제

        commentService.deleteComment(comment);
        int bno= comment.getBno();
        return "redirect:read/" + bno;
    }

    @RequestMapping("/updateComment")
    public String updateComment(CommentDTO comment, Model model) throws Exception { // 댓글 수정

        commentService.updateComment(comment);
        List<CommentDTO> comments = commentService.selectComment(comment);
        model.addAttribute("comments", comments);
        return "/comment";
    }




}