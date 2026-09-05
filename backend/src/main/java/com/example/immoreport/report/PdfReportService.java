package com.example.immoreport.report;

import com.example.immoreport.company.Company;
import com.example.immoreport.property.Property;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

@Service
public class PdfReportService {
    private final FopFactory fopFactory;
    private final XmlGeneratorService xmlGeneratorService;

    public PdfReportService(XmlGeneratorService xmlGeneratorService) throws SAXException {
        this.xmlGeneratorService = xmlGeneratorService;
        this.fopFactory = FopFactory.newInstance(Paths.get(".").toUri());
    }

    public byte[] create(Company company, List<Property> properties) {
        try (ByteArrayOutputStream pdf = new ByteArrayOutputStream();
             InputStream stylesheet = new ClassPathResource("reports/properties.xsl").getInputStream()) {
            Document xml = xmlGeneratorService.create(company, properties);
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, pdf);
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            Source source = new DOMSource(xml);
            Result result = new SAXResult(fop.getDefaultHandler());
            transformer.transform(source, result);
            return pdf.toByteArray();
        } catch (IOException | FOPException | TransformerException | ParserConfigurationException exception) {
            throw new ReportGenerationException("Could not generate property report", exception);
        }
    }
}
