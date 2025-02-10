package uz.com.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.entity.AuditLogsEntity;
import uz.com.service.AuditLogService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/audit-logs")
public class AuditLogsController {

    private final AuditLogService auditLogService;


    @Operation(summary = "Get audit logs by HTTP Method",description = "Get all audit logs through httpMethod by ADMIN")
    @GetMapping("/get-by-http-method")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<Page<AuditLogsEntity>>> getByHttpMethod(@RequestParam String method,
                                                                                  @RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        return ResponseEntity.ok(auditLogService.getAuditByHttpMethod(method,pageable));
    }




    @Operation(summary = "Get audit logs by URI",description = "Get all audit logs through URI by ADMIN")
    @GetMapping("/get-by-url")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<Page<AuditLogsEntity>>> getByUrl(@RequestParam String url,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        return ResponseEntity.ok(auditLogService.getAuditsByUrl(url,pageable));
    }


    @Operation(summary = "Get audit logs by ID",description = "Get audit log through ID by ADMIN")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<AuditLogsEntity>> getById(@PathVariable UUID id){
        return ResponseEntity.ok(auditLogService.getById(id));
    }



    @Operation(summary = "Get audit all logs",description = "Get all audit logs by ADMIN")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse<Page<AuditLogsEntity>>> getAll( @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        return ResponseEntity.ok(auditLogService.getAllAudits(pageable));
    }
}
