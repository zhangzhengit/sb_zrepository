package com.vo;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.tomcat.util.collections.ManagedConcurrentWeakHashMap;

import com.google.common.collect.Maps;

import cn.hutool.core.util.ClassUtil;

/**
 *
 * 暂存扫描的结果
 *
 * @author zhangzhen
 * @date 2023年9月5日
 *
 */
public class ClassMap {

	private static final Map<String, Set<Class<?>>> MAP = new WeakHashMap<>();

	public synchronized static Set<Class<?>> scanPackage(final String packageName) {
		final Set<Class<?>> v = MAP.get(packageName);
		if (v != null) {
			return v;
		}

		final Set<Class<?>> clsSet = ClassUtil.scanPackage(packageName);
		MAP.put(packageName, clsSet);

		return clsSet;
	}
}

