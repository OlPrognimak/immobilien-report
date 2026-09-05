# Immobilien Report MVP

A Maven multi-module MVP that lists real-estate companies in a Next.js/React frontend and generates an up-to-date PDF portfolio for each company with Spring Boot, H2, XSL-FO, Apache FOP, and external report images.

## Maven modules

```text
immobilien-report (parent)
├── backend   Spring Boot application and tests
└── frontend  Maven lifecycle integration for Next.js
```

The frontend source stays at the repository root, while `frontend/pom.xml` integrates its npm lifecycle into the Maven reactor.

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

## API

| Method | Endpoint | Result |
| --- | --- | --- |
| `GET` | `/api/companies` | Company list including property counts |
| `GET` | `/api/companies/{id}/report` | Inline `application/pdf` portfolio report |
| `GET` | `/h2-console` | H2 development console |

H2 console settings: JDBC URL `jdbc:h2:mem:immoreport`, user `sa`, empty password.

## Report flow

1. `CompanyController` loads the selected company and its properties from H2.
2. `PdfReportService` creates a small XML document from those entities.
3. `properties.xsl` transforms that XML into XSL-FO and renders property image URIs from the report data.
4. Apache FOP renders the FO tree to PDF and the API returns it with `Content-Disposition: inline`.

The backend integration test checks both seeded company data and the generated PDF signature. The root Maven reactor runs it automatically.
