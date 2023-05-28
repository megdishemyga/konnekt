package com.myga.konnekt.domain.invoice;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class InvoiceTrace {
    String gedKey;
    String emitter;
    String receiver;
    LocalDateTime integrationDate;
    String description;

}
