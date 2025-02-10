package uz.com.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uz.com.exception.DataNotFoundException;
import uz.com.model.entity.AuditLogsEntity;
import uz.com.model.entity.UserEntity;
import uz.com.repository.AuditLogsRepository;
import uz.com.repository.UserRepository;


@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogsRepository auditLogsRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logAudit(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        if (requestURI.startsWith("/swagger") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/api/v1/auth")) {
            return joinPoint.proceed();
        }

        if (httpMethod.startsWith("GET")){
            return joinPoint.proceed();
        }

        UserEntity userEntity = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserEntity) {
                userEntity = (UserEntity) principal;
            } else if (principal instanceof String username) {
                userEntity = userRepository.findUserEntityByEmailAndDeletedFalse(username);
            }

            if (userEntity == null) {
                throw new DataNotFoundException("User not found!");
            }
        }
        String requestData;
        try {
            requestData = objectMapper.writeValueAsString(joinPoint.getArgs());
        } catch (Exception e) {
            requestData = "Error converting request args to JSON: " + e.getMessage();
        }

        Object result = joinPoint.proceed();

        String responseData;
        try {
            responseData = objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            responseData = "Error converting response to JSON: " + e.getMessage();
        }
        AuditLogsEntity auditLog = new AuditLogsEntity();
        auditLog.setUrl(requestURI);
        auditLog.setHttpMethod(httpMethod);
        auditLog.setUser(userEntity);
        auditLog.setRequest(requestData);
        auditLog.setResponse(responseData);
        auditLogsRepository.save(auditLog);

        return result;
    }
}
