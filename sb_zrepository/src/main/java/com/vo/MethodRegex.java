package com.vo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;

import groovy.lang.GrabConfig;

/**
 * ZRepository 方法命名规则的正则表达式
 *
 * @author zhangzhen
 * @date 2023年6月15日
 *
 */
// FIXME 2023年6月15日 下午7:42:24 zhanghen: 参照 https://www.fcors.com/archives/1719 全完成
public class MethodRegex {
	public static final String GROUP_findByXXOrderByXXLimit = "findBy(.*)OrderBy(.*)Limit";
	public static final String GROUP_findByXXOrderByXXDescLimit = "findBy(.*)OrderBy(.*)DescLimit";
	public static final String GROUP_count = "count";
	public static final String GROUP_CountingByXXX = "countingBy(.*)";
	public static final String GROUP_EXISTBYId = "existById";
	public static final String GROUP_DeleteById = "deleteById";
	public static final String GROUP_SAVEALL = "saveAll";
	public static final String GROUP_SAVE = "save";

	public static final String GROUP_UPDATE = "update";
	public static final String GROUP_FINDALL = "findAll";

	public static final String GROUP_findByXXAndXX = "findBy(.*)And(.*)";
	public static final String GROUP_findByXX = "findBy(.*)";
	public static final String GROUP_findByxxNot = "findBy(.*)Not";
	public static final String GROUP_findByxx_in = "findBy(.*)In";

	public static final String GROUP_findByXXXEndingWith= "findBy(.*)EndingWith";
	public static final String GROUP_findByXXXStartingWith= "findBy(.*)StartingWith";

	public static final String GROUP_findByXXGreaterThanEquals = "findBy(.*)GreaterThanEquals";

	public static final String GROUP_findByXXGreaterThan = "findBy(.*)GreaterThan";
	public static final String GROUP_findByXXLessThanEquals = "findBy(.*)LessThanEquals";
	public static final String GROUP_findByXXLessThan = "findBy(.*)LessThan";

	public 	static final String GROUP_findByXXLike = "findBy(.*)Like";

	public 	static final String GROUP_findByXXIsNull = "findBy(.*)IsNull";

	public static final String saveAll = GROUP_SAVEALL;
	public static final String save = GROUP_SAVE;
	public static final String findAll = GROUP_FINDALL;
	public static final String findByXX = GROUP_findByXX;
	public static final String findByXXAndYY = GROUP_findByXXAndXX;
	public static final String findByXXAndYYAndYY = "findBy(.*)And(.*)And(.*)";
	public static final String findByXXAndYYAndYYAndYY = "findBy(.*)And(.*)And(.*)And(.*)";
	public static final String findByXXAndYYAndYYAndYYAndYY = "findBy(.*)And(.*)And(.*)And(.*)And(.*)";


	public 	static final String findByXXXEndingWith = GROUP_findByXXXEndingWith;
	public 	static final String findByXXXStartingWith = GROUP_findByXXXStartingWith;
	public 	static final String findByXXNot = GROUP_findByxxNot;
	public 	static final String findByXXIn = GROUP_findByxx_in;
	public static final String findByXXInAndYYIn = "findBy(.*)InAnd(.*)In";
	public static final String findByXXInAndYYInAndYYIn = "findBy(.*)InAnd(.*)InAnd(.*)In";
	public static final String findByXXInAndYYInAndYYInAndYYIn = "findBy(.*)InAnd(.*)InAnd(.*)InAnd(.*)In";

	public static final String findByXXOrYY = "findBy(.*)Or(.*)";
	public static final String findByXXOrYYOrYY = "findBy(.*)Or(.*)Or(.*)";
	public static final String findByXXOrYYOrYYOrYY = "findBy(.*)Or(.*)Or(.*)Or(.*)";
	public static final String findByXXOrYYOrYYOrYYOrYY = "findBy(.*)Or(.*)Or(.*)Or(.*)Or(.*)";

	public 	static final String findByXXBetween = "findBy(.*)Between";

	public 	static final String findByXXGreaterThanEquals = GROUP_findByXXGreaterThanEquals;
	public 	static final String findByXXGreaterThan = GROUP_findByXXGreaterThan;
	public 	static final String findByXXLessThanEquals = GROUP_findByXXLessThanEquals;
	public 	static final String findByXXLessThan = GROUP_findByXXLessThan;

	public 	static final String findByXXOrderByXXDescLimit = GROUP_findByXXOrderByXXDescLimit;
	public 	static final String findByXXOrderByXXLimit = GROUP_findByXXOrderByXXLimit;
	public 	static final String count = GROUP_count;
	public 	static final String countingByXXX = GROUP_CountingByXXX;
	public 	static final String existById = GROUP_EXISTBYId;
	public 	static final String deleteById = GROUP_DeleteById;
	public 	static final String deleteAll = "deleteAll";

