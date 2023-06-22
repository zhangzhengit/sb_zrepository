package com.vo.core;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentMap;

import javax.swing.text.MaskFormatter;

import org.aspectj.weaver.tools.cache.CacheBacking;
import org.checkerframework.checker.units.qual.g;

import com.google.common.collect.Maps;
import com.mysql.cj.conf.StringProperty;
import com.votool.redis.mq.TETS_MQ_1;

import cn.hutool.core.util.ArrayUtil;
import groovy.json.StreamingJsonBuilder;

/**
 *
 *
 * @author zhangzhen
 * @date 2023年6月18日
 *
 */
public class ZU {

	static ConcurrentMap<Class, Object> clsMap = Maps.newConcurrentMap();

	public static Object getSingletonInstance(final Class cls) {
		final Object v1 = clsMap.get(cls);
		if (v1 != null) {
			return v1;
		}

		synchronized (cls) {
			try {
				final Object v2 = cls.newInstance();
				clsMap.put(cls, v2);
				return v2;
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static String[] getSuperType(final Class cls) {

		final Type[] genericInterfaces = cls.getGenericInterfaces();
		if (ArrayUtil.isEmpty(genericInterfaces)) {
			return null;
		}
//		System.out.println("genericInterfaces = " + Arrays.toString(genericInterfaces));

		final StringJoiner joiner = new StringJoiner(",");
		for (final Type type : genericInterfaces) {
			final String s = type.toString();
			final int i = s.indexOf("<");
			final int i2 = s.indexOf(">");
			final String sa = s.substring(i + 1, i2);

			joiner.add(sa);
		}

		return joiner.toString().split(",");
	}
}
