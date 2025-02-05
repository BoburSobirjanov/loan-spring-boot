package uz.com.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.com.model.entity.AccountsEntity;
import uz.com.model.entity.TransactionEntity;
import uz.com.model.enums.TransactionType;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query("select t from transactions as t where t.isDeleted=false and t.id=?1")
    TransactionEntity findTransactionEntityByIdAndDeletedFalse(UUID id);

    @Query("select t from transactions as t where t.isDeleted=false")
    Page<TransactionEntity> findAllByDeletedIsFalse(Pageable pageable);

    @Query("select t from transactions as t where t.isDeleted=false and t.account=?1")
    Page<TransactionEntity> findAllByAccountAndDeletedIsFalse(AccountsEntity accounts, Pageable pageable);

    @Query("select t from transactions as t where t.isDeleted=false and t.type=?1")
    Page<TransactionEntity> findAllByTypeAndDeletedIsFalse(Pageable pageable, TransactionType type);
}
