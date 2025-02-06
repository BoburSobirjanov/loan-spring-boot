package uz.com.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "audit_logs")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLogsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(nullable = false)
    String httpMethod;

    @Column(nullable = false)
    String apiEndpoint;

    @Column(nullable = false)
    String request;

    @Column(nullable = false)
    String response;

    @Column(nullable = false)
    int statusCode;

    @ManyToOne
    UserEntity user;
}
