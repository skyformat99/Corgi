package com.ibeiliao.deployment.common.util;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;

/**
 * json 工具类
 * @author linyi 2016/7/12
 */
public final class JsonUtil {

	private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

	private JsonUtil() {
	}

	public final static String EMPTY_Object = "{}";
	public final static String EMPTY_ARRAY = "[]";

	public static ObjectMapper mapper = null;
	static {
		try {
			mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		} catch (Exception e) {
			logger.error("初始化错误！", e);
		}
	}


	/**
	 * 把json文本parse成JsonObject
	 */
	public static JsonObject toJsonObject(String text) {
		Reader reader = new StringReader(text);
		JsonReader jsonReader = Json.createReader(reader);
		try {
			return jsonReader.readObject();
		} finally {
			try {
				if (null != jsonReader)
					jsonReader.close();
				if (null != reader)
					reader.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * 把对象转成JsonObject
	 */
	public static JsonObject toJsonObject(Object object) {
		String json = toJSONString(object);// 寻找 JSON-API 自身解决方法
		return toJsonObject(json);
	}

	/**
	 * 把json文本parse成JsonArray
	 */
	public static JsonArray toJsonArray(String text) {
		Reader reader = new StringReader(text);
		JsonReader jsonReader = Json.createReader(reader);
		try {
			return jsonReader.readArray();
		} finally {
			try {
				if (null != jsonReader)
					jsonReader.close();
				if (null != reader)
					reader.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	public static <T> T parseObject(JsonObject json, Class<T> clazz) {
		if (null == json)
			return null;
		return parseObject(json.toString(), clazz);// 寻找 JSON-API 自身的解决方法
	}

//	public static <T> List<T> parseArray(JsonArray json, Class<T> clazz) {
//		if (null == json)
//			return null;
//		return parseArray(json.toString(), clazz);// 寻找 JSON-API 自身的解决方法
//	}

	// ####
	// ## jsonText 2 Object
	// ####

	public static <T> T parseObject(String text, TypeReference<T> tr) {
		try {
			return mapper.readValue(text, tr);
		} catch (Exception e) {
			throw new RuntimeException(e);// 查看 mapper 成员变量上的注释
		}
	}

	/**
	 * 把JSON文本parse为JavaBean
	 *
	 * @return
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public static <T> T parseObject(String text, Class<T> clazz) {
		try {
			return mapper.readValue(text, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e);// 查看 mapper 成员变量上的注释
		}
	}

//	/**
//	 * 把JSON文本parse成JavaBean集合
//	 */
//	public static <T> List<T> parseArray(String text, final Class<T> clazz) {
//		try {
////			JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
//			return mapper.readValue(text, new ArrayT<T>(clazz));
//		} catch (Exception e) {
//			throw new RuntimeException(e);// 查看 mapper 成员变量上的注释
//		}
//	}
//
//	public static <T> Map<String, T> parseMap(String text, final Class<T> valueClazz){
//		try {
//			return mapper.readValue(text, new MapT<T>(valueClazz));
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	public static <K, V> Map<K, V> parseMap(String text, final Class<K> keyClazz, final Class<V> valueClazz) {
//		try {
//			return mapper.readValue(text, new MapKV<K, V>(keyClazz, valueClazz));
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}

	public static boolean isJSON(final String json) {
		boolean valid = false;
		try {
			final JsonParser parser = new ObjectMapper().getFactory().createParser(json);
			while (parser.nextToken() != null) {
			}
			valid = true;
		} catch (JsonParseException jpe) {
			jpe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return valid;
	}

	// ####
	// ## Object 2 JsonText
	// ####

	/**
	 * 将JavaBean序列化为JSON文本
	 *
	 * @throws JsonProcessingException
	 */
	public static String toJSONString(Object object) {
		if (null == object)
			return null;
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);// 查看 mapper 成员变量上的注释
		}

	}


	public static JsonObjectBuilder toJsonObjectBuilder(String text) {
		JsonObject json = toJsonObject(text);
		return toJsonObjectBuilder(json);
	}

	public static JsonObjectBuilder toJsonObjectBuilder(JsonObject json, String name) {
		return toJsonObjectBuilder(json.getJsonObject(name));
	}

	public static JsonObjectBuilder toJsonObjectBuilder(JsonObject json) {
		JsonObjectBuilder jsonObjBuild = Json.createObjectBuilder();
		if (null != json)
			for (String key : json.keySet())
				jsonObjBuild.add(key, json.get(key));
		return jsonObjBuild;
	}

	public static JsonArrayBuilder toJsonArrayBuilder(String text) {
		JsonArray json = toJsonArray(text);
		return toJsonArrayBuilder(json);
	}

	public static JsonArrayBuilder toJsonArrayBuilder(JsonObject json, String name) {
		return toJsonArrayBuilder(json.getJsonArray(name));
	}

	public static JsonArrayBuilder toJsonArrayBuilder(JsonArray array) {
		JsonArrayBuilder jsonArrayBuild = Json.createArrayBuilder();
		if (null != array)
			for (int i = 0; i < array.size(); i++)
				jsonArrayBuild.add(array.get(i));
		return jsonArrayBuild;
	}

	// ####
	// ## static class T 2 Type
	// ####

	public static class ObjectT<T> extends TypeReference<T> {

		private Class<T> clz;

		public ObjectT(Class<T> clz) {
			this.clz = clz;
		}

		/*
		 * （非 Javadoc）
		 * 
		 * @see com.fasterxml.jackson.core.type.TypeReference#getType()
		 */
		@Override
		public Type getType() {
			return clz;
		}

	}

}
