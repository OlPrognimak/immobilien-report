package com.example.immoreport.property;

import com.example.immoreport.company.Company;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "real_estate_property")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String city;
    private String type;
    private BigDecimal area;
    @Column(name = "market_value")
    private BigDecimal value;
    private String status;
    @Column(name = "image_path")
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    protected Property() {}

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getType() { return type; }
    public BigDecimal getArea() { return area; }
    public BigDecimal getValue() { return value; }
    public String getStatus() { return status; }
    public String getImagePath() { return imagePath; }
}
