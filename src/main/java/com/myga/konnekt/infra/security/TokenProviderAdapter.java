package com.myga.konnekt.infra.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.myga.konnekt.domain.credentials.Credentials;
import com.myga.konnekt.usecase.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TokenProviderAdapter implements TokenProvider {
    private final ReactiveClientRegistrationRepository reactiveClientRegistrationRepository;

    public Mono<Credentials> refreshToken(Credentials credentials) {
        return reactiveClientRegistrationRepository.findByRegistrationId(credentials.getProvider())
                .flatMap(clientRegistration ->
                        WebClient.builder()
                                .baseUrl(clientRegistration.getProviderDetails().getTokenUri())
                                .build()
                                .post()
                                .uri(uriBuilder -> uriBuilder
                                        .queryParam("client_id", clientRegistration.getClientId())
                                        .queryParam("client_secret", clientRegistration.getClientSecret())
                                        .queryParam("grant_type", "refresh_token")
                                        .queryParam("refresh_token", credentials.getRefreshToken())
                                        .build()
                                )
                                .retrieve()
                                .bodyToMono(AccessTokenResponse.class))
                .map(AccessTokenResponse::accessToken)
                .map(credentials::withAccessToken);
    }

    private record AccessTokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in") int expiresIn, String scope,
            @JsonProperty("token_type") String tokenType,
            @JsonProperty("id_token") String idToken
    ) {
    }

}
