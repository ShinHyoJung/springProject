package com.project.dao;

import com.project.dto.MemberDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Member;


/**
 * Created with IntellliJ IDEA.
 * User: nandsoft
 * Date: 2022-02-15
 * Time: 오전 9:19
 * Comments:
 */

@Repository
public class MemberDAOImpl implements MemberDAO
{ //mapper에서 Impl을 사용함

    @Autowired
    SqlSession sqlSession;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public void insertMember(MemberDTO member) throws Exception {
        sqlSession.insert("insertMember", member);
    }


    @Override
    public MemberDTO loginMember(MemberDTO member) throws Exception
    {
       return sqlSession.selectOne("loginMember", member);

    }

    @Override
    public MemberDTO selectMember(String id) throws Exception {
        return sqlSession.selectOne("selectMember", id);

    }

    @Override
    public void updateMember(MemberDTO member) throws Exception {
        sqlSession.update("updateMember", member);
    }

    @Override
    public void deleteMember(String id) throws Exception {
        sqlSession.delete("deleteMember", id);
    }

    @Override
    public int checkMember(MemberDTO member) throws Exception {
        int result = sqlSession.selectOne("checkMember", member);
        return result;
    }
}
