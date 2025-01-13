package interfaces

import (
	"context"
	"pms_backend/pms_api/internal/pkg/model"
)

type IntegrationService interface {
	ActivateTesters(ctx context.Context, testers []string) error
	DeactivateTesters(ctx context.Context, pageInfo []string) error
	ChangeRoles(ctx context.Context, roles map[string]string) error
	ChangeUserData(ctx context.Context, user model.NUser) error
	GetAllUsers(ctx context.Context) ([]model.NUser, error)
}
