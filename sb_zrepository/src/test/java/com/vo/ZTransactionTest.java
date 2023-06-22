package com.vo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.vo.test.TeaRepository;

/**
 *
 * 事务测试
 *
 * @author zhangzhen
 * @date 2023年6月15日
 *
 */
@SpringBootTest
class ZTransactionTest {
	@Autowired
	TeaRepository teaRepository;

	@Test
	void test1() {
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "ZTransactionTest.test1()");


	}

}
