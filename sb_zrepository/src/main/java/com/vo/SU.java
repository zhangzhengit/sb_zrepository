package com.vo;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PrimitiveIterator.OfDouble;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.LittleEndianDataInputStream;
import com.vo.anno.ZEntity;
import com.vo.conn.Mode;
import com.vo.conn.ZCPool;
import com.vo.conn.ZConnection;
import com.vo.conn.ZDatasourceProperties;
import com.vo.conn.ZDatasourcePropertiesLoader;
import com.vo.core.Page;
import com.vo.core.ZLog2;
import com.vo.transaction.ZTransactionAspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

/**
 * @see ZRepository 接口和其子接口里的方法的具体实现
 *
 * @author zhangzhen
 * @date 2023年6月16日
 *
 */
// FIXME 2023年9月16日 下午7:57:12 zhanghen: 考虑清楚每个方法 @ZID 字段为空怎么处理
public class SU {

	private static final String LIMIT = "limit";
	private static final ZLog2 LOG = ZLog2.getInstance();
	private static final ZDatasourceProperties ZDP = ZDatasourcePropertiesLoader.getInstance();

	private static final ZCPool INSTANCE = ZCPool.getInstance();

	public static <T> Page<T> page(final Mode mode, final Class<T> cls, final T t, final String sql, final Integer size, final Integer page) {
		System.out
				.println(java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t" + "SU.page()");

		if (size <= 0) {
			throw new IllegalArgumentException("size 必须大于0！size = " + size);
		}
		if (page <= 0) {
			throw new IllegalArgumentException("page 必须大于0！page = " + page);
		}

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();
//		PreparedStatement ps = null;
//		ResultSet rs  = null;
		try {
			connection.setAutoCommit(false);

			final Map<String, Object> fMap = getNotNullFieldMap(t);
			final String sqlFinal = sql.replace("COLUMN", "").replace("where", "");
			if (CollUtil.isEmpty(fMap)) {
				return page0(mode, cls, size, page, zc, sqlFinal, true);
			}

			final Set<Entry<String, Object>> es = fMap.entrySet();
			final StringBuilder builder = new StringBuilder();
			for (final Entry<String, Object> entry : es) {
				final String fieldName = entry.getKey();
				final Object fieldValue = entry.getValue();

				// FIXME 2023年9月6日 下午9:05:35 zhanghen: 测试不同类型的字段，看直接append(toString)是否报错
				builder.append(" ").append(fieldName).append(" = ");
				if (fieldValue instanceof String) {
					builder.append("'").append(fieldValue).append("'");
				} else if (fieldValue instanceof Number) {
					builder.append(fieldValue);
				}

				builder.append(" and ");
			}
			final String x = builder.replace(builder.length()-5, builder.length(), "").toString();
			final String sqlFinalX = sql.replace("COLUMN", x);

			return page0(mode, cls, size, page, zc, sqlFinalX, false);

		} catch (final SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException e1) {
			e1.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		} finally {
//			close(ps, rs);
			ZCPool.getInstance().returnZConnectionAndCommit(zc);
		}

		return null;
	}

	private static <T> Page<T> page0(final Mode mode, final Class<T> cls, final Integer size, final Integer page,
			final ZConnection zc, final String sqlFinal, final boolean tAllFieldNull)
			throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		final PreparedStatement	ps = zc.getConnection().prepareStatement(sqlFinal);
		final int offset = (page -1) * size;
		final int rows = size;
		ps.setInt(1, offset);
		ps.setInt(2, rows);

		if (ZDP.getShowSql()) {
			LOG.info("[{}],[{},{}]", sqlFinal, offset, rows);
		}

		final ResultSet	rs = ps.executeQuery();
		final ResultSetMetaData metaData = rs.getMetaData();

		final int count = metaData.getColumnCount();
		final List<T> rL = Lists.newArrayList();
		while (rs.next()) {
			final T tR = newT(cls, rs, metaData, count);
			rL.add(tR);
		}

		final String tableName = cls.getAnnotation(ZEntity.class).tableName();

		final int limitI = sqlFinal.indexOf(LIMIT);
		final String countSQLNotNUll = sqlFinal.replace("select * ", "select count(*) ");

		final String countSQL = tAllFieldNull ? "select count(*) from " + tableName
				: sqlFinal.substring(0, limitI).replace("select * ", "select count(*) ");
		final Long countR = count(mode, cls, countSQL, zc);

		final long pages = countR.longValue() % size == 0 ? countR.longValue() / size
				: countR.longValue() / size + 1;
		final Page<T> pageR = new Page(size, Long.valueOf(String.valueOf(page)), pages, countR, ImmutableList.copyOf(rL));
		return pageR;
	}

