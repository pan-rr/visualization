package com.visualization;

import com.visualization.enums.UserTypeEnum;
import com.visualization.model.db.SystemPermission;
import com.visualization.model.db.SystemResource;
import com.visualization.model.db.SystemUser;
import com.visualization.model.db.SystemUserPermission;
import com.visualization.service.PermissionService;
import com.visualization.service.ResourceService;
import com.visualization.service.UserService;
import com.visualization.utils.SnowIdUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class DBTest {

    @Resource
    private UserService userService;

    @Resource
    private ResourceService resourceService;

    @Resource
    private PermissionService permissionService;

    @Test
    public void test2() {
        SystemUser user = SystemUser.builder()
                .oa("testTenant")
                .username("testTenant")
                .userType(UserTypeEnum.TENANT.getCode())
                .build();
        userService.createUser(user);
    }

    @Test
    public void test3() {
        SystemResource resource = SystemResource.builder()
                .resourceId(SnowIdUtil.generateId())
                .resourceName("resource")
                .tenantId(1276916670077599744L)
                .build();
        resourceService.createResource(resource);
    }

    @Test
    public void test4() {
        SystemPermission permission = SystemPermission.builder()
                .tenantId(1276916670077599744L)
                .permissionId(SnowIdUtil.generateId())
                .permissionName("testPermission")
                .resourceList("1276926628198350849")
                .build();
        permissionService.createPermission(permission);

    }

    @Test
    public void test5() {

        SystemUserPermission userPermission = SystemUserPermission.builder()
                .userId(1276690573041860608L)
                .permissionId(1276926846335713281L).build();
        permissionService.grantPermissionToUser(userPermission);
    }

    @Test
    public void test6() {

        List<String> permission = userService.getPermission(1276690573041860608L);
        System.err.println(permission);
    }
}
