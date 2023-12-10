package com.myga.konnekt.domain.exchangedata.route.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode
public final class MailSearchCriteria implements SearchCriteria {
    private String subjectSearchTerm;
    private SearchPeriod searchPeriod;
    private List<String> senders;
    private String attachmentSearchTerm;





}
