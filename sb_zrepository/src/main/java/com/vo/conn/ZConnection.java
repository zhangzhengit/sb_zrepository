package com.vo.conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.Data;

/**
 *
 *
 * @author zhangzhen
 * @date 2023年6月15日
 *
 */
@Data
public class ZConnection {

	private Boolean busy;
	private Connection connection;

	public static ZConnection newConnection(final ZDatasourceProperties.P p) {

		try {
			final String c = p.getDatasourceDriverClass();
			Class.forName(c);
		} catch (final ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		final String url = p.getDatasourceUrl();
		final String userName = p.getDatasourceUsername();
		final String pwd = p.getDatasourcePassword();
		try {
			final Connection connection = DriverManager.getConnection(url, userName, pwd);

			final ZConnection zc = new ZConnection();
			zc.setBusy(false);
			zc.setConnection(connection);

			return zc;

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

}
