package com.vo;

import java.util.List;

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

	Long count();

	T findById(ID id);

	T update(T t);

	/**
	 * save方法会先判断对象是否存在，如果存在，则更新参数t中不为null的值到数据库; 如果不存在，则直接save参数t对象
	 *
	 * @param t
	 * @return
	 *
	 */
	// FIXME 2023年6月16日 下午2:55:15 zhanghen: 先判断t是否存在
	T save(T t);

	List<ID> saveAll(List<T> tList);

	List<T> findByIdIn(List<ID> idList);

	List<T> findAll();

	boolean existById(ID id);

	/**
	 * 根据ID删除一个对象
	 *
	 * @param id
	 * @return 是否成功删除了
	 *
	 */
	boolean deleteById(ID id);

	/**
	 * 删除全部的对象
	 *
	 * @return 是否全部删除了
	 *
	 */
	boolean deleteAll();

}
