package io.github.moyifengxue.log.record.result;

import lombok.Data;

import java.util.Map;

/**
 * 统一响应
 *
 * @author liuzhen
 */
@Data
public final class Result<T> {

    /**
     * 响应码
     */
    private final int code;

    /**
     * 具体内容
     */
    private final T data;

    /**
     * 消息
     */
    private final String message;

    /**
     * 元数据
     */
    private final Map<?, ?> metadata;


    private Result(int code, String message, T data, Map<?, ?> metadata) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.metadata = metadata;
    }

    public static <Response> Result<Response> of(int code, String message, Response data, Map<?, ?> metadata) {
        return new Result<>(code, message, data, metadata);
    }

    public static <Response> Result<Response> of(int code, String message) {
        return new Result<>(code, message, null, null);
    }

    public Boolean isSuccess() {
        return this.code == 200;
    }

}
