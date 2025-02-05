package uz.com.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.com.model.entity.AccountsEntity;
import uz.com.model.entity.UserEntity;
import uz.com.model.enums.AccountType;

import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountsEntity, UUID> {

    @Query("select a from accounts as a where a.isDeleted=false and a.id=?1")
    AccountsEntity findAccountsEntityByIdAndDeletedFalse(UUID id);

    @Query("select a from accounts as a where a.isDeleted=false")
    Page<AccountsEntity> findAllAccountEntityAndDeletedFalse(Pageable pageable);

    @Query("select a from accounts as a where a.isDeleted=false and a.type=?1")
    Page<AccountsEntity> findAllByTypeAndDeletedIsFalse(AccountType type, Pageable pageable);

    @Query("select a from accounts as a where a.isDeleted=false and a.user=?1")
    AccountsEntity findAccountsEntityByUser(UserEntity user);
}
