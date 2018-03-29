package com.ibeiliao.deployment.exception;

/**
 * Service层异常
 * @author linyi 2016/7/12.
 */
public class ServiceException extends RuntimeException {

    static final long serialVersionUID = 1L;

    /**
     * 错误代码
     */
    private int code;

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
