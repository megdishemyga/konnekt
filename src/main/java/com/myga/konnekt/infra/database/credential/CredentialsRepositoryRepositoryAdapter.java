package com.myga.konnekt.infra.database.credential;

import com.myga.konnekt.domain.credentials.Credentials;
import com.myga.konnekt.domain.credentials.CredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@RequiredArgsConstructor
public class CredentialsRepositoryRepositoryAdapter implements CredentialsRepository {
    private final ReactiveMongoOperations operations;


    @Override
    public Mono<Credentials> loadCredentials(String email) {
        return operations.findOne(query(where("email").is(email)), Credentials.class);
    }

    @Override
    public Flux<Credentials> findAll() {
        return operations.findAll(Credentials.class);
    }

    @Override
    public Mono<Credentials> save(Credentials credentials) {
        return operations.save(credentials);
    }

    @Override
    public Mono<Void> delete(String id) {
        return operations.remove(query(where("id").is(id)), Credentials.class).then();
    }

    @Override
    public Mono<Void> clearStore() {
        return operations.dropCollection(Credentials.class).then();
    }
}
