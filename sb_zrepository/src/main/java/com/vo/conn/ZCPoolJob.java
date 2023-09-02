package com.vo.conn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.common.collect.ImmutableList;
import com.vo.conn.ZDatasourceProperties.P;
import com.vo.core.ZLog2;
import com.votool.ze.ZE;
import com.votool.ze.ZES;

/**
 * 连接的定时任务 1、验证连接还是否可用，可用则保留，不可用则剔除 2、判断一个连接是否超过最大存储时间，超过则关闭并剔除
 *
 * @author zhangzhen
 * @date 2023年8月29日
 *
 */
public class ZCPoolJob {

	private final static ZLog2 LOG = ZLog2.getInstance();
	private final static ZE ZE = ZES.newZE(1, "ZCPool-Job-Thread-");

	public void start() {
		System.out.println(
				java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t" + "ZCPoolJob.start()");

		ZE.executeInQueue(() -> ZCPoolJob.this.job());

	}

	private void job() {
		System.out.println(
				java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t" + "ZCPoolJob.job()");

		while (true) {

			try {
				// FIXME 2023年8月29日 下午6:26:16 zhanghen: 改为配置项
				Thread.sleep(1000 * 5);
			} catch (final InterruptedException e1) {
				e1.printStackTrace();
			}

			final ZCPool pool = ZCPool.getInstance();
			final ImmutableList<ZConnection> cList = pool.getAll();
			for (final ZConnection cR : cList) {

				try {
					ZCPoolJob.select1(cR.getConnection());
				} catch (final SQLException e) {

					LOG.info("select1 job 发现失效的Connection对象，开始重置连接.c = {}", cR.getConnection());

					try {
						cR.getConnection().close();
					} catch (final SQLException e1) {
						e1.printStackTrace();
					}

					final P p = new P();
					p.setDatasourceDriverClass(cR.getDriverClass());
					p.setDatasourceUrl(cR.getUrl());
					p.setDatasourceUsername(cR.getUserName());
					p.setDatasourcePassword(cR.getPwd());
					final ZConnection newConnection = ZConnection.newConnection(p);
					if (newConnection == null) {
						LOG.error("select1 job 获取新连接失败");
					} else {
						cR.setConnection(newConnection.getConnection());
						LOG.info("select1 job 重置连接成功.cN = {}", newConnection.getConnection());
					}
					continue;
				}

			}
		}
	}

	private static void select1(final Connection connection) throws SQLException {
		final PreparedStatement ps = connection.prepareStatement("select 1;");

		final ResultSet rs = ps.executeQuery();
		if (rs.next()) {
//			System.out.println("select 1 = " + rs.getObject(1));
		}
		rs.close();
		ps.close();

	}

}
