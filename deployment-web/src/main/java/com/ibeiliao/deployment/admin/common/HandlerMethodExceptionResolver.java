package com.ibeiliao.deployment.admin.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 视图与JSON结合异常处理,有@ResponseBody方法异常，输出JSON，无@ResponseBody方法异常，输出错误页面
 * 
 * @author linyi 2015/11/16
 * 
 */
public class HandlerMethodExceptionResolver extends ExceptionHandlerExceptionResolver {

	private static final Logger logger = LoggerFactory.getLogger(HandlerMethodExceptionResolver.class);

	private String defaultErrorView;

	/**
	 * 出错时的错误代码，仅对返回结果是 json 的时候有效
	 */
	private int errorCode = -1;

	/**
	 * 返回的json信息里，错误信息的属性名。如果为 ""，不返回 message
	 */
	private String errorMessageAttr = "message";

	/**
	 * 返回的json信息里，错误代码的属性名。如果为 ""，不返回 code
	 */
	private String errorCodeAttr = "code";

	public String getDefaultErrorView() {
		return defaultErrorView;
	}

	public void setDefaultErrorView(String defaultErrorView) {
		this.defaultErrorView = defaultErrorView;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessageAttr() {
		return errorMessageAttr;
	}

	public void setErrorMessageAttr(String errorMessageAttr) {
		this.errorMessageAttr = errorMessageAttr;
	}

	public String getErrorCodeAttr() {
		return errorCodeAttr;
	}

	public void setErrorCodeAttr(String errorCodeAttr) {
		this.errorCodeAttr = errorCodeAttr;
	}

	@Override
	public ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {
		logger.error("exception | 异常 ...", exception);

		if (handlerMethod == null) {
			return null;
		}
		Method method = handlerMethod.getMethod();
		if (method == null) {
			return null;
		}

		super.doResolveHandlerMethodException(request, response, handlerMethod, exception);

		ResponseBody responseBody = method.getAnnotation(ResponseBody.class);
		if (responseBody != null) {
			return handleResponseBody(request, response, exception);
		}

		return handleOther(request, response, exception);
	}

	/**
	 * 处理异常(ResponseBody)
	 * 
	 * @param request
	 * @param response
	 * @param exception
	 * @return
	 */
	private ModelAndView handleResponseBody(HttpServletRequest request, HttpServletResponse response, Exception exception) {
		if (exception instanceof IllegalArgumentException) {
			String uri = getURI(request);
			if (uri.contains("/open/monitor")) {
				throw (IllegalArgumentException) exception;
			}
		}
		String message = getThrowableMessage(exception);

		Map<String, Object> attributes = new HashMap<>(4);
		int code = getErrorCode();
		if (StringUtils.isNotEmpty(errorCodeAttr)) {
			attributes.put("code", Integer.valueOf(code));
		}
		if (StringUtils.isNotEmpty(errorMessageAttr)) {
			attributes.put("message", message);
		}

		MappingJackson2JsonView view = new MappingJackson2JsonView();
		view.setAttributesMap(attributes);

		ModelAndView model = new ModelAndView();
		model.setView(view);

		return model;
	}

	/**
	 * 获取系统资源地址
	 */
	private String getURI(HttpServletRequest request) {
		UrlPathHelper helper = new UrlPathHelper();
		String uri = helper.getOriginatingRequestUri(request);
		String ctxPath = helper.getOriginatingContextPath(request);
		return uri.replaceFirst(ctxPath, "");
	}

	/**
	 * 处理异常(Other)
	 * 
	 * @param request
	 * @param response
	 * @param exception
	 * @return
	 */
	private ModelAndView handleOther(HttpServletRequest request, HttpServletResponse response, Exception exception) {
		String message = getThrowableMessage(exception);

		ModelAndView model = new ModelAndView(defaultErrorView);
		model.addObject("message", message);
		return model;
	}

	/**
	 * 获取异常信息(如无信息,则返回'未知错误')
	 * 
	 * @param exception
	 * @return
	 */
	private String getThrowableMessage(Throwable exception) {
		if (exception != null) {
			String message = exception.getMessage();
			if (StringUtils.isEmpty(message)) {
				return getThrowableMessage(exception.getCause());
			}
			return message;
		}
		return "未知错误";
	}
}
