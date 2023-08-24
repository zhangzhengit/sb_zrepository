package com.vo;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.common.hash.Hashing;
import com.vo.test.NumberEntity;
import com.vo.test.NumberZRepository;
import com.votool.ze.ZE;
import com.votool.ze.ZES;

/**
 *
 *
 * @author zhangzhen
 * @date 2023年8月21日
 *
 */
@SpringBootTest
class SbZrepositoryTestApplicationTests {

	@Autowired
	NumberZRepository nnnnnnnnn;


	@Test
	void test_save4() {
		final int id = 1;
		final NumberEntity entity = this.nnnnnnnnn.findById(id);
		assertThat(entity == null || entity.getId() == id);

		if (entity != null) {
			entity.setAge(232323);
			entity.setName("aa");
			this.nnnnnnnnn.save(entity);

			final NumberEntity s2 = this.nnnnnnnnn.findById(id);
			assertThat(s2.getId().intValue() == id);
			assertThat("aa".equals(s2.getName()));
			assertThat(s2.getAge().intValue() == entity.getAge().intValue());

		}

	}

	/**
	 * 根据id查，结果必须是id等于参数id的那一条
	 *
	 */
	@Test
	void test_save3() {
		final int n = 4010;
		final AtomicInteger saveA = new AtomicInteger();
		for (int id = 1; id <= n; id++) {

			final NumberEntity entity = this.nnnnnnnnn.findById(id);
			assertThat(entity == null || entity.getId() == id);
		}
	}

	/**
	 * save N 此，Db中必须有save的N条，全删了才删，必须剩余0条
	 *
	 */
	@Test
	void test_save2() {

		final ZE ze = ZES.newZE();

		final int n = 400;
		final AtomicInteger saveA = new AtomicInteger();
		for (int i = 1; i <= n; i++) {

			final Integer id = i;
			ze.executeInQueue(() -> {

				final NumberEntity numberEntity = new NumberEntity();
				numberEntity.setId(id);
				numberEntity.setUserId(200 + id);
				final NumberEntity save = SbZrepositoryTestApplicationTests.this.nnnnnnnnn.save(numberEntity);
				saveA.incrementAndGet();
			});
		}

		while (saveA.get() < n) {

		}
		System.out.println("OK");
		final IntStream range = IntStream.range(1, n + 1);

		final List<Integer> idList = range.mapToObj(i -> i).collect(Collectors.toList());

		final List<NumberEntity> ll = this.nnnnnnnnn.findByIdIn(idList);
		assertThat(ll.size() == n);


//		this.numberZRepository.deleteAll();
//		final List<NumberEntity> llc = this.numberZRepository.findByIdIn(idList);
//		assertThat(llc.size() == 0);
	}


	/**
	 * save 一个对象，然后修改其中一个字段，然后select出来，字段值必须是最后一次save的值
	 *
	 */
	@Test
	void test_save1() {

		final NumberEntity numberEntity = new NumberEntity();
		numberEntity.setId(1);
		numberEntity.setUserId(200);
		numberEntity.setSmallint1(2);
		numberEntity.setBigint1(300000L);
		this.nnnnnnnnn.save(numberEntity);

		numberEntity.setUserId(400);
		this.nnnnnnnnn.save(numberEntity);

		final NumberEntity ue = this.nnnnnnnnn.findById(numberEntity.getId());
		System.out.println("ue = " + ue);
		assertThat(ue.getUserId() == 400);

	}

	public static void assertThat(final boolean actual) {
		if (!actual) {
			throw new IllegalArgumentException("断言错误");
		}
	}
}
