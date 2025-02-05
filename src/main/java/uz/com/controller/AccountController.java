package uz.com.controller;

import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/accounts")
public class AccountController {


    private final AccountService accountService;


    @Operation(summary = "Save account",description = "Save account by managers for clients")
    @PostMapping("/save")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<GeneralResponse<AccountResponse>> save(@RequestBody AccountCreateRequest request,
                                                                 Principal principal) {
        return ResponseEntity.ok(accountService.save(request, principal));
    }


    @Operation(summary = "Get account",description = "Get account through id by users")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or hasRole('CLIENTS')")
    public ResponseEntity<GeneralResponse<AccountResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getById(id));
    }


    @Operation(summary = "Delete account", description = "Delete account through id by admins")
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> deleteOne(@PathVariable UUID id,
                                                             Principal principal) {
        return ResponseEntity.ok(accountService.deleteById(id, principal));
    }


    @Operation(summary = "Multi delete",description = "Multi delete accounts through ids by admins")
    @DeleteMapping("/multi-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> multiDelete(@RequestBody List<String> ids, Principal principal) {
        return ResponseEntity.ok(accountService.multiDeleteAccount(ids, principal));
    }


    @Operation(summary = "Get all account",description = "Get all default accounts or get all accounts sort by type by admins")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AccountResponse>> getAllAcc(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(required = false) String type){
        Pageable pageable = PageRequest.of(page,size);
        return ResponseEntity.ok(accountService.getAllAccount(pageable,type));
    }



    @Operation(summary = "Get user account",description = "Get principal user's account or get account by userId")
    @GetMapping("/get-user-account")
    public ResponseEntity<GeneralResponse<AccountResponse>> getUSerAccount(Principal principal,
                                                                           @RequestParam(required = false) UUID userId){
        return ResponseEntity.ok(accountService.getUserAccount(principal, userId));
    }


}