	/**
	 * 获取T对象里非空的字段，返回<字段名称,字段值>
	 *
	 * @param <T>
	 * @param t
	 * @return
	 *
	 */
	private static <T> Map<String, Object> getNotNullFieldMap(final T t) {
		final Map<String, Object> fMap = Maps.newHashMap();
		final Field[] fs = t.getClass().getDeclaredFields();
		for (final Field f : fs) {
			f.setAccessible(true);
			try {
				final Object v = f.get(t);
				if (v == null) {
					continue;
				}

				fMap.put(f.getName(), v);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return fMap;
	}

	public static <T> T update(final Mode mode, final Class<T> cls, final T t, final String sql) {
		final Field[] fs = t.getClass().getDeclaredFields();
//		final ArrayList<Field> fList = Lists.newArrayList(fs);
		final StringBuilder column =new StringBuilder();
		for (int i = 0; i < fs.length; i++) {
			final Field field = fs[i];
			field.setAccessible(true);

			try {
				final Object fV = field.get(t);

				final String dbName = ZFieldConverter.toDbField(field.getName());
				column.append(dbName).append('=');
				if (fV != null) {
					if (fV instanceof String) {
						column.append("'").append(fV).append("'");
					} else if (fV instanceof Date) {
						// FIXME 2023年8月1日 下午8:50:26 zhanghen: TODO 日期时间的字段，新增注解：表示插入的格式
						final String vD = DateUtil.format((Date) fV, DatePattern.NORM_DATETIME_FORMAT);
						column.append("'").append(vD).append("'");
					} else {
						column.append(fV);
					}
				} else {
					column.append(" null");
				}
				column.append(',');

			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}

		}

		if (column.length() <= 0) {
			// XXX T 所有字段值都是null， 是不执行update，还是抛异常？
			return t;
		}

		final Optional<Field> zidO = Lists.newArrayList(fs).stream().filter(f -> f.isAnnotationPresent(ZID.class)).findAny();
		if (!zidO.isPresent()) {
			throw new IllegalArgumentException(
					"无 " + ZID.class.getSimpleName() + " 标记的属性，t = " + t.getClass().getCanonicalName());
		}

		final Field idField = zidO.get();

		idField.setAccessible(true);
		Object idValue = null;
		try {
			idValue = idField.get(t);
		} catch (IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}

		if (Objects.isNull(idValue)) {
			throw new IllegalArgumentException("update方法参数 t 的 " + ZID.class.getSimpleName() + " 字段不能为空！t = " + t);
		}

		final String sqlFinal = sql.replace("COLUMN", column.replace(column.length()-1, column.length(), ""));

		final ZConnection zc = ZCPool.getInstance().getZConnection(mode);

		try {
			zc.getConnection().setAutoCommit(false);
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}
		PreparedStatement ps  = null;
		try {
			ps = zc.getConnection().prepareStatement(sqlFinal);
			ps.setObject(1, idValue);

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", sqlFinal, idValue);
			}

			final int executeUpdate = ps.executeUpdate();

		} catch (final Exception e) {
			e.printStackTrace();
			try {
				zc.getConnection().rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			ZCPool.getInstance().returnZConnectionAndCommit(zc);
			close(ps);
		}
		// XXX 直接返回T可以吗？
		return t;
	}

	public static <T> boolean deleteAll(final Mode mode, final Class<T> cls, final String sql) {

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();
		try {
			connection.setAutoCommit(false);
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}
		PreparedStatement ps =null;
		try {
			final String s = sql;
			ps = connection.prepareStatement(s);
			// FIXME 2023年9月6日 上午2:39:45 zhanghen: 配置为参数
			ps.setQueryTimeout(22);

			if (ZDP.getShowSql()) {
				LOG.info("[{}]", s);
			}

			final int executeUpdate = ps.executeUpdate();

			return executeUpdate >= 1;

		} catch (SQLException | SecurityException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			INSTANCE.returnZConnectionAndCommit(zc);
			close(ps);
		}

		return false;
	}

	public static <T> boolean deleteByIdIn(final Mode mode, final List<Object> idList, final Class<T> cls, final String sql) {

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();
		try {
			connection.setAutoCommit(false);
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}

		PreparedStatement ps = null;

		try {

			final String params = String.join(",", Collections.nCopies(idList.size(), "?"));

			final String sqlT = sql.replace("?", params);

			ps = connection.prepareStatement(sqlT);

			int index = 1;
			for (final Object id : idList) {
				ps.setObject(index, id);
				index++;
			}

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", sqlT, idList);
			}

			final int executeUpdate = ps.executeUpdate();

			return executeUpdate >= 1;

		} catch (SQLException | SecurityException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
//			try {
//				connection.commit();
//			} catch (final SQLException e1) {
//				e1.printStackTrace();
//			}
			INSTANCE.returnZConnectionAndCommit(zc);
			close(ps);
		}

		return false;
	}

	public  static <T> boolean deleteById(final Mode mode, final Object id, final Class<T> cls, final String sql) {
		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();
		try {
			connection.setAutoCommit(false);
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}
		PreparedStatement ps = null;
		try {
			final String s = sql;
			ps= connection.prepareStatement(s);
			ps.setObject(1, id);

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s,id);
			}

			final int executeUpdate = ps.executeUpdate();

			return executeUpdate >= 1;

		} catch (SQLException | SecurityException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				connection.commit();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
			close(ps);
			INSTANCE.returnZConnectionAndCommit(zc);
		}

