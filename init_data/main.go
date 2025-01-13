package main

import (
	"context"
	"crypto/sha256"
	"fmt"
	"log"

	"github.com/jackc/pgx/v5"
)

func main() {
	connStr := "postgres://admin:pmsmephi@pms_postgresql:5433/pms-develop?sslmode=disable"

	conn, err := pgx.Connect(context.Background(), connStr)
	if err != nil {
		log.Fatalf("Не удалось подключиться к базе данных: %v", err)
	}
	defer conn.Close(context.Background())

	username := "admin"
	password := "admin"
	hash := sha256.New()
	_, err = hash.Write([]byte(password))
	if err != nil {
		log.Fatalf("creating admin: %s", err.Error())
	}
	hashedPassword := hash.Sum(nil)

	sqlQuery := `
    INSERT INTO public.users (id, first_name, middle_name, last_name, position, is_admin, login, password, created_at, updated_at)
    VALUES ('445e2563-67d1-474e-9b8b-ba325dc17ac8', 'Главный администратор', '', '', 'Администратор системы', true, $1, $2, 'NOW()', 'NOW()')
	`

	_, err = conn.Exec(context.Background(), sqlQuery, username, hashedPassword)
	if err != nil {
		log.Fatalf("Ошибка при вставке данных: %v", err)
	}

	fmt.Printf("Пользователь %s успешно добавлен.\n", username)
}
