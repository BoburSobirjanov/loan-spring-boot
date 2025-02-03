package uz.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.com.model.entity.AuditLogsEntity;

import java.util.UUID;

@Repository
public interface AuditLogsRepository extends JpaRepository<AuditLogsEntity, UUID> {


}
