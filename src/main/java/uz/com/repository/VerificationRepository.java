package uz.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.com.model.entity.Verification;

import java.util.UUID;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, UUID> {

    @Query("select u from verifications as u where u.to_to=?1")
    Verification findVerificationByTo(UUID to);

    @Query("select u from verifications as u where u.to_to=?1 and u.code=?2 ")
    Verification findByUserEmailAndCode(UUID id, Integer code);
}
