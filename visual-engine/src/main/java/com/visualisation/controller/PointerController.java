package com.visualisation.controller;

import com.visualisation.manager.PointerQueueManager;
import com.visualisation.model.Response;
import com.visualisation.model.dag.db.DAGPointer;
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
            pointerQueueManager.addAll(pointers);
        }
        return Response.builder().message("success").build();
    }
}
