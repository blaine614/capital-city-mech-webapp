# Capital City Mechanical — website

Astro + React frontend, Spring Boot + PostgreSQL backend.

```
capcity/
├── frontend/          Astro 5, React islands, plain JavaScript
├── backend/           Spring Boot 3.3, Java 21, JPA, Flyway
└── docker-compose.yml PostgreSQL 16
```

## Run it

**1. Database**

```bash
docker compose up -d db
```

**2. Backend** (port 8080)

```bash
cd backend
./mvnw spring-boot:run       # or: mvn spring-boot:run
```

Flyway creates the schema and seeds services and job postings on first boot.

**3. Frontend** (port 4321)

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

In dev, Vite proxies `/api` to `localhost:8080`, so the browser sees one origin
and CORS never comes up. In production set `PUBLIC_API_BASE` to the API origin
and add that origin to `CORS_ORIGINS` on the backend.

## API

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/services` | Service list rendered on Home and Services |
| GET | `/api/careers/postings` | Open roles on Careers |
| POST | `/api/careers/applications` | Job application |
| POST | `/api/contact` | Service request from Contact |
| GET | `/api/admin/contact-requests` | Office inbox (needs `X-Admin-Token`) |
| GET | `/api/admin/applications` | Applications (needs `X-Admin-Token`) |
| GET | `/actuator/health` | Health check |

Validation failures return `400` with `{ message, fieldErrors }`. The React
forms read `fieldErrors` and attach each message to its input.

Both write endpoints rate-limit by email: 5 contact requests per hour, 3
applications per day.

## Environment

Backend (`application.yml` reads these):

```
DB_URL=jdbc:postgresql://localhost:5432/capitalcity
DB_USER=capitalcity
DB_PASSWORD=capitalcity
CORS_ORIGINS=https://www.capitalcitymech.com
ADMIN_TOKEN=<generate a real one>
```

Frontend:

```
PUBLIC_API_BASE=https://api.capitalcitymech.com
```

## Content

Services and job postings live in Postgres and render from the API, so the
office can edit them without a redeploy. `frontend/src/data/site.js` holds the
same content as a fallback — if the API is down at build time, the site still
builds and ships. Phone, address, and hours live only in `site.js`.

## Design

- **Palette** — charcoal `#101418`, steel blue `#4a7b9d`, safety orange `#ff6a13`
- **Type** — Barlow Condensed (display, uppercase) + Inter (body)
- **Signature** — the diagonal service marquee: a skewed full-bleed band of
  trade names that runs across the page like pipe tape. It appears once per
  page at most, and stops moving under `prefers-reduced-motion`.
- **Structural device** — the `.spec-tag` blueprint callout, used only where it
  names a real category

All tokens are CSS custom properties in `frontend/src/styles/global.css`.

## Before launch

- [ ] Replace `ADMIN_TOKEN` and the Postgres password
- [ ] Swap the admin token guard for Spring Security if the office needs logins
- [ ] Wire an email or SMS notification into `ContactService.submit` so
      emergency requests page dispatch instead of sitting in a table
- [ ] Add the real contractor license number in `Footer.astro`
- [ ] Drop real job-site photography into `frontend/public/`
- [ ] Add a captcha if bots find the forms
