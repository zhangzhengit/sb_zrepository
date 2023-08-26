package com.vo.test;

import java.util.List;

import com.vo.ZRepository;

/**
 *
 *
 * @author zhangzhen
 * @date 2023年8月20日
 *
 */
public interface NumberZRepository extends ZRepository<NumberEntity, Integer>{

	// FIXME 2023年8月26日 下午5:40:39 zhanghen: 测试 启动时检验方法名是否声明正确
	List<NumberEntity> findByNameLike(String name);
	List<NumberEntity> findByNameStartingWith(String name);
	List<NumberEntity> findByBigint1(Long bigint1);
	List<NumberEntity> findByUserId(Long userId);
	List<NumberEntity> findByUserIdAndName(Integer userId,String name);
	List<NumberEntity> findByAgeAndName(Integer age,String name);
}
