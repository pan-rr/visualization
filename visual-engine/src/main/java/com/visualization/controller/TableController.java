package com.visualization.controller;

import com.visualization.model.Response;
import com.visualization.table.parse.VisualParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/table")
public class TableController {

    @GetMapping("/validate")
    public Response<Object> validate(@RequestParam("sql") String sql) throws Exception {
        VisualParser parser = VisualParser.parse(sql);
        return Response.success(parser.getErrors());
    }

}
