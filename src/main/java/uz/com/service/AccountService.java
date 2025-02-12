package uz.com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.com.exception.DataHasAlreadyExistsException;
import uz.com.exception.DataNotAcceptableException;
import uz.com.exception.DataNotFoundException;
import uz.com.mapper.AccountMapper;
import uz.com.model.dto.request.AccountCreateRequest;
import uz.com.model.dto.response.AccountResponse;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.dto.response.PageResponse;
import uz.com.model.entity.AccountsEntity;
import uz.com.model.entity.UserEntity;
import uz.com.model.enums.AccountType;
import uz.com.model.enums.UserRole;
import uz.com.repository.AccountRepository;
import uz.com.repository.UserRepository;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;


    public GeneralResponse<AccountResponse> save(AccountCreateRequest request, Principal principal) {
        AccountType type = AccountType.valueOf(request.getType().toUpperCase());
        AccountsEntity accounts = accountMapper.toEntity(request);
        UserEntity user = userRepository.findUserEntityByIdAndDeletedFalse(UUID.fromString(request.getUserId()));
        List<AccountsEntity> accountsEntities = accountRepository.findAllByUserAndDeletedIsFalse(user);
        if (accountsEntities != null) {
            for (AccountsEntity accountsEntity : accountsEntities) {
                if (accountsEntity.getType().equals(type))
                    throw new DataHasAlreadyExistsException("This type account has already exists in this user!");
            }
        }
        UserEntity principalUser = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        if (user == null || !user.getRole().contains(UserRole.CLIENT)) {
            throw new DataNotAcceptableException("Bad request! Something error! Try again later! This user is not client!");
        }
        if (request.getInterestRate() < 0 || request.getInterestRate() > 100) {
            throw new DataNotAcceptableException("Invalid interest rate!");
        }
        accounts.setInterestRate(request.getInterestRate());
        try {
            accounts.setType(type);
        } catch (Exception e) {
            throw new DataNotAcceptableException("Wrong account type!");
        }
        accounts.setUser(user);
        accounts.setCreatedBy(principalUser.getId());
        if (request.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new DataNotAcceptableException("Invalid balance!");
        }
        accounts.setBalance(request.getBalance());
        AccountsEntity save = accountRepository.save(accounts);
        AccountResponse accountResponse = accountMapper.toResponse(save);

        return GeneralResponse.ok("Account created!", accountResponse);
    }


    public GeneralResponse<AccountResponse> getById(UUID id) {
        AccountsEntity accounts = accountRepository.findAccountsEntityByIdAndDeletedFalse(id);
        if (accounts == null) {
            throw new DataNotFoundException("Account did not find!");
        }
        AccountResponse accountResponse = accountMapper.toResponse(accounts);

        return GeneralResponse.ok("This is account!", accountResponse);
    }


    public GeneralResponse<String> deleteById(UUID id, Principal principal) {
        AccountsEntity accounts = accountRepository.findAccountsEntityByIdAndDeletedFalse(id);
        UserEntity user = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        if (accounts == null) {
            throw new DataNotFoundException("Account did not find!");
        }
        accounts.setDeleted(true);
        accounts.setDeletedAt(LocalDateTime.now());
        accounts.setDeletedBy(user.getId());
        accountRepository.save(accounts);

        return GeneralResponse.ok("Account deleted!", "DELETED");
    }


    public GeneralResponse<String> multiDeleteAccount(List<String> ids, Principal principal) {
        UserEntity user = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        for (String id : ids) {
            AccountsEntity accounts = accountRepository.findAccountsEntityByIdAndDeletedFalse(UUID.fromString(id));
            if (accounts == null) {
                throw new DataNotFoundException("Account not found!");
            }
            accounts.setDeleted(true);
            accounts.setDeletedAt(LocalDateTime.now());
            accounts.setDeletedBy(user.getId());
            accountRepository.save(accounts);
        }

        return GeneralResponse.ok("Accounts deleted!", "DELETED");
    }


    public GeneralResponse<PageResponse<AccountResponse>> getAllAccount(int page, int size, String accType) {
        Pageable pageable = PageRequest.of(page,size);
        List<AccountsEntity> accountsEntities = accountRepository.findAllAccountEntityAndDeletedFalse(pageable).getContent();
        if (accType == null) {
            int accCount = accountsEntities.size();
            int pageCount = accCount/ size;
            if (accCount% size !=0) pageCount++;
            if (accountsEntities == null) throw new DataNotFoundException("Accounts not found!");
            List<AccountResponse> accountResponses = new ArrayList<>();
            for (AccountsEntity accounts: accountsEntities) {
                AccountResponse response = accountMapper.toResponse(accounts);
                accountResponses.add(response);
            }

            return GeneralResponse.ok("These are accounts",PageResponse.ok(pageCount,accountResponses));
        }
        return getAllAccountByType(size, accType, pageable);
    }

    private GeneralResponse<PageResponse<AccountResponse>> getAllAccountByType(int size, String accType, Pageable pageable) {
        AccountType type = AccountType.valueOf(accType.toUpperCase());
        List<AccountsEntity> accountsEntitiesByType = accountRepository.findAllByTypeAndDeletedIsFalse(type, pageable).getContent();
        if (accountsEntitiesByType == null) throw new DataNotFoundException("Accounts not found!");
        int accCount = accountsEntitiesByType.size();
        int pageCount = accCount/ size;
        if (accCount% size !=0) pageCount++;
        if (accountsEntitiesByType == null) throw new DataNotFoundException("Accounts not found!");
        List<AccountResponse> accountResponses = new ArrayList<>();
        for (AccountsEntity accounts: accountsEntitiesByType) {
            AccountResponse response = accountMapper.toResponse(accounts);
            accountResponses.add(response);
        }
        return GeneralResponse.ok("These are accounts", PageResponse.ok(pageCount, accountResponses));
    }


    public GeneralResponse<PageResponse<AccountResponse>> getUserAccount(Principal principal, UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        if (userId != null) {
            return getAllAccountByUserId(userRepository.findUserEntityByIdAndDeletedFalse(userId), pageable, size);
        }
        return getAllAccountByUserId(userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName()), pageable, size);
    }

    private GeneralResponse<PageResponse<AccountResponse>> getAllAccountByUserId(UserEntity userRepository, Pageable pageable, int size) {
        List<AccountsEntity> accounts = accountRepository.findAccountsEntityByUser(userRepository, pageable).getContent();
        if (accounts == null) throw new DataNotFoundException("Account not found!");
        int accCount = accounts.size();
        int pageCount = accCount / size;
        if (accCount % size != 0) pageCount++;
        if (accounts == null) throw new DataNotFoundException("Accounts not found!");
        List<AccountResponse> accountResponses = new ArrayList<>();
        for (AccountsEntity account : accounts) {
            AccountResponse response = accountMapper.toResponse(account);
            accountResponses.add(response);
        }
        return GeneralResponse.ok("These are accounts", PageResponse.ok(pageCount, accountResponses));
    }
}
