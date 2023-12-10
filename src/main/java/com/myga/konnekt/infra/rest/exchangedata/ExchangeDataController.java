package com.myga.konnekt.infra.rest.exchangedata;

import com.myga.konnekt.infra.inbound.mail.google.GmailIntegrationConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ExchangeDataController {
    private final GmailIntegrationConfig integrationConfig;

    @PostMapping("/process")
    public Flux<String> messageFlux(@RequestBody Route route) throws MessagingException, IOException {
        return integrationConfig.emailIntegrationFlow(route.email);
    }

    public record Route(String email) {
    }


}
