package com.amitthk.stockdatalabelprinter.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class StockData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private String companyName;
    private BigDecimal peRatio;
    private BigDecimal pbRatio;
    private BigDecimal marketCap;
    private BigDecimal dividendYield;
    private BigDecimal roe;
    private BigDecimal eps;
    private BigDecimal operatingMargin;
    private BigDecimal freeCashFlow;
    private BigDecimal debtToEquity;
    private BigDecimal revenueGrowth;
    private BigDecimal earningsGrowth;
    private BigDecimal beta;

    private LocalDateTime lastUpdated = LocalDateTime.now();
}
