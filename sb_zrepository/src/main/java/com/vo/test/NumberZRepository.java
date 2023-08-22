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

	List<NumberEntity> findByBigint1(Long bigint1);
//	List<NumberEntity> findByUserId(Long userId);
}
