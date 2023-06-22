package com.vo.transaction;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解式事务，用在方法上，方法执行成功则自动提交，否则回滚
 *
 * @author zhangzhen
 * @date 2023年6月17日
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ZTransaction {

}
