package uz.com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.com.exception.DataNotFoundException;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.dto.response.PageResponse;
import uz.com.model.entity.AuditLogsEntity;
import uz.com.repository.AuditLogsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogsRepository auditLogsRepository;


    public GeneralResponse<PageResponse<AuditLogsEntity>> getAuditByHttpMethod(String method, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<AuditLogsEntity> auditLogsEntities = auditLogsRepository.findAuditLogsEntityByHttpMethod(method, pageable).getContent();
        if (auditLogsEntities == null) throw new DataNotFoundException("AuditLogs not found!");
        int auditCount = auditLogsEntities.size();
        int pageCount = auditCount / size;
        if (auditCount % size != 0) pageCount++;
        List<AuditLogsEntity> auditLogsEntityList = new ArrayList<>(auditLogsEntities);
        return GeneralResponse.ok("This is audits", PageResponse.ok(pageCount, auditLogsEntityList));
    }


    public GeneralResponse<PageResponse<AuditLogsEntity>> getAuditsByUrl(String url, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<AuditLogsEntity> auditLogsEntities = auditLogsRepository.findAuditLogsEntityByUrl(url, pageable).getContent();
        if (auditLogsEntities == null) throw new DataNotFoundException("AuditLogs not found!");
        int auditCount = auditLogsEntities.size();
        int pageCount = auditCount / size;
        if (auditCount % size != 0) pageCount++;
        List<AuditLogsEntity> auditLogsEntityList = new ArrayList<>(auditLogsEntities);
        return GeneralResponse.ok("This is audits", PageResponse.ok(pageCount, auditLogsEntityList));
    }


    public GeneralResponse<AuditLogsEntity> getById(UUID id) {
        Optional<AuditLogsEntity> auditLogs = auditLogsRepository.findById(id);
        if (auditLogs.isEmpty()) {
            throw new DataNotFoundException("AuditLos not found!");
        }

        return GeneralResponse.ok("This is AuditLog!", auditLogs.get());
    }


    public GeneralResponse<PageResponse<AuditLogsEntity>> getAllAudits(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<AuditLogsEntity> auditLogsEntities = auditLogsRepository.findAll(pageable).getContent();
        if (auditLogsEntities == null) throw new DataNotFoundException("AuditLogs not found!");
        int auditCount = auditLogsEntities.size();
        int pageCount = auditCount / size;
        if (auditCount % size != 0) pageCount++;
        List<AuditLogsEntity> auditLogsEntityList = new ArrayList<>(auditLogsEntities);
        return GeneralResponse.ok("This is audits", PageResponse.ok(pageCount, auditLogsEntityList));
    }

}
