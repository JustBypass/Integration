package postgres

const (
	countUsersQuery = `
		SELECT COUNT(id)
		FROM users
	`

	getTestersQuery = `
		SELECT *
		FROM testers_view 
	`

	getUsersQuery = `
		SELECT id, login, is_admin, first_name, middle_name, last_name
		FROM users
	`
	getUsersQueryTail = `
		ORDER BY login LIMIT @page_size OFFSET @page_offset
	`
	getUserByID = `
		SELECT id, login, is_admin, first_name, middle_name, last_name, position, created_at, updated_at
		FROM users
		WHERE id = @id
	`
	createUser = `
		INSERT INTO users(id, login, password, is_admin, first_name, middle_name, last_name, position, created_at, updated_at)
		VALUES (@id, @login, @password, @is_admin, @first_name, @middle_name, @last_name, @position, @created_at, @updated_at)
	`
	updateUser = `
		UPDATE users
		SET
		login = @login,
		first_name = @first_name,
		middle_name = @middle_name,
		last_name = @last_name,
		position = @position,
		updated_at = @updated_at
		WHERE id = @id
	`
	deleteUser = `
		DELETE FROM users
		WHERE id = @id
	`
	getProjectsOfUserQuery = `
		SELECT p.id, p.name, p.is_active
		FROM project p
		JOIN participants_project pp on pp.project_id = p.id
		WHERE pp.user_id = @user_id
	`

	//	insertRole = `
	//		-- 1. Находим id роли по её имени, например 'Руководитель проекта'
	//		WITH role_id AS (
	//			SELECT id
	//			FROM public.role
	//			WHERE name_role = @roleName
	//			LIMIT 1
	//		),
	//		-- 2. Находим id пользователя, если его нужно искать по логину (например 'ivanov123')
	//		user_id AS (
	//			SELECT id
	//			FROM public.users
	//			WHERE fullName = @fullName
	//			LIMIT 1
	//		)
	//		-- 3. Вставляем данные в таблицу participants_project
	//		INSERT INTO public.participants_project (user_id, project_id, role_id, is_admin_project)
	//		SELECT u.id, 'project-uuid', r.id, true -- 'project-uuid' замените на реальный UUID проекта
	//		FROM user_id u, role_id r
	//		WHERE u.id IS NOT NULL AND r.id IS NOT NULL;
	//`
)
