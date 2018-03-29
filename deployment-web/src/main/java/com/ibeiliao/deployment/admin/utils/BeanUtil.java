package com.ibeiliao.deployment.admin.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @描述:
 * @author: liuhaihui
 * @date: 2016年12月22日上午11:44:53
 * @version: 1.0
 * @see:
 */
public class BeanUtil {

	private static Logger logger = Logger.getLogger(BeanUtil.class);
	
	/**
	 * 利用反射实现对象之间相同属性复制,我引入这个类主要是解决下面的[需求2]
	 * 
	 * 使用BeanUtils会碰到这样的需求： 
	 * 假设是从A复制到B：
	 * 需求1：如果B中某字段有值（不为null），则该字段不复制；也就是B中该字段没值时，才进行复制，适合于对B进行补充值的情况。
	 * 需求2：如果A中某字段没值（为null），则该字段不复制，也就是不要把null复制到B当中。
	 * 
	 * 这个类就是用来解决[需求2]的
	 * 
	 * 用法: VOUtil.copyProperties(vo, po);
	 * 
	 * @param source 要复制的
	 * @param to	 复制给
	 */
	public static void copyProperties(Object source, Object target) {
		try {
			copyPropertiesExclude(source, target, null);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 复制对象属性
	 * 
	 * @param from
	 * @param to
	 * @param excludsArray
	 *            排除属性列表,结果是：to对象的在排除列表中的字段值将不会被改变！
	 *            比如： 	vo: {"studentId":0,"createTime":null,"studentNo":"222222","isMaster":1}
 						po: {"studentId":100,"createTime":"2016-12-22 12:00:55","studentNo":"333333","isMaster":0}
 						
            		使用　BeanUtil.copyPropertiesExclude(vo, po, new String[]{"studentId", "isMaster"});
            		拷贝之后的结果是:
 					po: {"studentId":100,"createTime":"2016-12-22 12:00:55","studentNo":"222222","isMaster":0}

	 * @throws Exception
	 */
	public static void copyPropertiesExclude(Object from, Object to, String[] excludsArray) {
		try {
			List<String> excludesList = null;
			if (excludsArray != null && excludsArray.length > 0) {
				//excludesList = Arrays.asList(excludsArray); // 构造列表对象
				excludesList = new ArrayList<String>();
				for (String excludedProperty : excludsArray) {
					excludesList.add(excludedProperty.toLowerCase());
				}
			}

			Method[] fromMethods = from.getClass().getDeclaredMethods();
			Method[] toMethods = to.getClass().getDeclaredMethods();
			Method fromMethod = null, toMethod = null;
			String fromMethodName = null, toMethodName = null;

			for (int i = 0; i < fromMethods.length; i++) {

				fromMethod = fromMethods[i];
				fromMethodName = fromMethod.getName();

				if (!fromMethodName.contains("get"))
					continue;
				// 排除列表检测
				if (excludesList != null
						&& excludesList.contains(fromMethodName.substring(3)
								.toLowerCase())) {

					continue;
				}
				toMethodName = "set" + fromMethodName.substring(3);
				toMethod = findMethodByName(toMethods, toMethodName);

				if (toMethod == null)
					continue;
				Object value = fromMethod.invoke(from, new Object[0]);

				if (value == null)
					continue;
				// 集合类判空处理
				if (value instanceof Collection) {
					Collection<?> newValue = (Collection<?>) value;
					if (newValue.size() <= 0)
						continue;
				}
				toMethod.invoke(to, new Object[] { value });
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	/**
	 * 从方法数组中获取指定名称的方法
	 * 
	 * @param methods
	 * @param name
	 * @return
	 */
	private static Method findMethodByName(Method[] methods, String name) {

		for (int j = 0; j < methods.length; j++) {

			if (methods[j].getName().equals(name)) {

				return methods[j];
			}

		}
		return null;
	}
	
}
