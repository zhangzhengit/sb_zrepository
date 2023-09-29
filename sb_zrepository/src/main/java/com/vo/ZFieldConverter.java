package com.vo;

import java.util.HashSet;

import com.google.common.collect.Sets;

/**
 * 数据库字段 <> java对象字段转换, 如： order_count <> orderCount
 *
 * @author zhangzhen
 * @date 2023年6月16日
 *
 */
public class ZFieldConverter {

	public static String toJavaField(final String dbFieldName) {
		final char[] charArray = dbFieldName.toCharArray();

		String n = dbFieldName;
		for (int i = 0; i < charArray.length; i++) {
			final char c = charArray[i];
			if (c == '_') {
				final char nextC = charArray[i + 1];
				n = n.replace("_" + nextC, String.valueOf(nextC).toUpperCase());

			}
		}

		return n;
	}

	public static String toDbField(final String javaFieldName) {
		final char[] charArray = javaFieldName.toCharArray();

//		String n = javaFieldName;
		final StringBuilder n = new StringBuilder(javaFieldName);
		for (int i = 0; i < charArray.length; i++) {
			final char c = charArray[i];
			if (daxie.contains(c)) {
				if (i == 0) {
					n.replace(i, i + 1, String.valueOf(c).toLowerCase());
//					n = n.replace(String.valueOf(c), String.valueOf(c).toLowerCase());
				} else {
					n.replace(i, i + 1, "_" + String.valueOf(c).toLowerCase());
//					n = n.replace(String.valueOf(c), "_" + String.valueOf(c).toLowerCase());
				}
			}
		}

		return n.toString();
	}

	static HashSet<Character> daxie = Sets.newHashSet('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');

}
