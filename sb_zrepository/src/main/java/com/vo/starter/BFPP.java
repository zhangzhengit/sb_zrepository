package com.vo.starter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 *
 *
 * @author zhangzhen
 * @date 2023年6月16日
 *
 */
@Component
public class BFPP implements BeanFactoryPostProcessor {

	public static ConfigurableListableBeanFactory beanFactory;

	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory bf) throws BeansException {
		beanFactory = bf;
	}

}
