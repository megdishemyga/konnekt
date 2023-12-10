package com.myga.konnekt.usecase;

import com.myga.konnekt.domain.credentials.Credentials;
import com.myga.konnekt.domain.credentials.CredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MailAttachedInvoiceUseCase {
    private final CredentialsRepository credentialsRepository;

    public Mono<Credentials> load(String email) {
        return credentialsRepository.loadCredentials(email)
                .map(this::refreshToken);
    }

    public Credentials refreshToken(Credentials credentials) {
        if (credentials.isExpired()) {
            //not implemented
        }
        return credentials;
    }
}
