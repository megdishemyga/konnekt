package com.myga.konnekt.domain.exchangedata.trace;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Builder
@Document("exchange_traces")
@EqualsAndHashCode
public class ExchangeTrace {
    private String gedKey;
    private String emitter;
    private String receiver;
    private String FileName;
    private LocalDateTime integrationDate;

}
