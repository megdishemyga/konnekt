package com.myga.konnekt.infra.inbound.mail.google.oauth2;

import java.util.Arrays;
import java.util.Map;

import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;

public class OAuth2SaslClientFactory implements SaslClientFactory {
  public static final String OAUTH_TOKEN_PROP = "mail.imaps.sasl.mechanisms.oauth2.oauthToken";
  private static final String MECHANISM = "XOAUTH2";

  public SaslClient createSaslClient(String[] mechanisms,
                                     String authorizationId,
                                     String protocol,
                                     String serverName,
                                     Map<String, ?> props,
                                     CallbackHandler callbackHandler) {
    boolean matchedMechanism = Stream.of(mechanisms).anyMatch(MECHANISM::equalsIgnoreCase);
    return matchedMechanism? new OAuth2SaslClient((String) props.get(OAUTH_TOKEN_PROP),
                                callbackHandler): null;
  }

  public String[] getMechanismNames(Map<String, ?> props) {
    return new String[] {MECHANISM};
  }
}