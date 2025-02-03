package uz.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.com.model.entity.AccountsEntity;

import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountsEntity, UUID> {

    @Query("select a from accounts as a where a.isDeleted=false and a.id=?1")
    AccountsEntity findAccountsEntityByIdAndDeletedFalse(UUID id);
}
