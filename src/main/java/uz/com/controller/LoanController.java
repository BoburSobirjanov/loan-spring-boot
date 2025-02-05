package uz.com.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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


    @Operation(summary = "Save loans",description = "Save loans by managers")
    @PostMapping("/save")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<GeneralResponse<LoanResponse>> save(@RequestBody LoanCreateRequest request,
                                                              Principal principal) {
        return ResponseEntity.ok(loanService.save(request, principal));
    }



    @Operation(summary = "Get loan", description = "Get loan through id by users")
    @GetMapping("{id}")
    public ResponseEntity<GeneralResponse<LoanResponse>> getById(@PathVariable UUID id){
        return ResponseEntity.ok(loanService.getById(id));
    }



    @Operation(summary = "Delete loan",description = "Delete loan through id by admins")
    @DeleteMapping("{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> deleteOne(@PathVariable UUID id,
                                                             Principal principal){
        return ResponseEntity.ok(loanService.deleteOne(id, principal));
    }



    @Operation(summary = "Change status", description = "Change loan status by admins or managers")
    @PutMapping("/{id}/change-status")
    @PreAuthorize("hasRole('MANAGER' OR hasRole('ADMIN'))")
    public ResponseEntity<GeneralResponse<LoanResponse>> changeStatus(@RequestParam String status,
                                                                      @PathVariable UUID id,
                                                                      Principal principal){
        return ResponseEntity.ok(loanService.changeLoanStatus(id, principal, status));
    }



    @Operation(summary = "Multi delete",description = "Multi delete loans through id by admins")
    @DeleteMapping("/multi-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> multiDeleteLoan(@RequestBody List<String> ids, Principal principal){
        return ResponseEntity.ok(loanService.multiDeleteLoan(ids, principal));
    }



    @Operation(summary = "Get all loans",description = "Get all default loans or get all sort by status by admins")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<LoanResponse>> getAllLoans(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(required = false) String status){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(loanService.getAllLoans(pageable,status));
    }



    @Operation(summary = "Get my loans",description = "Get principal user's loans by users")
    @GetMapping("/get-my-loans")
    public ResponseEntity<Page<LoanResponse>> getMyLoans(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         Principal principal){
        Pageable pageable = PageRequest.of(page,size);
        return ResponseEntity.ok(loanService.getMyLoans(pageable,principal));
    }
}
