package com.example.immoreport.report;

import com.example.immoreport.company.Company;
import com.example.immoreport.property.Property;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class XmlGeneratorService {
    private final Map<String, URI> classpathImageUris = new ConcurrentHashMap<>();

    public Document create(Company company, List<Property> properties) throws ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        Document document = factory.newDocumentBuilder().newDocument();
        Element report = document.createElement("report");
        report.setAttribute("generatedAt", LocalDate.now().toString());
        document.appendChild(report);
        append(document, report, "companyName", company.getName());
        append(document, report, "companyCity", company.getCity());
        Element items = document.createElement("properties");
        report.appendChild(items);
        BigDecimal totalArea = BigDecimal.ZERO;
        BigDecimal totalValue = BigDecimal.ZERO;
        for (Property property : properties) {
            Element item = document.createElement("property");
            items.appendChild(item);
            append(document, item, "name", property.getName());
            append(document, item, "address", property.getAddress() + ", " + property.getCity());
            append(document, item, "type", property.getType());
            append(document, item, "area", property.getArea().toPlainString());
            append(document, item, "value", property.getValue().toPlainString());
            append(document, item, "status", property.getStatus());
            append(document, item, "imageUri", resolveImageUri(property.getImagePath()));
            totalArea = totalArea.add(property.getArea());
            totalValue = totalValue.add(property.getValue());
        }
        append(document, report, "propertyCount", Integer.toString(properties.size()));
        append(document, report, "totalArea", totalArea.toPlainString());
        append(document, report, "totalValue", totalValue.toPlainString());
        return document;
    }

    private String resolveImageUri(String imagePath) throws IOException {
        if (imagePath == null || imagePath.isBlank()) {
            return "";
        }
        URI uri = URI.create(imagePath);
        if (uri.isAbsolute()) {
            return uri.toString();
        }
        if (imagePath.startsWith("/") || imagePath.startsWith(".")) {
            return Paths.get(imagePath).toAbsolutePath().normalize().toUri().toString();
        }
        return classpathImageUris.computeIfAbsent(imagePath, this::copyClasspathImage).toString();
    }

    private URI copyClasspathImage(String imagePath) {
        ClassPathResource resource = new ClassPathResource(imagePath);
        if (!resource.exists()) {
            throw new ReportGenerationException("Report image not found: " + imagePath, null);
        }
        String suffix = imagePath.lastIndexOf('.') >= 0 ? imagePath.substring(imagePath.lastIndexOf('.')) : ".img";
        try (InputStream input = resource.getInputStream()) {
            Path image = Files.createTempFile("immoreport-", suffix);
            image.toFile().deleteOnExit();
            Files.copy(input, image, StandardCopyOption.REPLACE_EXISTING);
            return image.toUri();
        } catch (IOException exception) {
            throw new ReportGenerationException("Could not prepare report image: " + imagePath, exception);
        }
    }

    private void append(Document document, Element parent, String name, String value) {
        Element element = document.createElement(name);
        element.setTextContent(value);
        parent.appendChild(element);
    }
}
