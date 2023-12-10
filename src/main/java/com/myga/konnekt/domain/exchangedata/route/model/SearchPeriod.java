package com.myga.konnekt.domain.exchangedata.route.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
public enum SearchPeriod {
    DAY(Duration.ofDays(1)), WEEK(Duration.ofDays(7)), MONTH(Duration.ofDays(30));
    private final Duration duration;
}
