package postgres

import (
	"context"
	"errors"
	"pms_backend/pms_api/internal/pkg/model"

	"github.com/jackc/pgx/v5"
)

func (r *repository) GetProjectMembers(ctx context.Context, projectID string) ([]*model.UserShort, error) {
	rows, err := r.pool.Query(ctx, getProjectMembers, pgx.NamedArgs{"project_id": projectID})
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items, err := pgx.CollectRows(rows, pgx.RowToStructByName[user])
	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			return []*model.UserShort{}, nil
		}
		return nil, err
	}

	return toUserShortsFromRepo(items), nil
}

func (r *repository) AddProjectMember(ctx context.Context, projectID string, member *model.Member) error {
	rows, err := r.pool.Query(ctx, "SELECT id, name_role FROM role")
	if err != nil {
		return err
	}
	defer rows.Close()
	type role struct {
		ID   int    `db:"id"`
		Name string `db:"name_role"`
	}
	items, err := pgx.CollectRows(rows, pgx.RowToStructByName[role])
	if err != nil {
		return err
	}
	roleID := -1
	for _, v := range items {
		if member.Role == v.Name {
			roleID = v.ID
		}
	}
	if roleID == -1 {
		return errors.New("incorrect value of role")
	}

	_, err = r.pool.Exec(ctx, insertUserToProject, pgx.NamedArgs{
		"project_id":       projectID,
		"user_id":          member.UserID,
		"role_id":          roleID,
		"is_admin_project": member.IsAdminProject,
	})
	// Извлечь fullname из юзера
	// func (u User) FullName() string {
	//	// Используем strings.TrimSpace, чтобы избежать лишних пробелов, если MiddleName пустой
	//	return strings.TrimSpace(fmt.Sprintf("%s %s %s", u.FirstName, u.MiddleName, u.LastName))
	//}
	// по role_id найти название роли
	return err
}

func (r *repository) DeleteProjectMember(ctx context.Context, projectID, userID string) error {
	_, err := r.pool.Exec(ctx, deleteUserFromProject, pgx.NamedArgs{
		"project_id": projectID,
		"user_id":    userID,
	})
	return err
}
