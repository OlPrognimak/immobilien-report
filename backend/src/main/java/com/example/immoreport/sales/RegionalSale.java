package com.example.immoreport.sales;

import com.example.immoreport.company.Company;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "regional_sale")
public class RegionalSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    private String region;
    private BigDecimal amount;

    protected RegionalSale() {}

    public Long getId() { return id; }
    public Company getCompany() { return company; }
    public String getRegion() { return region; }
    public BigDecimal getAmount() { return amount; }
}
