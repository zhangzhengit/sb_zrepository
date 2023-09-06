package com.vo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vo.anno.UserRepositoryTest1;
import com.vo.anno.ZEntity;
import com.vo.anno.ZRead;
import com.vo.anno.ZWrite;
import com.vo.conn.Mode;
import com.vo.conn.ZCPool;
import com.vo.conn.ZConnection;
import com.vo.core.ZClass;
import com.vo.core.ZField;
import com.vo.core.ZLog2;
import com.vo.core.ZMethod;
import com.vo.core.ZMethodArg;
import com.vo.core.ZPackage;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;

/**
 *
 * ZR 启动类
 *
 * @author zhangzhen
 * @date 2023年6月15日
 *
 */
public class ZRepositoryMain {

	public static final String TABLE_NAME = "TABLE_NAME";

	private static final ZLog2 LOG = ZLog2.getInstance();

	public static final String _Z_CLASS = "_ZClass";
	// private static final ZCPool INSTANCE = ZCPool.getInstance();

//	public static void main(final String[] args) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		start();

//		final Set<Class<?>> scanZEntity = scanZEntity();
//		System.out.println("scanZEntity.size = " + scanZEntity.size());

//		final Mode mode = Mode.READ;
//		if(mode == Mode.READ) {
//			final String string = "com.vo.conn.Mode.READ";
//			System.out.println(string);
//		}

//		final UserRepository_ZClass zc = new UserRepository_ZClass();
//		final int n = 100;
//		final long t1 = System.currentTimeMillis();
//		final ArrayList<UserEntity> list = Lists.newArrayList();
//		for (int x = 1; x <= n; x++) {
//			final UserEntity uuu = new UserEntity();
//			uuu.setAge(x);
//			uuu.setOrderCount(x);
//			uuu.setStatus(1);
//			uuu.setName("zhang-" + x);
//
//			list.add(uuu);
//		}
//		final List<Integer> saveAll = zc.saveAll(list);
//		System.out.println("saveAll.size = " + saveAll.size());


//		final Long countingByAge = zc.countingByAge(1);
//		System.out.println("countingByAge = " + countingByAge);
//
//		System.out.println("findByNameLike.size = " + findByNameLike.size());
//		for (final UserEntity userEntity : findByNameLike) {
//			System.out.println(userEntity);
//		}
//		System.out.println("findByNameLike.size = " + findByNameLike.size());
//		final List findByIdLessThan = zc.findByIdLessThan(9);
//		System.out.println("findByIdLessThan.size = " + findByIdLessThan.size());
//		for (final Object object : findByIdLessThan) {
//			System.out.println(object);
//		}
//		System.out.println("findByIdLessThan.size = " + findByIdLessThan.size());

//		final List findByAgeLessThan = zc.findByAgeLessThan(232);
//		System.out.println("findByAgeLessThan.size = " + findByAgeLessThan.size());
//		for (final Object object : findByAgeLessThan) {
//			System.out.println(object);
//		}
//		System.out.println("findByAgeLessThan.size = " + findByAgeLessThan.size());

//		final List<UserEntity> findByNameInAndAgeIn = zc.findByNameInAndAgeIn(Lists.newArrayList("ww","bb"), Lists.newArrayList(1,2,3));
//		System.out.println("findByNameInAndAgeIn = " + findByNameInAndAgeIn);


//		final List<UserEntity> findByNameIn = zc.findByNameIn(Lists.newArrayList("zhang","list"));
//		System.out.println("findByNameIn = " + findByNameIn);

//		final List findByAgeIn = zc.findByAgeIn(Lists.newArrayList(2,3,1));
//		System.out.println("findByAgeIn = " + findByAgeIn);

//		final List findByAgeAndName = zc.findByAgeAndName(2, "zhang");
//		System.out.println("findByAgeAndName = " + findByAgeAndName);

//		final UserEntity e = new UserEntity();
//		e.setAge(333);
//		e.setName("333");
//		final UserEntity save = zc.save(e);
//		System.out.println("save = " + save);

//		final List findAll = zc.findAll();
//		System.out.println("findAll = " + findAll.size());


//		final UserEntity findById = zc.findById(1);
//		System.out.println("findByIdid = 1 = " + findById);
//
//		final List<?> in = zc.findByIdIn(Lists.newArrayList(1,2));
//		System.out.println("findByIdIn.size = " + in.size());
//		for (final Object object : in) {
//			System.out.println( "\t" + object);
//		}
//		System.out.println("findByIdIn.size = " + in.size());

//
////		final UserEntity findById = SU.findById(1, UserEntity.class, "select * from user where id = ?");
////		System.out.println("findById = " + findById);

//		final ZE ze = ZES.newZE();
//
//		final int n = 10;
//
//		final long t1 = System.currentTimeMillis();
//		for (int i = 1; i <= n; i++) {
//			test_select_1(UserEntity.class);
//		}
//		final long t2 = System.currentTimeMillis();
//		System.out.println("n = " + n + "\t ms = " + (t2-t1));
//		INSTANCE.shutdown();
//	}



