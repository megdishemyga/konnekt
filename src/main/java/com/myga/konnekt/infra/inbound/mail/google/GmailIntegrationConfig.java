package com.myga.konnekt.infra.inbound.mail.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.GmailScopes;
import com.myga.konnekt.infra.inbound.mail.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.myga.konnekt.infra.inbound.mail.google.oauth2.OAuth2Authenticator.connectToImap;

@Slf4j
@Configuration
@EnableIntegration
@RequiredArgsConstructor
public class GmailIntegrationConfig {

    private static final String INBOX = "INBOX";
    private static final String AUTH_PROVIDER_KEY = "auth_provider_x509_cert_url";
    private static final String PROJECT_KEY = "project_id";
    private static final String EMAIL = "test@gmail.com";
    private static final String IMAP_GMAIL_HOST = "imap.gmail.com";
    private final GoogleClientProperties googleClientProperties;

    @Bean
    public IntegrationFlow emailIntegrationFlow() throws MessagingException {
        String oauth2AccessToken = getOauth2AccessToken();
        try (var imapStore = connectToImap(IMAP_GMAIL_HOST,
                993,
                EMAIL,
                oauth2AccessToken,
                true);
             var inbox = imapStore.getFolder(INBOX)) {

            inbox.open(Folder.READ_ONLY);
            //Todo: build searchTerm dynamically based on http request to trigger integration
            //todo: use SearchTermBuilder()
            SearchTerm searchTerm = new AndTerm(
                    new ReceivedDateTerm(ComparisonTerm.GT, getDateTimeCriteria(Duration.ofDays(1))),
                    new SubjectTerm("invoice")
                    //new BodyTerm("multipart")
            );
            //Todo: implement our solution for integrationFlow
            Queue<Message<?>> linkedBlockingQueue = Stream.of(inbox.search(searchTerm))
                    .map(mail -> {
                        EmailUtils.saveAttachment(mail);
                        return MessageBuilder.withPayload(mail).build();
                    })
                    .collect(Collectors.toCollection(LinkedBlockingQueue::new));
            return IntegrationFlow.from(MessageChannels.queue(linkedBlockingQueue))
                    .handle(m -> System.out.println("Received email: " + m.getPayload()))
                    .get();
        }
    }

    //TODO: to be deleted the token will be embedded in the http request
    private GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow() {
        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            var details = new GoogleClientSecrets.Details().setClientId(googleClientProperties.getClientId())
                    .setClientSecret(googleClientProperties.getClientSecret())
                    .setAuthUri(googleClientProperties.getAuthUri())
                    .setTokenUri(googleClientProperties.getTokenUri())
                    .set(AUTH_PROVIDER_KEY, googleClientProperties.getAuthProvider())
                    .set(PROJECT_KEY, googleClientProperties.getProjectId());
            GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setInstalled(details);
            return new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, jsonFactory, clientSecrets,
                    List.of(GmailScopes.MAIL_GOOGLE_COM)).build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getOauth2AccessToken() {
        try {
            Credential credential = new AuthorizationCodeInstalledApp(googleAuthorizationCodeFlow(), new LocalServerReceiver.Builder().setPort(51564).build()).authorize(EMAIL);
            return credential.getAccessToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Date getDateTimeCriteria(Duration duration) {
        LocalDateTime pastDateTime = LocalDateTime.now().minus(duration);
        return Date.from(pastDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}

