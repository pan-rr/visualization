package com.visualization.exeception;

import com.visualization.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalHandler {

    @ExceptionHandler(value = Throwable.class)
    public Response handle(Throwable e) {
        log.error(Arrays.toString(e.getStackTrace()));
        return Response.error(e.getMessage());
    }
}
