package com.vo;

import java.util.Date;

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
@ZEntity(tableName = "number")
public class NumberEntity {

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

//	@ZDateFormat(format = ZDateFormatEnum.YYYY_MM_DD_HH_MM_SS)
	private Date createTime;

}