		return false;
	}

	public static <T> boolean existById(final Mode mode, final Object id, final Class<T> cls, final String sql) {

		if (Objects.isNull(id)) {
			return false;
		}

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();

		try {
			connection.setAutoCommit(false);
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			final String s = sql;
			ps = connection.prepareStatement(s);
			ps.setObject(1, id);

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s,id);
			}

			rs = ps.executeQuery();
			if (rs.next()) {
				final int count = rs.getInt(1);
				return count >= 1;
			}

		} catch (SQLException | SecurityException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			INSTANCE.returnZConnectionAndCommit(zc);
			close(rs, ps);
		}

		return false;
	}

	public static  <T> List<Object> saveAll(final Mode mode, final Class<T> cls, final String sqlParam, final List<T> tList) {

		if (CollUtil.isEmpty(tList)) {
			return Collections.emptyList();
		}

		final Field[] declaredFields = cls.getDeclaredFields();
		final Optional<Field> zid = Lists.newArrayList(declaredFields).stream()
				.filter(f -> f.isAnnotationPresent(ZID.class)).findAny();
		if (!zid.isPresent()) {
			throw new IllegalArgumentException(
					"类中无 " + ZID.class.getSimpleName() + " 字段，cls = " + cls.getCanonicalName());
		}

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();

		final String sql = generateSaveAllSQL(cls, sqlParam);

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			if (ZDP.getShowSql()) {
				final ArrayList<ArrayList<Object>> p0 = Lists.newArrayList();
				for (final T t : tList) {
					final ArrayList<Object> p1 = Lists.newArrayList();
					int i = 1;
					for (final Field field : cls.getDeclaredFields()) {
						if (field.isAnnotationPresent(ZID.class)) {
							continue;
						}
						field.setAccessible(true);
						final Object value = field.get(t);
						ps.setObject(i, value);
						i++;
						p1.add(value);
					}
					p0.add(p1);
					ps.addBatch();
				}
				// TODO p0可能会太长
				LOG.info("[{}],[{}]", sql, p0);
			} else {
				for (final T t : tList) {
					int i = 1;
					for (final Field field : cls.getDeclaredFields()) {
						if (field.isAnnotationPresent(ZID.class)) {
							continue;
						}
						field.setAccessible(true);
						final Object value = field.get(t);
						ps.setObject(i, value);
						i++;
					}
					ps.addBatch();
				}
			}

			ps.executeBatch();

			rs = ps.getGeneratedKeys();


			final Field idField = zid.get();
			final Class<?> type = idField.getType();

			final List<Object> r = new ArrayList<>();
			while (rs.next()) {
				// 类型转换
				final Object id = rs.getObject(1);
				if (type.getCanonicalName().equals(String.class.getCanonicalName())) {
					r.add(String.valueOf(id));
				} else if (type.getCanonicalName().equals(Integer.class.getCanonicalName())) {
					r.add(Integer.valueOf(String.valueOf(id)));
				} else if (type.getCanonicalName().equals(Byte.class.getCanonicalName())) {
					r.add(Byte.valueOf(String.valueOf(id)));
				} else if (type.getCanonicalName().equals(Short.class.getCanonicalName())) {
					r.add(Short.valueOf(String.valueOf(id)));
				} else if (type.getCanonicalName().equals(Long.class.getCanonicalName())) {
					r.add(Long.valueOf(String.valueOf(id)));
				} else if (type.getCanonicalName().equals(Float.class.getCanonicalName())) {
					r.add(Float.valueOf(String.valueOf(id)));
				} else if (type.getCanonicalName().equals(Double.class.getCanonicalName())) {
					r.add(Double.valueOf(String.valueOf(id)));
				} else if (type.getCanonicalName().equals(BigInteger.class.getCanonicalName())) {
					r.add(new BigInteger(String.valueOf(id)));
				} else if (type.getCanonicalName().equals(BigDecimal.class.getCanonicalName())) {
					r.add(new BigDecimal(String.valueOf(id)));
				} else if (type.getCanonicalName().equals(Character.class.getCanonicalName())) {
					r.add(Character.valueOf(String.valueOf(id).charAt(0)));
				}
			}
			return r;

		} catch (final Exception e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}

		} finally {
			INSTANCE.returnZConnectionAndCommit(zc);
			close(rs, ps);
		}

		return null;
	}

	private static <T> String generateSaveAllSQL(final Class<T> cls, final String sql) {
		final StringJoiner arg = new StringJoiner(",");
		final StringJoiner v = new StringJoiner(",");
		for (final Field field : cls.getDeclaredFields()) {
			if (field.isAnnotationPresent(ZID.class)) {
				continue;
			}

			final String dbFieldname = ZFieldConverter.toDbField(field.getName());
			arg.add(dbFieldname);
			v.add("?");
		}

		final String sql2 = sql.replace("F", arg.toString()).replace("A", v.toString());
		return sql2;
	}

	public  static <T> T save(final Mode mode, final Class<T> cls, final T t, final String sql) {
		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();

		try {
			connection.setAutoCommit(false);
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}

		final String sqlFinal = gSaveSql(cls, t, sql);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sqlFinal);
			final boolean execute = ps.execute(sqlFinal, Statement.RETURN_GENERATED_KEYS);
			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", sqlFinal, t);
			}
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				final Object id = rs.getObject(1);
				final ZEntity zEntity = t.getClass().getAnnotation(ZEntity.class);
				final String selectById = "select * from " + zEntity.tableName() + " where id = ?";
				final T findByIdNew = findById(mode, id, cls, selectById, zc);
				return findByIdNew;
			}

		} catch (final SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			close(ps);
			INSTANCE.returnZConnectionAndCommit(zc);
		}

		return null;
	}

	private static ZConnection getZCAndSetAutoCommitFALSE(final Mode mode) {
		// FIXME 2023年6月18日 上午12:00:42 zhanghen: 先从 ZTAs 拿，无再从下面方法拿
		final ZConnection zcT = ZTransactionAspect.ZCONNECTION_THREADLOCAL.get();
		if (zcT != null) {
			try {
				zcT.getConnection().setAutoCommit(false);
			} catch (final SQLException e) {
				e.printStackTrace();
			}
			return zcT;
		}

		// FIXME 2023年9月6日 下午2:33:36 zhanghen: 在此setAutoCommit(false)然后在归还方法里commit?
		final ZConnection zc = INSTANCE.getZConnection(mode);
		try {
			zc.getConnection().setAutoCommit(false);
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return zc;
	}

	private static <T> String gSaveSql(final Class<T> cls, final T t, final String sql) {
		final Field[] fs = cls.getDeclaredFields();

		final StringJoiner arg = new StringJoiner(",");
		final StringJoiner v = new StringJoiner(",");
		for (final Field field : fs) {
			if (field.isAnnotationPresent(ZID.class)) {
				continue;
			}

			final String dbFieldname = ZFieldConverter.toDbField(field.getName());
			arg.add(dbFieldname);
			field.setAccessible(true);
			try {
				final Object v2 = field.get(t);
				if (v2 instanceof String) {
					v.add("'" + String.valueOf(v2) + "'");
				} else if (v2 instanceof Date) {
					// FIXME 2023年8月1日 下午8:50:26 zhanghen: TODO 日期时间的字段，新增注解：表示插入的格式
					final String vD = DateUtil.format((Date)v2, DatePattern.NORM_DATETIME_FORMAT);
					v.add("'" + vD + "'");
				} else {
					v.add(String.valueOf(v2));
				}


			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		final String sql2 = sql.replace("F", arg.toString()).replace("A", v.toString());
		return sql2;
	}

	public static <T> List<T> findAll(final Mode mode, final Class<T> cls, final String sql) {

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();

		try {
			connection.setAutoCommit(false);
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// "select * from user where id = ?"
			final String s = sql;
			ps = connection.prepareStatement(s);
			if (ZDP.getShowSql()) {
				LOG.info("[{}]", sql);
			}

			rs = ps.executeQuery();

			final ResultSetMetaData metaData = rs.getMetaData();

			final ArrayList<T> r = Lists.newArrayList();
			while (rs.next()) {
				final int count = metaData.getColumnCount();
				final T t = cls.newInstance();
				for (int i = 0; i < count; i++) {
					final Object columValue = rs.getObject(i + 1);
					final String columnName = metaData.getColumnLabel(i + 1);
					final String javaFieldName = ZFieldConverter.toJavaField(columnName);
					final Field field = cls.getDeclaredField(javaFieldName);
					field.setAccessible(true);

					final Object handValue = handValue(t, columValue, field);
					field.set(t, handValue);
				}

				r.add(t);
			}
			INSTANCE.returnZConnectionAndCommit(zc);
			return r;

		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			INSTANCE.returnZConnectionAndCommit(zc);
			close(rs, ps);
		}

		return null;
	}

	// FIXME 2023年9月24日 下午3:41:35 zhanghen: 继续写，测试各种db和java的日期类型转换
	private static <T> Object handValue(final T t, final Object columValue, final Field field) {
		if (columValue instanceof LocalDateTime) {
			final Date date = Date.from(((LocalDateTime) columValue).atZone(ZoneId.systemDefault()).toInstant());
			final ZDateFormat zdf = field.getAnnotation(ZDateFormat.class);
			if (zdf != null) {
				final String format = zdf.format().getFormat();
				final SimpleDateFormat sss = new SimpleDateFormat(format);

				final String format2 = sss.format(date);

				final DateTime parse = DateUtil.parse(format2, format);
				return parse;
			}

			return date;
		}

		return columValue;
	}

	public static <T> List<T> findByIdIn(final Mode mode, final List<Object> idList, final Class<T> cls, final String sql) {

		final ZConnection zc = INSTANCE.getZConnection(mode);
		final Connection connection = zc.getConnection();

		try {
			connection.setAutoCommit(false);
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			final String s = sql;
			// select * from user where id in (?)
			final StringJoiner joiner = new StringJoiner(",");
			for (final Object id : idList) {
				joiner.add(String.valueOf(id));
			}
			final String param = joiner.toString();
			final String s2 = s.replace("?", param);
			ps = connection.prepareStatement(s2);
			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, param);
			}

			rs = ps.executeQuery();

			final ResultSetMetaData metaData = rs.getMetaData();

			final ArrayList<T> rList = Lists.newArrayListWithCapacity(idList.size());
			while (rs.next()) {
				final int count = metaData.getColumnCount();
				final T t = newT(cls, rs, metaData, count);
				rList.add(t);
			}
			return rList;
		} catch (SQLException | SecurityException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();

			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			INSTANCE.returnZConnectionAndCommit(zc);
			close(rs, ps);
		}

		return null;
	}

	private  static <T> T findById(final Mode mode, final Object id, final Class<T> cls, final String sql,final ZConnection zc) {

		final Connection connection = zc.getConnection();

		try {
			connection.setAutoCommit(false);
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}


		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			final String s = sql;
			ps = connection.prepareStatement(s);
			ps.setObject(1, id);

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, id);
			}

			rs = ps.executeQuery();

			final ResultSetMetaData metaData = rs.getMetaData();

			if (rs.next()) {
				final int count = metaData.getColumnCount();
				final T t = newT(cls, rs, metaData, count);
				return t;
			}

		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			INSTANCE.returnZConnectionAndCommit(zc);
			close(rs, ps);
		}

		return null;
	}

	public static <T> T findById(final Mode mode, final Object id, final Class<T> cls, final String sql) {
		final ZConnection zc = INSTANCE.getZConnection(mode);
		return findById(mode, id, cls, sql, zc);
	}

	private static <T> T newT(final Class<T> cls, final ResultSet rs, final ResultSetMetaData metaData, final int count)
			throws InstantiationException, IllegalAccessException, SQLException, NoSuchFieldException {
		final T t = cls.newInstance();
		for (int i = 0; i < count; i++) {
			final Object columValue = rs.getObject(i + 1);
			final String columnName = metaData.getColumnLabel(i + 1);
			final String javaFieldName = ZFieldConverter.toJavaField(columnName);
			final Field field = cls.getDeclaredField(javaFieldName);
			field.setAccessible(true);
//			field.set(t, columValue);

			final Object handValue = handValue(t, columValue, field);
			field.set(t, handValue);
		}
		return t;
	}

	public static <T> List<T> findByXX(final Mode mode, final Class<T> cls, final String sql, final Object... fieldArray) {

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();
		try {
			connection.setAutoCommit(false);
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// "select * from user where id = ?"
			final String s = sql;
			ps = connection.prepareStatement(s);

			int i = 1;
			for (final Object object : fieldArray) {
				ps.setObject(i, object);
				i++;
			}

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, Arrays.toString(fieldArray));
			}

			rs = ps.executeQuery();

			final ResultSetMetaData metaData = rs.getMetaData();

			final ArrayList<T> r = Lists.newArrayList();
			while (rs.next()) {
				final int count = metaData.getColumnCount();
				final T t = newT(cls, rs, metaData, count);
				r.add(t);
			}

			return r;
		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
