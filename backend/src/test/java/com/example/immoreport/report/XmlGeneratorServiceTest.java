package com.example.immoreport.report;

import com.example.immoreport.company.Company;
import com.example.immoreport.property.Property;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlGeneratorServiceTest {
    private final XmlGeneratorService service = new XmlGeneratorService();
    private final XPath xpath = XPathFactory.newInstance().newXPath();

    @Test
    void createsReportSummaryXml() throws Exception {
        Document xml = service.create(company("Rheinblick Immobilien GmbH", "Bonn"), List.of(
                property("Rheinblick Offices", "Friedrichstrasse 12", "Bonn", "Office", "2450.00", "6850000.00", "Leased", null),
                property("Villa am Park", "Rheinaustrasse 31", "Bonn", "Residential", "420.00", "2290000.00", "Available", null)
        ));

        assertEquals("report", xml.getDocumentElement().getNodeName());
        assertEquals("Rheinblick Immobilien GmbH", text(xml, "/report/companyName"));
        assertEquals("Bonn", text(xml, "/report/companyCity"));
        assertEquals("2", text(xml, "/report/propertyCount"));
        assertEquals("2870.00", text(xml, "/report/totalArea"));
        assertEquals("9140000.00", text(xml, "/report/totalValue"));
        assertTrue(text(xml, "/report/@generatedAt").matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    void createsPropertyRowsWithResolvedImageUris() throws Exception {
        Document xml = service.create(company("Rheinblick Immobilien GmbH", "Bonn"), List.of(
                property("Rheinblick Offices", "Friedrichstrasse 12", "Bonn", "Office", "2450.00", "6850000.00", "Leased", "reports/images/rheinblick-offices.svg")
        ));

        assertEquals("1", text(xml, "count(/report/properties/property)"));
        assertEquals("Rheinblick Offices", text(xml, "/report/properties/property[1]/name"));
        assertEquals("Friedrichstrasse 12, Bonn", text(xml, "/report/properties/property[1]/address"));
        assertEquals("Office", text(xml, "/report/properties/property[1]/type"));
        assertEquals("2450.00", text(xml, "/report/properties/property[1]/area"));
        assertEquals("6850000.00", text(xml, "/report/properties/property[1]/value"));
        assertEquals("Leased", text(xml, "/report/properties/property[1]/status"));
        assertTrue(text(xml, "/report/properties/property[1]/imageUri").startsWith("file:"));
        assertTrue(text(xml, "/report/properties/property[1]/imageUri").endsWith(".svg"));
    }

    @Test
    void keepsImageUriEmptyWhenPropertyHasNoImagePath() throws Exception {
        Document xml = service.create(company("UrbanNest Property AG", "Koeln"), List.of(
                property("Retail Arcades", "Aachener Strasse 126", "Koeln", "Retail", "1910.00", "8975000.00", "Available", " ")
        ));

        assertEquals("", text(xml, "/report/properties/property[1]/imageUri"));
    }

    private String text(Document document, String expression) throws Exception {
        Object result = xpath.evaluate(expression, document, XPathConstants.STRING);
        return (String) result;
    }

    private Company company(String name, String city) throws Exception {
        Company company = instantiate(Company.class);
        ReflectionTestUtils.setField(company, "name", name);
        ReflectionTestUtils.setField(company, "city", city);
        return company;
    }

    private Property property(String name, String address, String city, String type, String area, String value, String status, String imagePath) throws Exception {
        Property property = instantiate(Property.class);
        ReflectionTestUtils.setField(property, "name", name);
        ReflectionTestUtils.setField(property, "address", address);
        ReflectionTestUtils.setField(property, "city", city);
        ReflectionTestUtils.setField(property, "type", type);
        ReflectionTestUtils.setField(property, "area", new BigDecimal(area));
        ReflectionTestUtils.setField(property, "value", new BigDecimal(value));
        ReflectionTestUtils.setField(property, "status", status);
        ReflectionTestUtils.setField(property, "imagePath", imagePath);
        return property;
    }

    private <T> T instantiate(Class<T> type) throws Exception {
        Constructor<T> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}
