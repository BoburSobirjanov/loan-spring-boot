package uz.com.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.com.model.entity.AuditLogsEntity;

import java.util.UUID;

@Repository
public interface AuditLogsRepository extends JpaRepository<AuditLogsEntity, UUID> {

    Page<AuditLogsEntity> findAuditLogsEntityByHttpMethod(String method, Pageable pageable);

    Page<AuditLogsEntity> findAuditLogsEntityByUrl(String url, Pageable pageable);


}
