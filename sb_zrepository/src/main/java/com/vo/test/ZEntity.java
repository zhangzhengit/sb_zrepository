package com.vo.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 用在数据库表对应的实体类
 *
 * @author zhangzhen
 * @date 2023年6月15日
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ZEntity {

	// FIXME 2023年6月15日 下午1:45:52 zhanghen: 支持分库分表

	/**
	 * 对应的数据表名称
	 *
	 * @return
	 *
	 */
	String tableName();

}
