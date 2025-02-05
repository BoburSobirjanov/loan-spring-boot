package uz.com.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import uz.com.exception.RequestValidationException;
import uz.com.model.dto.request.ForgotPasswordRequest;
import uz.com.model.dto.request.LoginRequest;
import uz.com.model.dto.request.UserCreateRequest;
import uz.com.model.dto.response.GeneralResponse;
import uz.com.model.dto.response.JwtResponse;
import uz.com.service.MailSendingService;
import uz.com.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final MailSendingService mailSendingService;



    @Operation(summary = "Register a new user", description = "Registers a new user and returns a JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/sign-up")
    public ResponseEntity<GeneralResponse<JwtResponse>> save(@Valid @RequestBody UserCreateRequest userCreateRequest,
                                                             BindingResult bindingResult) throws RequestValidationException {
        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            throw new RequestValidationException(allErrors);
        }
        return ResponseEntity.ok(userService.save(userCreateRequest));
    }




    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/sign-in")
    public ResponseEntity<GeneralResponse<JwtResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }



    @Operation(summary = "Send verification email", description = "Sends a verification email to the user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verification email sent"),
            @ApiResponse(responseCode = "400", description = "Invalid email"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/send-verification")
    public GeneralResponse<String> sendMessage(
            @RequestParam String email
    ) {
        return mailSendingService.sendMessage(email);
    }



    @Operation(summary = "Forgot password", description = "Sends a password reset link to the user's email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset link sent"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/forgot-password")
    public GeneralResponse<String> forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) {
        return userService.forgotPassword(request);
    }
}
