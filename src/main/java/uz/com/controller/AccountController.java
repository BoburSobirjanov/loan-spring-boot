package uz.com.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.com.model.dto.request.AccountCreateRequest;
import uz.com.model.dto.response.AccountResponse;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.service.AccountService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Account controller APIs for managing", description = "Account Controller")
@RequestMapping("/api/v1/accounts")
public class AccountController {


    private final AccountService accountService;


    @Operation(summary = "Save account",description = "Save account by managers for clients")
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "Data created successfully!"),
            @ApiResponse(responseCode = "404",description = "Data not found!"),
            @ApiResponse(responseCode = "406",description = "Data not acceptable"),
            @ApiResponse(responseCode = "500",description = "Internal server error"),
            @ApiResponse(responseCode = "401",description = "Invalid credentials"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @PostMapping("/save")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<GeneralResponse<AccountResponse>> save(@RequestBody AccountCreateRequest request,
                                                                 Principal principal) {
        return ResponseEntity.ok(accountService.save(request, principal));
    }


    @Operation(summary = "Get account",description = "Get account through id by users")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Get data successfully!"),
            @ApiResponse(responseCode = "404",description = "Data not found!"),
            @ApiResponse(responseCode = "406",description = "Data not acceptable"),
            @ApiResponse(responseCode = "500",description = "Internal server error"),
            @ApiResponse(responseCode = "401",description = "Invalid credentials"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or hasRole('CLIENTS')")
    public ResponseEntity<GeneralResponse<AccountResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getById(id));
    }


    @Operation(summary = "Delete account", description = "Delete account through id by admins")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Data deleted successfully!"),
            @ApiResponse(responseCode = "404",description = "Data not found!"),
            @ApiResponse(responseCode = "406",description = "Data not acceptable"),
            @ApiResponse(responseCode = "500",description = "Internal server error"),
            @ApiResponse(responseCode = "401",description = "Invalid credentials"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> deleteOne(@PathVariable UUID id,
                                                             Principal principal) {
        return ResponseEntity.ok(accountService.deleteById(id, principal));
    }


    @Operation(summary = "Multi delete",description = "Multi delete accounts through ids by admins")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Data deleted successfully!"),
            @ApiResponse(responseCode = "404",description = "Data not found!"),
            @ApiResponse(responseCode = "406",description = "Data not acceptable"),
            @ApiResponse(responseCode = "500",description = "Internal server error"),
            @ApiResponse(responseCode = "401",description = "Invalid credentials"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @DeleteMapping("/multi-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> multiDelete(@RequestBody List<String> ids, Principal principal) {
        return ResponseEntity.ok(accountService.multiDeleteAccount(ids, principal));
    }


    @Operation(summary = "Get all account",description = "Get all default accounts or get all accounts sort by type by admins")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Get data successfully!"),
            @ApiResponse(responseCode = "404",description = "Data not found!"),
            @ApiResponse(responseCode = "406",description = "Data not acceptable"),
            @ApiResponse(responseCode = "500",description = "Internal server error"),
            @ApiResponse(responseCode = "401",description = "Invalid credentials"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AccountResponse>> getAllAcc(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(required = false) String type){
        Pageable pageable = PageRequest.of(page,size);
        return ResponseEntity.ok(accountService.getAllAccount(pageable,type));
    }



    @Operation(summary = "Get user account",description = "Get principal user's account or get account by userId")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Get data successfully!"),
            @ApiResponse(responseCode = "404",description = "Data not found!"),
            @ApiResponse(responseCode = "406",description = "Data not acceptable"),
            @ApiResponse(responseCode = "500",description = "Internal server error"),
            @ApiResponse(responseCode = "401",description = "Invalid credentials"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @GetMapping("/get-user-account")
    public ResponseEntity<Page<AccountResponse>> getUSerAccount(Principal principal,
                                                                @RequestParam(required = false) UUID userId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        return ResponseEntity.ok(accountService.getUserAccount(principal, userId, pageable));
    }

}