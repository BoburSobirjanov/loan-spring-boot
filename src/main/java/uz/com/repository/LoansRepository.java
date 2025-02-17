package uz.com.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.com.model.entity.LoansEntity;
import uz.com.model.entity.UserEntity;
import uz.com.model.enums.LoanStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoansRepository extends JpaRepository<LoansEntity, UUID> {

    @Query("select l from loans as l where l.isDeleted=false and l.id=?1")
    LoansEntity findLoansEntityByIdAndDeletedFalse(UUID id);

    @Query("select l from loans as l where l.isDeleted=false")
    Page<LoansEntity> findAllLoansEntity(Pageable pageable);

    @Query("select l from loans as l where l.isDeleted=false and l.user=?1")
    Page<LoansEntity> findAllByUserAndDeletedIsFalse(UserEntity user, Pageable pageable);

    @Query("select l from loans as l where l.isDeleted=false and l.status=?1")
    List<LoansEntity> findLoansEntityByStatusAndDeletedIsFalse(LoanStatus status);

    @Query("select l from loans as l where l.isDeleted=false and l.user=?1 and (l.status='ACTIVE' OR l.status='FREEZE')")
    List<LoansEntity> findAllLoanEntityByUserAndStatus(UserEntity user);

    @Query("select l from loans as l where l.isDeleted=false")
    List<LoansEntity> findAllLoanEntityList();
}
