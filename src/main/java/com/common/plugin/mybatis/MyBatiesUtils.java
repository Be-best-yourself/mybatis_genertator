package com.common.plugin.mybatis;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatiesUtils {
	private static SqlSessionFactory sqlSessionFactory;

	public static SqlSession getSqlSession(){
		try {
			InputStream inputStream = Resources.getResourceAsStream("mybatis.xml");
			
			if (sqlSessionFactory==null) {
				sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
			}
			return sqlSessionFactory.openSession();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}