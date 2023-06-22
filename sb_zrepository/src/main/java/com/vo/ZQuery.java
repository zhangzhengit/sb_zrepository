package com.vo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * 自定义sql
 *
 * @author zhangzhen
 * @date 2023年6月16日
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ZQuery {
// FIXME 2023年6月16日 下午8:03:51 zhanghen: 解析这个
	String sql();

}
