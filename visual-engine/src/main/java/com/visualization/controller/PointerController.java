package com.visualization.controller;

import com.visualization.manager.PointerQueueManager;
import com.visualization.model.Response;
import com.visualization.model.dag.db.DAGPointer;
import org.springframework.util.CollectionUtils;
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
    private PointerQueueManager pointerQueueManager;

    @PostMapping("/acceptPointers")
    public Response<Object> acceptPointers(@RequestBody List<DAGPointer> pointers) {
        if (!CollectionUtils.isEmpty(pointers)) {
            pointerQueueManager.offerPointer(pointers);
        }
        return Response.builder().message("success").build();
    }
}
