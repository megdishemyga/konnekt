package com.myga.konnekt.infra.rest.attachment;

import com.myga.konnekt.usecase.MailAttachedInvoiceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class InvoiceAttachmentController {
    private final MailAttachedInvoiceUseCase mailAttachedInvoiceUseCase;

    @PostMapping("/process")
    public Mono<Void> invoiceAttachments(@RequestBody Route route) throws MessagingException, IOException {
        return Mono.fromRunnable(() -> mailAttachedInvoiceUseCase.process(route.email));
    }

    public record Route(String email) {
    }


}
