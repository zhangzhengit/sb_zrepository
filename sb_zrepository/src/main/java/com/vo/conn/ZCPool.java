package com.vo.conn;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.vo.conn.ZDatasourceProperties.P;
import com.vo.core.ZLog2;

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

	private final Object lock  = new Object();

	private final AtomicInteger writeI = new AtomicInteger();
	private final AtomicInteger readI = new AtomicInteger();

	private ZCPool() {
		System.out.println(
				java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t" + "ZCPool.ZCPool()");

		this.create();
		final ZCPoolJob job = new ZCPoolJob();
		job.start();
	}


	static final ZCPool POOL = new ZCPool();

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
	public synchronized ZConnection getZConnection(final Mode mode) {
		if (mode == Mode.WRITE) {
			this.incrementWriteI();
			final ZConnection zc = this.writeVector.get(this.writeI.get());
			return zc;
		}

		if (mode == Mode.READ) {
			this.incrementReadI();
			final ZConnection zc = this.readVector.get(this.readI.get());
			return zc;
		}

		throw new IllegalArgumentException("mode 错误");
	}

	/**
	 * 归还一个连接
	 *
	 * @param zConnection
	 *
	 */
	public synchronized void returnZConnection(final ZConnection zConnection) {
		for (final ZConnection zc : this.writeVector) {
			if (zc.getConnection() == zConnection.getConnection()) {
				zc.setBusy(false);
			}
		}

		for (final ZConnection zc : this.readVector) {
			if (zc.getConnection() == zConnection.getConnection()) {
				zc.setBusy(false);
			}
		}
	}

	/**
	 * 移除一个连接
	 *
	 * @param zConnection
	 *
	 */
	public synchronized void removeZConnection(final ZConnection zConnection) {
		final Optional<ZConnection> findAnyWRITE = this.writeVector.stream()
				.filter(zc -> zc.getConnection() == zConnection.getConnection()).findAny();
		if (findAnyWRITE.isPresent()) {
			this.writeVector.remove(findAnyWRITE.get());
		} else {
			final Optional<ZConnection> findAnyREAD = this.readVector.stream()
					.filter(zc -> zc.getConnection() == zConnection.getConnection()).findAny();
			if (findAnyREAD.isPresent()) {
				this.readVector.remove(findAnyREAD.get());
			}
		}
	}

	/**
	 * 获取全部的连接，包括写连接和读连接
	 *
	 * @return
	 *
	 */
	public synchronized ImmutableList<ZConnection> getAll() {
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

	public synchronized void newReadConnection(final ZDatasourceProperties.P p) {
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
		}
	}

	public synchronized void newWriteConnection(final ZDatasourceProperties.P p) {
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

	public synchronized void shutdown() {
		LOG.info("开始关闭连接池,当前连接数量={}", this.writeVector.size());
		for (final ZConnection zConnection : this.writeVector) {
			final Connection c = zConnection.getConnection();
			try {
				c.close();
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		LOG.info("成功关闭连接池");
	}

}
