package com.wanglei.zhixing.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "zhixing.jwt")
public class JwtProperties {

    private String secret;

    private Integer expireHours;
}