	public static <T> List<T> test_select_1(final Class<T> cls) throws SQLException, InstantiationException,
			IllegalAccessException, NoSuchFieldException, SecurityException {
		final ZConnection zc = ZCPool.getInstance().getZConnection(Mode.WRITE);
		final Connection connection = zc.getConnection();

		final PreparedStatement ps = connection.prepareStatement("select * from user where id = ?");

		ps.setInt(1, 1);

		final ResultSet rs = ps.executeQuery();

		final ResultSetMetaData metaData = rs.getMetaData();
		final List<T> r = Lists.newArrayList();
		while (rs.next()) {
			final int count = metaData.getColumnCount();
			final T t = cls.newInstance();
			for (int i = 0; i < count; i++) {
				final Object columValue = rs.getObject(i + 1);
				final String columnName = metaData.getColumnLabel(i + 1);
				final String javaFieldName = ZFieldConverter.toJavaField(columnName);
				final Field field = cls.getDeclaredField(javaFieldName);
				field.setAccessible(true);
				field.set(t, columValue);
			}

			r.add(t);
//			System.out.println("T =" + t);
		}

		ZCPool.getInstance().returnZConnectionAndCommit(zc);

		return r;
	}

	public static void start() {
		System.out.println(
				java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t" + "ZRMain.start()");


		// 0 checkZEntityField
//		checkZEntityField();

		// 1 建立连接池
//		ZCPool.getInstance();

		// 2 扫描 @ZEntity的类
//		final Set<Class<?>> zeClassSet = scanZEntity();
//		gWrapperRepository(zeClassSet);

		// 3给 ZR 的子类根据方法名称来生成对应的sql
//		gSqlForZRSubclass(zeClassSet);
//		final Set<Class<?>> zrSubclassSet = scanZRSubclass();

//		generateSqlForZRSubclass(zrSubclassSet);


		// 测试 插入一条数据

		// FIXME 2023年6月16日 上午11:08:00 zhanghen: 先生成ZR的子接口的实现类
//		generateClassForZRSubinterface(zrSubclassSet);


// FIXME 2023年6月16日 上午10:59:52 zhanghen: 自己写一个aop

	}

	public static Map<Class, ZClass> generateClassForZRSubinterfaceMap(final Set<Class<?>> zrSubclassSet) {

		ZCPool.getInstance();

		LOG.info("开始给[{}]的子接口生成实现类", ZRepository.class.getCanonicalName());

		final Map<Class, ZClass> map = Maps.newConcurrentMap();

//		final Set<ZClass> zrsubIZClass = Sets.newHashSet();
		for (final Class<?> cls : zrSubclassSet) {
			LOG.info("开始给[{}]的子接口[{}]生成实现类", ZRepository.class.getCanonicalName(), cls.getCanonicalName());
			final String canonicalName = cls.getCanonicalName();
//			System.out.println("canonicalName = \n\n" + canonicalName);

			final ZClass generateZRepositorySubclass = generateMyZRepositorySubclass(cls);
			LOG.info("给[{}]的子接口[{}]生成实现类完成,className={},class=\n\n{}", ZRepository.class.getCanonicalName(),
					cls.getCanonicalName(), generateZRepositorySubclass.getName(),
					generateZRepositorySubclass.toString());

//			zrsubIZClass.add(generateZRepositorySubclass);
			map.put(cls, generateZRepositorySubclass);
//			final Object newInstance = generateZRepositorySubclass.newInstance();
//			System.out.println("generateZRepositorySubclass-newInstance = \n\n" + newInstance);
		}

		return map;
	}

	public static Map<Class, ZClass> generateClassForZRSubinterface(final Set<Class<?>> zrSubclassSet) {

		LOG.info("开始给[{}]的子接口生成实现类", ZRepository.class.getCanonicalName());

		final Map<Class, ZClass> map = Maps.newHashMap();

		for (final Class<?> cls : zrSubclassSet) {
			LOG.info("开始给[{}]的子接口[{}]生成实现类", ZRepository.class.getCanonicalName(), cls.getCanonicalName());

			final ZClass generateZRepositorySubclass = generateMyZRepositorySubclass(cls);
			LOG.info("给[{}]的子接口[{}]生成实现类完成,className={},class=\n\n{}", ZRepository.class.getCanonicalName(),
					cls.getCanonicalName(), generateZRepositorySubclass.getName(),
					generateZRepositorySubclass.toString());

			map.put(cls, generateZRepositorySubclass);

		}

		return map;
	}

	/**
	 * 扫描接口 ZRepository 的子接口(自定义的继承了ZRepository的接口)
	 *
	 * @param packageName 扫描的包名，如： com.vo
	 *
	 * @return
	 *
	 */
	public static Set<Class<?>> scanZRepositorySubinterface(final String packageName) {

		checkZEntityField(packageName);

		final Set<Class<?>> zrSubclassSet = Sets.newHashSet();
		final Set<Class<?>> clsSet = ClassMap.scanPackage(packageName);
		for (final Class<?> cls : clsSet) {

			final Class<?>[] ia = cls.getInterfaces();
			for (final Class<?> i : ia) {
				final boolean isZRSubclass = i.getCanonicalName().equals(ZRepository.class.getCanonicalName());
				if (isZRSubclass) {
					zrSubclassSet.add(cls);
				}
			}
		}

		return zrSubclassSet;
	}

	public static Set<Class<?>> scanPackage_COM() {
		// FIXME 2023年6月17日 下午6:46:40 zhanghen: com 改为属性配置
		final Set<Class<?>> clsSet = ClassUtil.scanPackage("com.vo");
		return clsSet;
	}


	// FIXME 2023年9月5日 下午9:46:27 zhanghen: 改为private，并且在第一步scanZR子接口时就校验
	public static void checkTableExist(final Set<Class<?>> zrClassSet) {
		for (final Class<?> class1 : zrClassSet) {
			final String[] typeArray = UserRepositoryTest1.findZRSubclassFanxing(class1);
			final String type = typeArray[0];
			try {
				final Class<?> typeClass = Class.forName(type);
				final ZEntity zEntity = typeClass.getAnnotation(ZEntity.class);

				checkZEntity_TableNameExist(zEntity.tableName());
			} catch (final ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 * @param zrClassSet
	 *
	 * @return
	 */
	public static List<SqlResult> generateSqlForZRSubclass(final Set<Class<?>> zrClassSet) {

		LOG.info("ZRepositoryStarter开始生成[{}]的子接口的SQL模板", ZRepository.class.getCanonicalName());

		final List<SqlResult> sqlResultlist = Lists.newArrayList();

		for (final Class<?> zrSubClass : zrClassSet) {
			LOG.info("ZRepositoryStarter开始生成[{}]的SQL模板", zrSubClass.getCanonicalName());
			final String[] typeArray = UserRepositoryTest1.findZRSubclassFanxing(zrSubClass);

			final Method[] ms = zrSubClass.getMethods();

			for (final Method m : ms) {
				LOG.info("ZRepositoryStarter开始生成[{}]的方法[{}]的SQL模板", zrSubClass.getCanonicalName(), m.getName());

				final Entry<String, String> check = MethodRegex.check(m.getName(), m);

				final String type = typeArray[0];
				try {
					final Class<?> typeClass = Class.forName(type);
					final ZEntity zEntity = typeClass.getAnnotation(ZEntity.class);

					checkZEntityZID(typeClass);

					final String zrSubClassName = zrSubClass.getCanonicalName();
					final String methodName = m.getName();
					final String sqlTemplate = check.getValue();

					final String tableName = zEntity.tableName();
					final String sqlTemplateTemp = sqlTemplate.replace(TABLE_NAME, tableName);

					final String sqlFinal = checkMethodName(typeClass, methodName, sqlTemplateTemp);

					final SqlResult result = new SqlResult(zrSubClassName, methodName, sqlFinal);

					sqlResultlist.add(result);

				} catch (final ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		return sqlResultlist;
	}

	/**
	 * 校验 @ZEntity 指定的tableName是否存在
	 *
	 * @param tableName
	 *
	 */
	private static void checkZEntity_TableNameExist(final String tableName) {
		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "ZRepositoryMain.checkZEntity_TableNameExist()");

		final ZConnection zc = ZCPool.getInstance().getZConnection(Mode.READ);
		ResultSet rs = null;
		final Connection connection = zc.getConnection();
		try {
			connection.setAutoCommit(false);
			final DatabaseMetaData metaData = connection.getMetaData();
			rs = metaData.getTables(null, null, tableName, null);
			if (!rs.next()) {
				throw new IllegalArgumentException(
						ZEntity.class.getSimpleName() + " 指定的tableName不存在，tableName = " + tableName);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			ZCPool.getInstance().returnZConnectionAndCommit(zc);
			if (rs != null) {
				try {
					rs.close();
				} catch (final SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 校验一下方法声明是否正确，是否符合命名规则，见MethodRegex.GROUP_ 开头的常量正则表达式
	 *
	 * @param typeClass  @ZEntity标记的类
	 * @param methodName ZRepository 子类中的自定义的findByXX的方法名称,如findByUserId
	 * @param sql        sql模板，如：select * from user where @ = ?
	 * @return 返回可用于java.sql.PreparedStatement 的SQL语句， 如 : select * from user where
	 *         id = ?
	 *
	 */
	private static String checkMethodName(final Class<?> typeClass,final String methodName, final String sql) {
		// methodName 从每个大写字母分开分成一个数组，如findByUserId 分成[find, By, User, Id]

		final List<String> fnLIst = getDeclaredFieldName(typeClass);

		System.out.println("methodName = " + methodName);
		final List<String> alList = splitMethodNameToArray(methodName);
		System.out.println("fnLIst = \n");
		System.out.println(fnLIst);
		System.out.println("alList = \n");
		System.out.println(alList);

		// findByUserId 分成[find, By, User, Id] ，从前往后计算是否sql关键字，否则按照entity字段处理

		final HashSet<String> sqlKeyword = SqlPattern.SQL_KEYWORD;

		// SQL关键字按从长到短排序，防止出现 Or优先于Order被替换掉，剩余 der
		final ArrayList<String> skList = Lists.newArrayList(sqlKeyword);
		skList.sort(Comparator.comparing(String::length).reversed());

		String mn2 = methodName;
		for (final String sk : skList) {
			mn2 = mn2.replace(sk, "-");
			System.out.println("sk = " + sk);
			System.out.println("mn2 = " + mn2);
		}

		System.out.println("alList-removeAll-sqlKeyword = \n");
		System.out.println(alList);

		System.out.println("mn2 = " + mn2);
		final String[] fieldNameArray = mn2.split("-");
		System.out.println("fieldNameArray = " + Arrays.toString(fieldNameArray));
		final List<String> fieldNameList = Lists.newArrayList(fieldNameArray).stream().filter(x -> StrUtil.isNotBlank(x)).collect(Collectors.toList());

		for (final String fn : fieldNameList) {
			final Optional<String> findAny = fnLIst.stream().filter(f1 -> f1.equalsIgnoreCase(fn)).findAny();
			if (!findAny.isPresent()) {
				// FIXME 2023年8月26日 下午5:50:54 zhanghen: TODO 提示信息再详细一点
				throw new IllegalArgumentException(ZRepository.class.getSimpleName() + " 子类自定义方法声明错误，methodName = " + methodName + "，"
						+  "请确认方法名由SQL关键字和@ZEntity类中的字段组成，"
						+  "方法名称命名规则见 " + MethodRegex.class.getSimpleName() + " 中以 GROUP_ 开头的常量。"
						);
			}
		}

		System.out.println("fieldNameList = " + fieldNameList);
		String sqlA = sql;
		for (final String fieldName : fieldNameList) {
			final String dbColumnName = ZFieldConverter.toDbField(fieldName);
			sqlA = sqlA.replaceFirst("@", dbColumnName);
		}
		System.out.println("sql = " + sql);
		System.out.println("sqlA = " + sqlA);

		// 仍然包含 @
		if (sqlA.contains("@")) {
			throw new IllegalArgumentException("请检查自定义方法名称，methodName = " + methodName);
		}

		return sqlA;

	}

	private static List<String> getDeclaredFieldName(final Class<?> typeClass) {
		final Field[] fs = typeClass.getDeclaredFields();
		final List<String> fieldNameList = Arrays.asList(fs).stream().map(f -> f.getName()).collect(Collectors.toList());
		return fieldNameList;
	}

	/**
	 * 把一个自定义方法名按大写字母拆开，拆成一个数组。如findByUserId拆分成[find, By, User, Id]
	 *
	 * @param zRepositoryMethodName
	 * @return
	 *
	 */
	private static List<String> splitMethodNameToArray(final String zRepositoryMethodName) {
		if (StrUtil.isEmpty(zRepositoryMethodName)) {
			return Collections.emptyList();
		}

		final char[] ch = zRepositoryMethodName.toCharArray();
		final List<String> aL = Lists.newArrayList();
		int from = 0;
		for (int i = 1; i < ch.length; i++) {
			final boolean isDaxie = SqlPattern.daxie.contains(ch[i]);
			if (isDaxie) {
				final String temp = zRepositoryMethodName.substring(from, i);
//				System.out.println("temp = " + temp);
				aL.add(temp);
				from = i;
			}
			if (i == ch.length - 1) {
				final String temp2 = zRepositoryMethodName.substring(from,  ch.length);
//				System.out.println("temp2 = " + temp2);
				aL.add(temp2);
			}

		}

		return aL;
	}


	/**
	 * 用指定的 className 生成一个 ZRepository 的实现类
	 *
	 * @return
	 *
	 */
	private static ZClass generateMyZRepositorySubclass(final Class<?> myZRClass) {

		final ZClass zClass = new ZClass();

		// FIXME 2023年8月26日 下午5:54:25 zhanghen: com.vo改为配置项
		zClass.setPackage1(new ZPackage("com"));

		//		userR.setImplementsSet(Sets.newHashSet(canonicalName + " <T, ID> "));
//		userR.setName(canonicalName + "_ZClass" + "<T,ID>");

		zClass.setImportSet(Sets.newHashSet(myZRClass.getName()));
		zClass.setImplementsSet(Sets.newHashSet(myZRClass.getSimpleName()));
		zClass.setName(myZRClass.getSimpleName() + _Z_CLASS);


		final String[] typeArray = UserRepositoryTest1.findZRSubclassFanxing(myZRClass);

		final ZField zField = new ZField("Class<" + typeArray[0] + ">", "classType",typeArray[0] + ".class");

		zClass.setFieldSet(Sets.newHashSet(zField));

		final Set<ZMethod> zmSet = new HashSet<>();

		final Set<ZMethod> zmSet1 = addZMethod(myZRClass);
		zmSet.addAll(zmSet1);


		zClass.setMethodSet(zmSet);
		return zClass;
	}

	private static Set<ZMethod> addZMethod(final Class myZRClass) {
		final Method[] ms = myZRClass.getMethods();

		final String[] typeArray = UserRepositoryTest1.findZRSubclassFanxing(myZRClass);
//		System.out.println("typeArray = " + Arrays.toString(typeArray));

		final Set<ZMethod> zmSet = new HashSet<>();
		for (final Method method : ms) {

			final ZWrite write = method.getAnnotation(ZWrite.class);
			final ZRead read = method.getAnnotation(ZRead.class);
			if (write != null && read != null) {
				throw new IllegalArgumentException("方法[" + method.getName() + "] 不能同时存在 "
						+ ZWrite.class.getCanonicalName() + " 和 " + ZRead.class.getCanonicalName());
			}

			final ZMethod zm = new ZMethod();
			zm.setAbstract(false);
			// FIXME 2023年9月6日 下午3:54:50 zhanghen: 加入 zm.setSynchronized(true);后ZR子接口的代理类的自定义方法上加了sync，是否合理？
			zm.setSynchronized(true);
			zm.setName(method.getName());
			final Class<?> returnType = method.getReturnType();
			zm.setReturnType(returnType.getCanonicalName());
//			System.out.println("returnType.getCanonicalName() = " + returnType.getCanonicalName());
			if (returnType.getCanonicalName().equals(Object.class.getCanonicalName())) {
				zm.setReturnType(typeArray[0]);
			}

			final ArrayList<ZMethodArg> argLIst = Lists.newArrayList();

			final Parameter[] parameters = method.getParameters();
			for (final Parameter p1 : parameters) {

				if ("id".equals(p1.getName())) {
					argLIst.add(new ZMethodArg(typeArray[1], p1.getName()));
					continue;
				}

				if ("save".equals(method.getName())) {
					if (p1.getType().getCanonicalName().equals(Object.class.getCanonicalName())) {
						argLIst.add(new ZMethodArg(typeArray[0], p1.getName()));
					}
				} else {
					argLIst.add(new ZMethodArg(p1.getType(), p1.getName()));
				}
			}

			zm.setgReturn(false);
			zm.setMethodArgList(argLIst);
			// FIXME 2023年9月6日 下午3:16:13 zhanghen: Save 方法加入sync关键字，因为它有save和findBYid两个方法组成


			zmSet.add(zm);
			final String sql = ZRSqlMap.get(myZRClass.getCanonicalName(), zm.getName());

			final String body = "String sql = \""+sql+"\";";

			final String body2 = "";
//			final String methodS = "return " + SU.class.getCanonicalName() + ".findById(id,classType,sql);";
			final String methodS = getSuMethod(method.getName(), method);

//			final String body = sql;
			zm.setBody("\t" +body + "\n\t" + body2  + "\n\t" + methodS);
		}
		return zmSet;
	}

	static String getSuMethod(final String methodName, final Method method) {

		final String modeString = modeString(method);

		switch (method.getName()) {
		case "findById":
			return "return " + SU.class.getCanonicalName() + ".findById(" + modeString + ", id,classType,sql);";

		case "findByIdIn":
			return "return " + SU.class.getCanonicalName() + ".findByIdIn(" + modeString + " ,idList,classType,sql);";

		case "findAll":
			return "return " + SU.class.getCanonicalName() + ".findAll(" + modeString + ", classType,sql);";

		case "saveAll":
			return "return " + SU.class.getCanonicalName() + ".saveAll(" + modeString + ", classType,sql,tList);";

		case "update":
			return "return " + SU.class.getCanonicalName() + ".update(" + modeString + ", classType,t,sql);";

		case "save":

			return "return " + SU.class.getCanonicalName() + ".save(" + modeString + ", classType,t,sql);";

//			final String string =
//				   "Object id = " + SU.class.getCanonicalName() + ".save(" + modeString + ", classType,t,sql);" + "\n\t"
//				+ "final Object v1 = com.vo.ZC.get(\"myZRSubclass_save_getInterfaces_0.getCanonicalName\");" + "\n\t"
//				+ "final Object v = v1 == null ? this.getClass().getInterfaces()[0].getCanonicalName() : v1;" + "\n\t"
//				+ "final String zrCN = String.valueOf(v);" + "\n\t"
//				+ "com.vo.ZC.put(\"myZRSubclass_save_getInterfaces_0.getCanonicalName\", v);" + "\n\t"
//				+ "final String findByIDSql = "+ZRSqlMap.class.getCanonicalName()+".get(zrCN, \"findById\");"  + "\n\t"
//				+ "return "+SU.class.getCanonicalName()+".findById(" + modeString + ", id, this.classType, findByIDSql);";
//
//			return string;

		case "existById":

			return "return " + SU.class.getCanonicalName() + ".existById(" + modeString + ", id,classType,sql);";

		case "deleteById":
			return "return " + SU.class.getCanonicalName() + ".deleteById(" + modeString + ", id,classType,sql);";

		case "deleteByIdIn":
			return "return " + SU.class.getCanonicalName() + ".deleteByIdIn(" + modeString + ", idList,classType,sql);";


		case "deleteAll":

			return "return " + SU.class.getCanonicalName() + ".deleteAll(" + modeString + ", classType,sql);";

		default:

			// default  ZR的子类声明的方法
			final Entry<String, String> check = MethodRegex.check(methodName, method);
//			System.out.println("getSuMethod-check = " + check);
			final String key = check.getKey();

			if (key.matches(MethodRegex.GROUP_findByXXOrderByXXDescLimit)) {
				return gROUP_findByXXOrderByXXDescLimit(method, key);
			}

			if (key.matches(MethodRegex.GROUP_findByXXOrderByXXLimit)) {
				return gROUP_findByXXOrderByXXLimit(method, key);
			}

			if (key.matches(MethodRegex.GROUP_findByXXXEndingWith)) {
				return gROUP_findByXXXEndingWith(method, key);
			}

			if (key.matches(MethodRegex.GROUP_findByXXXStartingWith)) {
				return gROUP_findByXXXStartingWith(method, key);
			}

			if (key.matches(MethodRegex.GROUP_findByXXGreaterThan)) {
				return gROUP_findByXXGreaterThan(method, key);
			}

			if (key.matches(MethodRegex.GROUP_findByXXLessThanEquals)) {
				return GROUP_findByXXLessThanEquals(method, key);
			}

			if (key.matches(MethodRegex.GROUP_findByxx_in)) {
				return gROUP_findByxx_in(method, key);
			}

			if (key.matches(MethodRegex.GROUP_findByXXLessThan)) {
				return gROUP_findByXXLessThan(method, key);
			}

			if (key.matches(MethodRegex.GROUP_CountingByXXX)) {
				return gROUP_CountingByXXX(method, key);
			}
//
			if (key.matches(MethodRegex.GROUP_findByXXIsNull)) {
				return "return " + SU.class.getCanonicalName() + ".findByXXIsNull(" + modeString + ",classType,sql);";
			}

			if (key.matches(MethodRegex.GROUP_findByXXLike)) {
				return gROUP_findByXXLike(method, key);
			}

			if (key.matches(MethodRegex.GROUP_count)) {
				return "return " + SU.class.getCanonicalName() + ".count(" + modeString + ",classType,sql);";
			}

			if (key.matches(MethodRegex.GROUP_findByXXAndXX)) {
				return gROUP_findByXXAndXX(method, key);
			}
			// 最短的排最后
			if (key.matches(MethodRegex.GROUP_findByXX)) {
				return gROUP_findByXX(method, key);
			}

			break;
		}

		return "";
	}


	/**
	 * 返回声明方法是 @ZRead 还是 @ZWrite ，无默认为 @ZWrite
	 *
	 *
	 * @param method
	 * @return
	 */
	private static String modeString(final Method method) {
		if (method.isAnnotationPresent(ZRead.class)) {
			return "com.vo.conn.Mode.READ";
		}
		return "com.vo.conn.Mode.WRITE";
	}


	private static String gROUP_findByXXXEndingWith(final Method method, final String key) {
		final HashMap<String, String> hh = MethodRegex.R_M.get(MethodRegex.GROUP_findByXXXEndingWith);
		final Set<Entry<String, String>> entrySet = hh.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			if (key.matches(entry.getKey())) {
				final Parameter[] parameters2 = method.getParameters();
				final StringJoiner joiner = new StringJoiner(",");
				for (final Parameter parameter : parameters2) {
					joiner.add(parameter.getName());
				}
				final String modeString = modeString(method);
				return "return " + SU.class.getCanonicalName() + ".findByXXXEndingWith("+modeString+",classType,sql," + joiner.toString()	+ ");";
			}
		}
		// FIXME 2023年6月16日 下午4:49:57 zhanghen: 抛异常，不支持的方法
		return "";
	}


	private static String gROUP_findByXXXStartingWith(final Method method, final String key) {
		final HashMap<String, String> hh = MethodRegex.R_M.get(MethodRegex.GROUP_findByXXXStartingWith);
		final Set<Entry<String, String>> entrySet = hh.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			if (key.matches(entry.getKey())) {
				final Parameter[] parameters2 = method.getParameters();
				final StringJoiner joiner = new StringJoiner(",");
				for (final Parameter parameter : parameters2) {
					joiner.add(parameter.getName());
				}
				final String modeString = modeString(method);
				return "return " + SU.class.getCanonicalName() + ".findByXXXStartingWith("+modeString+",classType,sql," + joiner.toString()	+ ");";
			}
		}
		// FIXME 2023年6月16日 下午4:49:57 zhanghen: 抛异常，不支持的方法
		return "";
	}


	private static String gROUP_findByXXGreaterThan(final Method method, final String key) {
		final HashMap<String, String> hh = MethodRegex.R_M.get(MethodRegex.GROUP_findByXXGreaterThan);
		final Set<Entry<String, String>> entrySet = hh.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			if (key.matches(entry.getKey())) {
				final Parameter[] parameters2 = method.getParameters();
				final StringJoiner joiner = new StringJoiner(",");
				for (final Parameter parameter : parameters2) {
					joiner.add(parameter.getName());
				}
				final String modeString = modeString(method);
				return "return " + SU.class.getCanonicalName() + ".findByIdLessThan("+modeString+",classType,sql," + joiner.toString()	+ ");";
			}
		}
		// FIXME 2023年6月16日 下午4:49:57 zhanghen: 抛异常，不支持的方法
		return "";
	}


	private static String GROUP_findByXXLessThanEquals(final Method method, final String key) {
		final HashMap<String, String> hh = MethodRegex.R_M.get(MethodRegex.GROUP_findByXXLessThanEquals);
		final Set<Entry<String, String>> entrySet = hh.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			if (key.matches(entry.getKey())) {
				final Parameter[] parameters2 = method.getParameters();
				final StringJoiner joiner = new StringJoiner(",");
				for (final Parameter parameter : parameters2) {
					joiner.add(parameter.getName());
				}
				final String modeString = modeString(method);
				return "return " + SU.class.getCanonicalName() + ".findByIdLessThan("+modeString+",classType,sql," + joiner.toString()	+ ");";
			}
		}
		// FIXME 2023年6月16日 下午4:49:57 zhanghen: 抛异常，不支持的方法
		return "";
	}


	private static String gROUP_findByXXOrderByXXLimit(final Method method, final String key) {
		final HashMap<String, String> hh = MethodRegex.R_M.get(MethodRegex.GROUP_findByXXOrderByXXLimit);
		final Set<Entry<String, String>> entrySet = hh.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			if (key.matches(entry.getKey())) {
				final Parameter[] parameters2 = method.getParameters();
				final StringJoiner joiner = new StringJoiner(",");
				for (final Parameter parameter : parameters2) {
					joiner.add(parameter.getName());
				}
				final String modeString = modeString(method);
				return "return " + SU.class.getCanonicalName() + ".findByXXOrderByXXLimit("+modeString+",classType,sql," + joiner.toString()	+ ");";
			}
		}
		// FIXME 2023年6月16日 下午4:49:57 zhanghen: 抛异常，不支持的方法
		return "";
	}


	private static String gROUP_findByXXOrderByXXDescLimit(final Method method, final String key) {
		final HashMap<String, String> hh = MethodRegex.R_M.get(MethodRegex.GROUP_findByXXOrderByXXDescLimit);
		final Set<Entry<String, String>> entrySet = hh.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			if (key.matches(entry.getKey())) {
				final Parameter[] parameters2 = method.getParameters();
				final StringJoiner joiner = new StringJoiner(",");
				for (final Parameter parameter : parameters2) {
					joiner.add(parameter.getName());
				}

				final String modeString = modeString(method);
				return "return " + SU.class.getCanonicalName() + ".findByXXOrderByXXLimit(" + modeString
						+ ",classType,sql," + joiner.toString() + ");";
			}
		}
		// FIXME 2023年6月16日 下午4:49:57 zhanghen: 抛异常，不支持的方法
		return "";
	}


	private static String gROUP_CountingByXXX(final Method method, final String key) {
		final HashMap<String, String> hh = MethodRegex.R_M.get(MethodRegex.GROUP_CountingByXXX);
		final Set<Entry<String, String>> entrySet = hh.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			if (key.matches(entry.getKey())) {
				final Parameter[] parameters2 = method.getParameters();
				final StringJoiner joiner = new StringJoiner(",");
				for (final Parameter parameter : parameters2) {
					joiner.add(parameter.getName());
				}
				final String modeString = modeString(method);
				return "return " + SU.class.getCanonicalName() + ".countingByXX("+modeString+",classType,sql," + joiner.toString()
						+ ");";
			}
		}
		// FIXME 2023年6月16日 下午4:49:57 zhanghen: 抛异常，不支持的方法
		return "";
	}

	private static String gROUP_findByXXLike(final Method method, final String key) {
		final HashMap<String, String> hh = MethodRegex.R_M.get(MethodRegex.GROUP_findByXXLike);
		final Set<Entry<String, String>> entrySet = hh.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			if (key.matches(entry.getKey())) {
				final Parameter[] parameters2 = method.getParameters();
				final StringJoiner joiner = new StringJoiner(",");
				for (final Parameter parameter : parameters2) {
					joiner.add(parameter.getName());
				}
				final String modeString = modeString(method);
				return "return " + SU.class.getCanonicalName() + ".findByXXLike("+modeString+",classType,sql," + joiner.toString()
						+ ");";
			}
		}
		// FIXME 2023年6月16日 下午4:49:57 zhanghen: 抛异常，不支持的方法
		return "";
	}

	private static String gROUP_findByXXLessThan(final Method method, final String key) {
		final HashMap<String, String> hh = MethodRegex.R_M.get(MethodRegex.GROUP_findByXXLessThan);
		final Set<Entry<String, String>> entrySet = hh.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			if (key.matches(entry.getKey())) {
				final Parameter[] parameters2 = method.getParameters();
				final StringJoiner joiner = new StringJoiner(",");
				for (final Parameter parameter : parameters2) {
					joiner.add(parameter.getName());
				}
				final String modeString = modeString(method);
				return "return " + SU.class.getCanonicalName() + ".findByIdLessThan("+modeString+",classType,sql," + joiner.toString()
						+ ");";
			}
		}
		// FIXME 2023年6月16日 下午4:49:57 zhanghen: 抛异常，不支持的方法
		return "";
	}


	private static String gROUP_findByxx_in(final Method method, final String key) {
		final HashMap<String, String> hh = MethodRegex.R_M.get(MethodRegex.GROUP_findByxx_in);
		final Set<Entry<String, String>> entrySet = hh.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			if (key.matches(entry.getKey())) {
				final Parameter[] parameters2 = method.getParameters();
				final StringJoiner joiner = new StringJoiner(",");
				for (final Parameter parameter : parameters2) {
					joiner.add(parameter.getName());
				}
				final String modeString = modeString(method);
				return "return " + SU.class.getCanonicalName() + ".findByXXIn("+modeString+",classType,sql," + joiner.toString()	+ ");";
			}
		}
		// FIXME 2023年6月16日 下午4:49:57 zhanghen: 抛异常，不支持的方法
		return "";
	}


	private static String gROUP_findByXXAndXX(final Method method, final String key) {
		final HashMap<String, String> hh = MethodRegex.R_M.get(MethodRegex.GROUP_findByXX);
		final Set<Entry<String, String>> entrySet = hh.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			if (key.matches(entry.getKey())) {
				final Parameter[] parameters2 = method.getParameters();
				final StringJoiner joiner = new StringJoiner(",");
				for (final Parameter parameter : parameters2) {
					joiner.add(parameter.getName());
				}
				final String modeString = modeString(method);
				return "return " + SU.class.getCanonicalName() + ".findByXX("+modeString+",classType,sql," + joiner.toString()	+ ");";
			}
		}
		// FIXME 2023年6月16日 下午4:49:57 zhanghen: 抛异常，不支持的方法
		return "";
	}
	private static String gROUP_findByXX(final Method method, final String key) {
		final HashMap<String, String> hh = MethodRegex.R_M.get(MethodRegex.GROUP_findByXX);
		final Set<Entry<String, String>> entrySet = hh.entrySet();
		for (final Entry<String, String> entry : entrySet) {
			if (key.matches(entry.getKey())) {
				final Parameter[] parameters2 = method.getParameters();
				final StringJoiner joiner = new StringJoiner(",");
				for (final Parameter parameter : parameters2) {
					joiner.add(parameter.getName());
				}
				final String modeString = modeString(method);
				return "return " + SU.class.getCanonicalName() + ".findByXX("+modeString+",classType,sql," + joiner.toString()	+ ");";
			}
		}
		// FIXME 2023年6月16日 下午4:49:57 zhanghen: 抛异常，不支持的方法
		return "";
	}

