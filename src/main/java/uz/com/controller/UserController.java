package uz.com.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.com.model.dto.request.UserCreateRequest;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.dto.response.UserResponse;
import uz.com.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;


    @Operation(summary = "Add role", description = "Add client role to user by managers or admins")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}/add-client-role")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<UserResponse>> addClientRole(@PathVariable UUID id,
                                                                       @RequestParam String role,
                                                                       Principal principal) {
        return ResponseEntity.ok(userService.changeRoleTo(id, role, principal));
    }




    @Operation(summary = "Add role",description = "Add manager role to users by admins")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}/add-manager-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<UserResponse>> addManagerRole(@PathVariable UUID id,
                                                                        @RequestParam String role,
                                                                        Principal principal) {
        return ResponseEntity.ok(userService.changeRoleTo(id, role, principal));
    }



    @Operation(summary = "Remove role",description = "Remove manager role from users by admins")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role removed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials"),
            @ApiResponse(responseCode = "406", description = "Data not acceptable"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}/remove-manager-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<UserResponse>> removeManagerRole(@PathVariable UUID id,
                                                                           @RequestParam String role,
                                                                           Principal principal) {
        return ResponseEntity.ok(userService.removeRole(id, role, principal));
    }



    @Operation(summary = "Remove role",description = "Remove client role from users by admins")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role removed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials"),
            @ApiResponse(responseCode = "406", description = "Data not acceptable"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}/remove-client-role")
    @PreAuthorize("hasRole('MANAGER') OR hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<UserResponse>> removeClientRole(@PathVariable UUID id,
                                                                          @RequestParam String role,
                                                                          Principal principal) {
        return ResponseEntity.ok(userService.removeRole(id, role, principal));
    }


    @Operation(summary = "Get user", description = "Get user by id by admins or managers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get data successfully"),
            @ApiResponse(responseCode = "404", description = "Data not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<UserResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


    @Operation(summary = "Delete user",description = "Delete user by id by admins")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delete data successfully"),
            @ApiResponse(responseCode = "404", description = "Data not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> deleteOne(@PathVariable UUID id,
                                                             Principal principal) {
        return ResponseEntity.ok(userService.deleteById(id, principal));
    }


    @Operation(summary = "Get all user", description = "Get all default users or get all by role by admins")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get data successfully"),
            @ApiResponse(responseCode = "404", description = "Data not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(required = false) String role) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getAllUsers(pageable,role));
    }


    @Operation(summary = "Multi delete", description = "Multi delete users by admins")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delete data successfully"),
            @ApiResponse(responseCode = "404", description = "Data not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @DeleteMapping("/multi-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> multiDelete(@RequestBody List<String> ids, Principal principal) {
        return ResponseEntity.ok(userService.multiDeleteUser(ids, principal));
    }


    @Operation(summary = "Get by phone", description = "Get users through their phone number by admins or managers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get data successfully"),
            @ApiResponse(responseCode = "404", description = "Data not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @GetMapping("/get-by-number")
    @PreAuthorize("hasRole('ADMIN' or hasRole('MANAGER'))")
    public ResponseEntity<GeneralResponse<UserResponse>> getByPhone(@RequestParam String number){
        return ResponseEntity.ok(userService.getByPhone(number));
    }


    @Operation(summary = "Update user", description = "Update user API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Data updated successfully"),
            @ApiResponse(responseCode = "404", description = "Data not found"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "406", description = "Data not acceptable"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @PutMapping("/{id}/update")
    public ResponseEntity<GeneralResponse<UserResponse>> update(@PathVariable UUID id,
                                                                @Valid @RequestBody UserCreateRequest request){
        return ResponseEntity.ok(userService.update(id, request));
    }

}