//			try {
//				connection.commit();
//			} catch (final SQLException e1) {
//				e1.printStackTrace();
//			}
			INSTANCE.returnZConnectionAndCommit(zc);
			close(rs, ps);
		}

		return null;
	}

	public static <T> List<T> findByXX(final Mode mode, final Class<T> cls, final String sql, final Object fieldValue) {

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection.setAutoCommit(false);
			// "select * from user where id = ?"
			final String s = sql;
			ps = connection.prepareStatement(s);
			ps.setObject(1, fieldValue);

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, fieldValue);
			}

			rs = ps.executeQuery();

			final ResultSetMetaData metaData = rs.getMetaData();

			final ArrayList<T> r = Lists.newArrayList();
			while (rs.next()) {
				final int count = metaData.getColumnCount();
				final T t = newT(cls, rs, metaData, count);
				r.add(t);
			}

			return r;
		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException
				| SecurityException e) {
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			INSTANCE.returnZConnectionAndCommit(zc);
//			try {
//				connection.commit();
//			} catch (final SQLException e1) {
//				e1.printStackTrace();
//			}
			close(rs, ps);
		}

		return null;
	}

	public static <T> List<T> findByXXIn(final Mode mode, final Class<T> cls, final String sql, final Object... fieldArray) {

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();

		try {
			connection.setAutoCommit(false);
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}

		Statement statement = null;
		ResultSet rs = null;
		try {
			// "select * from user where id = ?"
			statement = connection.createStatement();

			String s = sql;
			for (final Object object : fieldArray) {
				// FIXME 2023年6月16日 下午5:14:50 zhanghen: 处理这里，自定义声明式方法不仅可以用List，改为支持Iterable和数组
				if(object instanceof List) {
					final List list = (List) object;

					final StringJoiner joiner = new StringJoiner(",");
					final Object[] a1 = ((List)object).toArray();
					for (final Object object2 : list) {
						if(object2  instanceof String) {
							joiner.add("'" + String.valueOf(object2) + "'");
						}else {
							joiner.add(String.valueOf(object2));
						}
					}
					final String string = Arrays.toString(a1);
					final String sss = joiner.toString().replace("[", "").replace("]", "");
					// ? 是 sql目标中的参数值占位符
					s = s.replaceFirst("\\?", sss);
				}
			}

			if (ZDP.getShowSql()) {
				LOG.info("[{}]", s);
			}

			rs = statement.executeQuery(s);

			final ResultSetMetaData metaData = rs.getMetaData();

			final ArrayList<T> r = Lists.newArrayList();
			while (rs.next()) {
				final int count = metaData.getColumnCount();
				final T t = newT(cls, rs, metaData, count);
				r.add(t);
			}

			return r;
		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
//			try {
//				connection.commit();
//			} catch (final SQLException e1) {
//				e1.printStackTrace();
//			}
			INSTANCE.returnZConnectionAndCommit(zc);
			close(rs, statement);
		}

		return null;
	}

	public static <T> List<T> findByIdLessThan(final Mode mode, final Class<T> cls, final String sql, final Object field) {

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection.setAutoCommit(false);
			// "select * from user where id = ?"
			final String s = sql;
			ps = connection.prepareStatement(s);
			ps.setObject(1, field);


			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, field);
			}

			rs = ps.executeQuery();

			final ResultSetMetaData metaData = rs.getMetaData();

			final ArrayList<T> r = Lists.newArrayList();
			while (rs.next()) {
				final int count = metaData.getColumnCount();
				final T t = newT(cls, rs, metaData, count);
				r.add(t);
			}

			return r;
		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			INSTANCE.returnZConnectionAndCommit(zc);
