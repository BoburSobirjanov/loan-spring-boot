package uz.com.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.com.exception.DataNotAcceptableException;
import uz.com.exception.DataNotFoundException;
import uz.com.model.dto.request.TransactionCreateRequest;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.dto.response.TransactionResponse;
import uz.com.model.entity.AccountsEntity;
import uz.com.model.entity.TransactionEntity;
import uz.com.model.entity.UserEntity;
import uz.com.model.enums.TransactionType;
import uz.com.repository.AccountRepository;
import uz.com.repository.TransactionRepository;
import uz.com.repository.UserRepository;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    public GeneralResponse<TransactionResponse> save(TransactionCreateRequest request, Principal principal){
        UserEntity user = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        TransactionEntity transactionEntity = modelMapper.map(request, TransactionEntity.class);
        AccountsEntity accounts = accountRepository.findAccountsEntityByIdAndDeletedFalse(UUID.fromString(request.getAccountId()));
        if (accounts==null){
            throw new DataNotFoundException("Account not found!");
        }
        transactionEntity.setAccount(accounts);
        try {
            transactionEntity.setType(TransactionType.valueOf(request.getType().toUpperCase()));
        } catch (Exception e){
            throw new DataNotAcceptableException("Invalid transaction type!");
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO)<0){
            throw new DataNotAcceptableException("Bad request! Action not acceptable! Invalid amount!");
        }
        BigDecimal transactionAmount = accounts.getBalance().subtract(request.getAmount());
        if (transactionAmount.compareTo(BigDecimal.ZERO)<0){
            throw new DataNotAcceptableException("Has no enough balance in account! Try again later!");
        }
        transactionEntity.setAmount(request.getAmount());
        accounts.setBalance(transactionAmount);
        accountRepository.save(accounts);
        transactionEntity.setCreatedBy(user.getId());
        TransactionEntity save = transactionRepository.save(transactionEntity);
        TransactionResponse response = modelMapper.map(save, TransactionResponse.class);

        return GeneralResponse.ok("Transaction created!",response);
    }




    public GeneralResponse<TransactionResponse> getById(UUID id){
        TransactionEntity transaction = transactionRepository.findTransactionEntityByIdAndDeletedFalse(id);
        if (transaction==null){
            throw new DataNotFoundException("Transaction not found!");
        }
        TransactionResponse response = modelMapper.map(transaction, TransactionResponse.class);
        return GeneralResponse.ok("This is transaction!",response);
    }




    public GeneralResponse<String> delete(UUID id, Principal principal){
        TransactionEntity transaction = transactionRepository.findTransactionEntityByIdAndDeletedFalse(id);
        UserEntity user = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        if (transaction==null){
            throw new DataNotFoundException("Transaction not found!");
        }
        transaction.setDeleted(true);
        transaction.setDeletedAt(LocalDateTime.now());
        transaction.setDeletedBy(user.getId());
        transactionRepository.save(transaction);

        return GeneralResponse.ok("Transaction deleted!","DELETED");
    }
}
