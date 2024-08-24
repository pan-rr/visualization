package com.visualization.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.visualization.enums.RoleEnum;
import com.visualization.enums.UserTypeEnum;
import com.visualization.exeception.AuthException;
import com.visualization.mapper.TenantMapper;
import com.visualization.mapper.UserMapper;
import com.visualization.mapper.ResourceMapper;
import com.visualization.model.api.Option;
import com.visualization.model.api.UserInfo;
import com.visualization.model.db.SystemResource;
import com.visualization.model.db.SystemTenant;
import com.visualization.model.db.SystemUser;
import com.visualization.service.UserService;
import com.visualization.utils.MD5Utils;
import com.visualization.utils.SnowIdUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private TenantMapper tenantMapper;

    @Resource
    private ResourceMapper resourceMapper;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void createUser(SystemUser user) {
        user.setPassword(MD5Utils.md5Encode(user.getPassword()));
        user.setUserId(SnowIdUtil.generateId());
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setModifyTime(now);
        if (UserTypeEnum.isTenant(user.getUserType())) {
            SystemTenant tenant = SystemTenant.builder()
                    .tenantId(user.getUserId())
                    .tenantName(user.getUsername())
                    .build();
            tenantMapper.insert(tenant);
        }
        userMapper.insert(user);
    }

    @Override
    public SystemUser getNonNullUser(String oa) {
        SystemUser user = userMapper.selectOneByOA(oa);
        if (Objects.isNull(user)){
            throw new AuthException("查无此账号！");
        }
        return user;
    }

    @Override
    public UserInfo login(SystemUser user) {
        SystemUser dbUser = getNonNullUser(user.getOa());
        Long userId = dbUser.getUserId();
        String password = MD5Utils.md5Encode(user.getPassword());
        boolean flag = StringUtils.equals(password, dbUser.getPassword());
        if (Boolean.FALSE.equals(flag)){
            throw new AuthException("账号密码不匹配！");
        }
        StpUtil.login(userId);
        String tokenValue = StpUtil.getTokenValue();
        List<Option> tenant = tenantMapper.selectUserTenant(userId);
        UserInfo userInfo = UserInfo.builder()
                .oa(dbUser.getOa())
                .name(dbUser.getUsername())
                .token(tokenValue)
                .userId(String.valueOf(dbUser.getUserId()))
                .tenantOptions(tenant)
                .build();
        userInfo.computeOptions();
        return userInfo;
    }

    @Override
    public List<String> getPermission(Long userId) {
        Set<Long> userResource = resourceMapper.selectUserResourceList(userId)
                .stream()
                .flatMap(resources -> Arrays.stream(resources.split(",")).map(Long::valueOf))
                .collect(Collectors.toSet());
        List<SystemResource> allResource = resourceMapper.selectTenantResourceByUserId(userId);
        return allResource.stream()
                .filter(i -> userResource.contains(i.getResourceId()))
                .map(SystemResource::computeResourceKey)
                .collect(Collectors.toList());
    }

    @Override
    public RoleEnum getRole(Long userId) {
        SystemTenant systemTenant = tenantMapper.selectById(userId);
        if (Objects.isNull(systemTenant) || Objects.nonNull(systemTenant.getRootId())){
            return RoleEnum.NORMAL;
        }
        return RoleEnum.ADMIN;
    }
}