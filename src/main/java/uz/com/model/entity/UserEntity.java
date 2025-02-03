package uz.com.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.com.model.BaseModel;
import uz.com.model.enums.Gender;
import uz.com.model.enums.UserRole;

import java.util.*;

@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity extends BaseModel implements UserDetails {

    @Column(nullable = false)
    @Size(min = 5, message = "Full Name must be at least 5 characters!")
    @NotBlank(message = "Full name is required!")
    String fullName;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^[A-Za-z0-9._]+@(gmail\\.com|mail\\.com)$", message = "Email should be in the format ...@gmail.com or ...@email.com")
    @NotBlank(message = "Email is required")
    String email;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^\\+998\\d{9}$", message = "Phone must be +998XX XXX XX XX format!")
    @NotBlank(message = "Phone is required!")
    String phone;

    @Enumerated(value = EnumType.STRING)
    Gender gender;

    @Column(nullable = false)
    @Size(min = 8, message = "Password must be at least 8 characters long!")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*]).+$",
            message = "Password must contain at least one uppercase letter and one special character!")
    @NotBlank(message = "Password is required")
    String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    Set<UserRole> role;

    @Column(nullable = false)
    @NotBlank(message = "Address is required!")
    String address;

    UUID changeRoleBy;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (UserRole userRole : role) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
