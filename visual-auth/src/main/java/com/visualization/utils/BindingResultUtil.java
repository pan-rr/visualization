package com.visualization.utils;

import com.visualization.model.Response;
import org.springframework.validation.BindingResult;

public class BindingResultUtil {
    public static Response checkError(BindingResult bindingResult) {
        return bindingResult.hasErrors() ? Response.error(bindingResult.getFieldError().getDefaultMessage()) : null;
    }
}
