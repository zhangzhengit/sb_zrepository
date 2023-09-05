package com.vo.starter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import com.vo.SqlResult;
import com.vo.ZRSqlMap;
import com.vo.ZRepository;
import com.vo.ZRepositoryMain;
import com.vo.core.ZClass;
import com.vo.core.ZLog2;

import cn.hutool.core.collection.CollUtil;

/**
 *
 *
 * @author zhangzhen
 * @date 2023年6月16日
 *
 */
@Component
public class ZRepositoryStarter implements InstantiationAwareBeanPostProcessor {

	private static final ZLog2 LOG = ZLog2.getInstance();

	private final AtomicBoolean gZRepository = new AtomicBoolean(false);

	@Autowired
	private DefaultListableBeanFactory defaultListableBeanFactory;

	@Override
	public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {

		if (!this.gZRepository.get()) {
			// FIXME 2023年9月5日 下午8:46:00 zhanghen: 改为@value 取值
			final String packageName = "com";
			ZRepositoryStarter.gZRepository(packageName);
			this.gZRepository.set(true);

			return InstantiationAwareBeanPostProcessor.super.postProcessAfterInstantiation(bean, beanName);
		}

		return InstantiationAwareBeanPostProcessor.super.postProcessAfterInstantiation(bean, beanName);
	}

	private static void gZRepository(final String packageName) {

		final Map<Class, ZClass> clsMap = startZRepository(packageName);
		final Set<Entry<Class, ZClass>> es = clsMap.entrySet();
		for (final Entry<Class, ZClass> entry : es) {

//			final String x = classNameToJavaFieldName(zClass.getName());
			LOG.info("开始注入实现类[{}]", entry.getValue().getName());
			BFPP.beanFactory.registerSingleton(entry.getKey().getName(), entry.getValue().newInstance());
			LOG.info("注入实现类[{}]成功", entry.getValue().getName());
		}
	}

	/**
	 * 启动ZRepository程序，扫描 ZRepository 子接口并且生成代理类
	 *
	 * @param packageName
	 * @return 返回<ZRepository的子接口的Class,生成的ZRepository的子接口的ZClass代理类>
	 *
	 */
	public static Map<Class, ZClass> startZRepository(final String packageName) {
		LOG.info("ZRepositoryStarter启动");
		LOG.info("ZRepositoryStarter开始扫描[{}]的子接口", ZRepository.class.getCanonicalName());
		// 1 查找ZRepository的子接口
		final Set<Class<?>> zrSubinterfaceSet = ZRepositoryMain.scanZRepositorySubinterface(packageName);
		if (CollUtil.isEmpty(zrSubinterfaceSet)) {
			LOG.info("ZRepositoryStarter没有[{}]的子接口", ZRepository.class.getCanonicalName());
			return Collections.emptyMap();
		}

		LOG.info("ZRepositoryStarter[{}]的子接口个数={}", ZRepository.class.getCanonicalName(), zrSubinterfaceSet.size());

		// 2 给ZRepository的子接口的每个方法生成 SQL
		final List<SqlResult> sqlForZRSubclassList = ZRepositoryMain.generateSqlForZRSubclass(zrSubinterfaceSet);
		for (final SqlResult sqlResult : sqlForZRSubclassList) {
			ZRSqlMap.put(sqlResult.getZRepositorySubClassName()	, sqlResult.getMethodName(), sqlResult.getSqlFinal());
		}

		// 3 给ZRepository的子接口生成动态代理类，ZClass
		final Map<Class, ZClass> clsMap = ZRepositoryMain.generateClassForZRSubinterface(zrSubinterfaceSet);
		return clsMap;
	}

	private static String classNameToJavaFieldName(final String className) {
		final String zc = ZRepositoryMain._Z_CLASS;
		final int i = className.lastIndexOf(zc);
		if (i > -1) {
			final String s = className.substring(0, i);
			final char[] ca = s.toCharArray();
			ca[0] = Character.toLowerCase(ca[0]);

			final String r = new String(ca);
			return r;
		}

		return null;
	}
}
