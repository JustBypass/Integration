package model

import "time"

type User struct {
	ID         string    `json:"id"`
	Username   string    `json:"username"`
	Password   []byte    `json:"-"`
	IsAdmin    bool      `json:"is_admin"`
	FirstName  string    `json:"first_name"`
	MiddleName string    `json:"middle_name"`
	LastName   string    `json:"last_name"`
	Position   string    `json:"position"`
	CreatedAt  time.Time `json:"created_at"`
	UpdatedAt  time.Time `json:"updated_at"`
}

type UserShort struct {
	ID       string `json:"id"`
	Username string `json:"username"`
	IsAdmin  bool   `json:"is_admin"`
	FullName string `json:"full_name"`
}

type UserInserted struct {
	Username   string `json:"username"`
	Password   string `json:"password"`
	IsAdmin    bool   `json:"is_admin"`
	FirstName  string `json:"first_name"`
	MiddleName string `json:"middle_name"`
	LastName   string `json:"last_name"`
	Position   string `json:"position"`
}

type UsersPaged struct {
	PageIndex int          `json:"page_index"`
	PageSize  int          `json:"page_size"`
	Total     int          `json:"total"`
	Users     []*UserShort `json:"items"`
}

type NUser struct {
	Name       string `json:"name"`
	Surname    string `json:"surname"`
	Patronymic string `json:"patronymic"`
	Login      string `json:"login"`
	Password   string `json:"password"`
	FullName   string `json:"fullName"`
}
