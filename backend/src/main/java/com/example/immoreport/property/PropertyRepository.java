package com.example.immoreport.property;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    @EntityGraph(attributePaths = "company")
    List<Property> findAllByCompanyIdOrderByName(Long companyId);

    long countByCompanyId(Long companyId);
}