	/**
	 * 校验 @ZEntity 中的字段，如：不允许出现SQL关键字等等
	 * @param packageName TODO
	 *
	 * @return 返回 @ZEntity 的类
	 *
	 */
	private static Set<Class<?>> checkZEntityField(final String packageName) {
		final Set<Class<?>> zeSet = Sets.newHashSet();
		final Set<Class<?>> clsSet = ClassMap.scanPackage(packageName);
		for (final Class<?> c : clsSet) {
			final boolean isZE = c.isAnnotationPresent(ZEntity.class);
			if (!isZE) {
				continue;
			}

			final Field[] fs = c.getDeclaredFields();
			for (final Field f : fs) {
				final String fName = f.getName();
				final boolean sqlKeyword = SqlPattern.isSqlKeyword(fName);
				if (sqlKeyword) {
					throw new IllegalArgumentException(ZEntity.class.getSimpleName() + " 类中字段不允许出现SQL关键字," +ZEntity.class.getSimpleName()+" 类 = " +
							c.getSimpleName() + ",filed = " + fName);
				}

			}

			zeSet.add(c);
		}

		return zeSet;
	}

	/**
	 * 校验 @ZEntity 类必须有 @ZID 注解，并且字段是主键
	 *
	 * @param typeClass
	 * @param p TODO
	 *
	 */
	private synchronized static void checkZEntityZID(final Class<?> typeClass) {

		if (cc.contains(typeClass)) {
			return;
		}
		cc.add(typeClass);

		final ZEntity ze = typeClass.getAnnotation(ZEntity.class);
		if (ze == null) {
			return;
		}

		System.out.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t"
				+ "ZRMain.checkZEntityZID()");

