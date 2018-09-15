package com.common.service.impl;

import java.util.List;

import com.common.dao.BaseDao;
import com.common.entity.Page;
import com.common.service.BaseService;
public class BaseServiceImpl<T> implements BaseService<T>{
	//TODO 此处要注入，由于此项目没有引用spring，所以TODO
	private BaseDao<T> baseDao;
	@Override
	public T getById(int id) {
		return baseDao.selectByPrimaryKey(id);
	}
	@Override
	public int removeById(int id) {
		return baseDao.deleteByPrimaryKey(id);
	}
	@Override
	public int add(T t) {
		return baseDao.insert(t);
	}
	@Override
	public int addSelective(T t) {
		return baseDao.insertSelective(t);
	}
	@Override
	public int updateByPrimaryKeySelective(T t) {
		return baseDao.updateByPrimaryKeySelective(t);
	}
	@Override
	public int updateByPrimaryKey(T t) {
		return baseDao.updateByPrimaryKey(t);
	}
	@Override
	public List<T> getAll() {
		return baseDao.selectAll();
	}
	@Override
	public List<T> getByPage(Page page) {
		return baseDao.selectByPage(page);
	}
	@Override
	public List<T> getByT(T t) {
		return baseDao.selectByT(t);
	}
	@Override
	public List<T> getByPageAndT(Page page, T t) {
		return baseDao.selectByPageAndT(page, t);
	}
	@Override
	public int addTCacheId(T t) {
		return baseDao.insertTCacheId(t);
	}

}
