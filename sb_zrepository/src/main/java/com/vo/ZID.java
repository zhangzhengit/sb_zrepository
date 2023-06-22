package com.vo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示 @ZEntity 标记的类里面的某个属性为ID
 *
 *
 * @author zhangzhen
 * @date 2023年6月15日
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ZID {

}
