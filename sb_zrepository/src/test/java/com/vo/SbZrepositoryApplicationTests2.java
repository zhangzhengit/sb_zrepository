package com.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.assertj.core.api.Assertions.linesOf;
import static org.assertj.core.api.Assertions.not;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.common.collect.Lists;
import com.vo.conn.ZCPool;
import com.vo.test.UserEntity;
import com.vo.test.UserRepository;
import com.votool.ze.ZE;
import com.votool.ze.ZES;

import ch.qos.logback.classic.spi.ThrowableProxyVO;
import net.bytebuddy.dynamic.scaffold.InstrumentedType.Frozen;
import reactor.core.Fuseable.SynchronousSubscription;

/**
 *
 *
 * @author zhangzhen
 * @date 2023年6月15日
 *
 */
@SpringBootTest
class AAAATests2 {

	@Autowired
	UserRepository userRepository;

	@Test
	void test1() {
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "AAAATests2.test1()");

		final UserEntity findById = this.userRepository.findById(1L);
		System.out.println("findById = " + findById);
	}

}
