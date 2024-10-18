package com.visualization.controller;

import com.visualization.stage.VisualJobHandler;
import com.visualization.model.Response;
import com.visualization.model.dag.db.DAGPointer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/dag")
public class PointerController {

    @Resource
    private VisualJobHandler visualJobHandler;

    @PostMapping("/acceptPointers")
    public Response<Object> acceptPointers(@RequestBody List<DAGPointer> pointers) {
        visualJobHandler.offerPointer(pointers);
        return Response.builder().message("success").build();
    }
}
