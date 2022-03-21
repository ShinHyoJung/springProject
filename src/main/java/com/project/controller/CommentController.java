package com.project.controller;

import com.project.dto.CommentDTO;
import com.project.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
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

    @Secured({"ROLE_USER","ROLE_ADMIN"})
    @RequestMapping("/writeComment")
    public String writeComment(CommentDTO comment) { // 댓글 쓰기

        commentService.insertComment(comment);
        int bno = comment.getBno();
        return "redirect:read/"+bno;
    }

    @Secured({"ROLE_USER","ROLE_ADMIN"})
    @RequestMapping("/deleteComment")
    public String deleteComment(CommentDTO comment) { // 댓글 삭제

        commentService.deleteComment(comment);
        int bno= comment.getBno();
        return "redirect:read/" + bno;
    }

    @Secured({"ROLE_USER","ROLE_ADMIN"})
    @RequestMapping("/updateComment")
    public String updateComment(CommentDTO comment, Model model) { // 댓글 수정

        commentService.updateComment(comment);
        int bno = comment.getBno();
        List<CommentDTO> comments = commentService.selectComment(comment);
        model.addAttribute("comments", comments);
        return "/comment";
    }

}
