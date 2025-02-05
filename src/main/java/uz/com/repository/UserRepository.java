package uz.com.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.com.model.entity.UserEntity;
import uz.com.model.enums.UserRole;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Query("select u from users as u where u.isDeleted=false and u.email=:email")
    UserEntity findUserEntityByEmailAndDeletedFalse(String email);

    @Query("select u from users as u where u.isDeleted=false and u.id=:id")
    UserEntity findUserEntityByIdAndDeletedFalse(UUID id);

    @Query("select u from users as u where u.isDeleted=false and u.phone=:phone")
    UserEntity findUserEntityByPhone(String phone);

    @Query("select u from users as u where u.isDeleted=false")
    Page<UserEntity> findAllByDeletedFalse(Pageable pageable);

    @Query("SELECT u FROM users u WHERE u.isDeleted = false AND :role MEMBER OF u.role")
    Page<UserEntity> findAllByRole(UserRole role, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM users u WHERE u.email = :email AND u.phone = :phone AND u.isDeleted = false")
    Boolean existsUserEntityByEmailAndPhoneAndDeletedIsFalse(String email, String phone);

}
