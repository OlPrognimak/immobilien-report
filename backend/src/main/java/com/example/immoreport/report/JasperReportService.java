package com.example.immoreport.report;

import com.example.immoreport.company.Company;
import com.example.immoreport.property.Property;
import com.example.immoreport.sales.RegionalSalesPoint;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

@Service
public class JasperReportService {
    private static final DecimalFormatSymbols GERMAN_SYMBOLS = new DecimalFormatSymbols(Locale.GERMANY);

    private final XmlGeneratorService xmlGeneratorService;

    public JasperReportService(XmlGeneratorService xmlGeneratorService) {
        this.xmlGeneratorService = xmlGeneratorService;
    }

    public byte[] create(Company company, List<Property> properties, List<RegionalSalesPoint> regionalSales) {
        try (InputStream template = new ClassPathResource("reports/properties-jasper.jrxml").getInputStream()) {
            Document xml = xmlGeneratorService.create(company, properties, regionalSales);
            JasperReport report = JasperCompileManager.compileReport(template);
            Map<String, Object> parameters = parameters(xml);
            JRXmlDataSource dataSource = new JRXmlDataSource(xml, "/report/properties/property");
            return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(report, parameters, dataSource));
        } catch (IOException | JRException | ParserConfigurationException | XPathExpressionException exception) {
            throw new ReportGenerationException("Could not generate Jasper property report", exception);
        }
    }

    public byte[] createRegionalSalesChart(Company company, List<RegionalSalesPoint> regionalSales) {
        try (InputStream template = new ClassPathResource("reports/regional-sales-chart-jasper.jrxml").getInputStream()) {
            Document xml = xmlGeneratorService.create(company, List.of(), regionalSales);
            JasperReport report = JasperCompileManager.compileReport(template);
            Map<String, Object> parameters = new HashMap<>();
            XPath xpath = XPathFactory.newInstance().newXPath();
            parameters.put("GeneratedAt", text(xpath, xml, "/report/@generatedAt"));
            parameters.put("RegionalSalesDataSource", new JRXmlDataSource(xml, "/report/regionalSales/sale"));
            return JasperExportManager.exportReportToPdf(JasperFillManager.fillReport(report, parameters, new JREmptyDataSource(1)));
        } catch (IOException | JRException | ParserConfigurationException | XPathExpressionException exception) {
            throw new ReportGenerationException("Could not generate Jasper regional sales chart report", exception);
        }
    }

    private Map<String, Object> parameters(Document xml) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("CompanyName", text(xpath, xml, "/report/companyName"));
        parameters.put("CompanyCity", text(xpath, xml, "/report/companyCity"));
        parameters.put("GeneratedAt", text(xpath, xml, "/report/@generatedAt"));
        parameters.put("PropertyCount", text(xpath, xml, "/report/propertyCount"));
        parameters.put("TotalArea", formatDecimal(text(xpath, xml, "/report/totalArea")) + " m2");
        parameters.put("TotalValue", "€ " + formatInteger(text(xpath, xml, "/report/totalValue")));
        parameters.put("FirstImageUri", text(xpath, xml, "/report/properties/property[1]/imageUri"));
        return parameters;
    }

    private String text(XPath xpath, Document xml, String expression) throws XPathExpressionException {
        return (String) xpath.evaluate(expression, xml, XPathConstants.STRING);
    }

    private String formatDecimal(String value) {
        return new DecimalFormat("#,##0.00", GERMAN_SYMBOLS).format(new BigDecimal(value));
    }

    private String formatInteger(String value) {
        return new DecimalFormat("#,##0", GERMAN_SYMBOLS).format(new BigDecimal(value));
    }
}
