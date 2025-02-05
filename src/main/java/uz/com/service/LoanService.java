package uz.com.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.com.exception.DataNotAcceptableException;
import uz.com.exception.DataNotFoundException;
import uz.com.model.dto.request.LoanCreateRequest;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.dto.response.LoanResponse;
import uz.com.model.dto.response.UserResponse;
import uz.com.model.entity.LoansEntity;
import uz.com.model.entity.UserEntity;
import uz.com.model.enums.LoanStatus;
import uz.com.model.enums.UserRole;
import uz.com.repository.LoansRepository;
import uz.com.repository.UserRepository;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoansRepository loansRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    public GeneralResponse<LoanResponse> save(LoanCreateRequest request, Principal principal) {
        UserEntity principalUser = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        LoansEntity loans = modelMapper.map(request, LoansEntity.class);
        UserEntity user = userRepository.findUserEntityByIdAndDeletedFalse(UUID.fromString(request.getUserId()));
        if (user == null) {
            throw new DataNotFoundException("User not found!");
        }
        if (!user.getRole().contains(UserRole.CLIENT)) {
            throw new DataNotAcceptableException("Invalid user! User is not client!");
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new DataNotAcceptableException("Invalid amount!");
        }
        loans.setAmount(request.getAmount());
        loans.setUser(user);
        loans.setCreatedBy(principalUser.getId());
        loans.setStatus(LoanStatus.ACTIVE);
        if (request.getInterestRate() < 0 || request.getInterestRate() > 100) {
            throw new DataNotAcceptableException("Invalid interest rate!");
        }
        loans.setInterestRate(request.getInterestRate());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate localDate = LocalDate.parse(request.getDueDate(), dateTimeFormatter);
        if (localDate.isBefore(LocalDate.now())) {
            throw new DataNotAcceptableException("Invalid due date time!");
        }
        loans.setDueDate(localDate);
        LoansEntity save = loansRepository.save(loans);
        LoanResponse loanResponse = modelMapper.map(save, LoanResponse.class);

        return GeneralResponse.ok("Loan created!", loanResponse);
    }



    public GeneralResponse<LoanResponse> getById(UUID id){
        LoansEntity loans = loansRepository.findLoansEntityByIdAndDeletedFalse(id);
        if (loans==null){
            throw new DataNotFoundException("Loan not found!");
        }
        LoanResponse loanResponse = modelMapper.map(loans, LoanResponse.class);

        return GeneralResponse.ok("This is loan!",loanResponse);
    }


    public GeneralResponse<String> deleteOne(UUID id, Principal principal){
        LoansEntity loans = loansRepository.findLoansEntityByIdAndDeletedFalse(id);
        UserEntity user = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        if (loans==null){
            throw new DataNotFoundException("Loan not found!");
        }
        if (loans.getStatus().equals(LoanStatus.ACTIVE) || loans.getStatus().equals(LoanStatus.FREEZE)){
            throw new DataNotAcceptableException("Can not delete loan! Because loan is ACTIVE now!");
        }
        loans.setDeleted(true);
        loans.setDeletedAt(LocalDateTime.now());
        loans.setDeletedBy(user.getId());
        loansRepository.save(loans);

        return GeneralResponse.ok("Loan deleted!","DELETED");
    }



    public GeneralResponse<LoanResponse> changeLoanStatus(UUID id, Principal principal, String status){
        LoansEntity loans = loansRepository.findLoansEntityByIdAndDeletedFalse(id);
        UserEntity principalUser = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        if (loans==null){
            throw new DataNotFoundException("Loan not found!");
        }
        loans.setChangeStatusBy(principalUser.getId());
        try {
            loans.setStatus(LoanStatus.valueOf(status.toUpperCase()));
        }catch (Exception e){
            throw new DataNotAcceptableException("Invalid status!");
        }

        LoanResponse response = modelMapper.map(loans, LoanResponse.class);
        return GeneralResponse.ok("Status changed!", response);
    }



    public GeneralResponse<String> multiDeleteLoan(List<String> ids, Principal principal){
        UserEntity user = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        for (String id:ids) {
            LoansEntity loans = loansRepository.findLoansEntityByIdAndDeletedFalse(UUID.fromString(id));
            if (loans==null){
                throw new DataNotFoundException("Loan not found!");
            }
            if (loans.getStatus().equals(LoanStatus.ACTIVE) || loans.getStatus().equals(LoanStatus.FREEZE)){
                throw new DataNotAcceptableException("Complete loan before!");
            }
            loans.setDeleted(true);
            loans.setDeletedBy(user.getId());
            loans.setDeletedAt(LocalDateTime.now());
            loansRepository.save(loans);
        }
        return GeneralResponse.ok("Loans deleted!", "DELETED");
    }


    public Page<LoanResponse> getAllLoans(Pageable pageable,String status){
        if (status==null){
        Page<LoansEntity> loansEntities = loansRepository.findAllLoansEntity(pageable);
        if (loansEntities==null) throw new DataNotFoundException("Loans not found!");
        return loansEntities.map(loansEntity -> new LoanResponse(loansEntity.getId(),loansEntity.getAmount(), loansEntity.getInterestRate(),
                loansEntity.getStatus(),loansEntity.getDueDate(),modelMapper.map(loansEntity.getUser(), UserResponse.class)));
        }
        LoanStatus loanStatus = LoanStatus.valueOf(status.toUpperCase());
        Page<LoansEntity> loansEntities = loansRepository.findLoansEntityByStatusAndDeletedIsFalse(loanStatus,pageable);
        if (loansEntities==null) throw new DataNotFoundException("Loans not found!");
        return loansEntities.map(loansEntity -> new LoanResponse(loansEntity.getId(),loansEntity.getAmount(), loansEntity.getInterestRate(),
                loansEntity.getStatus(),loansEntity.getDueDate(),modelMapper.map(loansEntity.getUser(), UserResponse.class)));
    }




    public Page<LoanResponse> getMyLoans(Pageable pageable , Principal principal){
        UserEntity user = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        Page<LoansEntity> loansEntities = loansRepository.findAllByUserAndDeletedIsFalse(user,pageable);
        if (loansEntities==null){
            throw new DataNotFoundException("Loans not found!");
        }
        return loansEntities.map(loansEntity -> new LoanResponse(loansEntity.getId(),loansEntity.getAmount(), loansEntity.getInterestRate(),
                loansEntity.getStatus(),loansEntity.getDueDate(),modelMapper.map(user, UserResponse.class)));
    }
}
