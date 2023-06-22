package com.vo.test;

import java.util.List;

import com.fasterxml.jackson.databind.jsontype.impl.AsExistingPropertyTypeSerializer;
import com.vo.ZRepository;
import com.vo.anno.ZRead;
import com.vo.anno.ZWrite;

/**
 * UserRepository
 *
 * @param <T>
 * @param <ID>
 *
 * @author zhangzhen
 * @date 2023年6月15日
 *
 */
public interface UserRepository extends com.vo.ZRepository<UserEntity, Long> {

	@Override
	@ZRead
	UserEntity findById(Long id);

	List<UserEntity> findByNameNot(String name);

//	List<UserEntity> getAll();
	List<UserEntity> findByNameEndingWith(String name);

	List<UserEntity> findByIdOrderByNameDescLimit(Long id, int offset, int rows);
	List<UserEntity> findByAgeOrderByNameDescLimit(Integer status, int offset, int rows);
	List<UserEntity> findByStatusOrderByIdDescLimit(Long status, int offset, int rows);

	List<UserEntity> findByNameStartingWith(String name);

	List<UserEntity> findByAgeGreaterThanEquals(Integer age);

	List<UserEntity> findByAgeGreaterThan(Integer age);

	List<UserEntity> findByAgeLessThanEquals(Integer age);

	List<UserEntity> findByAgeOrderByNameLimit(Integer age, int limit);

	List<UserEntity> findByAgeOrderByIdDescLimit(Integer age, int limit);

	List<UserEntity> findByAgeOrderByIdLimit(Integer age, int limit);

	Long countingByAge(Integer age);

	List<UserEntity> findByNameIsNull();

	List<UserEntity> findByNameLike(String name);

	List<UserEntity> findByAgeLessThan(Integer age);

	List<UserEntity> findByIdLessThan(Long id);

	List<UserEntity> findByNameInAndAgeIn(List<String> nameList, List<Integer> ageList);

	List<UserEntity> findByNameIn(List<String> nList);

	List<UserEntity> findByAgeIn(List<Integer> ages);

	List<UserEntity> findByAgeAndName(Integer age, String name);

	List<UserEntity> findByAge(Integer age);

	List<UserEntity> findByName(String name);

	List<UserEntity> findByStatus(Integer status);

	List<UserEntity> findByStatusIn(List<Integer> statusList);
}