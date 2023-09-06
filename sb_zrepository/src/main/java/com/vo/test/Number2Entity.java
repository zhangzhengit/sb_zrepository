package com.vo.test;

import com.vo.ZID;
import com.vo.anno.ZEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *
 * @author zhangzhen
 * @date 2023年8月20日
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ZEntity(tableName = "number2")
public class Number2Entity {

	@ZID
	private Integer id;
//	private Integer Order;

	private Integer userId;
	private Long bigint1;

	private Integer smallint1;

	private Integer orderCount;

	private String name;

	private Integer age;

	private Integer status;

}
