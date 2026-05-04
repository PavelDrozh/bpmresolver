# bpmresolver
> Spring Boot сервис для работы с BPM: получение инстансов из QBPM Cockpit, управление/очистка завершённых процессов, UI-страницы для операторских сценариев и встроенная аутентификация на JWT.

## Обзор

Сервис предоставляет:

- UI (Thymeleaf) страницы для работы с QBPM Cockpit/Player и просмотром удалённых процессов.
- REST API для получения инстансов и управления «завершёнными процессами».
- Security на основе JWT (Resource Server) + login/logout endpoints.
- Monitoring через Spring Boot Actuator (health/info) и кастомные health-индикаторы.

## Возможности

- Получение списка инстансов из QBPM Cockpit через Feign-клиент.
- CRUD/поиск/пакетное удаление записей `BpmFinishedProcess` (в привязке к пользователю).
- Встроенная аутентификация:
  - Form login endpoint, который кладёт JWT в cookie `ACCESS_TOKEN`.
  - JSON login endpoint, который возвращает JWT в ответе.
- Health-checks:
  - Проверка наличия технического пользователя `admin`.
  - Проверка наличия ролей `ROLE_ADMIN` и `ROLE_BPM`.

## Технологический стек

- Java 17
- Spring Boot 3.5.x
- Spring Web + Thymeleaf (+ htmx)
- Spring Security + OAuth2 Resource Server (JWT)
- Spring Data JPA + PostgreSQL
- Spring Data REST (base path: `/datarest`)
- Liquibase (миграции)
- Spring Cloud OpenFeign + Resilience4j Circuit Breaker
- Spring Boot Actuator + Micrometer Prometheus registry

## Архитектура

Высокоуровневая схема:

```mermaid
graph TB
    Browser[UI (Thymeleaf)] --> Web[Spring MVC Controllers]
    Client[REST Client] --> Api[REST Controllers]

    Web --> Services
    Api --> Services

    Services --> Repo[JPA Repositories]
    Repo --> DB[(PostgreSQL)]

    Services --> Feign[Feign Clients]
    Feign --> Ext1[QBPM Cockpit]
    Feign --> Ext2[QBPM Player]

    Api --> Sec[Spring Security (JWT)]
```

## API Endpoints

Ниже — endpoints, определённые в коде.

### Аутентификация

- **POST** `/auth/login`
  - **Content-Type**: `application/x-www-form-urlencoded`
  - **Параметры**: `username`, `password`, `redirect` (опционально)
  - **Результат**: устанавливает cookie `ACCESS_TOKEN` и делает redirect.

- **POST** `/auth/login-json`
  - **Content-Type**: `application/json`
  - **Body**: `{"username":"...","password":"..."}`
  - **Ответ**: `{"token":"...","expiresInSeconds":600}`

- **POST** `/auth/logout`
  - Требует аутентификации.
  - Ревокует текущий JWT и очищает cookie.

### REST API: QBPM Cockpit

- **GET** `/api/qbpmcockpit/instances`
  - Query params (все опциональны): `processName`, `state`, `serviceName`, `userLogin`, `lastStartDate`, `lastEndDate`, `businessKey`, `isRoot`, `withOpenIncidents`, `tenantId`, `sort`, `page` (default `0`), `size` (default `20`).
  - **Ответ**: `RestResponsePage<BpmInstanceDto>`.

Пример:

```bash
curl "http://localhost:7085/api/qbpmcockpit/instances?page=0&size=20" \
  -H "Authorization: Bearer <token>"
```

### REST API: Finished processes

Базовый путь: `/api/finished-processes`.

- **GET** `/api/finished-processes`
  - Query params: `page` (default `0`), `size` (default `20`).
  - Требует JWT. Данные фильтруются по `jwt.subject()`.

Пример:

```bash
curl "http://localhost:7085/api/finished-processes?page=0&size=20" \
  -H "Authorization: Bearer <token>"
```

- **GET** `/api/finished-processes/search`
  - Query params (опциональны): `processInstanceId`, `status`, `message`, `fromFinishedAt`, `toFinishedAt`, `page`, `size`.

- **GET** `/api/finished-processes/{id}`

- **GET** `/api/finished-processes/by-process-instance/{processInstanceId}`
  - Возвращает последний `BpmFinishedProcess` по `processInstanceId`.

- **POST** `/api/finished-processes/delete-batch`
  - **Content-Type**: `application/json`
  - **Body**: `{"ids":[1,2,3]}`
  - **Ответ**: `{"deletedCount":3}`

Пример:

```bash
curl "http://localhost:7085/api/finished-processes/delete-batch" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"ids":[1,2,3]}'
```

### UI страницы

Сервис содержит MVC контроллеры (Thymeleaf):

- **GET** `/` — главная.
- **GET** `/login` — страница входа.
- **GET/POST** `/register` — регистрация.
- **GET/POST** `/qbpmcockpit/*` — сценарии работы с QBPM Cockpit.
- **GET** `/finished-processes/*` — просмотр удалённых процессов.
- **GET/POST** `/token/*` — управление/обновление токена.

### Spring Data REST

- Базовый путь: `/datarest` (см. `spring.data.rest.basePath`).
- Доступ ограничен ролью `ADMIN` (см. SecurityConfig).

## Конфигурация

Основные параметры (см. `src/main/resources/application.yaml` и `docker-compose.yaml`).

