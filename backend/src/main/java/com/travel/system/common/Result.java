package com.travel.system.common;

/**
 * 统一返回结果类
 * 所有接口都返回这个格式，方便前端统一处理
 */
public class Result<T> {

    private Integer code;      // 状态码：200 成功，500 失败
    private String message;    // 提示信息
    private T data;            // 返回数据

    /** 成功 */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /** 失败 */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
