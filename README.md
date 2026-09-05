# Immobilien Report MVP

A Maven multi-module MVP that lists real-estate companies in a Next.js/React frontend and generates an up-to-date PDF portfolio for each company with Spring Boot, H2, XSL-FO, Apache FOP, and external report images.

## Maven modules

```text
immobilien-report (parent)
├── backend   Spring Boot application and tests
└── frontend  Maven lifecycle integration for Next.js
```

The frontend source stays at the repository root, while `frontend/pom.xml` integrates its npm lifecycle into the Maven reactor.

## Backend structure

The report backend is split into small responsibilities:

- `CompanyController` loads company and property data and returns the generated PDF.
- `XmlGeneratorService` converts company/property entities into the XML document used by the report.
- `PdfReportService` transforms that XML with `reports/properties.xsl` and renders the final PDF with Apache FOP.

## Stack

- Frontend: Next.js 16, React 19, TypeScript, Tailwind CSS
- Backend: Spring Boot 4.1.1, Java 21, Spring Data JPA
- Database: H2 in-memory database, seeded with two companies and six properties
- Reporting: XML → XSLT/XSL-FO → Apache FOP → PDF
- Graphic: external property images rendered by Apache Batik/FOP inside every PDF

## Run with Docker

```bash
docker compose up --build
```

Open <http://localhost:3005>. The API is available at <http://localhost:8085>.

To build and start the Docker stack through Maven:

```bash
mvn -Pdocker-deploy verify
```

The Maven `docker-deploy` profile is defined in the final frontend reactor module, so the Docker stack starts only after backend tests, frontend linting, and frontend production build complete successfully.

## Build the complete project with Maven

Java 21+ and Maven 3.9+ are required. Node.js does not need to be installed separately: Maven downloads the configured Node.js and npm versions for the frontend module.

```bash
mvn clean verify
```

This single command performs:

1. compilation and integration testing of the Spring Boot backend;
2. `npm ci` for the Next.js frontend;
3. frontend linting during Maven's `test` phase;
4. the production Next.js build during Maven's `package` phase.

To build only the backend:

```bash
mvn -pl backend -am clean verify
```

To build only the frontend:

```bash
mvn -pl frontend -am clean verify
```

## Run locally

Backend:

```bash
mvn -pl backend spring-boot:run
```

Frontend (Node.js 22+):

```bash
npm ci
cp .env.example .env.local
npm run dev
```

Open <http://localhost:3000> and select **Open PDF** for either company.

## Report Images

Property images are selected by data, not hardcoded in the XSL file.

The `real_estate_property.image_path` column contains the image location. Seed data in `backend/src/main/resources/data.sql` points to SVG files under:

```text
backend/src/main/resources/reports/images/
```

Supported `image_path` values:

- Classpath resource path, for bundled report assets: `reports/images/rheinblick-offices.svg`
- Absolute file URI: `file:///opt/report-images/building.svg`
- HTTP(S) URI when the runtime can access it: `https://example.com/building.png`

`XmlGeneratorService` resolves each `image_path` into an `imageUri` element in the generated XML. The XSL then renders `imageUri` with `fo:external-graphic`. Blank image paths are allowed; the XSL skips image rendering for those rows.

## API

| Method | Endpoint | Result |
| --- | --- | --- |
| `GET` | `/api/companies` | Company list including property counts |
| `GET` | `/api/companies/{id}/report` | Inline `application/pdf` portfolio report |
| `GET` | `/h2-console` | H2 development console |

H2 console settings: JDBC URL `jdbc:h2:mem:immoreport`, user `sa`, empty password.

## Report flow

1. `CompanyController` loads the selected company and its properties from H2.
2. `XmlGeneratorService` creates a small XML document from those entities, including totals and image URIs.
3. `PdfReportService` applies `properties.xsl` to transform the XML into XSL-FO.
4. Apache FOP renders the FO tree to PDF and the API returns it with `Content-Disposition: inline`.

The backend tests cover seeded company data, generated PDF signature, and XML generation with XPath assertions. The root Maven reactor runs them automatically.
