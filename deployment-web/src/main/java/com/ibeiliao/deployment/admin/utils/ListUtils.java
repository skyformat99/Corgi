package com.ibeiliao.deployment.admin.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 列表集合工具类
 * 
 * @author chenjianhong
 * @time 2014年11月27日 下午7:06:46
 */
public class ListUtils {
	/**
	 * 集合转换成列表
	 * 
	 * @param set
	 * @return
	 */
	public static <T> List<T> toList(Set<T> set) {
		if (set == null) {
			return null;
		}

		if (set.size() == 0) {
			return new ArrayList<T>();
		}

		List<T> list = new ArrayList<T>();


		Iterator<T> iterator = set.iterator();
		while (iterator.hasNext()) {
			T next = iterator.next();
			list.add(next);
 }
		return list;
	}

	/**
	 * 列表转换成集合
	 * 
	 * @param list
	 * @return
	 */
	public static <T> Set<T> toSet(List<T> list) {
		if (list == null) {
			return null;
		}

		if (list.size() == 0) {
			return new HashSet<T>();
		}

		Set<T> set = new HashSet<T>();

		for (T element : list) {
			set.add(element);
		}
		return set;
	}

	/**
	 * List是否为空
	 * 
	 * @param list
	 * 
	 * @return 若空返回true
	 */
	public static boolean isEmpty(List<?> list) {
		if (list == null || list.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * List是否非空
	 * 
	 * @param list
	 * 
	 * @return 若非空返回true
	 */
	public static boolean isNotEmpty(List<?> list) {
		return !isEmpty(list);
	}

	/**
	 * 当list==null赋予默认值ArrayList
	 * 
	 * @param list
	 * @return
	 */
	public static <T> List<T> defaultList(List<T> list) {
		if (list == null) {
			return new ArrayList<T>();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getPropertyList(List<? extends Object> list, Class<T> clazz, String fieldName) {
		List<T> result = new ArrayList<T>();
		Field field = null;

		for (int i = 0; i < list.size(); i++) {
			if (null == field) {
				try {
					field = list.get(i).getClass().getDeclaredField(fieldName);
				} catch (Exception e) {
					throw new RuntimeException("getDeclaredField ERROR, field:" + fieldName, e);
				}
			}

			field.setAccessible(true);
			try {
				if (field.get(list.get(i)).getClass().equals(clazz)) {
					result.add((T) field.get(list.get(i)));
				}
			} catch (Exception e) {
				throw new RuntimeException("UNKNOWN ERROR, field:" + field, e);
			}
		}
		return result;
	}

	/**
	 * 将list的某个字段抽取出来生成set
	 * 
	 * @param list 待处理的list
	 * @param clazz 抽取字段的类型
	 * @param fieldName 抽取的字段名
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> getPropertySet(List<? extends Object> list, Class<T> clazz, String fieldName) {
		Set<T> result = new HashSet<T>();
		Field field = null;

		for (int i = 0; i < list.size(); i++) {
			if (null == field) {
				try {
					field = list.get(i).getClass().getDeclaredField(fieldName);
				} catch (Exception e) {
					throw new RuntimeException("getDeclaredField ERROR, field:" + fieldName, e);
				}
			}

			field.setAccessible(true);
			try {
				if (field.get(list.get(i)).getClass().equals(clazz)) {
					result.add((T) field.get(list.get(i)));
				}
			} catch (Exception e) {
				throw new RuntimeException("UNKNOWN ERROR, field:" + field, e);
			}
		}
		return result;
	}

	/**
	 * 去除list中的重复数据，保持原有顺序
	 * 
	 * @param list
	 * @return
	 */
	public static <T> List<T> removeDuplicate(List<T> list) {
		Set<T> set = new HashSet<T>();
		List<T> newList = new LinkedList<>();
		for (Iterator<T> iter = list.iterator(); iter.hasNext();) {
			T element = iter.next();
			if (set.add(element))
				newList.add(element);
		}
		return newList;
	}

	public static void main(String[] args) {
		// ListUtils.Person p1 = new ListUtils.Person("a", "b");
		// ListUtils.Person p2 = new ListUtils.Person("c", "d");
		// List<Person> persons = new ArrayList<ListUtils.Person>();
		// persons.add(p1);
		// persons.add(p2);
		//
		// Set<String> set = ListUtils.getPropertySet(persons, String.class, "a");
		// for (String tmp : set) {
		// System.out.println(tmp);
		// }
		//
		// List<Long> list = new ArrayList<Long>();
		// list.add(1L);
		// list.add(2L);
		// list.add(3L);
		// System.out.println(list2Str(list));

//		List list = new ArrayList<>();
		//list.add(11);

		//list = removeDuplicate(list);

	}


}
