package integration

import (
	"bytes"
	"context"
	"encoding/json"
	"log"
	"net/http"
	"pms_backend/pms_api/internal/pkg/model"
	"pms_backend/pms_api/internal/pkg/repository/interfaces"
)

const URL = "http://testing-app:9091/api/integration/"

type integrationService struct {
	userRepository interfaces.UserRepository
}

func NewIntegrationService(r interfaces.UserRepository) *integrationService {
	return &integrationService{
		userRepository: r,
	}
}

// ActivateTesters активирует тестеров
func (s *integrationService) ActivateTesters(ctx context.Context, testersToActivate []string) error {
	{ /// На 9090
		bytesRepresentation, err := json.Marshal(testersToActivate)
		if err != nil {
			log.Fatalln(err)
		}

		_, err = http.Post(URL+"activate", "application/json", bytes.NewBuffer(bytesRepresentation))
		if err != nil {
			log.Fatalln(err)
		}
	}
	{ ///внутри

	}
	return nil
}

// DeactivateTesters деактивирует тестеров
func (s *integrationService) DeactivateTesters(ctx context.Context, testersToDeactivate []string) error {
	{ /// На 9090
		bytesRepresentation, err := json.Marshal(testersToDeactivate)
		if err != nil {
			log.Fatalln(err)
		}

		_, err = http.Post(URL+"deactivate", "application/json", bytes.NewBuffer(bytesRepresentation))
		if err != nil {
			log.Fatalln(err)
		}
	}
	{ ///внутри

	}
	return nil
}

// ChangeRoles изменяет роли пользователей
func (s *integrationService) ChangeRoles(ctx context.Context, rolesForUsers map[string]string) error {
	// process inside the app
	{ /// На 9090
		bytesRepresentation, err := json.Marshal(rolesForUsers)
		if err != nil {
			log.Fatalln(err)
		}

		_, err = http.Post(URL+"chroles", "application/json", bytes.NewBuffer(bytesRepresentation))
		if err != nil {
			log.Fatalln(err)
		}
	}
	{ /// Внутри

	}
	return nil
}

// ChangeUserData изменяет данные пользователя
func (s *integrationService) ChangeUserData(ctx context.Context, user model.NUser) error {
	{
		bytesRepresentation, err := json.Marshal(user)
		if err != nil {
			log.Fatalln(err)
		}

		_, err = http.Post(URL+"chdata", "application/json", bytes.NewBuffer(bytesRepresentation))
		if err != nil {
			log.Fatalln(err)
		}
	}
	{

	}
	return nil
}

func (s *integrationService) GetAllUsers(ctx context.Context) ([]model.NUser, error) {
	return s.userRepository.GetAllUsers(ctx)
}
