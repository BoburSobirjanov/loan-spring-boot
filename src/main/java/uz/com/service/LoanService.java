package uz.com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.com.exception.DataNotAcceptableException;
import uz.com.exception.DataNotFoundException;
import uz.com.mapper.LoanMapper;
import uz.com.model.dto.request.LoanCreateRequest;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.dto.response.LoanResponse;
import uz.com.model.dto.response.PageResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoansRepository loansRepository;
    private final UserRepository userRepository;
    private final LoanMapper loanMapper;


    public GeneralResponse<LoanResponse> save(LoanCreateRequest request, Principal principal) {
        UserEntity principalUser = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        LoansEntity loans = loanMapper.toEntity(request);
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
        LocalDate localDate = LocalDate.now().plusMonths(request.getMonths());
        if (localDate.isBefore(LocalDate.now())) {
            throw new DataNotAcceptableException("Invalid due date time!");
        }
        loans.setDueDate(localDate);

        BigDecimal allMustBePay = request.getAmount().add((request.getAmount().multiply(BigDecimal.valueOf(request.getInterestRate()/100)))
                .multiply(BigDecimal.valueOf(request.getMonths()/12)));

        BigDecimal payPerMonth = BigDecimal.valueOf(allMustBePay.intValue()/(request.getMonths()));
        loans.setPayPerMonth(payPerMonth);
        loans.setMustBePay(allMustBePay);
        loans.setPaidEver(BigDecimal.ZERO);
        LoansEntity save = loansRepository.save(loans);
        LoanResponse loanResponse = loanMapper.toResponse(save);

        return GeneralResponse.ok("Loan created!", loanResponse);
    }


    public GeneralResponse<LoanResponse> getById(UUID id) {
        LoansEntity loans = loansRepository.findLoansEntityByIdAndDeletedFalse(id);
        if (loans == null) {
            throw new DataNotFoundException("Loan not found!");
        }
        LoanResponse loanResponse = loanMapper.toResponse(loans);

        return GeneralResponse.ok("This is loan!", loanResponse);
    }


    public GeneralResponse<LoanResponse> payForLoan(UUID id, BigDecimal amount) {
        LoansEntity loans = loansRepository.findLoansEntityByIdAndDeletedFalse(id);
        if (loans == null) {
            throw new DataNotFoundException("Loan not found!");
        }
        if (loans.getMustBePay().compareTo(amount) < 0 || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DataNotAcceptableException("Invalid amount!");
        }
        loans.setPaidEver(loans.getPaidEver().add(amount));
        loans.setMustBePay(loans.getMustBePay().subtract(amount));
        loansRepository.save(loans);

        LoanResponse loanResponse = loanMapper.toResponse(loans);
        return GeneralResponse.ok("Paid for loan!",loanResponse);

    }


    public GeneralResponse<String> deleteOne(UUID id, Principal principal) {
        LoansEntity loans = loansRepository.findLoansEntityByIdAndDeletedFalse(id);
        UserEntity user = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        if (loans == null) {
            throw new DataNotFoundException("Loan not found!");
        }
        if (loans.getStatus().equals(LoanStatus.ACTIVE) || loans.getStatus().equals(LoanStatus.FREEZE)) {
            throw new DataNotAcceptableException("Can not delete loan! Because loan is not COMPLETED!");
        }
        loans.setDeleted(true);
        loans.setDeletedAt(LocalDateTime.now());
        loans.setDeletedBy(user.getId());
        loansRepository.save(loans);

        return GeneralResponse.ok("Loan deleted!", "DELETED");
    }


    public GeneralResponse<LoanResponse> changeLoanStatus(UUID id, Principal principal, String status) {
        LoansEntity loans = loansRepository.findLoansEntityByIdAndDeletedFalse(id);
        UserEntity principalUser = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        if (loans == null) {
            throw new DataNotFoundException("Loan not found!");
        }
        loans.setChangeStatusBy(principalUser.getId());
        try {
            loans.setStatus(LoanStatus.valueOf(status.toUpperCase()));
        } catch (Exception e) {
            throw new DataNotAcceptableException("Invalid status!");
        }

        LoanResponse response = loanMapper.toResponse(loans);
        return GeneralResponse.ok("Status changed!", response);
    }


    public GeneralResponse<String> multiDeleteLoan(List<String> ids, Principal principal) {
        UserEntity user = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        for (String id : ids) {
            LoansEntity loans = loansRepository.findLoansEntityByIdAndDeletedFalse(UUID.fromString(id));
            if (loans == null) {
                throw new DataNotFoundException("Loan not found!");
            }
            if (loans.getStatus().equals(LoanStatus.ACTIVE) || loans.getStatus().equals(LoanStatus.FREEZE)) {
                throw new DataNotAcceptableException("Can not delete loans! Because loans are not COMPLETED!");
            }
            loans.setDeleted(true);
            loans.setDeletedBy(user.getId());
            loans.setDeletedAt(LocalDateTime.now());
            loansRepository.save(loans);
        }
        return GeneralResponse.ok("Loans deleted!", "DELETED");
    }


    public GeneralResponse<PageResponse<LoanResponse>> getAllLoans(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page,size);
        if (status == null) {
            List<LoansEntity> loansEntities = loansRepository.findAllLoansEntity(pageable).getContent();
            if (loansEntities == null) throw new DataNotFoundException("Loans not found!");
            int loanCount = loansRepository.findAllLoanEntityList().size();
            int pageCount = loanCount/size;
            if (loanCount%size!=0) pageCount++;
            List<LoanResponse> loanResponse = new ArrayList<>();
            for (LoansEntity loans: loansEntities) {
                LoanResponse response = loanMapper.toResponse(loans);
                loanResponse.add(response);
            }
            return GeneralResponse.ok("These are loans", PageResponse.ok(pageCount,loanResponse));
        }
        return getAllLoansByStatus(size, status);
    }

    private GeneralResponse<PageResponse<LoanResponse>> getAllLoansByStatus(int size, String status) {
        LoanStatus loanStatus = LoanStatus.valueOf(status.toUpperCase());
        List<LoansEntity> loansEntities = loansRepository.findLoansEntityByStatusAndDeletedIsFalse(loanStatus);
        if (loansEntities == null) throw new DataNotFoundException("Loans not found!");
        int loanCount = loansEntities.size();
        int pageCount = loanCount/ size;
        if (loanCount% size !=0) pageCount++;
        List<LoanResponse> loanResponse = new ArrayList<>();
        for (LoansEntity loans: loansEntities) {
            LoanResponse response = loanMapper.toResponse(loans);
            loanResponse.add(response);
        }
        return GeneralResponse.ok("These are loans", PageResponse.ok(pageCount, loanResponse));
    }


    public GeneralResponse<PageResponse<LoanResponse>> getMyLoans(int page,int size, Principal principal) {
        Pageable pageable = PageRequest.of(page,size);
        UserEntity user = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        List<LoansEntity> loansEntities = loansRepository.findAllByUserAndDeletedIsFalse(user,pageable).getContent();
        if (loansEntities == null) throw new DataNotFoundException("Loans not found!");
        int loanCount = loansEntities.size();
        int pageCount = loanCount/ size;
        if (loanCount% size !=0) pageCount++;
        List<LoanResponse> loanResponse = new ArrayList<>();
        for (LoansEntity loans: loansEntities) {
            LoanResponse response = loanMapper.toResponse(loans);
            loanResponse.add(response);
        }
        return GeneralResponse.ok("These are loans", PageResponse.ok(pageCount, loanResponse));
    }
}
