package com.visualization.model.db;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_system_user")
public class SystemUser {

    @TableId
    private Long userId;

    @NotBlank(message = "oa不能为空")
    private String oa;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotNull(message = "类型不能为空")
    private Integer userType;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;
}
