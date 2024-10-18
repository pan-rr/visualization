--初期的权限表语句，后续表结构可能会改，请收悉
CREATE TABLE `t_system_permission` (
  `permission_id` bigint NOT NULL,
  `permission_name` varchar(255) DEFAULT NULL,
  `tenant_id` bigint NOT NULL,
  `resource_list` text,
  PRIMARY KEY (`permission_id`),
  KEY `idx_tenant` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `t_system_resource` (
  `resource_id` bigint NOT NULL,
  `resource_name` varchar(255) DEFAULT NULL,
  `tenant_id` bigint NOT NULL,
  PRIMARY KEY (`resource_id`) USING BTREE,
  KEY `idx_tenant` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `t_system_tenant` (
  `tenant_id` bigint NOT NULL,
  `tenant_name` varchar(255) DEFAULT NULL,
  `father_id` bigint DEFAULT NULL,
  `root_id` bigint DEFAULT NULL,
  PRIMARY KEY (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `t_system_user` (
  `user_id` bigint NOT NULL,
  `oa` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `user_type` int DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `idx_oa` (`oa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `t_system_user_permission` (
  `user_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;