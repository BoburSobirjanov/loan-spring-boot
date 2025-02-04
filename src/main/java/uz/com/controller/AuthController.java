package uz.com.controller;

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


    @PostMapping("/sign-up")
    public ResponseEntity<GeneralResponse<JwtResponse>> save(@Valid @RequestBody UserCreateRequest userCreateRequest,
                                                             BindingResult bindingResult) throws RequestValidationException {
        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            throw new RequestValidationException(allErrors);
        }
        return ResponseEntity.ok(userService.save(userCreateRequest));
    }


    @PostMapping("/sign-in")
    public ResponseEntity<GeneralResponse<JwtResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }


    @GetMapping("/send-verification")
    public GeneralResponse<String> sendMessage(
            @RequestParam String email
    ) {
        return mailSendingService.sendMessage(email);
    }


    @PutMapping("/forgot-password")
    public GeneralResponse<String> forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) {
        return userService.forgotPassword(request);
    }
}
