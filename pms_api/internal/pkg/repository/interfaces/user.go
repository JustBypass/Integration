package interfaces

import (
	"context"
	"pms_backend/pms_api/internal/pkg/model"
)

type UserRepository interface {
	GetUsers(ctx context.Context, pageInfo *model.PageInfo, isAdmin *bool) ([]*model.UserShort, int, error)
	GetUserByID(ctx context.Context, userID string) (*model.User, error)
	CreateUser(ctx context.Context, user *model.User) error
	UpdateUser(ctx context.Context, user *model.User) error
	DeleteUser(ctx context.Context, userID string) error
	GetUserProjects(ctx context.Context, userID string) ([]*model.ProjectShort, error)
	GetAllUsers(ctx context.Context) ([]model.NUser, error)
}