//			try {
//				connection.commit();
//			} catch (final SQLException e1) {
//				e1.printStackTrace();
//			}
			close(rs, ps);
		}

		return null;
	}

	public static <T> List<T> findByXXXEndingWith(final Mode mode, final Class<T> cls, final String sql, final Object field) {

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection.setAutoCommit(false);
			// "select * from user where id = ?"
			final String s = sql;
			ps = connection.prepareStatement(s);
			ps.setObject(1, "%" + field);

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, "%" + field);
			}

			rs = ps.executeQuery();

			final ResultSetMetaData metaData = rs.getMetaData();

			final ArrayList<T> r = Lists.newArrayList();
			while (rs.next()) {
				final int count = metaData.getColumnCount();
				final T t = newT(cls, rs, metaData, count);
				r.add(t);
			}

			return r;
		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			INSTANCE.returnZConnectionAndCommit(zc);
//			try {
//				connection.commit();
//			} catch (final SQLException e1) {
//				e1.printStackTrace();
//			}

			close(rs, ps);
		}

		return null;
	}
	public static <T> List<T> findByXXXStartingWith(final Mode mode, final Class<T> cls, final String sql, final Object field) {

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection.setAutoCommit(false);
			// "select * from user where id = ?"
			final String s = sql;
			ps = connection.prepareStatement(s);
			ps.setObject(1, field + "%");

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, field + "%");
			}

			rs = ps.executeQuery();

			final ResultSetMetaData metaData = rs.getMetaData();

			final ArrayList<T> r = Lists.newArrayList();
			while (rs.next()) {
				final int count = metaData.getColumnCount();
				final T t = newT(cls, rs, metaData, count);
				r.add(t);
			}

			return r;
		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			INSTANCE.returnZConnectionAndCommit(zc);
