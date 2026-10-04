package com.example.immoreport.sales;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

@Service
public class RegionalSalesService {
    private final RegionalSaleRepository sales;

    public RegionalSalesService(RegionalSaleRepository sales) {
        this.sales = sales;
    }

    @Transactional(readOnly = true)
    public List<RegionalSalesPoint> findRegionalSalesMatrix() {
        List<RegionalSale> regionalSales = sales.findAllWithCompanyOrderByRegionAndCompanyName();
        TreeSet<String> companies = new TreeSet<>();
        TreeSet<String> regions = new TreeSet<>();
        Map<String, Map<String, BigDecimal>> amountsByRegionAndCompany = new TreeMap<>();

        for (RegionalSale sale : regionalSales) {
            String companyName = sale.getCompany().getName();
            String region = sale.getRegion();
            companies.add(companyName);
            regions.add(region);
            amountsByRegionAndCompany
                    .computeIfAbsent(region, ignored -> new TreeMap<>())
                    .merge(companyName, sale.getAmount(), BigDecimal::add);
        }

        List<RegionalSalesPoint> points = new ArrayList<>();
        for (String region : regions) {
            Map<String, BigDecimal> companyAmounts = amountsByRegionAndCompany.getOrDefault(region, Map.of());
            for (String companyName : companies) {
                points.add(new RegionalSalesPoint(
                        companyName,
                        region,
                        companyAmounts.getOrDefault(companyName, BigDecimal.ZERO)
                ));
            }
        }
        return List.copyOf(points);
    }
}
