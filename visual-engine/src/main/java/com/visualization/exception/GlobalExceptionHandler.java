package com.visualization.exception;

import com.visualization.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public Response handle(Throwable e) {
        log.error("global error: {} ===> {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        return Response.error(e.getMessage());
    }
}
