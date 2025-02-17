package uz.com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.com.exception.DataHasAlreadyExistsException;
import uz.com.exception.DataNotAcceptableException;
import uz.com.exception.DataNotFoundException;
import uz.com.mapper.UserMapper;
import uz.com.model.dto.request.ForgotPasswordRequest;
import uz.com.model.dto.request.LoginRequest;
import uz.com.model.dto.request.UserCreateRequest;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.dto.response.JwtResponse;
import uz.com.model.dto.response.UserResponse;
import uz.com.model.entity.UserEntity;
import uz.com.model.entity.Verification;
import uz.com.model.enums.Gender;
import uz.com.model.enums.UserRole;
import uz.com.model.dto.response.PageResponse;
import uz.com.repository.UserRepository;
import uz.com.repository.VerificationRepository;
import uz.com.service.auth.JwtService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationRepository verificationRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public GeneralResponse<JwtResponse> save(UserCreateRequest request) {
        boolean b = userRepository.existsUserEntityByEmailAndPhoneAndDeletedIsFalse(request.getEmail(), request.getPhone());
        if (b) {
            throw new DataHasAlreadyExistsException("User has already exists!");
        }
        UserEntity user = userMapper.toEntity(request);
        user.setRole(Set.of(UserRole.USER));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());
        user.setFullName(request.getFullName());
        try {
            user.setGender(Gender.valueOf(request.getGender().toUpperCase()));
        } catch (Exception e) {
            throw new DataNotAcceptableException("Wrong input!");
        }
        userRepository.save(user);
        UserResponse userResponse = userMapper.toResponse(user);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userResponse(userResponse)
                .build();
        return GeneralResponse.ok("User registered!", jwtResponse);

    }


    public GeneralResponse<JwtResponse> login(LoginRequest request) {
        UserEntity user = userRepository.findUserEntityByEmailAndDeletedFalse(request.getEmail());
        if (user == null) {
            throw new DataNotFoundException("User did not find!");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new DataNotAcceptableException("Wrong username or password! Try again!");
        }
        UserResponse userResponse = userMapper.toResponse(user);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userResponse(userResponse)
                .build();
        return GeneralResponse.ok("User signed!", jwtResponse);
    }


    public GeneralResponse<UserResponse> changeRoleTo(UUID userId, String role, Principal principal) {
        UserRole setRole = UserRole.valueOf(role.toUpperCase());
        UserEntity user = userRepository.findUserEntityByIdAndDeletedFalse(userId);
        UserEntity principalUser = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        if (user == null) {
            throw new DataNotFoundException("User did not find!");
        }
        if (user.getRole().contains(setRole)) {
            throw new DataHasAlreadyExistsException("This role set before!");
        }
        user.getRole().add(setRole);
        user.setChangeRoleBy(principalUser.getId());
        user.setUpdatedAt(LocalDateTime.now());
        user.setCreatedBy(principalUser.getId());
        UserEntity save = userRepository.save(user);
        UserResponse userResponse = userMapper.toResponse(save);

        return GeneralResponse.ok("Client role added!", userResponse);
    }


    public GeneralResponse<UserResponse> removeRole(UUID userId, String role, Principal principal) {
        UserRole removeRole = UserRole.valueOf(role.toUpperCase());
        UserEntity user = userRepository.findUserEntityByIdAndDeletedFalse(userId);
        UserEntity principalUser = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        if (user == null) {
            throw new DataNotFoundException("User did not found!");
        }
        if (!user.getRole().contains(removeRole)) {
            throw new DataNotAcceptableException("Role has no this user!");
        }
        user.getRole().remove(removeRole);
        user.setChangeRoleBy(principalUser.getId());
        user.setUpdatedAt(LocalDateTime.now());
        user.setCreatedBy(principalUser.getId());
        UserEntity save = userRepository.save(user);
        UserResponse userResponse = userMapper.toResponse(save);

        return GeneralResponse.ok("Role removed!", userResponse);
    }


    public GeneralResponse<String> forgotPassword(ForgotPasswordRequest request) {
        UserEntity userEntity = userRepository.findUserEntityByEmailAndDeletedFalse(request.getEmail());
        Verification verification = verificationRepository.findByUserEmailAndCode(userEntity.getId(), request.getCode());
        if (!verification.getCode().equals(request.getCode()) ||
                verification.getCreatedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
            throw new DataNotAcceptableException("Verification code is incorrect or expired! Please, try again later!");
        }
        UserEntity user = userRepository.findUserEntityByEmailAndDeletedFalse(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        verificationRepository.delete(verification);
        userRepository.save(user);
        return GeneralResponse.ok("User password changed!", "CHANGED");
    }


    public GeneralResponse<UserResponse> getUserById(UUID id) {
        UserEntity user = userRepository.findUserEntityByIdAndDeletedFalse(id);
        if (user == null) {
            throw new DataNotFoundException("User did not find!");
        }
        UserResponse userResponse = userMapper.toResponse(user);
        return GeneralResponse.ok("This is user", userResponse);
    }


    public GeneralResponse<String> deleteUserById(UUID id, Principal principal) {
        UserEntity user = userRepository.findUserEntityByIdAndDeletedFalse(id);
        UserEntity principalUser = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        if (user == null) {
            throw new DataNotFoundException("user did not found!");
        }
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(principalUser.getId());
        userRepository.save(user);

        return GeneralResponse.ok("User deleted!", "DELETED");
    }


    public Page<UserResponse> getAllUsersByRole(Pageable pageable, String role) {
        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        Page<UserEntity> userEntities = userRepository.findAllByRole(userRole, pageable);
        if (userEntities == null) throw new DataNotFoundException("Users not found!");
        return userEntities.map(userEntity -> new UserResponse(userEntity.getId(), userEntity.getFullName(), userEntity.getEmail(), userEntity.getPhone(),
                userEntity.getAddress(), userEntity.getGender(), userEntity.getRole()));
    }


    public GeneralResponse<String> multiDeleteUser(List<String> ids, Principal principal) {
        UserEntity principalUser = userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName());
        for (String id : ids) {
            UserEntity user = userRepository.findUserEntityByIdAndDeletedFalse(UUID.fromString(id));
            if (user == null) {
                throw new DataNotFoundException("User not found!");
            }
            user.setDeleted(true);
            user.setDeletedAt(LocalDateTime.now());
            user.setDeletedBy(principalUser.getId());
            userRepository.save(user);
        }

        return GeneralResponse.ok("Users deleted!", "DELETED");
    }


    public GeneralResponse<UserResponse> getUserByPhone(String number) {
        UserEntity user = userRepository.findUserEntityByPhone(number);
        if (user == null) {
            throw new DataNotFoundException("User not found!");
        }
        UserResponse userResponse = userMapper.toResponse(user);
        return GeneralResponse.ok("This is user!", userResponse);
    }


    public GeneralResponse<UserResponse> updateUserProfile(UUID id, UserCreateRequest request) {
        UserEntity userEntity = userRepository.findUserEntityByEmailAndDeletedFalse(request.getEmail());
        UserEntity userByPhone = userRepository.findUserEntityByPhone(request.getPhone());
        UserEntity user = userRepository.findUserEntityByIdAndDeletedFalse(id);
        if (userEntity != user && userEntity != null) {
            throw new DataHasAlreadyExistsException("Email has already exists!");
        }
        if (userEntity != user && userByPhone != null) {
            throw new DataHasAlreadyExistsException("Phone has already exists!");
        }
        if (user == null) throw new DataNotFoundException("User not found!");
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());
        user.setFullName(request.getFullName());
        try {
            user.setGender(Gender.valueOf(request.getGender().toUpperCase()));
        } catch (Exception e) {
            throw new DataNotAcceptableException("Wrong input!");
        }
        UserEntity save = userRepository.save(user);
        UserResponse response = userMapper.toResponse(save);

        return GeneralResponse.ok("User updated!", response);
    }


    public GeneralResponse<PageResponse<UserResponse>> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<UserEntity> userEntities = userRepository.findAllByDeletedFalse(pageable).getContent();
        int userCount = userRepository.findAllUsersList().size();
        int pageCount = userCount / size;
        if (userCount % size != 0) pageCount++;
        List<UserResponse> userResponses = new ArrayList<>();
        for (UserEntity user : userEntities) {
            UserResponse response = userMapper.toResponse(user);
            userResponses.add(response);
        }
        return GeneralResponse.ok("These are users", PageResponse.ok(pageCount, userResponses));
    }
}
