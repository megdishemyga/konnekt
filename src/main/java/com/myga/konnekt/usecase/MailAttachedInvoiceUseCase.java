package com.myga.konnekt.usecase;

import com.myga.konnekt.domain.credentials.CredentialsRepository;
import com.myga.konnekt.infra.inbound.mail.google.GmailImapReceiver;
import com.myga.konnekt.usecase.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailAttachedInvoiceUseCase {
    private final CredentialsRepository credentialsRepository;
    private final TokenProvider tokenProvider;
    private final GmailImapReceiver gmailImapReceiver;

    public void process(final String email) {
        credentialsRepository.loadCredentials(email)
                .flatMap(tokenProvider::refreshToken)
                .flatMap(credentialsRepository::save)
                .subscribe(gmailImapReceiver::searchAttachedInvoices);
    }
}
