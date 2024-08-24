package com.visualization.model.db;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_system_user")
public class SystemUser {

    @TableId
    private Long userId;

    @NonNull
    private String oa;

    @NonNull
    private String username;

    @NonNull
    private String password;

    @NonNull
    private Integer userType;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;
}
