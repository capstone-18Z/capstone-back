package com.makedreamteam.capstoneback.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContestPeriod {
    private String startDate;
    private String endDate;
    private String startAuditDate;
    private String endAuditDate;
    private String releaseDate;
}
