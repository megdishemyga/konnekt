package com.myga.konnekt.domain.credentials;

import lombok.Builder;
import lombok.Getter;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Builder
@With
@Document(collection = "stored_tokens")
public class Credentials {
    @Id
    private String id;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String provider;
    private Long expirationTimeMilliseconds;

    public boolean isExpired() {
        return Instant.ofEpochMilli(expirationTimeMilliseconds).isAfter(Instant.now());
    }

}
