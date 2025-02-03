package uz.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.com.model.entity.UserEntity;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Query("select u from users as u where u.isDeleted=false and u.email=:email")
    UserEntity findUserEntityByEmailAndDeletedFalse(String email);

    @Query("select u from users as u where u.isDeleted=false and u.id=:id")
    UserEntity findUserEntityByIdAndDeletedFalse(UUID id);

    @Query("select u from users as u where u.isDeleted=false and u.phone=:phone")
    UserEntity findUserEntityByPhone(String phone);
}
