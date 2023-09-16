package com.vo;

import java.util.List;

import com.vo.core.Page;


/**
 *
 * 顶级接口，自定义interface来 extends 此interface，注意自定义interface里面只可以有数据操作相关的方法.
 * 并且自定义interface仅 extends 此interface，不要实现任何其他接口
 *
 *
 * @param <T>  @ZEntity 标记的类
 * @param <ID> @ZEntity 标记的类里的 @ZID 字段的类型
 *
 *
 */
public interface ZRepository<T, ID> {
	// FIXME 2023年9月11日 下午8:35:46 zhanghen: TODO
	//	1、子接口中自定义方法的返回值，可以自定义返回类型中的T，做到隐藏敏感字段和去除非必要的select 字段
	//  2、select * 也改为 select 具体字段，自定义类型T使用getDeclaredFields来获取字段来生成具体的字段


	// FIXME 2023年9月6日 下午7:40:07 zhanghen: TODO 分页

	/**
	 * 分页查询，按T中非空字段等值查询，有多个非空字段则用and连接
	 *
	 * @param t    查询条件，根据对象里非null的字段来查询，等值查询
	 * @param page 第几页，从1开始
	 * @param size 一页显示几条
	 * @return
	 *
	 */
	Page<T> page(T t, Integer page, Integer size);

	/**
	 * select count(*) from 表
	 *
	 * @return
	 *
	 */
	Long count();

	/**
	 * 根据 @ZID 字段查询一个对象
	 *
	 * @param id
	 * @return
	 *
	 */
	T findById(ID id);

	/**
	 * 根据 @ZID 字段值来update一个对象，字段是什么就update为什么包括null， @ZID 字段不能为空，否则抛异常
	 *
	 * @param t
	 * @return 返回update后的对象
	 *
	 */
	T update(T t);

	/**
	 * insert一个对象，忽略 @ZID 字段，其他字段值什么是insert为什么，包括null
	 *
	 * @param t
	 * @return 返回新插入的对象
	 *
	 */
	T save(T t);

	/**
	 * 批量insert，忽略 @ZID 字段，其他字段值什么是insert为什么，包括null
	 *
	 * @param tList
	 * @return 返回插入对象的ID
	 *
	 */
	List<ID> saveAll(List<T> tList);

	/**
	 * 根据 @ZID 字段批量查询
	 *
	 * @param idList
	 * @return
	 *
	 */
	List<T> findByIdIn(List<ID> idList);

	/**
	 * 查询出所有的内容，不带条件
	 *
	 * @return
	 */
	List<T> findAll();

	/**
	 * 根据 @ZID 字段判断对象是否存在
	 *
	 * @param id
	 * @return
	 *
	 */
	boolean existById(ID id);

	/**
	 * 根据 @ZID 字段删除一个对象
	 *
	 * @param id
	 * @return 是否成功删除了
	 *
	 */
	boolean deleteById(ID id);

	/**
	 * 根据 @ZID 字段批量删除对象
	 *
	 * @param idList
	 * @return
	 *
	 */
	boolean deleteByIdIn(List<ID> idList);

	/**
	 * 删除全部的对象
	 *
	 * @return 是否全部删除了
	 *
	 */
	boolean deleteAll();

}
