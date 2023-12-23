package com.myga.konnekt.infra.security;

import com.myga.konnekt.domain.credentials.Credentials;
import com.myga.konnekt.domain.credentials.CredentialsRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    private final ReactiveOAuth2AuthorizedClientService authorizedClientService;
    private final CredentialsRepository persistentDataStoreRepository;

    public CustomAuthenticationSuccessHandler(ReactiveOAuth2AuthorizedClientService authorizedClientService, CredentialsRepository persistentDataStoreRepository) {
        this.authorizedClientService = authorizedClientService;
        this.persistentDataStoreRepository = persistentDataStoreRepository;
    }

    private static Credentials toCredentials(OAuth2AuthorizedClient authorizedClient, String email) {
        final var refreshToken = Optional.ofNullable(authorizedClient.getRefreshToken()).map(AbstractOAuth2Token::getTokenValue).orElse(null);
        final var accessToken = authorizedClient.getAccessToken().getTokenValue();
        final var epochSecond = authorizedClient.getAccessToken().getExpiresAt().getEpochSecond();
        return Credentials.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .expirationTimeMilliseconds(epochSecond)
                .email(email)
                .provider(authorizedClient.getClientRegistration().getRegistrationId())
                .id(authorizedClient.getPrincipalName())
                .build();
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        OAuth2AuthenticationToken oauth2Authentication = (OAuth2AuthenticationToken) authentication;
        final var principal = (DefaultOidcUser) authentication.getPrincipal();
        final String email = principal.getEmail();
        String clientRegistrationId = oauth2Authentication.getAuthorizedClientRegistrationId();
        return authorizedClientService.loadAuthorizedClient(
                clientRegistrationId,
                oauth2Authentication.getName()
        ).flatMap(authorizedClient -> {
            final var credentials = toCredentials(authorizedClient, email);
            return persistentDataStoreRepository.save(credentials).then();
        });
    }

}
