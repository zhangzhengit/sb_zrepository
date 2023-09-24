package com.vo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用在 @ZEntity 标记的 日期类型的字段上，表示此字段的格式
 *
 * @author zhangzhen
 * @date 2023年9月24日
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ZDateFormat {

	ZDateFormatEnum format();

}
