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
@ZEntity(tableName = "orders")
//@ZEntityFragmentation(end = 10)
public class OrderEntity {

	@ZID
	private Integer id;

	private Integer userId;
	private String name;

}
