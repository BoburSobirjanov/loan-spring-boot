package uz.com.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.com.exception.DataHasAlreadyExistsException;
import uz.com.exception.DataNotAcceptableException;
import uz.com.exception.DataNotFoundException;
import uz.com.mapper.UserMapper;
import uz.com.model.dto.request.LoginRequest;
import uz.com.model.dto.request.UserCreateRequest;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.dto.response.JwtResponse;
import uz.com.model.dto.response.UserResponse;
import uz.com.model.entity.UserEntity;
import uz.com.model.enums.UserRole;
import uz.com.repository.UserRepository;
import uz.com.service.auth.JwtService;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void save_ShouldThrowException_WhenUserAlreadyExists() {
        UserCreateRequest request = new UserCreateRequest("test test", "test@example.com", "Password1!", "Test Address", "MALE", "+998900000000");

        when(userRepository.existsUserEntityByEmailAndPhoneAndDeletedIsFalse(request.getEmail(), request.getPhone()))
                .thenReturn(true);

        Exception exception = assertThrows(DataHasAlreadyExistsException.class, () -> userService.save(request));

        assertEquals("User has already exists!", exception.getMessage());

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void save_ShouldRegisterUser_WhenValidRequest() {
        UserCreateRequest request = new UserCreateRequest("test test", "test@example.com", "Password1!", "Test Address", "MALE", "+998900000000");

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.getEmail());
        userEntity.setPhone(request.getPhone());
        userEntity.setRole(Set.of());

        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(request.getEmail());

        when(userRepository.existsUserEntityByEmailAndPhoneAndDeletedIsFalse(request.getEmail(), request.getPhone()))
                .thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(userResponse);
        when(jwtService.generateAccessToken(any(UserEntity.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(UserEntity.class))).thenReturn("refresh-token");

        GeneralResponse<JwtResponse> response = userService.save(request);

        assertNotNull(response);
        assertEquals("User registered!", response.getMessage());
        assertEquals("access-token", response.getData().getAccessToken());
        assertEquals("refresh-token", response.getData().getRefreshToken());
        assertEquals("test@example.com", response.getData().getUserResponse().getEmail());

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void save_ShouldThrowException_WhenInvalidGender() {
        UserCreateRequest request = new UserCreateRequest("test test", "test@example.com", "Password1!", "Test Address", "MALE", "+998900000000");

        when(userRepository.existsUserEntityByEmailAndPhoneAndDeletedIsFalse(request.getEmail(), request.getPhone()))
                .thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(new UserEntity());

        assertThrows(DataNotAcceptableException.class, () -> userService.save(request));

        verify(userRepository, never()).save(any(UserEntity.class));
    }


    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        LoginRequest request = new LoginRequest("test@gmail.com", "Password1!");
        when(userRepository.findUserEntityByEmailAndDeletedFalse(request.getEmail()))
                .thenReturn(null);

        Exception exception = assertThrows(DataNotFoundException.class, () -> userService.login(request));
        assertEquals("Wrong username or password! Try again later!", exception.getMessage());

        verify(jwtService, never()).generateAccessToken(any());
        verify(jwtService, never()).generateRefreshToken(any());
    }


    @Test
    void login_ShouldReturnJwtResponse_WhenCredentialsAreCorrect() {
        LoginRequest request = new LoginRequest("test@gmail.com", "Password1!");

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword("encodePassword");

        UserResponse response = new UserResponse();
        response.setEmail(request.getEmail());

        when(userRepository.findUserEntityByEmailAndDeletedFalse(request.getEmail()))
                .thenReturn(user);
        when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(userMapper.toResponse(user)).thenReturn(response);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");

        GeneralResponse<JwtResponse> jwtResponseGeneralResponse = userService.login(request);

        assertNotNull(jwtResponseGeneralResponse);
        assertEquals("User signed", jwtResponseGeneralResponse.getMessage());
        assertEquals("access-token", jwtResponseGeneralResponse.getData().getAccessToken());
        assertEquals("refresh-token", jwtResponseGeneralResponse.getData().getRefreshToken());
        assertEquals("test@gmail.com", jwtResponseGeneralResponse.getData().getUserResponse().getEmail());

        verify(jwtService).generateAccessToken(user);
        verify(jwtService).generateRefreshToken(user);
    }


    @Test
    void changeRole_ShouldTrowException_WhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        String role = "ADMIN";
        Principal principal = mock(Principal.class);

        when(userRepository.findUserEntityByIdAndDeletedFalse(userId))
                .thenReturn(null);

        Exception exception = assertThrows(DataNotFoundException.class, () -> userService.changeRoleTo(userId, role, principal));
        assertEquals("User did not found!", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }


    @Test
    void changeRole_ShouldThrowException_WhenRoleHasAlreadyAssigned() {
        UUID userId = UUID.randomUUID();
        String role = "ADMIN";
        Principal principal = mock(Principal.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(Set.of(UserRole.ADMIN));

        when(userRepository.findUserEntityByIdAndDeletedFalse(userId))
                .thenReturn(userEntity);

        Exception exception = assertThrows(DataHasAlreadyExistsException.class, () -> userService.changeRoleTo(userId, role, principal));

        assertEquals("This role set before!", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }


    @Test
    void changeRole_ShouldThrowException_WhenValidRequest() {
        UUID userId = UUID.randomUUID();
        String role = "ADMIN";
        Principal principal = mock(Principal.class);

        when(principal.getName()).thenReturn("principal@gmail.com");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setRole(Set.of(UserRole.USER));

        UserEntity principalUser = new UserEntity();
        principalUser.setId(UUID.randomUUID());

        UserResponse userResponse = new UserResponse();

        when(userRepository.findUserEntityByIdAndDeletedFalse(userId)).thenReturn(userEntity);
        when(userRepository.findUserEntityByEmailAndDeletedFalse(principal.getName())).thenReturn(principalUser);
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(userEntity);
        when(userMapper.toResponse(any(UserEntity.class)))
                .thenReturn(userResponse);

        GeneralResponse<UserResponse> response = userService.changeRoleTo(userId, role, principal);

        assertNotNull(response);
        assertEquals("Client role added!", response.getMessage());
        assertNotNull(response.getData());

        verify(userRepository).save(userEntity);
    }
}
