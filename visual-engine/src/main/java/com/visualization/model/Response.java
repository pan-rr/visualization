package com.visualization.model;

import com.visualization.enums.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response<T> {

    private T result;
    private Integer code;
    private String message;


    public Response(T result) {
        this.result = result;
        message = ResponseType.SUCCESS.getMessage();
        code = ResponseType.SUCCESS.getCode();
    }

    public static <T> Response success(T result) {
        return new Response(result);
    }

    public static <T> Response error(T result) {
        return com.visualization.model.Response.builder()
                .result(result)
                .code(ResponseType.ERROR.getCode())
                .message(result.toString())
                .build();
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
