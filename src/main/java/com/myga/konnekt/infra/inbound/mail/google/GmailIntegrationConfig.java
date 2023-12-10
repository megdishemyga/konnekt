package com.myga.konnekt.infra.inbound.mail.google;

import com.myga.konnekt.domain.credentials.CredentialsRepository;
import com.myga.konnekt.infra.inbound.mail.EmailUtils;
import com.sun.mail.util.logging.MailHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.InboundChannelAdapters;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.SearchTermStrategy;
import org.springframework.integration.mail.dsl.Mail;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Stream;

import static com.myga.konnekt.infra.inbound.mail.google.oauth2.OAuth2Authenticator.connectToImap;

@Slf4j
@Component
@RequiredArgsConstructor
public class GmailIntegrationConfig {
    private static final String INBOX = "INBOX";
    private static final String IMAP_GMAIL_HOST = "imap.gmail.com";

    private final CredentialsRepository credentialsRepository;

    public Flux<String> emailIntegrationFlow(String email) {
        return credentialsRepository.loadCredentials(email)
                .flatMapIterable(credentials -> {
                    try (var imapStore = connectToImap(IMAP_GMAIL_HOST, 993, credentials.getEmail(), credentials.getAccessToken(), true);
                         var inbox = imapStore.getFolder(INBOX)) {
                        inbox.open(Folder.READ_ONLY);
                        SearchTerm searchTerm = new AndTerm(
                                new SearchTerm[]{
                                        new ReceivedDateTerm(ComparisonTerm.GT, getDateTimeCriteria(Duration.ofDays(1))),
                                        //new FlagTerm(new Flags(Flags.Flag.SEEN), false),
                                        //new SubjectTerm("invoice"),
                                        //new BodyTerm("multipart"),
                                }
                        );
                        return Stream.of(inbox.search(searchTerm))
                                .map(EmailUtils::saveAttachment)
                                .toList();
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private Date getDateTimeCriteria(Duration duration) {
        LocalDateTime pastDateTime = LocalDateTime.now().minus(duration);
        return Date.from(pastDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}

