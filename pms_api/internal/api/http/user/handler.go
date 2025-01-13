package user

import (
	"errors"
	"log/slog"
	"net/http"
	"pms_backend/pms_api/internal/pkg/apperror"
	"pms_backend/pms_api/internal/pkg/model"
	"pms_backend/pms_api/internal/pkg/service/interfaces"
	"strconv"

	"github.com/google/uuid"
	"github.com/labstack/echo/v4"
)

type handler struct {
	userService interfaces.UserService
}

func NewHandler(s interfaces.UserService) *handler {
	return &handler{
		userService: s,
	}
}

const (
	internalError   = "Internal server error"
	incorrectUserId = "Incorrect user id"
	bindError       = "Bind error"
	userNotFound    = "User not found"
)

// GetUsers
// @Tags User
// @Summary Get users
// @Description Get users
// @Accept json
// @Produce json
// @Param isAdmin query bool false "Get admins or users"
// @Param pageIndex query int false "Page index"
// @Param pageSize query int false "Page size"
// @Success 200 {object} model.UsersPaged
// @Security Login
// @Router /users [get]
func (h *handler) GetUsers(c echo.Context) error {
	qIsAdmin := c.QueryParam("isAdmin")
	qp := c.QueryParam("pageIndex")
	pageIndex, err := strconv.Atoi(qp)
	if err != nil || pageIndex < 1 {
		pageIndex = 1
	}
	qs := c.QueryParam("pageSize")
	pageSize, err := strconv.Atoi(qs)
	if err != nil || pageSize < 1 {
		pageSize = 10
	}
	pageInfo := &model.PageInfo{
		PageIndex: pageIndex,
		PageSize:  pageSize,
	}
	var pointerIsAdmin *bool = nil
	isAdmin, err := strconv.ParseBool(qIsAdmin)
	if err == nil {
		pointerIsAdmin = new(bool)
		*pointerIsAdmin = isAdmin
	}
	users, countUsers, err := h.userService.GetUsers(c.Request().Context(), pageInfo, pointerIsAdmin)
	if err != nil {
		slog.Error(err.Error())
		return c.JSON(http.StatusInternalServerError, model.Message{Message: internalError})
	}
	return c.JSON(http.StatusOK,
		model.UsersPaged{
			PageIndex: pageIndex,
			PageSize:  pageSize,
			Total:     countUsers,
			Users:     users,
		})
}

// GetUserByID
// @Tags User
// @Summary Get user by ID
// @Description Get user by ID
// @Accept json
// @Produce json
// @Param id path string true "User ID"
// @Success 200 {object} model.User
// @Security Login
// @Router /users/{id} [get]
func (h *handler) GetUserByID(c echo.Context) error {
	userID := c.Param("user_id")
	if err := uuid.Validate(userID); err != nil {
		return c.JSON(http.StatusUnprocessableEntity, model.Message{Message: incorrectUserId})
	}
	user, err := h.userService.GetUserByID(c.Request().Context(), userID)
	if err != nil {
		if errors.Is(err, apperror.NotFound) {
			return c.JSON(http.StatusNotFound, model.Message{Message: userNotFound})
		}
		slog.Error(err.Error())
		return c.JSON(http.StatusInternalServerError, model.Message{Message: internalError})
	}
	return c.JSON(http.StatusOK, user)
}

// CreateUser
// @Tags User
// @Summary Create user
// @Description Create user
// @Accept json
// @Produce json
// @Param user body model.UserInserted true "User"
// @Success 201 {object} model.User
// @Security Login
// @Router /users [post]
func (h *handler) CreateUser(c echo.Context) error {
	userInserted := &model.UserInserted{}
	println("CreateUser")
	/// Post request for user creation

	err := c.Bind(userInserted)
	if err != nil {
		return c.JSON(http.StatusUnprocessableEntity, model.Message{Message: bindError})
	}
	user, err := h.userService.CreateUser(c.Request().Context(), userInserted)
	if err != nil {
		slog.Error(err.Error())
		return c.JSON(http.StatusInternalServerError, model.Message{Message: internalError})
	}
	return c.JSON(http.StatusCreated, user)
}

// UpdateUser
// @Tags User
// @Summary Update user
// @Description Update user
// @Accept  json
// @Produce  json
// @Param id path string true "User ID"
// @Param user body model.UserInserted true "User"
// @Success 200 {object} model.User
// @Failure 400
// @Security Login
// @Router /users/{id} [put]
func (h *handler) UpdateUser(c echo.Context) error {
	userID := c.Param("user_id")
	if err := uuid.Validate(userID); err != nil {
		return c.JSON(http.StatusUnprocessableEntity, model.Message{Message: incorrectUserId})
	}
	userUpdated := &model.UserInserted{}
	err := c.Bind(userUpdated)
	if err != nil {
		return c.JSON(http.StatusUnprocessableEntity, model.Message{Message: bindError})
	}
	user, err := h.userService.UpdateUser(c.Request().Context(), userID, userUpdated)
	if err != nil {
		if errors.Is(err, apperror.NotFound) {
			return c.JSON(http.StatusNotFound, model.Message{Message: userNotFound})
		}
		slog.Error(err.Error())
		return c.JSON(http.StatusInternalServerError, model.Message{Message: internalError})
	}
	return c.JSON(http.StatusOK, user)
}

// DeleteUser
// @Tags User
// @Summary Delete user
// @Description Delete User
// @Accept  json
// @Produce  json
// @Param id path string true "User ID"
// @Success 204
// @Failure 400
// @Security Login
// @Router /users/{id} [delete]
func (h *handler) DeleteUser(c echo.Context) error {
	userID := c.Param("user_id")
	if err := uuid.Validate(userID); err != nil {
		return c.JSON(http.StatusUnprocessableEntity, model.Message{Message: incorrectUserId})
	}
	err := h.userService.DeleteUser(c.Request().Context(), userID)
	if err != nil {
		if errors.Is(err, apperror.NotFound) {
			return c.JSON(http.StatusNotFound, model.Message{Message: userNotFound})
		}
		slog.Error(err.Error())
		return c.JSON(http.StatusInternalServerError, model.Message{Message: internalError})
	}
	return c.NoContent(http.StatusNoContent)
}

// GetUserProject
// @Tags User
// @Summary Get user projects
// @Description Get user projects
// @Accept  json
// @Produce  json
// @Param id path string true "User ID"
// @Success 200 {object} []model.Project
// @Failure 404 {object} model.Message "User not found"
// @Failure 500 {object} model.Message
// @Security Login
// @Router /users/{id}/projects [get]
func (h *handler) GetUserProjects(c echo.Context) error {
	userID := c.Param("user_id")
	if err := uuid.Validate(userID); err != nil {
		return c.JSON(http.StatusUnprocessableEntity, model.Message{Message: incorrectUserId})
	}
	projects, err := h.userService.GetUserProjects(c.Request().Context(), userID)
	if err != nil {
		if errors.Is(err, apperror.NotFound) {
			return c.JSON(http.StatusNotFound, model.Message{Message: userNotFound})
		}
		slog.Error(err.Error())
		return c.JSON(http.StatusInternalServerError, model.Message{Message: internalError})
	}
	return c.JSON(http.StatusOK, projects)
}
