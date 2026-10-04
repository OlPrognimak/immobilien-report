package com.example.immoreport.sales;

import com.example.immoreport.company.Company;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegionalSalesServiceTest {
    private final RegionalSaleRepository repository = mock(RegionalSaleRepository.class);
    private final RegionalSalesService service = new RegionalSalesService(repository);

    @Test
    void aggregatesDuplicatesAndReturnsDeterministicCompleteMatrix() throws Exception {
        Company alpha = company("Alpha Immobilien", "Bonn");
        Company beta = company("Beta Property", "Koeln");
        when(repository.findAllWithCompanyOrderByRegionAndCompanyName()).thenReturn(List.of(
                sale(beta, "Koeln", "200.00"),
                sale(alpha, "Bonn", "100.00"),
                sale(alpha, "Bonn", "40.00"),
                sale(beta, "Bonn", "70.00")
        ));

        List<RegionalSalesPoint> points = service.findRegionalSalesMatrix();

        assertThat(points).containsExactly(
                new RegionalSalesPoint("Alpha Immobilien", "Bonn", new BigDecimal("140.00")),
                new RegionalSalesPoint("Beta Property", "Bonn", new BigDecimal("70.00")),
                new RegionalSalesPoint("Alpha Immobilien", "Koeln", BigDecimal.ZERO),
                new RegionalSalesPoint("Beta Property", "Koeln", new BigDecimal("200.00"))
        );
    }

    @Test
    void hasNoDuplicateMatrixEntries() throws Exception {
        Company alpha = company("Alpha Immobilien", "Bonn");
        Company beta = company("Beta Property", "Koeln");
        when(repository.findAllWithCompanyOrderByRegionAndCompanyName()).thenReturn(List.of(
                sale(alpha, "Bonn", "100.00"),
                sale(alpha, "Bonn", "50.00"),
                sale(beta, "Bonn", "70.00"),
                sale(beta, "Koeln", "200.00")
        ));

        List<RegionalSalesPoint> points = service.findRegionalSalesMatrix();

        Set<String> keys = new HashSet<>();
        for (RegionalSalesPoint point : points) {
            keys.add(point.companyName() + "|" + point.region());
        }
        assertThat(keys).hasSameSizeAs(points);
    }

    private RegionalSale sale(Company company, String region, String amount) throws Exception {
        RegionalSale sale = instantiate(RegionalSale.class);
        ReflectionTestUtils.setField(sale, "company", company);
        ReflectionTestUtils.setField(sale, "region", region);
        ReflectionTestUtils.setField(sale, "amount", new BigDecimal(amount));
        return sale;
    }

    private Company company(String name, String city) throws Exception {
        Company company = instantiate(Company.class);
        ReflectionTestUtils.setField(company, "name", name);
        ReflectionTestUtils.setField(company, "city", city);
        return company;
    }

    private <T> T instantiate(Class<T> type) throws Exception {
        Constructor<T> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}
