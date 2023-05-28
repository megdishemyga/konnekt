package com.myga.konnekt.infra.inbound.mail.google;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties(prefix = "konnekt", ignoreUnknownFields = false)
public class GoogleClientProperties {
    private String clientId;
    private String clientSecret;
    private String tokenUri;
    private String authUri;
    private String authProvider;
    private String projectId;
}
