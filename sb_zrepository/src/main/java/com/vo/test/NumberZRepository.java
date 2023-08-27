package com.vo.test;

import java.util.List;

import com.vo.ZRepository;

/**
 *
 * 测试自定义方法声明
 *
 * @author zhangzhen
 * @date 2023年8月20日
 *
 */
public interface NumberZRepository extends ZRepository<NumberEntity, Integer> {

	// FIXME 2023年8月27日 下午1:46:07 zhanghen: TODO
	// ，类似findByAgeOrderByIdDescLimit这种方法，在启动时提前提示
	// 方法参数个数、类型等问题，而不是等到运行时抛异常
	List<NumberEntity> findByAgeOrderByIdDescLimit(Integer age, Integer offset, Integer count);

	List<NumberEntity> findByAgeIsNull(Integer age);

	List<NumberEntity> findByAge(Integer age);

	List<NumberEntity> findByAgeNot(Integer age);

	Long countingByAge(Integer age);

	List<NumberEntity> findByNameEndingWith(String name);

	List<NumberEntity> findByNameLike(String name);

	List<NumberEntity> findByNameStartingWith(String name);

	List<NumberEntity> findByBigint1(Long bigint1);

	List<NumberEntity> findByUserId(Long userId);

	List<NumberEntity> findByUserIdAndName(Integer userId, String name);

	List<NumberEntity> findByAgeAndName(Integer age, String name);
}
