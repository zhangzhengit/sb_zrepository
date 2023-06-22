package com.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

/**
 *
 *
 * @author zhangzhen
 * @date 2023年6月15日
 *
 */
@SpringBootTest
class AAAATests {

	@Autowired
	UserRepository userRepository;



	//@Test
	void testZR_mot_1() {
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "AAAATests.testZR_mot_1()");

		final List<UserEntity> findByNameNot = this.userRepository.findByNameNot("zhang");
		System.out.println("findByNameNot.size = " + findByNameNot.size());
		for (final UserEntity userEntity : findByNameNot) {
			System.out.println("\t" + userEntity);
		}
		System.out.println("findByNameNot.size = " + findByNameNot.size());

	}

	@Test
	void testZR_insert_and_orderBy2() {
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "AAAATests.testZR_insert_and_orderBy2()");


		final List<UserEntity> findByIdOrderByIdLimit = this.userRepository.findByStatusOrderByIdDescLimit(1L, 0,42);
		System.out.println("findByIdOrderByIdLimit.size = " + findByIdOrderByIdLimit.size());
		for (final UserEntity userEntity : findByIdOrderByIdLimit) {
			System.out.println("\t" + userEntity);
		}
		System.out.println("findByIdOrderByIdLimit.size = " + findByIdOrderByIdLimit.size());

	}
	static {
		ZCPool.getInstance();
	}

	@Test
	void test_finbyId_bingfa_1() {
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "AAAATests.test_finbyId_bingfa_1()");

		final AtomicInteger wanch = new AtomicInteger();

		final int n = 10000 * 1;

		final ZE ze = ZES.newZE(24);
		for (int i = 1; i <= n; i++) {
			final long id = i;
			ze.executeInQueue(() -> {
				final long t1 = System.currentTimeMillis();
				final UserEntity findById = this.userRepository.findById(id);
				final long t2 = System.currentTimeMillis();

				System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t" + "id = " + id
						+ "\t" + "ms = " + (t2-t1)
						+ "\t" + "ue = " + findById);

				final int incrementAndGet = wanch.incrementAndGet();
			});

		}


		while (true) {
			final int i = wanch.get();
			if (i >= n) {
				break;
			}
		}
		System.out.println("全部完成 ,time = " + LocalDateTime.now());

	}


	@Test
	void test_saveAll1() {
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "AAAATests.test_saveAll1()");

		final int aa = 10000 * 1;

		final AtomicInteger wanch = new AtomicInteger();

		final ZE ze = ZES.newZE();

		for (int a = 1; a <= aa; a++) {

			final int k =a;

			ze.executeInQueue(() -> {
				final int n = 1000;
				final long t1 = System.currentTimeMillis();
				final ArrayList<UserEntity> list = Lists.newArrayList();
				for (int x = 1; x <= n; x++) {
					final UserEntity uuu = new UserEntity();
					uuu.setAge(x);
					uuu.setOrderCount(x);
					uuu.setStatus(1);
					uuu.setName("zhang-" + x);

					list.add(uuu);
				}
				final List<Long> saveAll = AAAATests.this.userRepository.saveAll(list);

				final long t2 = System.currentTimeMillis();

				System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t" + "insert a = " + k + "\t" +  "n = " + n +"\t" + "ms = " + (t2-t1));
				System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t" + "saveAll.size = " + saveAll.size());

//				final Long count = this.userRepository.count();
//				System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t" + "count = " + count);

				final int incrementAndGet = wanch.incrementAndGet();
			});

		}

		while (true) {
			final int i = wanch.get();
			if (i >= aa) {
				break;
			}
		}
		System.out.println("全部完成 ,time = " + LocalDateTime.now());
	}


//	@Test
	void testZR_insert_and_orderBy1() {
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "AAAATests.testZR_insert_and_orderBy1()");

		this.userRepository.deleteAll();

		final int n = 1000;
		final long t1 = System.currentTimeMillis();
		for (int x = 1; x <= n; x++) {
			final UserEntity uuu = new UserEntity();
			uuu.setAge(x);
			uuu.setOrderCount(x * 1);
			uuu.setStatus(1);
			uuu.setName("zhang-" + x);

			final UserEntity save = this.userRepository.save(uuu);
			System.out.println("i = " + x + "\t" + "save = " + save);
		}

		final long t2 = System.currentTimeMillis();
		System.out.println("insert n = " + n +"\t" + "ms = " + (t2-t1));

		System.out.println("SU.save_MS = " + SU.save_MS);
		System.out.println("SU.findByID_MS = " + SU.findByID_MS);
		System.out.println("SU.findByID__NEWT_MS = " + SU.findByID__NEWT_MS);
	}


	@Test
	void testZR1() {
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "AAAATests.test_r1()");

