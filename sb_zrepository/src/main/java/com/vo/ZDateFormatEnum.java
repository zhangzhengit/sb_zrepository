package com.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日期格式枚举
 *
 * @author zhangzhen
 * @date 2023年9月24日
 *
 */
@Getter
@AllArgsConstructor
public enum ZDateFormatEnum {

	// FIXME 2023年9月24日 下午3:41:59 zhanghen: TODO 添加各种格式
	YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
	YYYY_MM_DD("yyyy-MM-dd"),

	;

	private String format;

}
