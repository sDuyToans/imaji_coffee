# Imaji Coffee

Full-stack coffee e-commerce application.

## Project structure

- `imaji_coffee_be/` — Spring Boot backend (Java 17, Maven, MySQL)
- `imaji_coffee_ui/imaji_coffee_ui/` — React + TypeScript + Vite frontend
- `PROJECT_REVIEW_AND_ROADMAP.md` — current review and recommended next steps

## Local development

### Backend

1. Create `imaji_coffee_be/application-secret.properties` or provide the environment variables documented in the backend README.
2. Start MySQL, or run the backend Docker Compose setup from `imaji_coffee_be/`.
3. Run `./mvnw spring-boot:run` from `imaji_coffee_be/`.

### Frontend

1. Create `imaji_coffee_ui/imaji_coffee_ui/.env` with the local API URLs.
2. Run `npm ci` from `imaji_coffee_ui/imaji_coffee_ui/`.
3. Run `npm run dev`.

Never commit `.env`, secret properties, database runtime files, certificates, or private keys.

## Validation

- Backend tests: `cd imaji_coffee_be && ./mvnw test`
- Frontend build: `cd imaji_coffee_ui/imaji_coffee_ui && npm run build`
