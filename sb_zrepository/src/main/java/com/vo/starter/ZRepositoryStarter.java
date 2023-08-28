package com.vo.starter;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import com.vo.ZRMain;
import com.vo.ZRepository;
import com.vo.conn.ZCPool;
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
			ZRepositoryStarter.gZRepository(bean, beanName);
			this.gZRepository.set(true);

			return InstantiationAwareBeanPostProcessor.super.postProcessAfterInstantiation(bean, beanName);
		}

		return InstantiationAwareBeanPostProcessor.super.postProcessAfterInstantiation(bean, beanName);
	}

	private static void gZRepository(final Object bean, final String beanName) {
		LOG.info("ZRepository开始初始化连接池");
		final ZCPool instance = ZCPool.getInstance();
		LOG.info("ZRepository始化连接池完成");

		LOG.info("ZRepositoryStarter启动");
		LOG.info("ZRepositoryStarter开始扫描[{}]的子接口", ZRepository.class.getCanonicalName());
		final Set<Class<?>> zrSubclassSet = ZRMain.scanZRSubclass();
		if (CollUtil.isEmpty(zrSubclassSet)) {
			LOG.info("ZRepositoryStarter没有[{}]的子接口", ZRepository.class.getCanonicalName());
		}

		LOG.info("ZRepositoryStarter[{}]的子接口个数={}", ZRepository.class.getCanonicalName(), zrSubclassSet.size());

		ZRMain.generateSqlForZRSubclass(zrSubclassSet);

		final Set<ZClass> zcset = ZRMain.generateClassForZRSubinterface(zrSubclassSet);
		for (final ZClass zClass : zcset) {
			final String x = classNameToJavaFieldName(zClass.getName());
			LOG.info("开始注入实现类[{}]", zClass.getName());
			BFPP.beanFactory.registerSingleton(x, zClass.newInstance());
			LOG.info("注入实现类[{}]成功", zClass.getName());
		}
	}

	private static String classNameToJavaFieldName(final String className) {
		final String zc = ZRMain._Z_CLASS;
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
