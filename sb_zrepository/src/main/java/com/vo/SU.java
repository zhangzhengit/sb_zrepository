package com.vo;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.Lists;
import com.vo.conn.Mode;
import com.vo.conn.ZCPool;
import com.vo.conn.ZConnection;
import com.vo.conn.ZDatasourceProperties;
import com.vo.conn.ZDatasourcePropertiesLoader;
import com.vo.core.ZLog2;
import com.vo.transaction.ZTransactionAspect;

/**
 * @see ZRepository 接口和其子接口里的方法的具体实现
 *
 * @author zhangzhen
 * @date 2023年6月16日
 *
 */
public class SU {

	private static final ZLog2 LOG = ZLog2.getInstance();
	private static final ZDatasourceProperties ZDP = ZDatasourcePropertiesLoader.getInstance();

	private static final ZCPool instance = ZCPool.getInstance();

	public static <T> boolean deleteAll(final Mode mode, final Class<T> cls, final String sql) {

		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			final String s = sql;
			final PreparedStatement ps = connection.prepareStatement(s);

			if (ZDP.getShowSql()) {
				LOG.info("[{}]", s);
			}

			final int executeUpdate = ps.executeUpdate();

			return executeUpdate >= 1;

		} catch (SQLException | SecurityException e) {
			e.printStackTrace();
		} finally {
			instance.returnZConnection(zc);
		}

