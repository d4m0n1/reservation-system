# reservation-system

Учебный REST API для бронирования комнат на Spring Boot.

## Стек

- Java 21, Spring Boot 4
- Spring Web MVC, Spring Data JPA (Hibernate), Bean Validation
- PostgreSQL, Flyway
- JUnit 5, Mockito, AssertJ, Testcontainers
- Maven, Docker Compose

## Возможности

- Создание, просмотр, изменение, одобрение и отмена броней.
- Статусы брони: `PENDING` → `APPROVED` или `CANCELLED`. Изменить, одобрить или отменить можно только бронь в статусе `PENDING`.
- Проверка пересечения дат: бронь нельзя одобрить, если на эти даты в комнате уже есть одобренная бронь.
- Поиск по комнате и пользователю с пагинацией.
- Защита от гонок при одновременных запросах: пессимистическая блокировка при одобрении и оптимистическая (`@Version`) при изменении брони.
- Валидация входных данных и единый формат ошибок.
- Схема базы данных управляется миграциями Flyway.

## API

| Метод | Путь | Описание | Ответ |
|---|---|---|---|
| `POST` | `/reservations` | Создать бронь | `201` |
| `GET` | `/reservations/{id}` | Получить бронь | `200` |
| `GET` | `/reservations` | Поиск с пагинацией | `200` |
| `PUT` | `/reservations/{id}` | Изменить бронь | `200` |
| `POST` | `/reservations/{id}/approve` | Одобрить бронь | `200` |
| `POST` | `/reservations/{id}/cancel` | Отменить бронь | `204` |
| `GET` | `/reservations/availability/check` | Проверить, свободна ли комната | `200` |

Параметры поиска (`GET /reservations`): `roomId`, `userId`, `pageNumber` (с 0, по умолчанию 0), `pageSize` (1–100, по умолчанию 10).

Параметры проверки доступности: `roomId`, `startDate`, `endDate`.

### Пример

Запрос `POST /reservations`:

```json
{
  "userId": 1,
  "roomId": 5,
  "startDate": "2026-12-01",
  "endDate": "2026-12-05"
}
```

Ответ `201 Created`:

```json
{
  "id": 1,
  "userId": 1,
  "roomId": 5,
  "startDate": "2026-12-01",
  "endDate": "2026-12-05",
  "status": "PENDING"
}
```

Ответ поиска:

```json
{
  "content": [ ... ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 42,
  "totalPages": 5
}
```

### Ошибки

Все ошибки возвращаются в одном формате:

```json
{
  "message": "Conflict",
  "detailedMessage": "Cannot approve reservation because of conflict",
  "errorTime": "2026-11-10T12:00:00"
}
```

- `400` — неверные входные данные (в `detailedMessage` перечислены поля с ошибками);
- `404` — бронь не найдена;
- `409` — действие недоступно в текущем статусе, пересечение дат или одновременное изменение брони.

## Запуск

Нужны Java 21 и Docker. Maven устанавливать не обязательно, в проекте есть Maven Wrapper (`./mvnw`).

1. Запустить PostgreSQL:
   ```bash
   docker compose up -d
   ```
2. Запустить приложение:
   ```bash
   ./mvnw spring-boot:run
   ```

Приложение доступно на `http://localhost:8080`. При старте Flyway создаёт схему базы данных. Параметры подключения указаны в `src/main/resources/application.properties` и совпадают с `docker-compose.yml`.

## Тесты

```bash
./mvnw test
```

- Юнит-тесты `ReservationService` с моками репозитория.
- Интеграционные тесты HTTP-эндпоинтов на настоящем PostgreSQL. Testcontainers сам запускает базу в Docker, поэтому для тестов нужен только запущенный Docker.
