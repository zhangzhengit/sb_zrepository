package com.vo.starter;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 *
 *
 * @author zhangzhen
 * @date 2023年6月16日
 *
 */
@Component
@Import(value = { ZRepositoryStarter.class })
public class Starter {

}
