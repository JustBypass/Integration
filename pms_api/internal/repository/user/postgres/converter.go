package postgres

import (
	"pms_backend/pms_api/internal/pkg/model"
	"strings"
)

func toUserShortsFromRepo(usersFromDb []userShort) []*model.UserShort {
	users := make([]*model.UserShort, len(usersFromDb))
	for i := range users {
		sb := strings.Builder{}
		sb.WriteString(usersFromDb[i].FirstName)
		if usersFromDb[i].MiddleName != "" {
			sb.WriteString(" ")
			sb.WriteString(usersFromDb[i].MiddleName)
		}
		sb.WriteString(" ")
		sb.WriteString(usersFromDb[i].LastName)
		users[i] = &model.UserShort{
			ID:       usersFromDb[i].ID,
			Username: usersFromDb[i].Username,
			IsAdmin:  usersFromDb[i].IsAdmin,
			FullName: sb.String(),
		}
	}
	return users
}

func toUserFromRepo(userFromDb user) *model.User {
	return &model.User{
		ID:         userFromDb.ID,
		Username:   userFromDb.Username,
		IsAdmin:    userFromDb.IsAdmin,
		FirstName:  userFromDb.FirstName,
		MiddleName: userFromDb.MiddleName,
		LastName:   userFromDb.LastName,
		Position:   userFromDb.Position,
		CreatedAt:  userFromDb.CreatedAt,
		UpdatedAt:  userFromDb.UpdatedAt,
	}
}

func toUsersFromRepoList(usersFromDb []user) []model.User {
	var users []model.User
	for _, userFromDb := range usersFromDb {
		users = append(users, model.User{
			ID:         userFromDb.ID,
			Username:   userFromDb.Username,
			IsAdmin:    userFromDb.IsAdmin,
			FirstName:  userFromDb.FirstName,
			MiddleName: userFromDb.MiddleName,
			LastName:   userFromDb.LastName,
			Position:   userFromDb.Position,
			CreatedAt:  userFromDb.CreatedAt,
			UpdatedAt:  userFromDb.UpdatedAt,
		})
	}
	return users
}

func toProjectShortsFromDb(projectsFromDb []projectShort) []*model.ProjectShort {
	projects := make([]*model.ProjectShort, len(projectsFromDb))
	for i := range projects {
		projects[i] = &model.ProjectShort{
			ID:       projectsFromDb[i].ID,
			Name:     projectsFromDb[i].Name,
			IsActive: projectsFromDb[i].IsActive,
		}
	}
	return projects
}
