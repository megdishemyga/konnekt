package com.myga.konnekt.usecase.service;

import com.myga.konnekt.domain.credentials.Credentials;
import reactor.core.publisher.Mono;

public interface TokenProvider {
    Mono<Credentials> refreshToken(Credentials credentials);
}
