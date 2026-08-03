# Spring Boot Upgrade Plan

Status as of 2026-08-03: **Phase 1 complete** — this app now runs Spring Boot
**3.5.16** (last OSS release of the 3.5.x line before its 2026-06-30
end-of-life). Test suite and local smoke tests pass. Not yet deployed to any
shared environment. Phase 2 (major migration to **4.1.x**) has not started.

## Pre-existing issue to fix regardless of path

`pom.xml` pins `spring-boot-testcontainers` to version `4.1.0` (test scope)
while the parent POM is `3.5.5`. That's a mismatch left over from some earlier
edit and should be corrected to match whichever line we end up on, otherwise
Maven is silently resolving a 4.x test module against a 3.5.x runtime.

## Two-phase approach

### Phase 1 — patch bump to 3.5.16 — DONE (2026-08-03)

Stay on the 3.x major line, just take the last 11 patch releases.

- [x] Bump `spring-boot-starter-parent` version `3.5.5` → `3.5.16`
- [x] Fix the `spring-boot-testcontainers` version mismatch — dropped the
      stray explicit `<version>4.1.0</version>` so it inherits `3.5.16` from
      the parent BOM instead
- [x] Run full test suite (`mvn test`) — **100 tests run, 96 pass, 4 fail**.
      The 4 failures are all in `FAQRepositoryTest` and are **pre-existing**:
      confirmed identical failures on unmodified 3.5.5 by stashing the pom
      change and re-running. Root cause is unrelated to the Boot version —
      `V1__baseline.sql` uses MySQL-only DDL (backticked identifiers,
      `ENGINE=InnoDB`, `ON UPDATE CURRENT_TIMESTAMP`) that the H2 in-memory
      DB used by `@DataJpaTest` can't parse. Worth fixing separately
      (e.g. point `@DataJpaTest` at a MySQL Testcontainer like the rest of
      the suite already does), but it's not a blocker for this bump.
- [x] Smoke test locally against the real dev MySQL container (`imajicoffeedb`,
      already baselined at Flyway v1 — schema validated clean on startup,
      `Schema imajicoffee is up to date. No migration necessary.`):
      - `GET /actuator/health` → `{"status":"UP"}`
      - `POST /api/v1/auth/signup` → user created
      - `POST /api/v1/auth/login` → JWT issued via `HttpOnly` cookie
      - `GET /api/v1/account/me` with the cookie → 200, correct user payload
        (confirms the full Spring Security + JWT filter chain works)
      - Same endpoint with no cookie → 401 as expected
      - `GET /api/v1/ws/info` (SockJS handshake) → 200, websocket capable
      - Test user cleaned up from the DB afterward
      - Stripe webhook and Google OAuth2 login were **not** exercised — both
        need real third-party credentials this environment doesn't have.
        The app boots fine with their auto-configuration wired up (dummy
        keys), which at least confirms no bean-wiring breakage, but the
        actual external round-trip is unverified.
- [ ] Deploy, verify actuator health endpoint

Expected effort: small. Patch releases don't carry the breaking changes below.

### Phase 2 — major migration to Spring Boot 4.1.x (larger effort, separate PR)

Only start this once Phase 1 is merged and stable. Spring Boot 4.0 sits on
**Spring Framework 7** / **Jakarta EE 11** / **Servlet 6.1**, and several
things this app actually uses are affected:

| Area | What changes | Relevant here because |
|---|---|---|
| **OAuth2 client starter** | `spring-boot-starter-oauth2-client` → `spring-boot-starter-security-oauth2-client` | We depend on `spring-boot-starter-oauth2-client` directly |
| **Flyway** | Needs an explicit `spring-boot-starter-flyway` starter; bare `flyway-core`/`flyway-mysql` deps are no longer auto-configured the same way | We use Flyway for schema migrations (`flyway-core`, `flyway-mysql`) |
| **Mail** | Starter must be explicit (already true for us) | We already depend on `spring-boot-starter-mail` — likely no change needed, verify config keys |
| **Jackson 2 → 3** | New group ID `tools.jackson`, some class/annotation renames, `spring.jackson.read.*`/`write.*` properties move under `spring.jackson.json.*` | Affects `jjwt-jackson` (0.11.5) — need to confirm a Jackson-3-compatible jjwt release exists before upgrading, or switch to `jjwt`'s Gson/Orgjson module as a stopgap |
| **Testing annotations** | `@MockBean`/`@SpyBean` removed → `@MockitoBean`/`@MockitoSpyBean`; `@SpringBootTest` no longer auto-provides MockMvc/WebClient/TestRestTemplate | Grep found no `@MockBean`/`@SpyBean` usage in this codebase today — good, one less thing to fix. `@SpringBootTest` web-layer tests will need explicit `@AutoConfigureMockMvc` etc. |
| **Actuator** | Liveness/readiness probes on by default | We use `spring-boot-starter-actuator`; check nothing assumes the old default |
| **Web starter** | `spring-boot-starter-web` → `spring-boot-starter-webmvc` (classic alias exists for a soft landing) | We use `spring-boot-starter-web` |
| **javax.* imports** | Confirmed **not an issue** — the three `javax.*` imports in this codebase (`javax.crypto.SecretKey` in `JwtUtil`, `javax.sql.DataSource` in `OrderSchemaMigrationRunner`) are JDK packages, not Jakarta EE, so they're unaffected by the jakarta namespace migration |
| **Undertow** | Dropped entirely (needs Servlet 6.1) | Not used here (default embedded Tomcat) — no action |

Not yet checked, needs a look before starting Phase 2:
- Caffeine cache config compatibility (`3.2.2` pinned) — likely fine, low-risk lib
- Stripe SDK (`stripe-java 29.5.0`) — independent of Spring Boot version, but re-verify webhook signature handling after the Jackson bump if Stripe's SDK shades Jackson
- Selenium/WebDriverManager/TestNG test stack — independent of Spring Boot, unaffected

### Recommended step order for Phase 2

1. Confirm Phase 1 (3.5.16) has been running cleanly for a bit
2. Add the `spring-boot-properties-migrator` dependency temporarily to surface
   renamed/removed config keys at startup
3. Bump parent to latest `4.1.x`
4. Update starter artifact IDs per the table above
5. Switch `flyway-core`/`flyway-mysql` to the new Flyway starter
6. Resolve Jackson 3 fallout (jjwt compatibility is the main unknown — spike
   this first, it could block the whole phase)
7. Fix any `@MockBean`/`@SpyBean`/`@SpringBootTest` test annotations (none
   found today, but re-check after adding tests in the meantime)
8. Remove the properties migrator once `mvn test` is clean
9. Full regression pass: auth, payments, chat/WebSocket, Flyway migrations
10. Update this doc's status line once merged

## Rollback

Each phase is its own PR/branch. If Phase 2 stalls on the Jackson/jjwt
compatibility spike, Phase 1 alone (3.5.16) is a safe, supportable place to
stay parked for a while — 3.x is EOL for new patches but not an active
security fire drill on day one.
