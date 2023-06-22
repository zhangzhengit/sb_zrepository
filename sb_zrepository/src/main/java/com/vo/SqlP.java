package com.vo;

import java.awt.Menu;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.checkerframework.checker.units.qual.s;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.data.redis.connection.ReactiveKeyCommands.RenameCommand;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mysql.cj.protocol.a.TextResultsetReader;

/**
 * 根据方法名拼出sql
 *
 * @author zhangzhen
 * @date 2023年6月15日
 *
 */
public class SqlP {

	public static ArrayList<String> sp(final String methodName) {
		final ArrayList<String> pl = Lists.newArrayList();
		if (!xiaoxie.contains(methodName.charAt(0))) {
			throw new IllegalArgumentException("方法名[" + methodName + "]必须以小写字母开始");
		}

		final char[] ca = methodName.toCharArray();

		for (int i = 0; i < ca.length;) {
			final char c = ca[i];
			P.add(c);

			if (daxie.contains(c)) {
				P.poll();
				final String string = P.get();
				pl.add(string);

				P.add(c);
			}
			if (i + 1 >= ca.length) {
				final String s2 = P.get();
				pl.add(s2);
//				System.out.println("一个关键词 = " + s2);
			}
			i++;

		}

		return pl;

	}

	public static List<String> getField(final ArrayList<String> pList) {

		final List<String> fieldNameLIst = pList.stream().filter(p -> !zrKeyword.contains(p))
				.collect(Collectors.toList());
		return fieldNameLIst;

	}

	/**
	 * ZRepository 声明式方法的关键字
	 */
	static HashSet<String> zrKeyword = Sets.newHashSet();

	static HashSet<String> inOrLikeAnd = Sets.newHashSet("And", "In", "Or", "Like");
	static HashSet<String> keyword = Sets.newHashSet("find", "delete", "save", "exist", "page", "By", "In", "Or",
			"Like");
	static HashSet<String> methodPrefix = Sets.newHashSet("find", "delete", "save", "exist", "page");

	static HashSet<Character> xiaoxie = Sets.newHashSet('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');

	static HashSet<Character> daxie = Sets.newHashSet('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');

	static {
		zrKeyword.addAll(inOrLikeAnd);
		zrKeyword.addAll(keyword);
		zrKeyword.addAll(methodPrefix);

		zrKeyword.add("All");
		zrKeyword.add("Less");
		zrKeyword.add("Than");
		zrKeyword.add("Is");
		zrKeyword.add("Null");
		zrKeyword.add("counting");
		zrKeyword.add("count");

		zrKeyword.add("Limit");
		zrKeyword.add("Order");
		zrKeyword.add("Desc");
		zrKeyword.add("Asc");
		zrKeyword.add("Equals");
		zrKeyword.add("Equal");
		zrKeyword.add("Greater");

		zrKeyword.add("Starting");
		zrKeyword.add("Ending");
		zrKeyword.add("With");
		zrKeyword.add("Not");

	}

	public static class P {

		static ArrayList<Character> cl = Lists.newArrayList();

		public static void add(final char c) {
			cl.add(c);
		}

		public static void poll() {
			cl.remove(cl.size() - 1);

		}

		public static String get() {
			final char[] ca = new char[cl.size()];
			for (int i = 0; i < cl.size(); i++) {
				ca[i] = cl.get(i);
			}

			cl.clear();

			final String r = new String(ca);
			return r;
		}

	}

}
