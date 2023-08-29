package com.vo.conn;

import java.security.KeyStore.PrivateKeyEntry;
import java.util.List;

import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 *
 * @author zhangzhen
 * @date 2023年6月17日
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZDatasourceProperties {

	private Boolean showSql;

	private P write;

	private Integer datasourceReadUrlCount;

	private List<P> readList;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor

	public static class P {
		private String datasourceUrl;
		private String datasourceUsername;
		private String datasourcePassword;
		private String datasourceDriverClass;
		private Integer datasourceMinConnection;
		private Integer datasourceMaxConnection;
	}

}
