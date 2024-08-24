package com.visualization.exeception;

import com.visualization.model.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
public class GlobalHandler {

    @ExceptionHandler(value = Throwable.class)
    public Response handle(Throwable e){
        System.err.println(e);
        return Response.error(e.getMessage());
    }
}
