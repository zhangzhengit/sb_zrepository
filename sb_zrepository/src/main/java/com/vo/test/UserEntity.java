package com.vo.test;

import com.vo.ZID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *
 * @author zhangzhen
 * @date 2023年6月15日
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ZEntity(tableName = "user")
//@ZEntityFragmentation(end = 10)
public class UserEntity {

	@ZID
	private Long id;

	private Integer orderCount;

	private String name;

	private Integer age;

	private Integer status;

}
