# payment-system
Payment system for OptimaBank

REST API сервис для работы с клиентами и банковскими картами разных платежных систем.
Проект выполнен как тестовое задание и демонстрирует расширяемую архитектуру с использованием паттерна Strategy.

Что реализовано

- создание и просмотр клиентов;
- выпуск карт для клиента;
- пополнение карты;
- списание средств;
- изменение статуса карты;
- выбор процессинговой логики в зависимости от платежной системы;
- централизованная обработка ошибок;
- автоматические тесты для service, strategy, validation и controller слоя.

Технологии

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Validation
- Maven
- H2 Database
- Swagger / OpenAPI
- JUnit 5
- Mockito
- MockMvc
- Lombok

Архитектура

Проект разделен на слои:

- controller — REST API;
- service — бизнес-логика;
- strategy — логика процессинговых центров;
- repository — работа с БД;
- validation — бизнес-валидация;
- mapper — преобразование Entity ↔ DTO;
- exception — единый формат обработки ошибок.

Strategy

Для каждой платежной системы реализована отдельная стратегия:

- VisaProcessingCenterStrategy
- MasterCardProcessingCenterStrategy
- ElcartProcessingCenterStrategy

Выбор стратегии выполняется через CardProcessingCenterStrategyResolver.

Поддерживаемые платежные системы

- VISA
- MASTERCARD
- ELCART

Бизнес-правила

Выпуск карты

При выпуске карты:
- создается карта для клиента;
- выбирается стратегия по paymentSystemCode;
- генерируется номер карты;
- назначается дата окончания действия;
- устанавливается статус ACTIVE;
- баланс инициализируется 0.

Префиксы карт
- VISA → 4
- MASTERCARD → 5
- ELCART → 9

Длина номера карты
Задается в application.properties:

card.number.length=16

Срок действия карты
- VISA → 4 года
- MASTERCARD → 5 лет
- ELCART → 3 года

Пополнение
Разрешено только если:
- карта существует;
- сумма больше 0;
- карта активна;
- карта не просрочена.

Списание
Разрешено только если:
- карта существует;
- сумма больше 0;
- карта активна;
- карта не просрочена;
- на карте достаточно средств.

При недостатке средств возвращается ошибка:
Insufficient funds

Изменение статуса карты
Разрешены переходы:
- ACTIVE -> BLOCKED
- ACTIVE -> CLOSED
- BLOCKED -> ACTIVE
- BLOCKED -> CLOSED

Запрещено:
- менять статус на тот же;
- менять статус карты в CLOSED;
- переводить просроченную карту в любой статус, кроме CLOSED.

Структура проекта

src/main/java/com/joka/optima
├── config
├── controller
├── dto
│   ├── request
│   └── response
├── entity
├── enums
├── exception
├── mapper
├── repository
├── service
│   └── impl
├── strategy
└── validation

Тесты:

src/test/java/com/joka/optima
├── controller
├── service
│   └── impl
├── strategy
└── validation

Запуск проекта

Требования
- Java 17
- Maven 3.9+

Сборка
mvn clean install

Запуск
mvn spring-boot:run

Приложение поднимается по адресу:
http://localhost:8080

Swagger

Swagger подключен для просмотра и проверки API.
Обычно доступен по адресу:
http://localhost:8080/swagger-ui/index.html

H2 Console

Для просмотра данных in-memory базы:
http://localhost:8080/h2-console

Параметры подключения:
- JDBC URL — как в application.properties
- Username — sa
- Password — пусто, если не задано иное

Инициализация данных

При старте приложения автоматически создаются:
- платежные системы VISA, MASTERCARD, ELCART;
- тестовый клиент, если таблица клиентов пуста.

REST API

Клиенты

Создать клиента
POST /api/clients

{
"name": "Aibek",
"lastName": "Asanov"
}

Получить клиента по id
GET /api/clients/{clientId}

Получить список клиентов
GET /api/clients

Карты

Выпустить карту
POST /api/cards

{
"clientId": 1,
"paymentSystemCode": "VISA"
}

Получить карту по id
GET /api/cards/{cardId}

Пополнить карту
POST /api/cards/{cardId}/top-up

{
"amount": 500
}

Списать средства
POST /api/cards/{cardId}/debit

{
"amount": 300
}

Изменить статус карты
PATCH /api/cards/{cardId}/status

{
"status": "BLOCKED"
}

Обработка ошибок

Все ошибки возвращаются в едином формате через GlobalExceptionHandler.

Пример:

{
"timestamp": "2026-04-01T12:00:00",
"status": 400,
"error": "Bad Request",
"message": "Insufficient funds"
}

Обрабатываются:
- несуществующий клиент;
- несуществующая карта;
- неверная платежная система;
- невалидный request body;
- недостаточно средств;
- недопустимый переход статуса;
- ошибки валидации DTO.

Тесты

Покрыты:
- CardServiceImplTest
- ClientServiceImplTest
- VisaProcessingCenterStrategyTest
- MasterCardProcessingCenterStrategyTest
- ElcartProcessingCenterStrategyTest
- CardOperationValidatorTest
- CardControllerTest
- ClientControllerTest

Запуск всех тестов:
mvn test

Что показывает проект

- построение REST API на Spring Boot;
- применение Strategy для расширяемой логики;
- корректную работу с JPA и H2;
- централизованную валидацию и обработку ошибок;
- покрытие ключевой бизнес-логики тестами.
