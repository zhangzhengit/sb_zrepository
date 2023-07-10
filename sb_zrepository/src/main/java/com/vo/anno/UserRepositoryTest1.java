package com.vo.anno;

import java.lang.reflect.Type;
import java.util.Set;

import com.vo.ZRMain;
import com.vo.ZRepository;

/**
 *
 *
 * @author zhangzhen
 * @date 2023年6月23日
 *
 */
public class UserRepositoryTest1 {

	public static String[] findZRSubclassFanxing(final Class cls) {
		// FIXME 2023年6月15日 下午8:46:27 zhanghen: 去掉ccc，暂时方便测试用的
		final Class ccc =  cls;

		// FIXME 2023年6月17日 下午6:48:51 zhanghen: com改为配置
//		final Set<Class<?>> c = ClassUtil.scanPackage("com");
		final Set<Class<?>> c = ZRMain.scanPackage_COM();
		for (final Class<?> class1 : c) {
			if(!ccc.getCanonicalName() .equals(class1.getCanonicalName())) {
				continue;
			}

			final Class<?>[] is = class1.getInterfaces();
			for (final Class<?> class2 : is) {
				if(class2.getCanonicalName().equals(ZRepository.class.getCanonicalName())) {
//					System.out.println("是ZR的子类,class = " + class1);
					final Type[] genericInterfaces = class1.getGenericInterfaces();
					for (final Type type : genericInterfaces) {
//						System.out.println("\t type = " + type);
						final String typeName = type.getTypeName();
//						System.out.println("typeName = " + typeName);
						final int i = typeName.indexOf("<");
						if (i > -1) {
							final int i2 = typeName.lastIndexOf(">");
							if (i2 > i) {
								final String t = typeName.substring(i + 1, i2);
//								System.out.println("t = " + t);
								final String[] a = t.split(",");
//								System.out.println("a = " + Arrays.toString(a));

								return a;
							}
						}
					}
				}
			}
		}
		return null;
	}
}
