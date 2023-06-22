package com.vo;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import org.checkerframework.checker.units.qual.m;
import org.checkerframework.checker.units.qual.s;

import com.google.common.collect.Maps;

import groovy.transform.stc.FirstParam.ThirdGenericType;

/**
 *
 *
 * @author zhangzhen
 * @date 2023年6月17日
 *
 */
public class ZC {

	static ConcurrentMap<Object, Object> map = Maps.newConcurrentMap();

	public static Object get(final Object key) {
		return map.get(key);

	}

	public static Object getBySupplier(final Object key, final Supplier<Object> supplier) {
		final Object v = get(key);
		if (v != null) {
			return v;
		}

		synchronized (key) {
			final Object v2 = supplier.get();
			put(key, v2);
			return v2;
		}

	}

	public static void put(final Object key, final Object value) {
		map.put(key, value);
	}

}