| Переменная/ключ | Обязательна | По умолчанию | Описание |
|---|---:|---|---|
| `SERVICE_PORT` / `server.port` | нет | `7085` (локально) / `7081` (docker-compose) | Порт HTTP сервера |
| `SPRING_DATASOURCE_URL` | да | `jdbc:postgresql://localhost:5432/postgres?currentSchema=bpmresolver` (локально), `jdbc:postgresql://postgres:5432/postgres?currentSchema=public` (docker-compose) | JDBC URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | да | `postgres` | Пользователь БД |
| `SPRING_DATASOURCE_PASSWORD` | да | `postgres` | Пароль БД |
| `SPRING_LIQUIBASE_CONTEXTS` | нет | `dev` | Liquibase contexts |
| `APP_QBPMCOCKPIT` / `app.qbpmcockpit.service` | нет | `qbpmcockpit` | ID сервиса для Feign-конфига (`spring.cloud.openfeign.client.config.<service>.url`) |
| `APP_QBPMCOCKPIT_CONTEXT` / `app.qbpmcockpit.context` | нет | `qbpmcockpit` | Контекст (path) QBPM Cockpit |
| `APP_QBPMCOCKPIT_TOKEN` | зависит от окружения | пусто | Токен для доступа к QBPM Cockpit (если требуется) |
| `APP_QBPMPLAYER` / `app.qbpmplayer.service` | нет | `qesevaluationsbpm` | ID сервиса для Feign-конфига (`spring.cloud.openfeign.client.config.<service>.url`) |
| `APP_QBPMPLAYER_CONTEXT` / `app.qbpmplayer.context` | нет | `qesevaluationsbpm` | Контекст (path) QBPM Player |
| `app.routing.qbpmcockpit-allowed-service-ids` | нет | `[]` | Список разрешённых service id для маршрутизации QBPM Cockpit |
| `app.routing.qbpmplayer-allowed-service-ids` | нет | `[]` | Список разрешённых service id для маршрутизации QBPM Player |
| `APP_AUTH_ACCESS_URL` | нет | `https://login.diasoft.ru/auth/realms/hcm/protocol/openid-connect/token` | URL получения access token (интеграция) |
| `jwt.access-ttl-seconds` | нет | `600` | TTL access token (сек) |
| `JWT_PUBLIC_KEY_PEM` / `jwt.public-key-pem` | нет | пусто | Публичный ключ PEM (если задаётся через env) |
| `JWT_PRIVATE_KEY_PEM` / `jwt.private-key-pem` | нет | пусто | Приватный ключ PEM (если задаётся через env) |
| `JWT_PUBLIC_KEY_LOCATION` / `jwt.public-key-location` | нет | `classpath:keys/app.pub` | Публичный ключ (classpath/file) |
| `JWT_PRIVATE_KEY_LOCATION` / `jwt.private-key-location` | нет | `classpath:keys/app.key` | Приватный ключ (classpath/file) |
| `jwt.public-key-location` | да | `classpath:keys/app.pub` | Публичный ключ для валидации JWT |
| `jwt.private-key-location` | да | `classpath:keys/app.key` | Приватный ключ для подписи JWT |

Примечание по QBPM URL:

- URL-ы для Feign задаются в `application.yaml` в секции `spring.cloud.openfeign.client.config.*.url`.
- Ключи `app.qbpmcockpit.service` / `app.qbpmplayer.service` определяют, какой именно блок Feign-конфига использовать.

## Безопасность и доступ

Правила доступа определены в `src/main/java/com/example/spring/bpmresolver/config/SecurityConfig.java`:

- `/api/**` — требуется аутентификация.
- `/actuator/health` + probes (`/liveness`, `/readiness`) + `/actuator/info` — доступны без аутентификации.
- `/actuator/**` (прочее) и `/datarest/**` — только `ROLE_ADMIN`.

JWT может приходить:

- в заголовке `Authorization: Bearer <token>`
- или в cookie `ACCESS_TOKEN` (см. `CookieBearerTokenResolver`).

## Быстрый старт

### Локально (Maven)

1. Подними PostgreSQL и укажи `SPRING_DATASOURCE_*` (или используй defaults).
2. Собери и запусти:

```bash
mvn clean package
mvn spring-boot:run
```

По умолчанию сервис стартует на порту `7085`.

### Docker Compose

`docker-compose.yaml` поднимает:

- `postgres` (порт `5432` наружу)
- `bpmresolver` (порт `7081` наружу)

Важно: `Dockerfile` ожидает, что jar уже собран в `target/bpmresolver.jar`.

Пример последовательности:

```bash
mvn clean package
docker compose up --build
```

## Мониторинг

Actuator endpoints (см. `application.yaml`):

- `/actuator/health`
- `/actuator/health/liveness`
- `/actuator/health/readiness`
- `/actuator/info`

Prometheus:

- В зависимостях подключён `micrometer-registry-prometheus`, но по умолчанию наружу опубликованы только `health` и `info`.
- Чтобы включить Prometheus endpoint, нужно добавить `prometheus` в `management.endpoints.web.exposure.include` и использовать `/actuator/prometheus`.

Кастомные health-индикаторы:

- `UsersHealthIndicator` — проверяет наличие пользователя `admin`.
- `RolesHealthIndicator` — проверяет наличие ролей `ROLE_ADMIN` и `ROLE_BPM`.

## Схема базы данных и миграции

Liquibase включён (`spring.liquibase.enabled: true`). Основной changelog:

- `src/main/resources/db/changelog/db.changelog-master.yaml`

Он применяет:

- `src/main/resources/schema.sql` (структура)
- `src/main/resources/data.sql` (данные) — только в контексте `dev`.

## Решение проблем

- Если `/actuator/health` возвращает `DOWN` с сообщением про администратора/роли — проверь, что в БД созданы пользователь `admin` и роли `ROLE_ADMIN`/`ROLE_BPM`.
- Если REST endpoints `/api/**` отвечают `401` — выполни `/auth/login-json` или `/auth/login`, и передавай токен в `Authorization` или cookie `ACCESS_TOKEN`.