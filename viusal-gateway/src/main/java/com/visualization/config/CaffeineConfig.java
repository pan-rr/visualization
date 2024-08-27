//package com.visualization.config;
//
//import com.github.benmanes.caffeine.cache.Cache;
//import com.github.benmanes.caffeine.cache.Caffeine;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//public class CaffeineConfig {
//
//    @Bean
//    public Cache<String,String> cache(){
//        Cache<String, String> cache = Caffeine.newBuilder()
//                //初始数量
//                .initialCapacity(10)
//                //最大条数
//                .maximumSize(10)
//                //expireAfterWrite和expireAfterAccess同时存在时，以expireAfterWrite为准
//                //最后一次写操作后经过指定时间过期
//                .expireAfterWrite(1, TimeUnit.SECONDS)
//                //最后一次读或写操作后经过指定时间过期
//                .expireAfterAccess(1, TimeUnit.SECONDS)
//                //监听缓存被移除
//                .removalListener((key, val, removalCause) -> { })
//                .build();
//
//    }
//}
