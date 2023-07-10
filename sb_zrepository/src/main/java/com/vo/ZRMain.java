package com.vo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;

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
public class ZRMain {

	public static final String _Z_CLASS = "_ZClass";
	// private static final ZCPool INSTANCE = ZCPool.getInstance();
	private static final ZLog2 LOG = ZLog2.getInstance();

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

		ZCPool.getInstance().returnZConnection(zc);

		return r;
	}

	public static void start() {
		System.out.println(
				java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t" + "ZRMain.start()");


		// 1 建立连接池
//		ZCPool.getInstance();

		// 2 扫描 @ZEntity的类
		final Set<Class<?>> zeClassSet = scanZEntity();
//		gWrapperRepository(zeClassSet);

		// 3给 ZR 的子类根据方法名称来生成对应的sql
//		gSqlForZRSubclass(zeClassSet);
		final Set<Class<?>> zrSubclassSet = scanZRSubclass();

		generateSqlForZRSubclass(zrSubclassSet);


		// 测试 插入一条数据

		// FIXME 2023年6月16日 上午11:08:00 zhanghen: 先生成ZR的子接口的实现类
		generateClassForZRSubinterface(zrSubclassSet);


// FIXME 2023年6月16日 上午10:59:52 zhanghen: 自己写一个aop

	}

	public static Map<Class, ZClass> generateClassForZRSubinterfaceMap(final Set<Class<?>> zrSubclassSet) {

		LOG.info("开始给[{}]的子接口生成实现类", ZRepository.class.getCanonicalName());

		final Map<Class, ZClass> map = Maps.newConcurrentMap();

		final Set<ZClass> zrsubIZClass = Sets.newHashSet();
		for (final Class<?> cls : zrSubclassSet) {
			LOG.info("开始给[{}]的子接口[{}]生成实现类", ZRepository.class.getCanonicalName(), cls.getCanonicalName());
			final String canonicalName = cls.getCanonicalName();
//			System.out.println("canonicalName = \n\n" + canonicalName);

			final ZClass generateZRepositorySubclass = generateMyZRepositorySubclass(canonicalName, cls);
			LOG.info("给[{}]的子接口[{}]生成实现类完成,className={},class=\n\n{}", ZRepository.class.getCanonicalName(),
					cls.getCanonicalName(), generateZRepositorySubclass.getName(),
					generateZRepositorySubclass.toString());

			zrsubIZClass.add(generateZRepositorySubclass);
			map.put(cls, generateZRepositorySubclass);
//			final Object newInstance = generateZRepositorySubclass.newInstance();
//			System.out.println("generateZRepositorySubclass-newInstance = \n\n" + newInstance);
		}

		return map;
	}

	public static Set<ZClass> generateClassForZRSubinterface(final Set<Class<?>> zrSubclassSet) {


		LOG.info("开始给[{}]的子接口生成实现类", ZRepository.class.getCanonicalName());

		final Set<ZClass> zrsubIZClass = Sets.newHashSet();
		for (final Class<?> cls : zrSubclassSet) {
			LOG.info("开始给[{}]的子接口[{}]生成实现类", ZRepository.class.getCanonicalName(), cls.getCanonicalName());
			final String canonicalName = cls.getCanonicalName();
//			System.out.println("canonicalName = \n\n" + canonicalName);

			final ZClass generateZRepositorySubclass = generateMyZRepositorySubclass(canonicalName, cls);
			LOG.info("给[{}]的子接口[{}]生成实现类完成,className={},class=\n\n{}", ZRepository.class.getCanonicalName(),
					cls.getCanonicalName(), generateZRepositorySubclass.getName(),
					generateZRepositorySubclass.toString());

			zrsubIZClass.add(generateZRepositorySubclass);
//			final Object newInstance = generateZRepositorySubclass.newInstance();
//			System.out.println("generateZRepositorySubclass-newInstance = \n\n" + newInstance);
		}

		return zrsubIZClass;

	}

	public static Set<Class<?>> scanZRSubclass() {
		final Set<Class<?>> zrSubclassSet = Sets.newHashSet();
		final Set<Class<?>> clsSet = scanPackage_COM();
		for (final Class<?> class1 : clsSet) {

			final Class<?>[] ia = class1.getInterfaces();
			for (final Class i : ia) {
				final boolean isZRSubclass = i.getCanonicalName().equals(ZRepository.class.getCanonicalName());
				if (isZRSubclass) {

					zrSubclassSet.add(class1);
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


	/**
	 *
	 * @param zeClassSet
	 *
	 * @author zhangzhen
	 * @date 2023年6月16日
	 */
	public static void generateSqlForZRSubclass(final Set<Class<?>> zeClassSet) {

		LOG.info("ZRepositoryStarter开始生成[{}]的子接口的SQL模板", ZRepository.class.getCanonicalName());

		for (final Class<?> class1 : zeClassSet) {
			LOG.info("ZRepositoryStarter开始生成[{}]的SQL模板", class1.getCanonicalName());
			final String[] typeArray = UserRepositoryTest1.findZRSubclassFanxing(class1);

			final Method[] ms = class1.getMethods();
			for (final Method m : ms) {
				LOG.info("ZRepositoryStarter开始生成[{}]的方法[{}]的SQL模板", class1.getCanonicalName(), m.getName());

				final Entry<String, String> check = MethodRegex.check(m.getName(), m);

				final String type = typeArray[0];
				try {
					final Class<?> typeClass = Class.forName(type);
					final ZEntity zea = typeClass.getAnnotation(ZEntity.class);
					if (zea == null) {
						throw new IllegalArgumentException("类型[" + typeClass.getCanonicalName() + "]缺少["
								+ ZEntity.class.getCanonicalName() + "]注解");
					}

					final String tableName = zea.tableName();

					final String sqlTemplate = check.getValue();

					final ArrayList<String> spList = SqlP.sp(m.getName());

					final List<String> fieldlist = SqlP.getField(spList);

					 String sql = sqlTemplate.replace("TABLE_NAME", tableName);


					final int atCount = StrUtil.count(sql, "@");
					if (atCount != fieldlist.size()) {
						throw new IllegalArgumentException("请检查方法声明 [" + m.getName() + "]");
					}
					for(int i = 0;i<fieldlist.size();i++) {
						sql = sql.replaceFirst("@", fieldlist.get(i).toLowerCase());
					}

					LOG.info("ZRepositoryStarter生成[{}]的方法[{}]的SQL模板=[{}]", class1.getCanonicalName(), m.getName(), sql);

					final String zrSubClassName = class1.getCanonicalName();
					final String methodName = m.getName();

					ZRSqlMap.put(zrSubClassName, methodName, sql);

				} catch (final ClassNotFoundException e) {
					e.printStackTrace();
				}

			}
		}

	}


	/**
	 * 用指定的 className 生成一个 ZRepository 的实现类
	 *
	 * @param className
	 * @param myZRClass TODO
	 * @return
	 *
	 */
	private static ZClass generateMyZRepositorySubclass(final String className, final Class myZRClass) {

		final String canonicalName = myZRClass.getCanonicalName();
//		final String canonicalName = ZRepository.class.getCanonicalName();
		final ZClass userR = new ZClass();
		userR.setPackage1(new ZPackage("com.vo"));

		//		userR.setImplementsSet(Sets.newHashSet(canonicalName + " <T, ID> "));
//		userR.setName(canonicalName + "_ZClass" + "<T,ID>");

		userR.setImportSet(Sets.newHashSet(myZRClass.getName()));
		userR.setImplementsSet(Sets.newHashSet(myZRClass.getSimpleName()));
		userR.setName(myZRClass.getSimpleName() + _Z_CLASS);


		final String[] typeArray = UserRepositoryTest1.findZRSubclassFanxing(myZRClass);

		final ZField zField = new ZField("Class<" + typeArray[0] + ">", "classType",typeArray[0] + ".class");

		userR.setFieldSet(Sets.newHashSet(zField));

		final Set<ZMethod> zmSet = new HashSet<>();

		final Set<ZMethod> zmSet1 = addZMethod(myZRClass);
		zmSet.addAll(zmSet1);


		userR.setMethodSet(zmSet);
		return userR;
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

		case "save":

			final String string =
				   "Object id = " + SU.class.getCanonicalName() + ".save(" + modeString + ", classType,t,sql);" + "\n\t"
				+ "final Object v1 = com.vo.ZC.get(\"myZRSubclass_save_getInterfaces_0.getCanonicalName\");" + "\n\t"
				+ "final Object v = v1 == null ? this.getClass().getInterfaces()[0].getCanonicalName() : v1;" + "\n\t"
				+ "final String zrCN = String.valueOf(v);" + "\n\t"
				+ "com.vo.ZC.put(\"myZRSubclass_save_getInterfaces_0.getCanonicalName\", v);" + "\n\t"
				+ "final String findByIDSql = "+ZRSqlMap.class.getCanonicalName()+".get(zrCN, \"findById\");"  + "\n\t"
				+ "return "+SU.class.getCanonicalName()+".findById(" + modeString + ", id, this.classType, findByIDSql);";

			return string;

		case "existById":

			return "return " + SU.class.getCanonicalName() + ".existById(" + modeString + ", id,classType,sql);";

		case "deleteById":
			return "return " + SU.class.getCanonicalName() + ".deleteById(" + modeString + ", id,classType,sql);";

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

	private static Set<Class<?>> scanZEntity() {
		final Set<Class<?>> zeSet = Sets.newHashSet();
		final Set<Class<?>> clsSet = ClassUtil.scanPackage();
		for (final Class<?> class1 : clsSet) {
			final boolean isZE = class1.isAnnotationPresent(ZEntity.class);
			if (isZE) {
//				System.out.println("@ZEntity = " + class1.getCanonicalName());
				zeSet.add(class1);
			}
		}

		return zeSet;
	}



}
