package uz.com.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uz.com.exception.DataNotAcceptableException;
import uz.com.exception.DataNotFoundException;
import uz.com.model.dto.request.AccountCreateRequest;
import uz.com.model.dto.response.AccountResponse;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.entity.AccountsEntity;
import uz.com.model.entity.UserEntity;
import uz.com.model.enums.AccountType;
import uz.com.model.enums.UserRole;
import uz.com.repository.AccountRepository;
import uz.com.repository.UserRepository;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;


    public GeneralResponse<AccountResponse> save(AccountCreateRequest request, Principal principal){
        AccountsEntity accounts = modelMapper.map(request, AccountsEntity.class);
        UserEntity user = userRepository.findUserEntityByIdAndDeletedFalse(UUID.fromString(request.getUserId()));
        UserEntity principalUser = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        if (user==null || !user.getRole().contains(UserRole.CLIENT)){
            throw new DataNotAcceptableException("Bad request! Something error! Try again later! This user is not client!");
        }
        accounts.setInterestRate(request.getInterestRate());
        try{
        accounts.setType(AccountType.valueOf(request.getType().toUpperCase()));
        } catch (Exception e){
            throw new DataNotAcceptableException("Wrong account type!");
        }
        accounts.setUser(user);
        accounts.setCreatedBy(principalUser.getId());
        if (request.getBalance().compareTo(BigDecimal.ZERO)<0){
            throw new DataNotAcceptableException("Invalid balance!");
        }
        accounts.setBalance(request.getBalance());
        AccountsEntity save = accountRepository.save(accounts);
        AccountResponse accountResponse = modelMapper.map(save, AccountResponse.class);

        return GeneralResponse.ok("Account created!",accountResponse);
    }



    public GeneralResponse<AccountResponse> getById(UUID id){
        AccountsEntity accounts = accountRepository.findAccountsEntityByIdAndDeletedFalse(id);
        if (accounts==null){
            throw new DataNotFoundException("Account did not find!");
        }
        AccountResponse accountResponse = modelMapper.map(accounts, AccountResponse.class);

        return GeneralResponse.ok("This is account!",accountResponse);
    }



    public GeneralResponse<String> deleteById(UUID id, Principal principal){
        AccountsEntity accounts = accountRepository.findAccountsEntityByIdAndDeletedFalse(id);
        UserEntity user = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        if (accounts==null){
            throw new DataNotFoundException("Account did not find!");
        }
        accounts.setDeleted(true);
        accounts.setDeletedAt(LocalDateTime.now());
        accounts.setDeletedBy(user.getId());
        accountRepository.save(accounts);

        return GeneralResponse.ok("Account deleted!","DELETED");
    }



    public GeneralResponse<String> multiDeleteAccount(List<String> ids, Principal principal){
        UserEntity user = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        for (String id : ids) {
            AccountsEntity accounts = accountRepository.findAccountsEntityByIdAndDeletedFalse(UUID.fromString(id));
            if (accounts==null){
                throw new DataNotFoundException("Account not found!");
            }
            accounts.setDeleted(true);
            accounts.setDeletedAt(LocalDateTime.now());
            accounts.setDeletedBy(user.getId());
            accountRepository.save(accounts);
        }

        return GeneralResponse.ok("Accounts deleted!","DELETED");
    }
}
