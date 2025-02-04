package uz.com.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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


    @PutMapping("/{id}/add-client-role")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<UserResponse>> addClientRole(@PathVariable UUID id,
                                                                       @RequestParam String role,
                                                                       Principal principal) {
        return ResponseEntity.ok(userService.changeRoleTo(id, role, principal));
    }


    @PutMapping("/{id}/add-manager-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<UserResponse>> addManagerRole(@PathVariable UUID id,
                                                                        @RequestParam String role,
                                                                        Principal principal) {
        return ResponseEntity.ok(userService.changeRoleTo(id, role, principal));
    }


    @PutMapping("/{id}/remove-manager-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<UserResponse>> removeManagerRole(@PathVariable UUID id,
                                                                           @RequestParam String role,
                                                                           Principal principal) {
        return ResponseEntity.ok(userService.removeRole(id, role, principal));
    }


    @PutMapping("/{id}/remove-client-role")
    @PreAuthorize("hasRole('MANAGER') OR hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<UserResponse>> removeClientRole(@PathVariable UUID id,
                                                                          @RequestParam String role,
                                                                          Principal principal) {
        return ResponseEntity.ok(userService.removeRole(id, role, principal));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<UserResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


    @DeleteMapping("/{id}/delete")
    public ResponseEntity<GeneralResponse<String>> deleteOne(@PathVariable UUID id,
                                                             Principal principal) {
        return ResponseEntity.ok(userService.deleteById(id, principal));
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }


    @DeleteMapping("/multi-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> multiDelete(@RequestBody List<String> ids, Principal principal) {
        return ResponseEntity.ok(userService.multiDeleteUser(ids, principal));
    }


    @GetMapping("/get-by-number")
    @PreAuthorize("hasRole('ADMIN' or hasRole('MANAGER'))")
    public ResponseEntity<GeneralResponse<UserResponse>> getByPhone(@RequestParam String number){
        return ResponseEntity.ok(userService.getByPhone(number));
    }

}
