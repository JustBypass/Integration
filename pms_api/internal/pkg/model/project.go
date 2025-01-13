package model

import "time"

type Project struct {
	ID          string    `json:"id"`
	Name        string    `json:"name"`
	Description string    `json:"description"`
	IsActive    bool      `json:"is_active"`
	CreatedAt   time.Time `json:"created_at"`
	UpdatedAt   time.Time `json:"updated_at"`
}

type ProjectShort struct {
	ID       string `json:"id"`
	Name     string `json:"name"`
	IsActive bool   `json:"is_active"`
}

type InsertProject struct {
	Name        string `json:"name"`
	Description string `json:"description"`
}

type ProjectsPaged struct {
	PageIndex int             `json:"page_index"`
	PageSize  int             `json:"page_size"`
	Total     int             `json:"total"`
	Projects  []*ProjectShort `json:"items"`
}

type UserRoleChanger struct {
	FullName  string `json:"fullName"`
	ProjectID string `json:"projectId"`
	RoleID    string `json:"roleId"`
}

// Tester - сущность, которая представляет запись из представления testers_view
type Tester struct {
	Name       string `db:"name"`       // Имя
	Surname    string `db:"surname"`    // Фамилия
	Patronymic string `db:"patronymic"` // Отчество
	IsActive   bool   `db:"is_active"`  // Статус активности (is_admin)
	Role       string `db:"role"`       // Роль
	Login      string `db:"login"`      // Логин
	//Password   string `db:"password"`   // Пароль
}

type User2 struct {
	ID         string    `db:"id"`
	Username   string    `db:"username"`
	Password   []byte    `db:"-"`
	IsAdmin    bool      `db:"is_admin"`
	FirstName  string    `db:"first_name"`
	MiddleName string    `db:"middle_name"`
	LastName   string    `db:"last_name"`
	Position   string    `db:"position"`
	CreatedAt  time.Time `db:"created_at"`
	UpdatedAt  time.Time `db:"updated_at"`
}
