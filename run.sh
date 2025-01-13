#!/usr/bin/env bash

set -e  # Останавливает выполнение скрипта при любой ошибке
set -u  # Выводит ошибку при использовании неинициализированных переменных
set -o pipefail  # Обрабатывает ошибки в пайпах

# === Переменные ===
BACKEND_DIR="./backend"
PMS_API_DIR="./pms-api"
DB_USER="admin"
DB_NAME="pms-develop"
DOCKER_NETWORK="pms_services"
INIT_DATA_IMAGE="init_data"
INIT_DATA_DOCKERFILE="./docker/init_data/initdata.dockerfile"
SWAGGO_PACKAGE="github.com/swaggo/swag/cmd/swag@latest"
SWAG_INIT_DIR="./pms_api/cmd"
SWAG_OUTPUT_DIR="../../api/docs"
SQL_FILE="./migrations/postgres/1_initialize_db.up.sql"
CONTAINER_NAME="pms_postgresql"

# === Функции ===
print_header() {
  echo -e "\n=== $1 ===\n"
}

# === Сборка Backend без тестов ===
print_header "Сборка Backend"
cd "$BACKEND_DIR" && mvn install -DskipTests && cd ../

# === Запуск docker-compose ===
print_header "Запуск Docker Compose"
docker compose -f "docker-compose.dev.yaml"  up -d

cd "$BACKEND_DIR" && docker compose  -f "docker-compose.yml" up -d && cd ..



# === Подключение к базе данных и выполнение SQL ===
echo "Executing SQL file: $SQL_FILE"
docker exec -i $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME < $SQL_FILE && \
echo "SQL execution completed."

# === Сборка и запуск контейнера init_data ===
print_header "Сборка и запуск init_data контейнера"
docker build -f "$INIT_DATA_DOCKERFILE" -t "$INIT_DATA_IMAGE" .
docker run --rm --net "$DOCKER_NETWORK" --name "$INIT_DATA_IMAGE" "$INIT_DATA_IMAGE"

# === Установка Swaggo ===
print_header "Установка Swagger"
go install "$SWAGGO_PACKAGE"

# === Генерация Swagger документации ===
print_header "Генерация Swagger документации"
cd "$SWAG_INIT_DIR"
swag init -g ../**/**.go --output "$SWAG_OUTPUT_DIR"

print_header "Скрипт завершён успешно!"
