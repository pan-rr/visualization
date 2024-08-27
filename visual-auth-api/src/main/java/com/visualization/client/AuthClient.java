package com.visualization.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "VISUAL-AUTH")
public interface AuthClient {


}