		return false;
	}

	public static <T> boolean deleteById(final Mode mode, final Object id, final Class<T> cls, final String sql) {
		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			final String s = sql;
			final PreparedStatement ps = connection.prepareStatement(s);
			ps.setObject(1, id);

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s,id);
			}

			final int executeUpdate = ps.executeUpdate();

			return executeUpdate >= 1;

		} catch (SQLException | SecurityException e) {
			e.printStackTrace();
		} finally {
			instance.returnZConnection(zc);
		}

		return false;
	}

	public static <T> boolean existById(final Mode mode, final Object id, final Class<T> cls, final String sql) {

		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			final String s = sql;
			final PreparedStatement ps = connection.prepareStatement(s);
			ps.setObject(1, id);

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s,id);
			}

			final ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				final int count = rs.getInt(1);
				return count >= 1;
			}

		} catch (SQLException | SecurityException e) {
			e.printStackTrace();
		} finally {
			instance.returnZConnection(zc);
		}

		return false;
	}

	public static AtomicLong save_MS = new AtomicLong();

	public static <T> List<Object> saveAll(final Mode mode, final Class<T> cls, final String sqlParam, final List<T> tList) {
		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			connection.setAutoCommit(false);

			final String sql = generateSaveAllSQL(cls, sqlParam);

			final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			if (ZDP.getShowSql()) {
				final ArrayList<ArrayList<Object>> p0 = Lists.newArrayList();
				for (final T t : tList) {
					final ArrayList<Object> p1 = Lists.newArrayList();
					int i = 1;
					for (final Field field : cls.getDeclaredFields()) {
						field.setAccessible(true);
						final Object value = field.get(t);
						ps.setObject(i, value);
						i++;
						p1.add(value);
					}
					p0.add(p1);
					ps.addBatch();
				}
				LOG.info("[{}],[{}]", sql, p0);
			} else {
				for (final T t : tList) {
					int i = 1;
					for (final Field field : cls.getDeclaredFields()) {
						field.setAccessible(true);
						final Object value = field.get(t);
						ps.setObject(i, value);
						i++;
					}
					ps.addBatch();
				}
			}

			ps.executeBatch();

			final ResultSet rs = ps.getGeneratedKeys();
			final List<Object> r = new ArrayList<>();
			while (rs.next()) {
				final Object id = rs.getObject(1);
				r.add(id);
			}
			ps.clearBatch();

			return r;

		} catch (final Exception e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}

		} finally {
			try {
				connection.commit();
				instance.returnZConnection(zc);
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private static <T> String generateSaveAllSQL(final Class<T> cls, final String sql) {
		final StringJoiner arg = new StringJoiner(",");
		final StringJoiner v = new StringJoiner(",");
		for (final Field field : cls.getDeclaredFields()) {
			final String dbFieldname = ZFieldConverter.toDbField(field.getName());
			arg.add(dbFieldname);
			v.add("?");
		}

		final String sql2 = sql.replace("F", arg.toString()).replace("A", v.toString());
		return sql2;
	}

	public static <T> Object save(final Mode mode, final Class<T> cls, final T t, final String sql) {
		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		final String sql2 = gSaveSql(cls, t, sql);

		try {
			final String s = sql2;
			final Statement statement = connection.createStatement();
			final long t1 = System.currentTimeMillis();
			final int executeUpdate = statement.executeUpdate(s, Statement.RETURN_GENERATED_KEYS);
			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", sql, t);
			}
			final long t2 = System.currentTimeMillis();
			save_MS.set(save_MS.get() + (t2-t1));

			final ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				final Object id = rs.getObject(1);
				return id;
			}
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			instance.returnZConnection(zc);
		}

		return null;

	}

	private static ZConnection getZC(final Mode mode) {
		// FIXME 2023年6月18日 上午12:00:42 zhanghen: 先从 ZTAs 拿，无再从下面方法拿
		final ZConnection zcT = ZTransactionAspect.ZCONNECTION_THREADLOCAL.get();
		if (zcT != null) {
			return zcT;
		}

		final ZConnection zc = instance.getZConnection(mode);
		return zc;
	}

	private static <T> String gSaveSql(final Class<T> cls, final T t, final String sql) {
		final Field[] fs = cls.getDeclaredFields();

		final StringJoiner arg = new StringJoiner(",");
		final StringJoiner v = new StringJoiner(",");
		for (final Field field : fs) {

			final String dbFieldname = ZFieldConverter.toDbField(field.getName());
			arg.add(dbFieldname);
			field.setAccessible(true);
			try {
				final Object v2 = field.get(t);
				if (v2 instanceof String) {
					v.add("'" + String.valueOf(v2) + "'");
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
		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			// "select * from user where id = ?"
			final String s = sql;
			final PreparedStatement ps = connection.prepareStatement(s);
			if (ZDP.getShowSql()) {
				LOG.info("[{}]", sql);
			}

			final ResultSet rs = ps.executeQuery();

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
					field.set(t, columValue);
				}

				r.add(t);
			}
			instance.returnZConnection(zc);
			return r;

		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
		} finally {
			instance.returnZConnection(zc);
		}

		return null;
	}

	public static <T> List<T> findByIdIn(final Mode mode, final List<Object> idList, final Class<T> cls, final String sql) {

		// XXX ps.setArray 报错，暂时循环用findById
		final ArrayList<T> r = Lists.newArrayListWithCapacity(idList.size());
		for (final Object id : idList) {
			final T findById = findById(mode, id, cls, sql);
			r.add(findById);
		}

		return r;

	}



	public static AtomicLong findByID_MS = new AtomicLong();
	public static AtomicLong findByID__NEWT_MS = new AtomicLong();
	public static <T> T findById(final Mode mode, final Object id, final Class<T> cls, final String sql) {

		final long t1 = System.currentTimeMillis();
		final ZConnection zc = instance.getZConnection(mode);
		final Connection connection = zc.getConnection();

		try {
			final String s = sql;
			final PreparedStatement ps = connection.prepareStatement(s);
			ps.setObject(1, id);

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, id);
			}

			final ResultSet rs = ps.executeQuery();

			final ResultSetMetaData metaData = rs.getMetaData();

			if (rs.next()) {
				final int count = metaData.getColumnCount();
				final long t11 = System.currentTimeMillis();
				final T t = newT(cls, rs, metaData, count);
				final long t22 = System.currentTimeMillis();
				findByID__NEWT_MS.set(findByID__NEWT_MS.get() + (t22-t11));
				final long t2 = System.currentTimeMillis();

				findByID_MS.set(findByID_MS.get() + (t2 - t1));
				return t;
			}

		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
		} finally {
			instance.returnZConnection(zc);
		}

		return null;
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
			field.set(t, columValue);
		}
		return t;
	}

	public static <T> List<T> findByXX(final Mode mode, final Class<T> cls, final String sql, final Object... fieldArray) {

		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			// "select * from user where id = ?"
			final String s = sql;
			final PreparedStatement ps = connection.prepareStatement(s);

			int i = 1;
			for (final Object object : fieldArray) {
				ps.setObject(i, object);
				i++;
			}

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, Arrays.toString(fieldArray));
			}

			final ResultSet rs = ps.executeQuery();

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
		} finally {
			instance.returnZConnection(zc);
		}

		return null;
	}

	public static <T> List<T> findByXX(final Mode mode, final Class<T> cls, final String sql, final Object fieldValue) {

		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			// "select * from user where id = ?"
			final String s = sql;
			final PreparedStatement ps = connection.prepareStatement(s);
			ps.setObject(1, fieldValue);

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, fieldValue);
			}

			final ResultSet rs = ps.executeQuery();

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
		} finally {
			instance.returnZConnection(zc);
		}

		return null;
	}

	public static <T> List<T> findByXXIn(final Mode mode, final Class<T> cls, final String sql, final Object... fieldArray) {

		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			// "select * from user where id = ?"
			final Statement statement = connection.createStatement();

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
//					System.out.println("object instanceof List - sss = " + sss);
					// ? 是 sql目标中的参数值占位符
					s = s.replaceFirst("\\?", sss);
				}
			}

			if (ZDP.getShowSql()) {
				LOG.info("[{}]", s);
			}

			final ResultSet rs = statement.executeQuery(s);

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
		} finally {
			instance.returnZConnection(zc);
		}

		return null;
	}

	public static <T> List<T> findByIdLessThan(final Mode mode, final Class<T> cls, final String sql, final Object field) {

		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			// "select * from user where id = ?"
			final String s = sql;
			final PreparedStatement ps = connection.prepareStatement(s);
			ps.setObject(1, field);


			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, field);
			}


			final ResultSet rs = ps.executeQuery();

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
		} finally {
			instance.returnZConnection(zc);
		}

		return null;
	}

	public static <T> List<T> findByXXXEndingWith(final Mode mode, final Class<T> cls, final String sql, final Object field) {

		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			// "select * from user where id = ?"
			final String s = sql;
			final PreparedStatement ps = connection.prepareStatement(s);
			ps.setObject(1, "%" + field);

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, "%" + field);
			}

			final ResultSet rs = ps.executeQuery();

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
		} finally {
			instance.returnZConnection(zc);
		}

		return null;
	}
	public static <T> List<T> findByXXXStartingWith(final Mode mode, final Class<T> cls, final String sql, final Object field) {

		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			// "select * from user where id = ?"
			final String s = sql;
			final PreparedStatement ps = connection.prepareStatement(s);
			ps.setObject(1, field + "%");

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, field + "%");
			}

			final ResultSet rs = ps.executeQuery();

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
		} finally {
			instance.returnZConnection(zc);
		}

		return null;
	}
	public static <T> List<T> findByXXLike(final Mode mode, final Class<T> cls, final String sql, final Object field) {

		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			// "select * from user where id = ?"
			final String s = sql;
			final PreparedStatement ps = connection.prepareStatement(s);
			ps.setObject(1, "%" + field + "%");

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, "%" + field + "%");
			}

			final ResultSet rs = ps.executeQuery();

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
		} finally {
			instance.returnZConnection(zc);
		}

		return null;
	}

	public static <T> List<T> findByXXIsNull(final Mode mode, final Class<T> cls, final String sql) {

		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			// "select * from user where id = ?"
			final String s = sql;
			final PreparedStatement ps = connection.prepareStatement(s);

			if (ZDP.getShowSql()) {
				LOG.info("[{}]", s);
			}


			final ResultSet rs = ps.executeQuery();

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
		} finally {
			instance.returnZConnection(zc);
		}

		return null;
	}

	public static <T> Long count(final Mode mode, final Class<T> cls, final String sql) {

		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			final String s = sql;
			final PreparedStatement ps = connection.prepareStatement(s);

			if (ZDP.getShowSql()) {
				LOG.info("[{}]", s);
			}

			final ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				final Long count = rs.getLong(1);
				return count;
			}

		} catch (SQLException | SecurityException e) {
			e.printStackTrace();
		} finally {
			instance.returnZConnection(zc);
		}

		return null;
	}

	public static <T> Long countingByXX(final Mode mode, final Class<T> cls, final String sql, final Object field) {

		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			final String s = sql;
			final PreparedStatement ps = connection.prepareStatement(s);
			ps.setObject(1, field);

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, field);
			}

			final ResultSet rs = ps.executeQuery();

			final ResultSetMetaData metaData = rs.getMetaData();

			if (rs.next()) {
				final Long count = rs.getLong(1);
				return count;
			}

		} catch (SQLException | SecurityException e) {
			e.printStackTrace();
		} finally {
			instance.returnZConnection(zc);
		}

		return null;
	}

	public static <T> List<T> findByXXOrderByXXLimit(final Mode mode, final Class<T> cls, final String sql, final Object... field) {

		final ZConnection zc = getZC(mode);
		final Connection connection = zc.getConnection();

		try {
			final String s = sql;
			final PreparedStatement ps = connection.prepareStatement(s);
			int i = 1;
			for (final Object object : field) {
				ps.setObject(i, object);
				i++;
			}

			if (ZDP.getShowSql()) {
				LOG.info("[{}],[{}]", s, Arrays.toString(field));
			}


			final ResultSet rs = ps.executeQuery();

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
		} finally {
			instance.returnZConnection(zc);
		}

		return null;
	}

}
