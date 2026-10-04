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
- `RegionalSalesService` loads `RegionalSale` rows, aggregates duplicate company/region values, and returns a complete region-by-company matrix for chart rendering.
- `RegionalSalesChartService` converts the same regional sales matrix into an inline vector SVG chart for the XSL-FO chart report.

## Stack

- Frontend: Next.js 16, React 19, TypeScript, Tailwind CSS
- Backend: Spring Boot 4.1.1, Java 21, Spring Data JPA
- Database: H2 in-memory database, seeded with two companies, property rows, and regional sales rows
- Reporting: XML → XSLT/XSL-FO → Apache FOP → PDF, plus XML → JasperReports → PDF
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

For headless environments, including CI agents without a display server, run report tests with AWT headless mode enabled:

```bash
mvn -pl backend -am test -Djava.awt.headless=true
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
| `GET` | `/api/companies/{id}/report` | Inline XSL-FO `application/pdf` portfolio report |
| `GET` | `/api/companies/regional-sales-report` | Inline XSL-FO `application/pdf` regional sales chart report comparing all companies |
| `GET` | `/api/companies/{id}/regional-sales-report` | Inline XSL-FO `application/pdf` regional sales chart report |
| `GET` | `/api/companies/{id}/jasper-report` | Inline JasperReports `application/pdf` portfolio report |
| `GET` | `/api/companies/jasper-regional-sales-report` | Inline JasperReports `application/pdf` regional sales chart report comparing all companies |
| `GET` | `/api/companies/{id}/jasper-regional-sales-report` | Inline JasperReports `application/pdf` regional sales chart report |
| `GET` | `/h2-console` | H2 development console |

H2 console settings: JDBC URL `jdbc:h2:mem:immoreport`, user `sa`, empty password.

## Report flow

1. `CompanyController` loads the selected company, its properties, and the regional sales matrix from H2.
2. `XmlGeneratorService` creates a small XML document from those entities, including property totals, image URIs, and `<regionalSales><sale>...</sale></regionalSales>` rows.
3. `PdfReportService` applies `properties.xsl` to transform the XML into XSL-FO.
4. Apache FOP renders the FO tree to PDF and the API returns it with `Content-Disposition: inline`.

The property report endpoints are property-only. Regional sales charts are generated by separate chart report endpoints and opened from an additional table row on the XSL-FO and JasperReports pages because the chart compares all companies rather than one selected company.

The Jasper chart endpoint uses the same XML document and passes a dedicated `RegionalSalesDataSource` parameter that reads `/report/regionalSales/sale` for the native Jasper grouped bar chart.

The XSL-FO chart endpoint renders the same regional sales values as a vector chart with this pipeline:

```text
RegionalSalesPoint
  -> DefaultCategoryDataset
  -> JFreeChart
  -> Batik SVGGraphics2D
  -> inline SVG
  -> XSL-FO fo:instream-foreign-object
  -> Apache FOP
  -> PDF
```

The implementation uses Java2D APIs internally through JFreeChart and Batik, but it does not open any UI window and is tested with `-Djava.awt.headless=true`; no X11 server or graphical desktop is required.

Regional sales are stored in the `regional_sale` table and are intentionally separate from `real_estate_property.market_value`. Market value describes individual properties; regional sales describe company performance by region and are aggregated by `RegionalSalesService`.

The backend tests cover seeded company data, generated PDF signature and text, Jasper final page generation, regional sales aggregation, and XML generation with XPath assertions. The root Maven reactor runs them automatically.
