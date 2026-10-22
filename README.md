# reservation-system

Учебный проект: REST API для бронирования. Не рассчитан на реальное использование.
Пока что проект на стадии разработки.

## Стек

- Java, Spring Boot, Spring Data JPA
- PostgreSQL (в Docker)
- Maven

## Запуск

Нужны установленные Java, Maven (или `./mvnw`) и Docker.

1. Запустить базу данных (в Docker только PostgreSQL, приложение запускается отдельно):
   `docker compose up -d`
2. Запустить приложение:
   `./mvnw spring-boot:run`
3. Параметры подключения к БД лежат в `src/main/resources/application.properties` (порт 5433, пользователь `postgres`) и совпадают с `docker-compose.yml`.

