package com.ibeiliao.deployment.admin.utils;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 验证参数工具类
 * @author linyi
 */
public class ValidatorUtil {
	
	private static final Validator validator;

	static {
		// 使用 Hibernate Validator
		// 快速失败，校验到一个出错即停止
		validator = Validation.byDefaultProvider().configure()
				.addProperty("hibernate.validator.fail_fast", "true").buildValidatorFactory().getValidator();
	}
	
	/**
	* 校验请求参数的属性，值校验使用了注解的属性
	* @param  obj 要校验的对象
	* @return 如果有错误信息，返回错误信息；没有错误信息则返回null
	 */
	public static <T> String validate(T obj) {
		Set<ConstraintViolation<T>> valideSert = validator.validate(obj);
		if (valideSert != null && valideSert.size() > 0) {
			return valideSert.iterator().next().getMessage();
		} else {
			return null;
		}
	}
	

	/**
	 * 
	* 用于一个bean有些类型不需要验证全部的属性
	* @param obj 要校验的对象
	* @param args 需要验证的属性名
	* @return 如果有错误信息，返回错误信息；没有错误信息则返回null
	 */
	public static <T> String validate(T obj, String... args) {
		StringBuilder buf = new StringBuilder("");

		if (args != null) {
			for (String param : args) {
				Set<ConstraintViolation<T>> valideSert = validator.validateProperty(obj, param);
				if (valideSert != null && valideSert.size() > 0) {
					return valideSert.iterator().next().getMessage();
				}
			}
		}
		return buf.length() == 0 ? null : buf.toString();
	}
	
}
