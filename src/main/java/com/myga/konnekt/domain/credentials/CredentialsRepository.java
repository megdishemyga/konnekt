package com.myga.konnekt.domain.credentials;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CredentialsRepository {
    Mono<Credentials> loadCredentials(String email);
    Flux<Credentials> findAll();
   Mono<Credentials> save(Credentials credentials);
    Mono<Void> delete(String id);

    Mono<Void> clearStore();
}
