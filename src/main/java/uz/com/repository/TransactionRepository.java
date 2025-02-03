package uz.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.com.model.entity.TransactionEntity;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query("select t from transactions as t where t.isDeleted=false and t.id=?1")
    TransactionEntity findTransactionEntityByIdAndDeletedFalse(UUID id);
}
