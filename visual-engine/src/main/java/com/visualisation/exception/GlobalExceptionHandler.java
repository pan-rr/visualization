package com.visualisation.exception;

import com.visualisation.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Response handle(Exception e) {
        log.error("global "+e.getMessage() + "---" + Arrays.toString(e.getStackTrace()));
        return Response.error(e.getMessage());
    }
}
