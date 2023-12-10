package com.myga.konnekt.infra.inbound.mail.google.oauth2;

import com.sun.mail.imap.IMAPSSLStore;
import lombok.extern.slf4j.Slf4j;

import javax.mail.MessagingException;
import javax.mail.Session;
import java.security.Provider;
import java.security.Security;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Slf4j
public class OAuth2Authenticator {

    public static IMAPSSLStore connectToImap(String host,
                                             int port,
                                             String userEmail,
                                             String oauthToken,
                                             boolean debug) {
        try {
            Security.addProvider(new OAuth2Provider());
            Properties props = new Properties();
            props.put("mail.imaps.sasl.enable", "true");
            props.put("mail.imaps.sasl.mechanisms", "XOAUTH2");
            props.setProperty("mail.imap.user", userEmail);
            props.put(OAuth2SaslClientFactory.OAUTH_TOKEN_PROP, oauthToken);
            Session session = Session.getInstance(props);
            session.setDebug(debug);
            IMAPSSLStore store = new IMAPSSLStore(session, null);
            store.connect(host, port, userEmail, EMPTY);
            return store;
        } catch (MessagingException exception) {
            log.error("Connection refused", exception);
            throw new RuntimeException(exception);
        }
    }

    public static final class OAuth2Provider extends Provider {
        private static final String FACTORY = "SaslClientFactory.XOAUTH2";

        public OAuth2Provider() {
            super(EMPTY, EMPTY, EMPTY);
            put(FACTORY, OAuth2SaslClientFactory.class.getName());
        }
    }
}