package com.example.immoreport.sales;

import java.math.BigDecimal;

public record RegionalSalesPoint(
        String companyName,
        String region,
        BigDecimal amount
) {}
