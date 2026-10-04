package com.example.immoreport.report;

import com.example.immoreport.sales.RegionalSalesPoint;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegionalSalesChartServiceTest {
    private final RegionalSalesChartService service = new RegionalSalesChartService();

    @Test
    void generatesVectorSvgWithExpectedLabelsInHeadlessMode() throws Exception {
        System.setProperty("java.awt.headless", "true");

        Element svg = service.createChartSvg(List.of(
                new RegionalSalesPoint("Rheinblick Immobilien GmbH", "Bonn", new BigDecimal("8200000.00")),
                new RegionalSalesPoint("UrbanNest Property AG", "Bonn", new BigDecimal("7450000.00")),
                new RegionalSalesPoint("Rheinblick Immobilien GmbH", "Köln", new BigDecimal("7800000.00")),
                new RegionalSalesPoint("UrbanNest Property AG", "Köln", new BigDecimal("10450000.00"))
        ));
        String serialized = serialize(svg);

        assertThat(svg.getNamespaceURI()).isEqualTo("http://www.w3.org/2000/svg");
        assertThat(serialized).isNotBlank();
        assertThat(serialized).contains("Rheinblick Immobilien GmbH");
        assertThat(serialized).contains("UrbanNest Property AG");
        assertThat(serialized).contains("Bonn");
        assertThat(serialized).contains("Köln");
        assertThat(serialized).doesNotContain("<image");
        assertThat(serialized).doesNotContain("data:image");
    }

    private String serialize(Element element) throws Exception {
        StringWriter writer = new StringWriter();
        var transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(element), new StreamResult(writer));
        return writer.toString();
    }
}