//			try {
//				connection.commit();
//			} catch (final SQLException e) {
//				e.printStackTrace();
//			}
			close(rs, ps);
		}

		return null;
	}
	public static <T> List<T> findByXXLike(final Mode mode, final Class<T> cls, final String sql, final Object field) {

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection.setAutoCommit(false);
			// "select * from user where id = ?"
			final String s = sql;
			ps = connection.prepareStatement(s);
			ps.setObject(1, "%" + field + "%");

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, "%" + field + "%");
			}

			rs = ps.executeQuery();

			final ResultSetMetaData metaData = rs.getMetaData();

			final ArrayList<T> r = Lists.newArrayList();
			while (rs.next()) {
				final int count = metaData.getColumnCount();
				final T t = newT(cls, rs, metaData, count);
				r.add(t);
			}

			return r;
		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			INSTANCE.returnZConnectionAndCommit(zc);
			close(rs, ps);
		}

		return null;
	}

	public static <T> List<T> findByXXIsNull(final Mode mode, final Class<T> cls, final String sql) {

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// "select * from user where id = ?"
			final String s = sql;
			ps = connection.prepareStatement(s);

			if (ZDP.getShowSql()) {
				LOG.info("[{}]", s);
			}

			rs = ps.executeQuery();

			final ResultSetMetaData metaData = rs.getMetaData();

			final ArrayList<T> r = Lists.newArrayList();
			while (rs.next()) {
				final int count = metaData.getColumnCount();
				final T t = newT(cls, rs, metaData, count);
				r.add(t);
			}

			return r;
		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			INSTANCE.returnZConnectionAndCommit(zc);
			close(rs, ps);
		}

		return null;
	}

	public static <T> Long count(final Mode mode, final Class<T> cls, final String sql,final ZConnection zc) {

		final Connection connection = zc.getConnection();

		try {
			connection.setAutoCommit(false);
		} catch (final SQLException e1) {
			e1.printStackTrace();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			final String s = sql;
			ps = connection.prepareStatement(s);

			if (ZDP.getShowSql()) {
				LOG.info("[{}]", s);
			}

			rs = ps.executeQuery();

			if (rs.next()) {
				final Long count = rs.getLong(1);
				return count;
			}

		} catch (SQLException | SecurityException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			INSTANCE.returnZConnectionAndCommit(zc);
			close(rs, ps);
		}

		return null;
	}

	public static <T> Long count(final Mode mode, final Class<T> cls, final String sql) {
		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		return count(mode, cls, sql,zc);
	}

	public static <T> Long countingByXX(final Mode mode, final Class<T> cls, final String sql, final Object field) {

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			final String s = sql;
			ps = connection.prepareStatement(s);
			ps.setObject(1, field);

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, field);
			}

			rs = ps.executeQuery();

			final ResultSetMetaData metaData = rs.getMetaData();

			if (rs.next()) {
				final Long count = rs.getLong(1);
				return count;
			}

		} catch (SQLException | SecurityException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			INSTANCE.returnZConnectionAndCommit(zc);
			close(rs, ps);
		}

		return null;
	}

	public static <T> List<T> findByXXOrderByXXLimit(final Mode mode, final Class<T> cls, final String sql, final Object... field) {

		final ZConnection zc = getZCAndSetAutoCommitFALSE(mode);
		final Connection connection = zc.getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			final String s = sql;
			ps = connection.prepareStatement(s);
			int i = 1;
			for (final Object object : field) {
				ps.setObject(i, object);
				i++;
			}

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, Arrays.toString(field));
			}

			rs = ps.executeQuery();

			final ResultSetMetaData metaData = rs.getMetaData();

			final ArrayList<T> r = Lists.newArrayList();
			while (rs.next()) {
				final int count = metaData.getColumnCount();
				final T t = newT(cls, rs, metaData, count);
				r.add(t);
			}

			return r;

		} catch (SQLException | SecurityException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			INSTANCE.returnZConnectionAndCommit(zc);
			close(rs, ps);
		}

		return null;
	}

	private static void close(final AutoCloseable... autoCloseables) {
		for (final AutoCloseable autoCloseable : autoCloseables) {
			if (autoCloseable == null) {
				continue;
			}
			try {
				autoCloseable.close();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}
}
