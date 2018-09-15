package com.common.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.common.entity.Page;

public interface BaseDao<T> {
	T selectByPrimaryKey(Integer id);

	int deleteByPrimaryKey(Integer id);

	int insert(T t);

	int insertSelective(T t);
	
	int updateByPrimaryKeySelective(T t);

	int updateByPrimaryKey(T t);
	
	int insertTCacheId(T t);

	List<T> selectAll();

	List<T> selectByPage(@Param("page") Page page);
	
	List<T> selectByT(@Param("t") T t);

	List<T> selectByPageAndT(@Param("page") Page page, @Param("t") T t);
	
}