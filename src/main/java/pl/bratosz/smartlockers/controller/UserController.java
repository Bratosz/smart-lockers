package pl.bratosz.smartlockers.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/add-employee-to-management-list/{employeeId}/{userId}")
    public StandardResponse addEmployeeToManagementList(
            @PathVariable("employeeId") long employeeId,
            @PathVariable("userId") long userId) {
        return userService.addEmployeeToManagementList(employeeId, userId);
    }
}
