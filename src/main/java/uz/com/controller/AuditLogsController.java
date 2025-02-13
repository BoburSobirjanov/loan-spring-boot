package uz.com.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.dto.response.PageResponse;
import uz.com.model.entity.AuditLogsEntity;
import uz.com.service.AuditLogService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "AuditLogs controller APIs", description = "AuditLogs controller APIs for managing by ADMINs ")
@RequestMapping("/brb/audit-logs")
@CrossOrigin
public class AuditLogsController {


    private final AuditLogService auditLogService;


    @Operation(summary = "Get audit logs by HTTP Method", description = "Get all audit logs through httpMethod by ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get data successfully"),
            @ApiResponse(responseCode = "404", description = "Data not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/get-by-http-method")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<PageResponse<AuditLogsEntity>>> getByHttpMethod(@RequestParam String method,
                                                                                          @RequestParam(defaultValue = "0") int page,
                                                                                          @RequestParam(defaultValue = "10") int size) {
        if (page != 0) page = page - 1;
        return ResponseEntity.ok(auditLogService.getAuditByHttpMethod(method, page, size));
    }


    @Operation(summary = "Get audit logs by URI", description = "Get all audit logs through URI by ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get data successfully"),
            @ApiResponse(responseCode = "404", description = "Data not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/get-by-url")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<PageResponse<AuditLogsEntity>>> getByUrl(@RequestParam String url,
                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "10") int size) {
        if (page != 0) page = page - 1;
        return ResponseEntity.ok(auditLogService.getAuditsByUrl(url, page, size));
    }


    @Operation(summary = "Get audit logs by ID", description = "Get audit log through ID by ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get data successfully"),
            @ApiResponse(responseCode = "404", description = "Data not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<AuditLogsEntity>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(auditLogService.getAuditLogsById(id));
    }


    @Operation(summary = "Get audit all logs", description = "Get all audit logs by ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get data successfully"),
            @ApiResponse(responseCode = "404", description = "Data not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<PageResponse<AuditLogsEntity>>> getAll(@RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "10") int size) {
        if (page != 0) page = page - 1;
        return ResponseEntity.ok(auditLogService.getAllAudits(page, size));
    }
}
