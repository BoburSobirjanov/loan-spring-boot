package uz.com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.com.exception.DataNotFoundException;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.entity.AuditLogsEntity;
import uz.com.repository.AuditLogsRepository;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogsRepository auditLogsRepository;



    public GeneralResponse<Page<AuditLogsEntity>> getAuditByHttpMethod(String method, Pageable pageable){
        Page<AuditLogsEntity> auditLogsEntities = auditLogsRepository.findAuditLogsEntityByHttpMethod(method,pageable);
        if (auditLogsEntities==null){
            throw new DataNotFoundException("AuditLogs not found!");
        }

        return GeneralResponse.ok("This is audits",auditLogsEntities);
    }





    public GeneralResponse<Page<AuditLogsEntity>> getAuditsByUrl(String url, Pageable pageable){
        Page<AuditLogsEntity> auditLogsEntities = auditLogsRepository.findAuditLogsEntityByUrl(url,pageable);
        if (auditLogsEntities==null){
            throw new DataNotFoundException("AuditLogs not found!");
        }

        return GeneralResponse.ok("This is audits",auditLogsEntities);
    }


    public GeneralResponse<AuditLogsEntity> getById(UUID id){
        Optional<AuditLogsEntity> auditLogs = auditLogsRepository.findById(id);
        if (auditLogs.isEmpty()){
            throw new DataNotFoundException("AuditLos not found!");
        }

        return GeneralResponse.ok("This is AuditLog!",auditLogs.get());
    }


    public GeneralResponse<Page<AuditLogsEntity>> getAllAudits(Pageable pageable){
        Page<AuditLogsEntity> auditLogsEntities = auditLogsRepository.findAll(pageable);
        if (auditLogsEntities==null){
            throw new DataNotFoundException("AuditLogs not found!");
        }

        return GeneralResponse.ok("This is AuditLogs", auditLogsEntities);
    }

}
