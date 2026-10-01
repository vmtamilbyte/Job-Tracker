# Job Tracker

Full-stack job application tracker: Kanban pipeline, follow-up reminders and a resume-vs-job-description match score.

**Live demo:**(https://job-tracker-z5hs.onrender.com/)  <!-- replace after deploying -->

## Features
- JWT authentication (register / login), BCrypt password hashing, per-user data isolation
- Kanban board (Applied, Interview, Offer, Rejected) with drag and drop
- Resume-to-JD match score with a list of missing skills (rule-based, tested)
- Follow-up dates with overdue highlighting, pipeline conversion stat
- Single Docker image serves API and UI; CI runs tests and builds on every push

## Tech stack
Java 17, Spring Boot 3, Spring Security, JPA/Hibernate, PostgreSQL (H2 for local), React 18, Vite, Docker, GitHub Actions

## API
| Method | Endpoint | Description |
|---|---|---|
| POST | /api/auth/register, /api/auth/login | Returns JWT |
| GET / POST | /api/applications | List / create |
| PUT / DELETE | /api/applications/{id} | Update / delete |
| PATCH | /api/applications/{id}/status | Move between columns |
| POST | /api/match | Score resume against a job description |

## Run locally
```bash
# terminal 1
cd backend && mvn spring-boot:run
# terminal 2
cd frontend && npm install && npm run dev   # http://localhost:5173
```

## Deploy (free)
1. Push this repo to GitHub.
2. Create a free PostgreSQL database on [Neon](https://neon.tech) (or Render).
3. On [Render](https://render.com): New > Web Service > connect the repo > Runtime: Docker.
4. Add environment variables:
   - `JWT_SECRET` = any random string of 32+ characters
   - `DB_URL` = `jdbc:postgresql://HOST/DBNAME?sslmode=require`
   - `DB_USER`, `DB_PASS` = database credentials
5. Deploy, then paste the URL at the top of this README.

## Resume bullet
Developed and deployed a full-stack job-tracking app (Spring Boot, React, PostgreSQL, Docker) with JWT auth, drag-and-drop Kanban pipeline, and a resume-JD skill-match scorer; CI via GitHub Actions.