	public 	static final String findByXXLike = GROUP_findByXXLike;
//	IsNull
	public 	static final String findByXXIsNull = GROUP_findByXXIsNull;

	public static ArrayList<String> regexList = Lists.newArrayList();

	public final static HashMap<String, HashMap<String, String>> R_M = new LinkedHashMap<>();
//	findByXXOrderByXXLimit
	public final static HashMap<String, String> REGEX_MAP_findByXXOrderByXXDescLimit = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_findByXXOrderByXXLimit = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_Count = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_CountingByXXX = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_EXISTBYID = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_DELETEBYID = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_SAVEALL = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_SAVE = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_UPDATE = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_FINDBYXXAndXX = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_FINDBYXX = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_findByXXXIsNull = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_findByXXXLike = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_findByXXXEndingWith = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_StartingWith = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_findByXXNot = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_FINDBYXXIN = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_GreaterThanEquals = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_GreaterThan = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_LessThanEquals = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_LessThan = new LinkedHashMap<>();
	public final static HashMap<String, String> REGEX_MAP_FINDALL = new LinkedHashMap<>();

	public final static HashMap<String, String> REGEX_MAP_BETWEEN = new LinkedHashMap<>();

	static {
		// findByXXOrderByXXDescLimit
		REGEX_MAP_findByXXOrderByXXDescLimit.put(findByXXOrderByXXDescLimit, "select * from TABLE_NAME where @ = ? order by @ desc limit ?,?");
		// findByXXOrderByXXLimit
		REGEX_MAP_findByXXOrderByXXLimit.put(findByXXOrderByXXLimit, "select * from TABLE_NAME where @ = ? order by @ asc limit ?,?");

		// count
		REGEX_MAP_Count.put(count, "select count(*) from TABLE_NAME");
		// countingByXXX
		REGEX_MAP_CountingByXXX.put(countingByXXX, "select count(*) from TABLE_NAME  where @ = ?;");

		// existById
		REGEX_MAP_EXISTBYID.put(existById, "select count(*) from TABLE_NAME  where @ = ?;");

		// deleteById
		REGEX_MAP_DELETEBYID.put(deleteById, "delete from TABLE_NAME where @ = ?");
		REGEX_MAP_DELETEBYID.put(deleteAll, "delete from TABLE_NAME");

		// saveAll
		REGEX_MAP_SAVEALL.put(saveAll, "insert into TABLE_NAME (F) values(A)");
		// save
		REGEX_MAP_SAVE.put(save, "insert into TABLE_NAME (F) values(A);");
		// update
		REGEX_MAP_UPDATE.put(GROUP_UPDATE, "update TABLE_NAME set COLUME where id = ?; ");

		// findAll
		REGEX_MAP_FINDALL.put(findAll, "select * from TABLE_NAME");

		// findByXXIsNull
		REGEX_MAP_findByXXXIsNull.put(findByXXIsNull, "select * from TABLE_NAME where @ is null");

		// findByXXXLike
		REGEX_MAP_findByXXXLike.put(findByXXLike, "select * from TABLE_NAME where @ like ?");

		// finByXX
		REGEX_MAP_FINDBYXX.put(findByXXAndYYAndYYAndYYAndYY, "select * from TABLE_NAME where @ = ? and @ = ?  and @ = ?  and @ = ?  and @ = ? ");
		REGEX_MAP_FINDBYXX.put(findByXXAndYYAndYYAndYY, "select * from TABLE_NAME where @ = ? and @ = ?  and @ = ?  and @ = ?");
		REGEX_MAP_FINDBYXX.put(findByXXAndYYAndYY, "select * from TABLE_NAME where @ = ? and @ = ?  and @ = ?");
		REGEX_MAP_FINDBYXX.put(findByXXAndYY, "select * from TABLE_NAME where @ = ? and @ = ?");
		REGEX_MAP_FINDBYXX.put(findByXX, "select * from TABLE_NAME where @ = ?");
		// findByXXXEndingWith
		REGEX_MAP_findByXXXEndingWith.put(findByXXXEndingWith, "select * from TABLE_NAME where @ like ?");
		// findByXXXStartingWith
		REGEX_MAP_StartingWith.put(findByXXXStartingWith, "select * from TABLE_NAME where @ like ?");

		// findByXXNot
		REGEX_MAP_findByXXNot.put(findByXXNot, "select * from TABLE_NAME where @ <> ?");

		// findByXXIn
		REGEX_MAP_FINDBYXXIN.put(findByXXInAndYYInAndYYInAndYYIn, "select * from TABLE_NAME where @ in (?) and @ in (?) and @ in (?) and @ in (?)");
		REGEX_MAP_FINDBYXXIN.put(findByXXInAndYYInAndYYIn, "select * from TABLE_NAME where @ in (?) and @ in (?) and @ in (?)");
		REGEX_MAP_FINDBYXXIN.put(findByXXInAndYYIn, "select * from TABLE_NAME where @ in (?) and @ in (?)");
		REGEX_MAP_FINDBYXXIN.put(findByXXIn, "select * from TABLE_NAME where @ in (?)");

		//  findByXXGreaterThanEquals
		REGEX_MAP_GreaterThanEquals.put(findByXXGreaterThanEquals, "select * from TABLE_NAME where @ >= (?)");
		//  findByXXGreaterThan
		REGEX_MAP_GreaterThan.put(findByXXGreaterThan, "select * from TABLE_NAME where @ > (?)");
		//  findByXXLessThan
		REGEX_MAP_LessThanEquals.put(findByXXLessThanEquals, "select * from TABLE_NAME where @ <= (?)");
		// lessThan
		REGEX_MAP_LessThan.put(findByXXLessThan, "select * from TABLE_NAME where @ < (?)");

		// Between
		// FIXME 2023年6月16日 下午5:37:32 zhanghen: between先不做
//		REGEX_MAP_BETWEEN.put(findByXXBetween, "select * from TABLE_NAME where @ in (?)");

		R_M.put(GROUP_findByXXGreaterThanEquals, REGEX_MAP_GreaterThanEquals);
		R_M.put(GROUP_findByXXGreaterThan, REGEX_MAP_GreaterThan);
		R_M.put(GROUP_findByXXLessThanEquals, REGEX_MAP_LessThanEquals);
		R_M.put(GROUP_findByXXOrderByXXDescLimit, REGEX_MAP_findByXXOrderByXXDescLimit);
		R_M.put(GROUP_findByXXOrderByXXLimit, REGEX_MAP_findByXXOrderByXXLimit);
		R_M.put(GROUP_findByXXXEndingWith, REGEX_MAP_findByXXXEndingWith);
		R_M.put(GROUP_findByXXXStartingWith, REGEX_MAP_StartingWith);
		R_M.put(GROUP_findByXXIsNull, REGEX_MAP_findByXXXIsNull);
		R_M.put(GROUP_findByXXLike, REGEX_MAP_findByXXXLike);
		R_M.put(GROUP_findByXXLessThan, REGEX_MAP_LessThan);
		R_M.put(GROUP_findByxx_in, REGEX_MAP_FINDBYXXIN);
		R_M.put(GROUP_findByxxNot, REGEX_MAP_findByXXNot);
		R_M.put(GROUP_findByXXAndXX, REGEX_MAP_FINDBYXX);
		R_M.put(GROUP_findByXX, REGEX_MAP_FINDBYXX);
		R_M.put(GROUP_FINDALL, REGEX_MAP_FINDALL);
		R_M.put(GROUP_SAVEALL, REGEX_MAP_SAVEALL);
		R_M.put(GROUP_SAVE, REGEX_MAP_SAVE);
		R_M.put(GROUP_UPDATE, REGEX_MAP_UPDATE);
		R_M.put(GROUP_DeleteById, REGEX_MAP_DELETEBYID);
		R_M.put(GROUP_EXISTBYId, REGEX_MAP_EXISTBYID);
		R_M.put(GROUP_CountingByXXX, REGEX_MAP_CountingByXXX);
		R_M.put(GROUP_count, REGEX_MAP_Count);
	}


