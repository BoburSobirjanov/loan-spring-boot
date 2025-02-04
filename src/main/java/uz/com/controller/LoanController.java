package uz.com.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.com.model.dto.request.LoanCreateRequest;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.dto.response.LoanResponse;
import uz.com.service.LoanService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/loans")
public class LoanController {

    private final LoanService loanService;


    @PostMapping("/save")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<GeneralResponse<LoanResponse>> save(@RequestBody LoanCreateRequest request,
                                                              Principal principal) {
        return ResponseEntity.ok(loanService.save(request, principal));
    }


    @GetMapping("{id}")
    public ResponseEntity<GeneralResponse<LoanResponse>> getById(@PathVariable UUID id){
        return ResponseEntity.ok(loanService.getById(id));
    }


    @DeleteMapping("{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> deleteOne(@PathVariable UUID id,
                                                             Principal principal){
        return ResponseEntity.ok(loanService.deleteOne(id, principal));
    }


    @PutMapping("/{id}/change-status")
    @PreAuthorize("hasRole('MANAGER' OR hasRole('ADMIN'))")
    public ResponseEntity<GeneralResponse<LoanResponse>> changeStatus(@RequestParam String status,
                                                                      @PathVariable UUID id,
                                                                      Principal principal){
        return ResponseEntity.ok(loanService.changeLoanStatus(id, principal, status));
    }


    @DeleteMapping("/multi-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> multiDeleteLoan(@RequestBody List<String> ids, Principal principal){
        return ResponseEntity.ok(loanService.multiDeleteLoan(ids, principal));
    }
}
