package uz.com.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.com.model.BaseModel;

@Entity(name = "audit_logs")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLogsEntity extends BaseModel {

    @Column(nullable = false)
    String action;


    @ManyToOne
    UserEntity user;
}
