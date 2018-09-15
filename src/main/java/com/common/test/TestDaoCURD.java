package com.common.test;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.common.entity.Page;
import com.common.plugin.mybatis.MyBatiesUtils;
import com.zfw.dao.TestDao;
import com.zfw.entity.Test;

public class TestDaoCURD {
	
	
	@org.junit.Test
	public void testInsert() {
		SqlSession sqlSession = MyBatiesUtils.getSqlSession();
		TestDao dao = sqlSession.getMapper(TestDao.class);
			Test t=new Test();
			t.setC1("c1_");
			t.setC2("c2_");
			t.setC3("c3_");
			int insert = dao.insert(t);
			System.out.println(insert);
		sqlSession.commit();
	}
	
	@org.junit.Test
	public void testSelectAll() {
		SqlSession sqlSession = MyBatiesUtils.getSqlSession();
		TestDao dao = sqlSession.getMapper(TestDao.class);
		List<Test> tests= dao.selectAll();
		System.out.println(tests);
	}
	@org.junit.Test
	public void testSelectPage() {
		SqlSession sqlSession = MyBatiesUtils.getSqlSession();
		TestDao dao = sqlSession.getMapper(TestDao.class);
		Page page=new Page(20, 10);
		List<Test> tests = dao.selectByPage(page);
		System.out.println(tests);
	}
	@org.junit.Test
	public void testSelectT() {
		SqlSession sqlSession = MyBatiesUtils.getSqlSession();
		TestDao dao = sqlSession.getMapper(TestDao.class);
		Test t=new Test();
		t.setC1("c1_123");
		t.setC2("c2_123");
		List<Test> tests = dao.selectByT(t);
		System.out.println(tests);
	}
	@org.junit.Test
	public void testSelectPageAndT() {
		SqlSession sqlSession = MyBatiesUtils.getSqlSession();
		TestDao dao = sqlSession.getMapper(TestDao.class);
		Test t=new Test();
		t.setC1("c1_123");
		t.setC2("c2_123");
		Page page=new Page(1, 1);
		List<Test> selectByPage = dao.selectByPageAndT(page, t);
		System.out.println(selectByPage);
	}
	
	@org.junit.Test
	public void insertAndCacheIdToT() {
		SqlSession sqlSession = MyBatiesUtils.getSqlSession();
		TestDao dao = sqlSession.getMapper(TestDao.class);
		Test t=new Test();
		t.setC1("c1_123");
		t.setC2("c2_123");
		t.setC3("c2_123");
		dao.insertTCacheId(t);
		sqlSession.commit();
		System.out.println(t.toString());
	}
	
}
