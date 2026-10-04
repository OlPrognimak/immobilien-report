package com.example.immoreport.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegionalSaleRepository extends JpaRepository<RegionalSale, Long> {
    @Query("""
            select sale
            from RegionalSale sale
            join fetch sale.company company
            order by sale.region asc, company.name asc
            """)
    List<RegionalSale> findAllWithCompanyOrderByRegionAndCompanyName();
}
