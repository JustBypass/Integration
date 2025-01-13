package integration

import (
	// "errors"
	"bytes"
	"encoding/json"
	"log"
	"net/http"

	// "pms_backend/pms_api/internal/pkg/apperror"
	"pms_backend/pms_api/internal/pkg/model"
	"pms_backend/pms_api/internal/pkg/service/interfaces"

	"github.com/labstack/echo/v4"
)

const (
	internalServerError = "Internal server error"
	bindError           = "Bind error"
	userNotFound        = "User not found"
)

type handler struct {
	integrationService interfaces.IntegrationService
}

func NewHandler(s interfaces.IntegrationService) *handler {
	return &handler{
		integrationService: s,
	}
}

func MakeRequest(message string) {

	// message := map[string]interface{}{
	// 	"hello": "world",
	// 	"life":  42,
	// 	"embedded": map[string]string{
	// 		"yes": "of course!",
	// 	},
	// }

	bytesRepresentation, err := json.Marshal(message)
	if err != nil {
		log.Fatalln(err)
	}

	resp, err := http.Post("https://localhost:9091/api/integration/", "application/json", bytes.NewBuffer(bytesRepresentation))
	if err != nil {
		log.Fatalln(err)
	}

	var result map[string]interface{}

	json.NewDecoder(resp.Body).Decode(&result)

	log.Println(result)
	log.Println("data: ", result["data"])
}

// ActivateTesters
// @Tags Integration
// @Summary Activate testers
// @Description Activate testers by providing a list of User IDs
// @Accept json
// @Produce json
// @Param users body []string true "List of User IDs to be activated"
// @Success 200 {string} string "ActivateTesters"
// @Failure 400 {object} model.Message "Invalid request body"
// @Failure 500 {object} model.Message "Internal server error"
// @Router /integration/activate [post]
func (h *handler) ActivateTesters(c echo.Context) error {
	var users []string
	println("Activate")
	if err := c.Bind(&users); err != nil {
		return c.JSON(http.StatusUnprocessableEntity, model.Message{Message: bindError})
	}
	//if err := h.integrationService.ActivateTesters(c.Request().Context(), users); err != nil {
	//	return c.JSON(http.StatusInternalServerError, model.Message{Message: internalServerError})
	//}
	h.integrationService.ActivateTesters(c.Request().Context(), users)
	return c.JSON(http.StatusOK, "ActivateTesters")
}

// DeactivateTesters
// @Tags Integration
// @Summary Deactivate testers
// @Description Deactivate testers by providing a list of User IDs
// @Accept json
// @Produce json
// @Param users body []string true "List of User IDs to be deactivated"
// @Success 200 {string} string "DeactivateTesters"
// @Failure 400 {object} model.Message "Invalid request body"
// @Failure 500 {object} model.Message "Internal server error"
// @Router /integration/deactivate [post]
func (h *handler) DeactivateTesters(c echo.Context) error {
	var users []string
	if err := c.Bind(&users); err != nil {
		return c.JSON(http.StatusUnprocessableEntity, model.Message{Message: bindError})
	}
	//if err := h.integrationService.DeactivateTesters(c.Request().Context(), users); err != nil {
	//	return c.JSON(http.StatusInternalServerError, model.Message{Message: internalServerError})
	//}
	h.integrationService.DeactivateTesters(c.Request().Context(), users)
	return c.JSON(http.StatusOK, "DeactivateTesters")
}

// ChangeRoles
// @Tags Integration
// @Summary Change roles of testers
// @Description Change the roles of testers by mapping User IDs to Roles
// @Accept json
// @Produce json
// @Param roles body map[string]string true "Map of User IDs to Roles"
// @Success 200 {string} string "ChangeRoles"
// @Failure 400 {object} model.Message "Invalid request body"
// @Failure 500 {object} model.Message "Internal server error"
// @Router /integration/chroles [post]
func (h *handler) ChangeRoles(c echo.Context) error {
	var roles map[string]string
	if err := c.Bind(&roles); err != nil {
		return c.JSON(http.StatusUnprocessableEntity, model.Message{Message: bindError})
	}
	//if err := h.integrationService.ChangeRoles(c.Request().Context(), roles); err != nil {
	//	return c.JSON(http.StatusInternalServerError, model.Message{Message: internalServerError})
	//}
	h.integrationService.ChangeRoles(c.Request().Context(), roles)
	return c.JSON(http.StatusOK, "ChangeRoles")
}

// ChangeUserData
// @Tags Integration
// @Summary Change user data
// @Description Change personal data of users by providing a list of user data
// @Accept json
// @Produce json
// @Param data body model.NUser true "List of User Data to be updated"
// @Success 200 {string} string "ChangeUserData"
// @Failure 400 {object} model.Message "Invalid request body"
// @Failure 500 {object} model.Message "Internal server error"
// @Router /integration/chdata [post]
func (h *handler) ChangeUserData(c echo.Context) error {
	var data model.NUser
	if err := c.Bind(&data); err != nil {
		return c.JSON(http.StatusUnprocessableEntity, model.Message{Message: bindError})
	}
	//if err := h.integrationService.ChangeUserData(c.Request().Context(), data); err != nil {
	//	return c.JSON(http.StatusInternalServerError, model.Message{Message: internalServerError})
	//}
	h.integrationService.ChangeUserData(c.Request().Context(), data)
	return c.JSON(http.StatusOK, "ChangeUserData")
}

// GetAllUsers
// @Tags Integration
// @Summary Get all users
// @Description Retrieve a list of all users
// @Accept json
// @Produce json
// @Success 200 {array} model.NUser "List of all users"
// @Failure 500 {object} model.Message "Internal server error"
// @Router /integration/getallusers [get]
func (h *handler) GetAllUsers(c echo.Context) error {
	var data []model.NUser
	if err := c.Bind(&data); err != nil {
		return c.JSON(http.StatusUnprocessableEntity, model.Message{Message: bindError})
	}
	//if users,err := h.integrationService.GetAllUsers(c.Request().Context()); err != nil {
	//	return c.JSON(http.StatusInternalServerError, model.Message{Message: internalServerError})
	//}
	users, _ := h.integrationService.GetAllUsers(c.Request().Context())
	return c.JSON(http.StatusOK, users)
}
