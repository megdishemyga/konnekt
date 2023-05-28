package com.myga.konnekt.domain.route;

import lombok.Value;

@Value
public class Route   {
   String sourceKey;
   String gedKey;
   SourceType sourceType;
   SearchCriteria searchCriteria;
}
