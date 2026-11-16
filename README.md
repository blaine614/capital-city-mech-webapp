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
| POST | `/api/portal/auth/login` | Employee portal sign-in |
| POST | `/api/portal/auth/forgot` | Email a set-password link |
| POST | `/api/portal/auth/set-password` | Set a password from an emailed link |
| GET, PUT | `/api/portal/me` | The signed-in employee's own profile |
| * | `/api/portal/admin/employees/**` | Office admins: add employees, job details, licenses |
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

# Email (see "Email" below)
SMTP_HOST=
SMTP_PORT=587
SMTP_USERNAME=
SMTP_PASSWORD=
MAIL_FROM=website@capitalcitymech.com
MAIL_SERVICE_REQUESTS_TO=service@capitalcitymech.com
MAIL_APPLICATIONS_TO=info@capitalcitymech.com

# Employee portal (see "Employee portal" below)
SITE_URL=https://www.capitalcitymech.com
PORTAL_ADMIN_EMAIL=<the office manager's email>
```

Frontend:

```
PUBLIC_API_BASE=https://api.capitalcitymech.com
```

## Email

Every form submission is saved to Postgres first, then emailed:

| Form | Goes to | Setting |
| --- | --- | --- |
| Contact page service request | `service@capitalcitymech.com` | `MAIL_SERVICE_REQUESTS_TO` |
| Careers page application | `info@capitalcitymech.com` | `MAIL_APPLICATIONS_TO` |

Reply-To is the visitor's address, so the office answers by hitting Reply.
Emergency requests get `EMERGENCY:` at the front of the subject.

The backend sends over SMTP, so it needs the four `SMTP_*` values from a mail
service. With `SMTP_HOST` empty nothing is sent: each email is written to the
backend log instead, which is how local development works.

- `MAIL_FROM` must be an address on a domain the mail service is set up to send
  for, or the messages land in spam.
- `SMTP_PORT` must be a STARTTLS port (587, or the service's alternate such as
  2587). Port 465 is not supported by the current settings.
- Free Render web services block ports 25, 465, and 587. On a free instance,
  use a service that offers another port, or move to a paid instance.
- A failed send is logged (`Email to ... failed`) and never shown to the
  visitor. The request is still in the database and in
  `/api/admin/contact-requests`.

## Employee portal

`/portal` is linked from the top strip of every page (and from the mobile
menu). Employees sign in with their email and a password they choose.

**Accounts are invite-only.** Nobody can sign up on their own.

1. Set `PORTAL_ADMIN_EMAIL` on the backend and restart it. That creates the
   first office admin account, with no password yet.
2. Open `/portal`, choose "First time here or forgot your password?", enter
   that address, and follow the emailed link to set a password. (Without SMTP
   configured, the link is in the backend log.)
3. Sign in. Office admins get a **Team** tab: add an employee and they are
   emailed a link, good for 7 days, to choose their own password.

Who can change what:

| | Employee | Office admin |
| --- | --- | --- |
| Name, phone, mailing address, emergency contact | Own profile | Their own profile |
| Sign-in email, job title, department, hire date, supervisor | View only | Any employee |
| Licenses and certifications (with expiration dates) | View only | Any employee |
| Add, deactivate, or re-invite employees | No | Yes |

Security notes: passwords are stored as BCrypt hashes; five wrong passwords
lock an account for 15 minutes; sessions last 7 days and are stored hashed;
setting a new password signs out every device; unticking "Can sign in" ends
that person's sessions immediately. The sign-in token is kept in the browser's
local storage, so the portal is only as private as the device it is used on:
sign out on shared computers.

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
- [ ] Set the `SMTP_*` values and send a test request from the Contact page
- [ ] Set `SITE_URL` and `PORTAL_ADMIN_EMAIL`, then set the first admin password
- [ ] Add an SMS or phone alert for emergency requests if email is too slow
- [ ] Add the real contractor license number in `Footer.astro`
- [ ] Drop real job-site photography into `frontend/public/`
- [ ] Add a captcha if bots find the forms
