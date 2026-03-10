package web.internship.SODSolutions.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.internship.SODSolutions.dto.request.ReqChangePasswordDTO;
import web.internship.SODSolutions.dto.request.ReqUpdateUserDTO;
import web.internship.SODSolutions.dto.request.ReqUserDTO;
import web.internship.SODSolutions.dto.response.ApiResponse;
import web.internship.SODSolutions.dto.response.ResUserDTO;
import web.internship.SODSolutions.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserController {

    final UserService userService;

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<ResUserDTO>>> getAllUsers() {
        List<ResUserDTO> res = userService.getAllUsers();
        ApiResponse<List<ResUserDTO>> response = ApiResponse.<List<ResUserDTO>>builder()
                .status(200)
                .message("Fetch data successfully")
                .data(res)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<ResUserDTO>> createUser(@RequestBody ReqUserDTO reqUserDTO) {
        ResUserDTO res = userService.createUser(reqUserDTO);

        ApiResponse<ResUserDTO> response = ApiResponse.<ResUserDTO>builder()
                .status(200)
                .message("Create user successfully")
                .data(res)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping()
    public ResponseEntity<ApiResponse<ResUserDTO>> updateUser(@Valid @RequestBody ReqUpdateUserDTO rqUser) {
        ResUserDTO updatedUser = userService.updateUser(rqUser);
        ApiResponse<ResUserDTO> response = ApiResponse.<ResUserDTO>builder()
                .status(200)
                .message("User updated successfully")
                .data(updatedUser)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<ResUserDTO>> changePassword(@Valid @RequestBody ReqChangePasswordDTO request) {
        ResUserDTO updatedUser = userService.changePassword(
                request.getOldPassword(),
                request.getNewPassword()
        );

        ApiResponse<ResUserDTO> response = ApiResponse.<ResUserDTO>builder()
                .status(200)
                .message("Password changed successfully")
                .data(updatedUser)
                .build();

        return ResponseEntity.ok(response);
    }




}
