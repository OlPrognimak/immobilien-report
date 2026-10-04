package com.example.immoreport.report;

import com.example.immoreport.sales.RegionalSalesPoint;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PdfReportServiceTest {
    @Test
    void importsRegionalSalesChartSvgIntoReportXmlWithoutFileReferences() throws Exception {
        PdfReportService service = new PdfReportService(new XmlGeneratorService(), new RegionalSalesChartService());
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        document.appendChild(document.createElement("report"));

        service.appendRegionalSalesChart(document, List.of(
                new RegionalSalesPoint("Rheinblick Immobilien GmbH", "Bonn", new BigDecimal("8200000.00")),
                new RegionalSalesPoint("UrbanNest Property AG", "Bonn", new BigDecimal("7450000.00"))
        ));

        String xml = serialize(document);
        assertThat(xml).contains("<regionalSalesChart>");
        assertThat(xml).contains("http://www.w3.org/2000/svg");
        assertThat(xml).doesNotContain("file:");
        assertThat(xml).doesNotContain("<image");
    }

    private String serialize(Document document) throws Exception {
        StringWriter writer = new StringWriter();
        var transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }
}
