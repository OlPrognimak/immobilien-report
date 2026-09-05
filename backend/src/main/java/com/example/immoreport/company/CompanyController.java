package com.example.immoreport.company;

import com.example.immoreport.property.PropertyRepository;
import com.example.immoreport.report.PdfReportService;
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

    public CompanyController(CompanyRepository companies, PropertyRepository properties, PdfReportService reports) {
        this.companies = companies;
        this.properties = properties;
        this.reports = reports;
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
        byte[] pdf = reports.create(company, properties.findAllByCompanyIdOrderByName(companyId));
        String filename = company.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-") + "-properties.pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(filename, StandardCharsets.UTF_8).build().toString())
                .body(pdf);
    }
}
