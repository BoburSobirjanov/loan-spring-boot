package uz.com.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.com.model.dto.request.AccountCreateRequest;
import uz.com.model.dto.response.AccountResponse;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.dto.response.PageResponse;
import uz.com.service.AccountService;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Account controller APIs for managing", description = "Account Controller")
@RequestMapping("/brb/accounts")
@CrossOrigin
public class AccountController {

    private final AccountService accountService;


    @Operation(summary = "Save account", description = "Save account by managers for clients")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Data created successfully!"),
            @ApiResponse(responseCode = "404", description = "Data not found!"),
            @ApiResponse(responseCode = "406", description = "Data not acceptable"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/save")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<GeneralResponse<AccountResponse>> save(@RequestBody AccountCreateRequest request,
                                                                 Principal principal) {
        return ResponseEntity.ok(accountService.saveAccountForClients(request, principal));
    }


    @Operation(summary = "Get account", description = "Get account through id by users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get data successfully!"),
            @ApiResponse(responseCode = "404", description = "Data not found!"),
            @ApiResponse(responseCode = "406", description = "Data not acceptable"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or hasRole('CLIENTS')")
    public ResponseEntity<GeneralResponse<AccountResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }


    @Operation(summary = "Delete account", description = "Delete account through id by admins")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Data deleted successfully!"),
            @ApiResponse(responseCode = "404", description = "Data not found!"),
            @ApiResponse(responseCode = "406", description = "Data not acceptable"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> deleteOne(@PathVariable UUID id,
                                                             Principal principal) {
        return ResponseEntity.ok(accountService.deleteAccountById(id, principal));
    }


    @Operation(summary = "Multi delete", description = "Multi delete accounts through ids by admins")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Data deleted successfully!"),
            @ApiResponse(responseCode = "404", description = "Data not found!"),
            @ApiResponse(responseCode = "406", description = "Data not acceptable"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @DeleteMapping("/multi-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> multiDelete(@RequestBody List<String> ids, Principal principal) {
        return ResponseEntity.ok(accountService.multiDeleteAccount(ids, principal));
    }


    @Operation(summary = "Get all account", description = "Get all default accounts or get all accounts sort by type by admins")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get data successfully!"),
            @ApiResponse(responseCode = "404", description = "Data not found!"),
            @ApiResponse(responseCode = "406", description = "Data not acceptable"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<PageResponse<AccountResponse>>> getAllAcc(@RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size,
                                                                                    @RequestParam(required = false) String type) {
        if (page != 0) page = page - 1;
        return ResponseEntity.ok(accountService.getAllAccount(page, size, type));
    }


    @Operation(summary = "Get user account", description = "Get principal user's account or get account by userId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get data successfully!"),
            @ApiResponse(responseCode = "404", description = "Data not found!"),
            @ApiResponse(responseCode = "406", description = "Data not acceptable"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/get-user-account")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<GeneralResponse<PageResponse<AccountResponse>>> getUserAccount(Principal principal,
                                                                                         @RequestParam(required = false) UUID userId,
                                                                                         @RequestParam(defaultValue = "0") int page,
                                                                                         @RequestParam(defaultValue = "10") int size) {
        if (page != 0) page = page - 1;
        return ResponseEntity.ok(accountService.getUserAccount(principal, userId, page, size));
    }

    @Operation(summary = "Fill balance", description = "Fill account balance")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Put data successfully!"),
            @ApiResponse(responseCode = "404", description = "Data not found!"),
            @ApiResponse(responseCode = "406", description = "Data not acceptable"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PutMapping("/fill-balance/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT','MANAGER')")
    public ResponseEntity<GeneralResponse<AccountResponse>> fillBalance(@PathVariable UUID id,
                                                                        @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(accountService.fillAccountBalance(id, amount));
    }
}