package com.itheima.dao;

import com.itheima.pojo.Member;

public interface MemberDao {
    public Member findByTelephone(String telephone);
    public void add(Member member);
    public Integer findMemberCountBeforeDate(String date);
    public Integer findMemberCountByDate(String today);
    public Integer findMemberTotalCount();
    public Integer findMemberCountAfterDate(String thisWeekMonday);
}
