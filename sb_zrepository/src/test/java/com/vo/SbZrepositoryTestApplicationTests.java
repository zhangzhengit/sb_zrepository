package com.vo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.vo.test.NumberEntity;
import com.vo.test.NumberZRepository;
import com.votool.ze.ZE;
import com.votool.ze.ZES;

import cn.hutool.core.lang.UUID;

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
	void test_findByXXLike_2() {
		final List<NumberEntity> list = this.nnnnnnnnn.findByNameLike("a");
		System.out.println("list.size = " + list.size());
		System.out.println("list = " + list);
	}

	@Test
	void test_findByXXLike_1() {
		final List<NumberEntity> list = this.nnnnnnnnn.findByNameStartingWith("1a");
		System.out.println("list.size = " + list.size());
		System.out.println("list = " + list);
	}

	@Test
	void test_findByUserIdAndName2() {

		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "SbZrepositoryTestApplicationTests.test_findByUserIdAndName2()");
		final NumberEntity e = new NumberEntity();
		e.setName(UUID.randomUUID().toString());
		e.setAge(200);


		final List<NumberEntity> l2 = this.nnnnnnnnn.findByAgeAndName(e.getAge(), e.getName());
		assertThat(l2.size() == 0);

		this.nnnnnnnnn.save(e);

		final List<NumberEntity> l2x = this.nnnnnnnnn.findByAgeAndName(e.getAge(), e.getName());
		assertThat(l2x.size() == 1);
		System.out.println("l2x = " + l2x);




	}
	@Test
	void test_findByUserIdAndName() {
		final NumberEntity e = new NumberEntity();
		e.setName(UUID.randomUUID().toString());

		final NumberEntity save = this.nnnnnnnnn.save(e);

		System.out.println("save.id = " + save.getId());
		assertThat(save.getName().equals(e.getName()));

	}


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
	 *
	 * 根据id查，结果必须是id等于参数id的那一条
	 *
	 */
	@Test
	void test_save3() {
		final int n = 2000;
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
