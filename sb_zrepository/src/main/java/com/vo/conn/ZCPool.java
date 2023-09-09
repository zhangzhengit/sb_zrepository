package com.vo.conn;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.vo.conn.ZDatasourceProperties.P;
import com.vo.core.ZLog2;
import com.vo.read.R;

/**
 *
 * 数据库连接池
 *
 * @author zhangzhen
 * @date 2023年6月15日
 *
 */
public class ZCPool {

	private static final ZLog2 LOG = ZLog2.getInstance();
	private final Vector<ZConnection> writeVector = new java.util.Vector<>();
	private final Vector<ZConnection> readVector = new java.util.Vector<>();

	private final AtomicInteger writeI = new AtomicInteger();
	private final AtomicInteger readI = new AtomicInteger();

	private static final ZCPool POOL = new ZCPool();


	private ZCPool() {
		System.out.println(
				java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t" + "ZCPool.ZCPool()");

		this.create();

		final ZCPoolJob job = new ZCPoolJob();
		job.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			LOG.info("JVM钩子执行，开始关闭连接池");
			this.shutdown();
			LOG.info("JVM钩子已成功关闭连接池");
		}));

	}


	public static ZCPool getInstance() {
		return POOL;
	}

	void incrementWriteI() {
		synchronized (this.incrementLock) {
			final int g = this.writeI.incrementAndGet();
			if (g >= this.writeVector.size()) {
				this.writeI.set(0);
			}
		}
	}

	void incrementReadI() {
		synchronized (this.incrementLock) {
			final int g = this.readI.incrementAndGet();
			if (g >= this.readVector.size()) {
				this.readI.set(0);
			}
		}
	}

	Object incrementLock = new Object();

	/**
	 * 轮询获取连接对象，暂定为轮询获取，不管当前连接是否忙碌，轮询获取
	 *
	 * @param mode TODO
	 *
	 * @return
	 *
	 */
	public ZConnection getZConnection(final Mode mode) {
		if (mode == Mode.WRITE) {

			return this.getWRITE();

		}

		if (mode == Mode.READ) {

			return this.getREAD();

		}

		throw new IllegalArgumentException("mode 错误");
	}

	private ZConnection getREAD() {

		final int ms = 1000 * 10;
		for (int i = 1; i <= ms; i++) {
			final Optional<ZConnection> findFirst = this.readVector.stream().filter(zc -> !zc.getBusy()).findFirst();
			if (findFirst.isPresent()) {
				final ZConnection zc = findFirst.get();
				zc.setBusy(true);
				return zc;
			}

			this.sleep1MS();
		}
		throw new IllegalArgumentException("获取不到空闲的[读]连接");

//		final Random random = new Random();
//		final ZConnection zConnection = this.readVector.get(random.nextInt(this.readVector.size()));
//		zConnection.setBusy(true);
//		return zConnection;
	}

	private ZConnection getWRITE() {
		// FIXME 2023年9月6日 上午2:36:59 zhanghen: ms 配置为参数
		final int ms = 1000 * 10;
		for (int i = 1; i <= ms; i++) {
			final Optional<ZConnection> findFirst = this.writeVector.stream().filter(zc -> !zc.getBusy()).findFirst();
			if (findFirst.isPresent()) {
				final ZConnection zc = findFirst.get();
				zc.setBusy(true);
				return zc;
			}

			this.sleep1MS();
		}

		throw new IllegalArgumentException("获取不到空闲的[写]连接");

//		final Random random = new Random();
//		final ZConnection zConnection = this.writeVector.get(random.nextInt(this.writeVector.size()));
//		zConnection.setBusy(true);
//		return zConnection;
	}

	private void sleep1MS() {
		try {
			Thread.sleep(1);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 归还一个连接
	 *
	 * @param zConnection
	 *
	 */
	public void returnZConnectionAndCommit(final ZConnection zConnection) {
		for (final ZConnection zc : this.writeVector) {
			if (zc.getConnection() == zConnection.getConnection()) {
				try {
					zConnection.getConnection().commit();
				} catch (final SQLException e) {
					e.printStackTrace();
				}
				zc.setBusy(false);

				break;
			}
		}

		for (final ZConnection zc : this.readVector) {
			if (zc.getConnection() == zConnection.getConnection()) {
				try {
					zConnection.getConnection().commit();
				} catch (final SQLException e) {
					e.printStackTrace();
				}
				zc.setBusy(false);

				break;
			}
		}
	}

	/**
	 * 移除一个连接
	 *
	 * @param zConnection
	 *
	 */
	public void removeZConnection(final ZConnection zConnection) {
		LOG.warn("开始删除一个连接ZConnection={}", zConnection);

		final Optional<ZConnection> findAnyWRITE = this.writeVector.stream()
				.filter(zc -> zc.getConnection() == zConnection.getConnection()).findAny();
		if (findAnyWRITE.isPresent()) {
			this.writeVector.remove(findAnyWRITE.get());
			LOG.warn("成功删除一个[写]连接ZConnection={}", zConnection);
		} else {
			final Optional<ZConnection> findAnyREAD = this.readVector.stream()
					.filter(zc -> zc.getConnection() == zConnection.getConnection()).findAny();
			if (findAnyREAD.isPresent()) {
				this.readVector.remove(findAnyREAD.get());
				LOG.warn("成功删除一个[读]连接ZConnection={}", zConnection);
			}
		}
	}

	/**
	 * 获取全部的连接，包括写连接和读连接
	 *
	 * @return
	 *
	 */
	public ImmutableList<ZConnection> getAll() {
		final List<ZConnection> r = Lists.newArrayList(this.writeVector);
		r.addAll(this.readVector);
		final ImmutableList<ZConnection> list = ImmutableList.copyOf(r);
		return list;
	}

	private void create() {
		System.out.println(
				java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t" + "ZCPool.create()");

		final ZDatasourceProperties zdp = ZDatasourcePropertiesLoader.getInstance();

		final P write = zdp.getWrite();
		this.newWriteConnection(write);

		final Integer datasourceReadUrlCount = zdp.getDatasourceReadUrlCount();
		final List<P> r = zdp.getReadList();
		for (int i = 0; i < datasourceReadUrlCount; i++) {
			this.newReadConnection(r.get(i));
		}

	}

	private synchronized void newReadConnection(final ZDatasourceProperties.P p) {
		final String url = p.getDatasourceUrl();
		// FIXME 2023年6月16日 下午12:35:04 zhanghen:先暂时处理为从1到max
		final int minConnection = 1;
//		 final Integer minConnection = p.getDatasourceMinConnection();
		final Integer maxConnection = p.getDatasourceMaxConnection();

		LOG.info("开始建立数据库[读]连接,min={},max={},url={}", minConnection, maxConnection, url);

 		for (int i = minConnection; i <= maxConnection; i++) {
			final ZConnection zConnection = ZConnection.newConnection(p);
			zConnection.setMode(Mode.READ);
			this.readVector.add(zConnection);
			LOG.info("第{}个数据库[读]连接创建成功,ZConnection={}", i, zConnection);
			LOG.info("当前已创建[读]连接数={}", this.readVector.size());
		}
	}

	private synchronized void newWriteConnection(final ZDatasourceProperties.P p) {
		final String url = p.getDatasourceUrl();
		 // FIXME 2023年6月16日 下午12:35:04 zhanghen:先暂时处理为从1到max
		 final int minConnection = 1;
//		 final Integer minConnection = p.getDatasourceMinConnection();
		 final Integer maxConnection = p.getDatasourceMaxConnection();

		LOG.info("开始建立数据库[写]连接,min={},max={},url={}", minConnection, maxConnection, url);

	 	for (int i = minConnection; i <= maxConnection; i++) {
			final ZConnection zConnection = ZConnection.newConnection(p);
			zConnection.setMode(Mode.WRITE);
			this.writeVector.add(zConnection);
			LOG.info("第{}个数据库[写]连接创建成功,ZConnection={}", i, zConnection);
		}
	}

	private synchronized void shutdown() {
		LOG.info("开始关闭连接池,当前连接数量={}", this.writeVector.size() + this.readVector.size());
		ZCPool.close(this.writeVector);
		ZCPool.close(this.readVector);
		LOG.info("成功关闭连接池");
	}

	private static void close(final Vector<ZConnection> writeVector2) {
		for (final ZConnection zConnection : writeVector2) {
			final Connection c = zConnection.getConnection();
			try {
				final boolean closed = c.isClosed();
				if (!closed) {
					c.close();
				}
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
