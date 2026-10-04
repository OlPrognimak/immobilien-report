package com.example.immoreport.company;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CompanyControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void returnsTwoCompanies() throws Exception {
        mvc.perform(get("/api/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Rheinblick Immobilien GmbH"))
                .andExpect(jsonPath("$[0].propertyCount").value(greaterThan(0)));
    }

    @Test
    void generatesPdfReport() throws Exception {
        mvc.perform(get("/api/companies/1/report"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("inline")))
                .andExpect(result -> {
                    byte[] bytes = result.getResponse().getContentAsByteArray();
                    org.junit.jupiter.api.Assertions.assertTrue(bytes.length > 1_000);
                    org.junit.jupiter.api.Assertions.assertEquals("%PDF", new String(bytes, 0, 4));
                    try (PDDocument document = Loader.loadPDF(bytes)) {
                        assertThat(new PDFTextStripper().getText(document)).doesNotContain("Regional sales comparison");
                    }
                });
    }

    @Test
    void generatesRegionalSalesPdfReport() throws Exception {
        mvc.perform(get("/api/companies/1/regional-sales-report"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("regional-sales")))
                .andExpect(result -> {
                    byte[] bytes = result.getResponse().getContentAsByteArray();
                    org.junit.jupiter.api.Assertions.assertTrue(bytes.length > 1_000);
                    org.junit.jupiter.api.Assertions.assertEquals("%PDF", new String(bytes, 0, 4));
                    assertChartOnlyPdf(bytes);
                });
    }

    @Test
    void generatesSharedRegionalSalesPdfReport() throws Exception {
        mvc.perform(get("/api/companies/regional-sales-report"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("regional-sales")))
                .andExpect(result -> {
                    byte[] bytes = result.getResponse().getContentAsByteArray();
                    org.junit.jupiter.api.Assertions.assertTrue(bytes.length > 1_000);
                    org.junit.jupiter.api.Assertions.assertEquals("%PDF", new String(bytes, 0, 4));
                    assertChartOnlyPdf(bytes);
                });
    }

    @Test
    void generatesJasperPdfReport() throws Exception {
        mvc.perform(get("/api/companies/1/jasper-report"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("jasper")))
                .andExpect(result -> {
                    byte[] bytes = result.getResponse().getContentAsByteArray();
                    org.junit.jupiter.api.Assertions.assertTrue(bytes.length > 1_000);
                    org.junit.jupiter.api.Assertions.assertEquals("%PDF", new String(bytes, 0, 4));
                    try (PDDocument document = Loader.loadPDF(bytes)) {
                        assertThat(new PDFTextStripper().getText(document)).doesNotContain("Regional sales comparison");
                    }
                });
    }

    @Test
    void generatesJasperRegionalSalesPdfReport() throws Exception {
        mvc.perform(get("/api/companies/1/jasper-regional-sales-report"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("regional-sales-jasper")))
                .andExpect(result -> {
                    byte[] bytes = result.getResponse().getContentAsByteArray();
                    org.junit.jupiter.api.Assertions.assertTrue(bytes.length > 1_000);
                    org.junit.jupiter.api.Assertions.assertEquals("%PDF", new String(bytes, 0, 4));
                    assertChartOnlyPdf(bytes);
                });
    }

    @Test
    void generatesSharedJasperRegionalSalesPdfReport() throws Exception {
        mvc.perform(get("/api/companies/jasper-regional-sales-report"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("regional-sales-jasper")))
                .andExpect(result -> {
                    byte[] bytes = result.getResponse().getContentAsByteArray();
                    org.junit.jupiter.api.Assertions.assertTrue(bytes.length > 1_000);
                    org.junit.jupiter.api.Assertions.assertEquals("%PDF", new String(bytes, 0, 4));
                    assertChartOnlyPdf(bytes);
                });
    }

    private void assertChartOnlyPdf(byte[] bytes) throws Exception {
        try (PDDocument document = Loader.loadPDF(bytes)) {
            assertThat(document.getNumberOfPages()).isEqualTo(1);
            String text = new PDFTextStripper().getText(document);
            assertThat(text).contains("Regional sales comparison");
            assertThat(text).contains("Sales by region");
            assertThat(text).doesNotContain("Property overview");
        }
    }
}
