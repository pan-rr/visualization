package com.visualization.config;

import com.visualization.constant.AuthConstant;
import com.visualization.constant.TenantConstant;
import com.visualization.enums.UserTypeEnum;
import com.visualization.mapper.UserMapper;
import com.visualization.model.api.Option;
import com.visualization.model.db.SystemUser;
import com.visualization.service.UserService;
import com.visualization.utils.MD5Utils;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Objects;

@Configuration
public class PublicTenantOptionConfig {

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @PostConstruct
    public void init() {
        String publicStr = AuthConstant.PUBLIC;
        SystemUser systemUser = userMapper.selectOneByOA(publicStr);
        if (Objects.isNull(systemUser)) {
            systemUser = SystemUser.builder()
                    .oa(publicStr)
                    .username(publicStr)
                    .password(MD5Utils.md5Encode(publicStr))
                    .userType(UserTypeEnum.TENANT.getCode())
                    .build();
            userService.createUser(systemUser);
        }
        Option option = Option.builder().label(AuthConstant.PUBLIC).value(systemUser.getUserId().toString()).build();
        TenantConstant.PUBLIC_TENANT_OPTION.set(option);
    }
}
