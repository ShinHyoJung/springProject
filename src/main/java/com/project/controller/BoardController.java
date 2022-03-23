package com.project.controller;

import com.project.dto.*;
import com.project.service.BoardService;
import com.project.service.CommentService;
import com.project.service.HeartService;
import com.project.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntellliJ IDEA.
 * User: nandsoft
 * Date: 2022-02-24
 * Time: 오후 4:12
 * Comments:
 */

@Controller
public class BoardController {

    @Autowired // 의존성주입방법 (속성, 메소드, setter을 자동으로 주입)
    private MemberService memberService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HeartService heartService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value="/list", method= RequestMethod.GET) // 게시판 목록
    public String List(Criteria cri, Model model)  {

        logger.info("board");
        List<BoardDTO> list= boardService.viewBoard(cri);
        model.addAttribute("list", list);

        int total = boardService.countBoard(cri);
        PagingDTO page = new PagingDTO(cri, total);
        model.addAttribute("page", page);
        return "board/list";
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping("/write") // 게시글 쓰기
    public String writeBoard(Model model) {
        logger.info("write");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();

        MemberDTO user = memberService.selectMember(username);
       // MemberDTO user = memberService.selectMember();
        model.addAttribute("user", user);
        return "board/write";
    }

    @Secured({"ROLE_USER","ROLE_ADMIN"})
    @RequestMapping("/enroll") // 게시글 등록
    public String enrollBoard(BoardDTO board, MultipartHttpServletRequest mpRequest) throws Exception {

        logger.info("enroll");
        boardService.insertBoard(board, mpRequest);

        Iterator<String> iterator = mpRequest.getFileNames();
        MultipartFile multipartFile = null;
        while(iterator.hasNext()) { // 컬렉션에 저장되어있는 요소들을 읽어오는 방법 표준화, 요소가 있으면 true, 없으면 false
            multipartFile = mpRequest.getFile(iterator.next());
            if(multipartFile.isEmpty() == false) {
                logger.debug("-------file start------");
                logger.debug("name : " + multipartFile.getName()); //
                logger.debug("filename : " + multipartFile.getOriginalFilename());
                logger.debug("size: " + multipartFile.getSize());
                logger.debug("----------end----------");
            }
        }
        return "redirect:/list";
    }

    @Secured({"ROLE_USER","ROLE_ADMIN"})
    @RequestMapping(value="/read/{bno}", method = RequestMethod.GET) // 게시글 읽기
    public String readBoard(@PathVariable("bno")int bno, MemberDTO member, Model model, HttpSession session) throws Exception {
        // 게시글 번호를 받아서 경로설정
        // pathVariable은 RESTful 방식에 맞게 좀 더 직관적
        logger.info("read");
        BoardDTO board = boardService.selectBoard(bno); //게시글
        model.addAttribute("board", board);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();

        member = memberService.selectMember(username);
        int idx = member.getIdx();
        MemberDTO user = memberService.selectMember(username);

        HeartDTO heart = heartService.selectHeart(bno, idx);


        CommentDTO comment = new CommentDTO(); // 댓글
        comment.setBno(bno);
        List<CommentDTO> comments = commentService.selectComment(comment);

        List<Map<String, Object>> file = boardService.selectFile(bno);

        model.addAttribute("user", user);
        model.addAttribute("heart", heart);
        model.addAttribute("comments", comments);
        model.addAttribute("file", file);

        return "board/read";
    }

    @Secured({"ROLE_USER","ROLE_ADMIN"})
    @RequestMapping(value="/modify/{bno}", method = RequestMethod.GET) // 게시글 수정 페이지
    public String modifyBoard(@PathVariable("bno")int bno, Model model) throws Exception {

        logger.info("update_page");
        BoardDTO board = boardService.modifyBoard(bno);
        model.addAttribute("board", board);

        List<Map<String, Object>> file = boardService.selectFile(bno);
        model.addAttribute("file", file);
        return "board/modify";
    }

    @Secured({"ROLE_USER","ROLE_ADMIN"
    })
    @RequestMapping(value = "/update", method = RequestMethod.POST) // 게시글 수정
    public String updateBoard(BoardDTO board, @RequestParam(value="fileNoDel[]", required = false) String[] files,
                              @RequestParam(value="fileNameDel[]", required = false) String[] fileNames, MultipartHttpServletRequest mpRequest) throws Exception {
        logger.info("update");
        boardService.updateBoard(board, files, fileNames, mpRequest);
        return "redirect:list";
    }

    @Secured({"ROLE_USER","ROLE_ADMIN"})
    @RequestMapping("/delete") // 게시글 삭제
    public String deleteBoard(int bno) throws Exception {

        boardService.deleteBoard(bno);
        return "redirect:list";
    }

    @Secured({"ROLE_USER","ROLE_ADMIN"})
    @RequestMapping(value="/search/{bwriter}", method=RequestMethod.GET) // 게시글 작성자 검색
    public String searchBoard(@PathVariable("bwriter")String bwriter, Model model) { // 어떤 요청이든간에 하나밖에 못씀

        logger.info("search");

        List<BoardDTO> result = boardService.searchBoard(bwriter);
        String writer = bwriter;
        model.addAttribute("result",result);
        model.addAttribute("writer", writer);
        return "board/search";
    }

    @Secured({"ROLE_USER","ROLE_ADMIN"})
    @RequestMapping("/upBoard") // 게시글 좋아요수 증가
    public String upBoard(int bno) {

        logger.info("heart_up");
        boardService.upBoard(bno);
        boardService.selectBoard(bno);
        return "success";
    }

    @Secured({"ROLE_USER","ROLE_ADMIN"})
    @RequestMapping("/downBoard")
    @ResponseBody
    public String downBoard(int bno) { // 게시글 좋아요수 감소
        logger.info("heart_down");
        boardService.downBoard(bno);
        boardService.selectBoard(bno);
        return "success";
    }

    @Secured({"ROLE_USER","ROLE_ADMIN"})
    @RequestMapping("/downFile") // 파일 다운로드
    public void downFile(@RequestParam Map<String, Object> map, HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = boardService.downFile(map);

        String storedFileName = (String) resultMap.get("stored_fname"); // 저장된 파일이름
        String originalFileName = (String) resultMap.get("org_fname"); // 원본 파일이름

        byte fileByte[] = org.apache.commons.io.FileUtils.readFileToByteArray(new File("D:\\file\\"+storedFileName)); //

        response.setContentType("application/octet-stream"); // contentType을 다르게 지정하여 응답. mime형식표준, binary데이터 (파일)8비트로 된 데이터
        response.setContentLength(fileByte.length); // 파일의 길이를 헤더의 길이에 명시
        response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(originalFileName, "UTF-8")+"\";"); // name 헤더의 값을 value로 지정
        response.setHeader("Content-Transfer-Encoding", "binary"); // 바이너리파일로 인코딩
        response.getOutputStream().write(fileByte); // 내용물을 채움 버퍼가 속도 조절
        response.getOutputStream().flush(); // 버퍼에서 빠져나감 버퍼를 비움
        response.getOutputStream().close(); // 닫음
    }


}
