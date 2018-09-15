package com.common.service;

import java.util.List;

import com.common.entity.Page;

public interface BaseService<T> {
	/**
	 * 通过id得到T
	 * @param id
	 * @return T
	 */
	T getById(int id);
	
	/**
	 * 通过id删除T
	 * @param id
	 * @return 返回1=成功
	 */
	int removeById(int id);
	
	/**
	 * 添加实体
	 * @param t
	 * @return 返回1=成功
	 */
	int add(T t);
	
	/**
	 * 选择性插入，对传进来的值做非空判断，非空的插入
	 * @param t
	 * @return 返回1=成功
	 */
	int addSelective(T t);

	/**
	 * 插入对象，并返回Id存入到t.setId()
	 * @param t
	 * @return 返回1=成功
	 */
	int addTCacheId(T t);
	
	/**
	 * 选择性更新，对传进来的值做非空判断，非空的更新
	 * @param t
	 * @return 返回1=成功
	 */
	int updateByPrimaryKeySelective(T t);
	
	/**
	 * 通过实体的id进行更新
	 * @param t
	 * @return 返回1=成功
	 */
	int updateByPrimaryKey(T t);
	
	/**
	 * 得到所有 
	 * @return List<T>
	 */
	List<T> getAll();
	
	/**
	 * 分页查询
	 * @param page 分页对象
	 * @return List<T>
	 */
	List<T> getByPage(Page page);
	
	/**
	 * 根据实体中属性值进行查询，非空的值为查询条件
	 * @param t
	 * @return List<T>
	 */
	List<T> getByT(T t);
	
	/**
	 * 根据实体中属性值进行分页查询，非空的值为查询条件
	 * @param page
	 * @param t
	 * @return List<T>
	 */
	List<T> getByPageAndT(Page page,T t);
	
}