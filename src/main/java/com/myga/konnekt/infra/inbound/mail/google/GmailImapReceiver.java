package com.myga.konnekt.infra.inbound.mail.google;

import com.myga.konnekt.domain.credentials.Credentials;
import com.myga.konnekt.infra.inbound.mail.EmailUtils;
import com.sun.mail.imap.IMAPMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static com.myga.konnekt.infra.inbound.mail.google.oauth2.OAuth2Authenticator.connectToImap;


@Slf4j
@Component
@RequiredArgsConstructor
public class GmailImapReceiver {
    private static final String INBOX = "INBOX";
    private static final String IMAP_GMAIL_HOST = "imap.gmail.com";


    public void searchAttachedInvoices(Credentials credentials) {
        try (var imapStore = connectToImap(IMAP_GMAIL_HOST, 993, credentials.getEmail(), credentials.getAccessToken(), true); var inbox = imapStore.getFolder(INBOX)) {
            inbox.open(Folder.READ_ONLY);
            final SearchTerm searchTerm = buildSearchTerms();
            Stream.of(inbox.search(searchTerm))
                    .filter(IMAPMessage.class::isInstance)
                    .map(IMAPMessage.class::cast)
                    .forEach(EmailUtils::saveAttachment);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private SearchTerm buildSearchTerms() {
        List<SearchTerm> searchTerms = List.of(new SubjectTerm("*"),
                new FlagTerm(new Flags(Flags.Flag.SEEN), false),
                new ReceivedDateTerm(ComparisonTerm.GT, getDateTimeCriteria(Duration.ofDays(100))),
                new FromStringTerm("*"));
        return new AndTerm(searchTerms.toArray(SearchTerm[]::new));
    }

    private Date getDateTimeCriteria(Duration duration) {
        LocalDateTime pastDateTime = LocalDateTime.now().minus(duration);
        return Date.from(pastDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}

