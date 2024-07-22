package com.visualisation.model;

import com.visualisation.constant.ResponseConstant;
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
        message = ResponseConstant.SUCCESS_MESSAGE;
        code = ResponseConstant.SUCCESS;
    }

    public static <T> Response success(T result) {
        return new Response(result);
    }

    public static <T> Response error(T result) {
        return Response.builder()
                .result(result)
                .code(ResponseConstant.ERROR)
                .message(ResponseConstant.ERROR_MESSAGE)
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