	public static Entry<String, String> check(final String methodName, final Method method) {
		final String find = methodName;

		final Collection<HashMap<String, String>> values = R_M.values();

		for (final HashMap<String, String> hashMap : values) {

			final Set<Entry<String, String>> es = hashMap.entrySet();
			for (final Entry<String, String> entry : es) {
//				System.out.println("find = " + find);
//				System.out.println("entry.getKey() = " + entry.getKey());
				if (find.matches(entry.getKey())) {
//					System.out.println(find);
//					System.out.println("匹配 k = \t" + entry.getKey());
//					System.out.println(entry.getValue());

//					System.out.println("MMMMMMMMMMMMMMMM k = " + entry.getKey());
					return entry;
				}

			}
		}

		final boolean isZQ = method.isAnnotationPresent(ZQuery.class);
		if (!isZQ) {
			throw new IllegalArgumentException(ZRepository.class.getCanonicalName() + " 不支持的方法声明 [" + methodName + "]");
		}
		// FIXME 2023年6月16日 下午8:03:40 zhanghen: 写这里，处理 @ZQuery
		return null;
	}

	public static ArrayList<String> getFieldFromMethodname(final String methdoName) {
		final ArrayList<String> sp = SqlPattern.sp(methdoName);
		return sp;

	}

}