//		final List<UserEntity> findByNameStartingWith = this.userRepository.findByNameEndingWith("i");
//		System.out.println("findByNameStartingWith.size = " + findByNameStartingWith.size());
//		for (final UserEntity userEntity : findByNameStartingWith) {
//			System.out.println(userEntity);
//		}
//		System.out.println("findByNameStartingWith.size = " + findByNameStartingWith.size());

//		final List<UserEntity> findByAgeGreaterThan = this.userRepository.findByAgeGreaterThan(99999999);
//		System.out.println("findByAgeGreaterThan.size = " + findByAgeGreaterThan.size());
//		for (final UserEntity userEntity : findByAgeGreaterThan) {
//			System.out.println(userEntity);
//		}
//		System.out.println("findByAgeGreaterThan.size = " + findByAgeGreaterThan.size());

//		final List<UserEntity> findByAgeLessThanEquals = this.userRepository.findByAgeLessThanEquals(200);
//		System.out.println("findByAgeLessThanEquals.size = " + findByAgeLessThanEquals.size());
//		for (final UserEntity userEntity : findByAgeLessThanEquals) {
//			System.out.println("\t" + userEntity);
//		}
//		System.out.println("findByAgeLessThanEquals.size = " + findByAgeLessThanEquals.size());


//		final List<UserEntity> findByAgeOrderByIdLimit = this.userRepository.findByAgeOrderByNameLimit(200, 12);
//		System.out.println("findByAgeOrderByIdLimit.size = " + findByAgeOrderByIdLimit.size());
//		for (final UserEntity userEntity : findByAgeOrderByIdLimit) {
//			System.out.println(userEntity);
//		}
//		System.out.println("findByAgeOrderByIdLimit.size = " + findByAgeOrderByIdLimit.size());

//		final Long count = this.userRepository.count();
//		System.out.println("count = " + count);




//		final Long countingByAge = this.userRepository.countingByAge(1);
//		System.out.println("countingByAge = " + countingByAge);

//		final List<UserEntity> findByNameIsNull = this.userRepository.findByNameIsNull();
//		System.out.println("findByNameIsNull.size = " + findByNameIsNull.size());
//		for (final UserEntity userEntity : findByNameIsNull) {
//			System.out.println(userEntity);
//		}
//		System.out.println("findByNameIsNull.size = " + findByNameIsNull.size());


//		final List<UserEntity> findByNameLike = this.userRepository.findByNameLike("zh");
//
//		System.out.println("findByNameLike.size = " + findByNameLike.size());
//		for (final UserEntity userEntity : findByNameLike) {
//			System.out.println(userEntity);
//		}
//		System.out.println("findByNameLike.size = " + findByNameLike.size());
//		final List<UserEntity> findAll = this.userRepository.findAll();
//		System.out.println("findAll.size = " + findAll.size());
	}


//	//@Test
	void test_r1() {
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "AAAATests.test_r1()");

		final int n = 2000;
		for (int i = 1; i <= n; i++) {


			assertThat(MethodRegex.findByXXIn.equals(MethodRegex.check("findByNameIn", null).getKey()));

			assertThat(MethodRegex.findByXX.equals(MethodRegex.check("findByName", null).getKey()));
			assertThat(MethodRegex.findByXXAndYY.equals(MethodRegex.check("findByNameAndBB", null).getKey()));
			assertThat(MethodRegex.findByXXAndYYAndYY.equals(MethodRegex.check("findByNameAndBAndC", null).getKey()));
			assertThat(
					MethodRegex.findByXXAndYYAndYYAndYY.equals(MethodRegex.check("findByNameAndBAndCAndD", null).getKey()));
			assertThat(MethodRegex.findByXXAndYYAndYYAndYYAndYY
					.equals(MethodRegex.check("findByNameAndBAndCAndDAndE", null).getKey()));
		}
	}

//	//@Test
	void testZR_deleteAll() {
		this.userRepository.deleteAll();
	}

	//@Test
	void contextLoads() {
	}

	public static void assertThat(final boolean ok) {
		if (!ok) {
			throw new IllegalArgumentException("出错");
		}

	}

}
