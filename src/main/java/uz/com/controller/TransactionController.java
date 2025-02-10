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
import uz.com.model.dto.request.TransactionCreateRequest;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.dto.response.TransactionResponse;
import uz.com.service.TransactionService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Transaction controller APIs for managing transactions", description = "Transaction Controller")
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;


    @Operation(summary = "Save transaction", description = "Save transaction by users")
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "Data created successfully!"),
            @ApiResponse(responseCode = "404",description = "Data not found!"),
            @ApiResponse(responseCode = "406",description = "Data not acceptable"),
            @ApiResponse(responseCode = "500",description = "Internal server error"),
            @ApiResponse(responseCode = "401",description = "Invalid credentials"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @PostMapping("/save")
    public ResponseEntity<GeneralResponse<TransactionResponse>> save(@RequestBody TransactionCreateRequest request,
                                                                     Principal principal) {
        return ResponseEntity.ok(transactionService.save(request, principal));
    }


    @Operation(summary = "Get by id", description = "Get transaction through id by users")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Get data successfully!"),
            @ApiResponse(responseCode = "404",description = "Data not found!"),
            @ApiResponse(responseCode = "500",description = "Internal server error"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @GetMapping("{id}")
    public ResponseEntity<GeneralResponse<TransactionResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getById(id));
    }


    @Operation(summary = "Delete transaction",description = "Delete transaction through id by admins")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Data deleted successfully!"),
            @ApiResponse(responseCode = "404",description = "Data not found!"),
            @ApiResponse(responseCode = "406",description = "Data not acceptable"),
            @ApiResponse(responseCode = "500",description = "Internal server error"),
            @ApiResponse(responseCode = "401",description = "Invalid credentials"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<String>> deleteOne(@PathVariable UUID id, Principal principal) {
        return ResponseEntity.ok(transactionService.delete(id, principal));
    }


    @Operation(summary = "Multi delete",description = "Multi delete transactions through id by admins")
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
    public ResponseEntity<GeneralResponse<String>> multiDelete(@RequestBody List<String> ids,
                                                               Principal principal) {
        return ResponseEntity.ok(transactionService.multiDeleteTransaction(ids, principal));
    }


    @Operation(summary = "Get all", description = "Get all default transactions or get all sort by account and type")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Get data successfully!"),
            @ApiResponse(responseCode = "404",description = "Data not found!"),
            @ApiResponse(responseCode = "406",description = "Data not acceptable"),
            @ApiResponse(responseCode = "500",description = "Internal server error"),
            @ApiResponse(responseCode = "401",description = "Invalid credentials"),
            @ApiResponse(responseCode = "400",description = "Bad request")
    })
    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(required = false) UUID accountId,
                                                            @RequestParam(required = false) String type){
        Pageable pageable = PageRequest.of(page,size);
        return ResponseEntity.ok(transactionService.getAllTransaction(pageable,accountId,type));
    }
}
