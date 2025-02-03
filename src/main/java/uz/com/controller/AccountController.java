package uz.com.controller;

import lombok.RequiredArgsConstructor;
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


    @PostMapping("/save")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<GeneralResponse<AccountResponse>> save(@RequestBody AccountCreateRequest request,
                                                                 Principal principal){
        return ResponseEntity.ok(accountService.save(request, principal));
    }



    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or hasRole('CLIENTS')")
    public ResponseEntity<GeneralResponse<AccountResponse>> getById(@PathVariable UUID id){
        return ResponseEntity.ok(accountService.getById(id));
    }



    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> deleteOne(@PathVariable UUID id,
                                                             Principal principal){
        return ResponseEntity.ok(accountService.deleteById(id, principal));
    }



    @DeleteMapping("/multi-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> multiDelete(@RequestBody List<String> ids, Principal principal){
        return ResponseEntity.ok(accountService.multiDeleteAccount(ids, principal));
    }

}
