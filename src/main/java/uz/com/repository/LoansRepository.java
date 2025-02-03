package uz.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.com.model.entity.LoansEntity;

import java.util.UUID;

@Repository
public interface LoansRepository extends JpaRepository<LoansEntity, UUID> {
}
