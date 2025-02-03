package uz.com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uz.com.exception.DataNotAcceptableException;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.entity.UserEntity;
import uz.com.model.entity.Verification;
import uz.com.repository.UserRepository;
import uz.com.repository.VerificationRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailSendingService {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;

    @Value("${spring.mail.username}")
    private String sender;
    Random random = new Random();

    public GeneralResponse<String> sendMessage(String email){

        int message = 10000000 + random.nextInt(90000000);
        UserEntity userEntity = userRepository.findUserEntityByEmailAndDeletedFalse(email);
        if (userEntity==null){
            throw new DataNotAcceptableException("Wrong! Did not sign up use this email!");
        }
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setText("Do not give to others this code. Your verification code: " + message);
        Verification verificationEntity = verificationRepository.findVerificationByTo(userEntity.getId());
        if (verificationEntity==null){
            Verification verification = new Verification();
            verification.setTo_to(userEntity.getId());
            verification.setCode(message);
            verificationRepository.save(verification);
            return GeneralResponse.ok("Verification code sent","SENT");
        }
        verificationEntity.setCode(message);
        verificationEntity.setCreatedAt(LocalDateTime.now());
        verificationRepository.save(verificationEntity);

        javaMailSender.send(simpleMailMessage);
        return GeneralResponse.ok("verification code sent","SENT");
    }
}