		final Field[] fs = typeClass.getDeclaredFields();
		final List<Field> zidList = Lists.newArrayList(fs).stream().filter(f -> f.isAnnotationPresent(ZID.class))
				.collect(Collectors.toList());

		if (CollUtil.isEmpty(zidList)) {
			throw new IllegalArgumentException(ZEntity.class.getSimpleName() + " 类型 " + typeClass.getSimpleName()
					+ " 必须有 " + ZID.class.getSimpleName() + " 字段");
		}

		if (zidList.size() != 1) {
			final String fsA = zidList.stream().map(f -> f.getName()).collect(Collectors.joining(","));
			throw new IllegalArgumentException(ZEntity.class.getSimpleName() + " 类型 " + typeClass.getSimpleName()
					+ " 只能有一个 " + ZID.class.getSimpleName() + " 字段，现有有两个：" + fsA);
		}

		final String name = zidList.get(0).getName();

		connnection(typeClass, name, ZCPool.getInstance().getZConnection(Mode.READ));

		connnection(typeClass, name, ZCPool.getInstance().getZConnection(Mode.WRITE));

		checkZEntityFiled(typeClass);

	}

	/**
	 * 校验 @ZEntity 类里的属性，必须和表的列名和类型匹配
	 *
	 * @param typeClass
	 *
	 */
	private static void checkZEntityFiled(final Class<?> typeClass) {

		final ZConnection zConnection = ZCPool.getInstance().getZConnection(Mode.READ);

		final String tableName = typeClass.getAnnotation(ZEntity.class).tableName();

		final Field[] fs = typeClass.getDeclaredFields();

		try {
			final DatabaseMetaData metaData = zConnection.getConnection().getMetaData();

			try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
				System.out.println("tablename = " + tableName);
				while (columns.next()) {
					final String columnName = columns.getString("COLUMN_NAME");
					final String columnType = columns.getString("TYPE_NAME");

					System.out.println("Column Name: " + columnName);
					System.out.println("Column Type: " + columnType);
					System.out.println("-----------------------");

					// FIXME 2023年9月4日 下午8:09:58 zhanghen:  TODO 写一个db类型和java类型对应关系，所有typeClass.field都必须名称和类型完全匹配，否则抛异常
				}
			}

		} catch (final SQLException e) {
			e.printStackTrace();
		}

	}



	private static void c(final Class<?> typeClass, final String name, final DatabaseMetaData metaDataREAD) {
		ResultSet primaryKeys = null;
		try {

			final String tableName = typeClass.getAnnotation(ZEntity.class).tableName();
			primaryKeys = metaDataREAD.getPrimaryKeys(null, null, tableName);
			if (!primaryKeys.next()) {
				throw new IllegalArgumentException(ZEntity.class.getSimpleName() + " 类型 " + typeClass.getSimpleName()
						+ " " + ZID.class.getSimpleName() + " 字段 " + name + " 在数据库中不存在");
			}

			final String columnName = primaryKeys.getString("COLUMN_NAME");
//			System.out.println("tableName = " + tableName + "\t" + "主键列名：" + columnName);
			if (!Objects.equals(name, columnName)) {
				throw new IllegalArgumentException(ZEntity.class.getSimpleName() + " 类型 " + typeClass.getSimpleName()
						+ " " + ZID.class.getSimpleName() + " 字段名称与数据库主键名称不一致，" + ZID.class.getSimpleName() + " 名称："
						+ name + "，数据库主键名称：" + columnName);
			}

		} catch (final SQLException e) {
			e.printStackTrace();
		} finally {
			if (primaryKeys != null) {
				try {
					primaryKeys.close();
				} catch (final SQLException e) {
					e.printStackTrace();
				}
			}

		}
	}

	static HashSet<Class> cc = Sets.newHashSet();

	private static void connnection(final Class<?> typeClass, final String name, final ZConnection zConnection) {
		final Connection connection = zConnection.getConnection();
		try {
			connection.setAutoCommit(false);

			final DatabaseMetaData metaData = connection.getMetaData();

			c(typeClass, name, metaData);
		} catch (final SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			ZCPool.getInstance().returnZConnectionAndCommit(zConnection);
		}
	}

}
