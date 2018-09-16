## 自定义Mybatis_Genertor插件 生成语句：mysql

### 功能：在Mybatis_Genertor原有的基础上，加上selectAll(),selectByPage(page),selectByT(T),selectByPageAndT(page,T)
### selectAll():查询所有
### selectByPage(page):分页查询
### selectByT(T):根据T实体类作为条件查询
### selectByPage(page,T):根据T实体类作为条件分页查询
### insertTCacheId(T):插入对象，并返回Id存入到t.setId()

---
# CustomPlugin自定义插件，主要功能：生成mapper.xml中的selectAll(),selectByPage(page)等

## 使用：在generatorConfig.xml加入此插件，此插件将原来的生成的Dao.java重写，实现接口，统一管理

```
		<!-- 自定义插件，生成SelectAll等，生成*Dao.java覆盖原有*Dao.java -->
		<plugin type="com.common.plugin.mybatis.CustomPlugin">
			<!--targetProject必须： *Dao目录 -->
			<property name="targetProject" value="src/main/java" />
			
			<!--targetProject必须： baseDao的全类名 -->
			<property name="baseDao" value="com.common.dao.BaseDao"/>
			
			<!-- annotation可选：默认无，为全限定注解名-->
			<property name="annotation" value="org.apache.ibatis.annotations.Mapper"/>
		</plugin>
```
## 原来生成的：
```
package com.zfw.dao;

import com.zfw.entity.Test;

public interface TestDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Test record);

    int insertSelective(Test record);

    int insertTCacheId(T t);
    
    Test selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Test record);

    int updateByPrimaryKey(Test record);
}
```
### 执行此插件后
```
package com.zfw.dao;

import com.common.dao.BaseDao;
import com.zfw.entity.Test;

/**
 * 自动生成
 */
public interface TestDao extends BaseDao<Test> {
}
```
### BaseDao<T>统一管理基本操作，其它Dao接口继承BaseDao<T>
```
package com.common.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.common.entity.Page;

public interface BaseDao<T> {

	int deleteByPrimaryKey(Integer id);

	int insert(T t);

	int insertSelective(T t);

	int insertTCacheId(T t);
	
	T selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(T t);

	int updateByPrimaryKey(T t);

	List<T> selectAll();

	List<T> selectByPage(@Param("page") Page page);

	List<T> selectByPageAndT(@Param("page") Page page, @Param("t") T t);
	
	List<T> selectByT(@Param("t") T t);
}

```
## Mapper.xml 生成效果
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zfw.dao.TestDao">
	......
	<insert id="insertTCacheId" parameterType="com.zfw.entity.Test">
		insert into t_test (id,c1,c2,c3) values
		(#{id,jdbcType=INTEGER},#{c1,jdbcType=VARCHAR},#{c2,jdbcType=VARCHAR},#{c3,jdbcType=VARCHAR})
		<selectKey resultType="int" keyProperty="id">
			select @@identity
		</selectKey>
	</insert>
	<select id="selectAll" parameterType="com.zfw.entity.Test"
		resultMap="BaseResultMap">
		select
		<include refid="Base_Column_List" />
		from t_test
	</select>
	<select id="selectByPage" parameterType="com.common.entity.Page"
		resultMap="BaseResultMap">
		select
		<include refid="Base_Column_List" />
		from t_test limit #{page.startSize,jdbcType=INTEGER},
		#{page.pageSize,jdbcType=INTEGER}
	</select>
	<select id="selectByT" parameterType="com.common.entity.Page"
		resultMap="BaseResultMap">
		select
		<include refid="Base_Column_List" />
		from t_test
		<where>
			<if test="t.id!= null">
				and id=#{t.id,jdbcType=INTEGER}
			</if>
			<if test="t.c1!= null">
				and c1=#{t.c1,jdbcType=VARCHAR}
			</if>
			<if test="t.c2!= null">
				and c2=#{t.c2,jdbcType=VARCHAR}
			</if>
			<if test="t.c3!= null">
				and c3=#{t.c3,jdbcType=VARCHAR}
			</if>
		</where>
	</select>
	<select id="selectByPageAndT" resultMap="BaseResultMap">
		select
		<include refid="Base_Column_List" />
		from t_test
		<where>
			<if test="t.id!= null">
				and id=#{t.id,jdbcType=INTEGER}
			</if>
			<if test="t.c1!= null">
				and c1=#{t.c1,jdbcType=VARCHAR}
			</if>
			<if test="t.c2!= null">
				and c2=#{t.c2,jdbcType=VARCHAR}
			</if>
			<if test="t.c3!= null">
				and c3=#{t.c3,jdbcType=VARCHAR}
			</if>
		</where>
		limit #{page.startSize,jdbcType=INTEGER},
		#{page.pageSize,jdbcType=INTEGER}
	</select>
</mapper>
```
---
# ServicePlugin 负责生成基本的Service接口
## 要求：必须有两个类：com.common.service.BaseService和	com.common.service.impl.BaseServiceImpl
## com.common.service.BaseService
```
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
```

## BaseServiceImpl
```
package com.common.service.impl;

import java.util.List;

import com.common.dao.BaseDao;
import com.common.entity.Page;
import com.common.service.BaseService;
public class BaseServiceImpl<T> implements BaseService<T>{
	//TODO 此处要加注入注解，由于此项目没有引用spring，所以TODO
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

```

## 使用：在generatorConfig.xml加入此插件，此插件生成基本的Service和实现类
```
		<!-- 自定义插件，生成基本的service-->
		<plugin type="com.common.plugin.mybatis.ServicePlugin">
			<!--必须：目录 -->
			<property name="serviceTargetDir" value="src/main/java"/>
			
			<!--必须：service接口全限定性类名-->
			<property name="serviceInterfaceClass" value="com.zfw.service.TestService"/>
			
			<!--必须：service实现类全限定性类名-->
			<property name="implementationClass" value="com.zfw.service.impl.TestServiceImpl"/>
			
			<!--可选： annotation可选：默认无，为全限定注解名-->
			<property name="annotation" value="org.springframework.stereotype.Service"/>
		
		</plugin>
```
## 效果：spring项目需加注解
```
package com.zfw.service;

import com.common.service.BaseService;
import com.zfw.entity.Test;

/**
 * 自动生成Sevice
 */
public interface TestService extends BaseService<Test> {
}


package com.zfw.service.impl;

import com.common.service.impl.BaseServiceImpl;
import com.zfw.entity.Test;
import com.zfw.service.TestService;

public class TestServiceImpl extends BaseServiceImpl<Test> implements TestService {
}
```

## 运行mybatis-generator
run as -> com.common.test.Generator.java
或者参考官方文档
http://www.mybatis.org/generator/running/running.html

