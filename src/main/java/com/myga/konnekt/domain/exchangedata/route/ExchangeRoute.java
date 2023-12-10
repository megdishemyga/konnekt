package com.myga.konnekt.domain.exchangedata.route;

import com.myga.konnekt.domain.exchangedata.route.model.SearchCriteria;
import com.myga.konnekt.domain.exchangedata.route.model.SourceType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document(collection = "exchange_routes")
@EqualsAndHashCode
public class ExchangeRoute {
    String sourceKey;
    String gedKey;
    SourceType sourceType;
    SearchCriteria searchCriteria;
}
