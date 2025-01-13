package integration

import "github.com/labstack/echo/v4"

func (h *handler) RegisterRoutes(router *echo.Group) {
	integration := router.Group("/integration")
	{
		integration.POST("/activate", h.ActivateTesters)
		integration.POST("/deactivate", h.DeactivateTesters)
		//integration.POST("/chroles", h.ChangeRoles)
		//integration.POST("/addROle", h.ChangeUserData)

		integration.GET("/allUsers", h.GetAllUsers) // change
	}
}
