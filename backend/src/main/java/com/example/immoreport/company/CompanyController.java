package com.example.immoreport.company;

import com.example.immoreport.property.PropertyRepository;
import com.example.immoreport.report.JasperReportService;
import com.example.immoreport.report.PdfReportService;
import com.example.immoreport.sales.RegionalSalesPoint;
import com.example.immoreport.sales.RegionalSalesService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyRepository companies;
    private final PropertyRepository properties;
    private final PdfReportService reports;
    private final JasperReportService jasperReports;
    private final RegionalSalesService regionalSales;

    public CompanyController(CompanyRepository companies, PropertyRepository properties, PdfReportService reports, JasperReportService jasperReports, RegionalSalesService regionalSales) {
        this.companies = companies;
        this.properties = properties;
        this.reports = reports;
        this.jasperReports = jasperReports;
        this.regionalSales = regionalSales;
    }

    @GetMapping
    public List<CompanyResponse> findAll() {
        return companies.findAll().stream()
                .map(company -> new CompanyResponse(company.getId(), company.getName(), company.getCity(), properties.countByCompanyId(company.getId())))
                .toList();
    }

    @GetMapping(value = "/{companyId}/report", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> report(@PathVariable Long companyId) {
        Company company = companies.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Company not found"));
        List<RegionalSalesPoint> sales = regionalSales.findRegionalSalesMatrix();
        byte[] pdf = reports.create(company, properties.findAllByCompanyIdOrderByName(companyId), sales);
        String filename = company.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-") + "-properties.pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(filename, StandardCharsets.UTF_8).build().toString())
                .body(pdf);
    }

    @GetMapping(value = "/{companyId}/regional-sales-report", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> regionalSalesReport(@PathVariable Long companyId) {
        Company company = companies.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Company not found"));
        byte[] pdf = reports.createRegionalSalesChart(company, regionalSales.findRegionalSalesMatrix());
        String filename = company.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-") + "-regional-sales.pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(filename, StandardCharsets.UTF_8).build().toString())
                .body(pdf);
    }

    @GetMapping(value = "/regional-sales-report", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> sharedRegionalSalesReport() {
        Company company = firstCompanyForSharedChartReport();
        byte[] pdf = reports.createRegionalSalesChart(company, regionalSales.findRegionalSalesMatrix());
        return chartResponse(pdf, "regional-sales.pdf");
    }

    @GetMapping(value = "/{companyId}/jasper-report", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> jasperReport(@PathVariable Long companyId) {
        Company company = companies.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Company not found"));
        List<RegionalSalesPoint> sales = regionalSales.findRegionalSalesMatrix();
        byte[] pdf = jasperReports.create(company, properties.findAllByCompanyIdOrderByName(companyId), sales);
        String filename = company.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-") + "-properties-jasper.pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(filename, StandardCharsets.UTF_8).build().toString())
                .body(pdf);
    }

    @GetMapping(value = "/{companyId}/jasper-regional-sales-report", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> jasperRegionalSalesReport(@PathVariable Long companyId) {
        Company company = companies.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Company not found"));
        byte[] pdf = jasperReports.createRegionalSalesChart(company, regionalSales.findRegionalSalesMatrix());
        String filename = company.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-") + "-regional-sales-jasper.pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(filename, StandardCharsets.UTF_8).build().toString())
                .body(pdf);
    }

    @GetMapping(value = "/jasper-regional-sales-report", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> sharedJasperRegionalSalesReport() {
        Company company = firstCompanyForSharedChartReport();
        byte[] pdf = jasperReports.createRegionalSalesChart(company, regionalSales.findRegionalSalesMatrix());
        return chartResponse(pdf, "regional-sales-jasper.pdf");
    }

    private Company firstCompanyForSharedChartReport() {
        return companies.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Company not found"));
    }

    private ResponseEntity<byte[]> chartResponse(byte[] pdf, String filename) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(filename, StandardCharsets.UTF_8).build().toString())
                .body(pdf);
    }
}
