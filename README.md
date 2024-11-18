
---

# Инструкция по запуску приложения

## Требования

Перед запуском приложения убедитесь, что у вас установлены следующие инструменты:

- **Java Development Kit (JDK) 21**
- **Maven**
- **Docker**
- **Git**

## Шаг 1: Сборка приложения с помощью Maven

1. Склонируйте репозиторий:
   ```bash
   git clone https://github.com/matthew8913/task-manager.git
   cd task-manager
   ```

2. Соберите проект с помощью Maven:
   ```bash
   mvn clean install
   ```

## Шаг 2: Создание Docker-образа для приложения

Соберите Docker-образ для вашего приложения:

```bash
docker build -t task-manager-service .
```

## Шаг 3: Запуск Docker Compose

Запустите Docker Compose для запуска базы данных и приложения:

```bash
docker-compose up
```

### Настройки портов и адресов

Рекомендуется проверить в `docker-compose.yml` порты для контейнеров, чтобы избежать конфликтов. Если вы ничего не меняли, будут использоваться следующие настройки:

- **Порт приложения:** 8080
- **Порт базы данных:** 5440
- **Корневой адрес для запросов:** [http://localhost:8080](http://localhost:8080)
- **Документация API:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---
