package id.holigo.services.holigopinservice.domain;

import id.holigo.services.common.model.AccountStatusEnum;
import id.holigo.services.common.model.EmailStatusEnum;
import id.holigo.services.common.model.UserGroupEnum;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {

    @Id
    private Long id;

    @Column(length = 100, columnDefinition = "varchar(100)")
    private String name;

    @Column(length = 20, columnDefinition = "varchar(20)")
    private String phoneNumber;

    private String email;

    @Enumerated(EnumType.STRING)
    private EmailStatusEnum emailStatus;

    private AccountStatusEnum accountStatus;

    @Convert(converter = UserGroupEnumConverter.class)
    private UserGroupEnum userGroup;

    @Column(length = 64)
    private String verificationCode;

    @Nullable
    private Timestamp emailVerifiedAt;

    @Nullable
    private String pin;

    @Nullable
    private String oneTimePassword;

    @Column(length = 20, columnDefinition = "varchar(20)")
    private String type;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    private Timestamp deletedAt;

    private String mobileToken;

    @Builder.Default
    private Boolean accountNonExpired = true;

    @Builder.Default
    private Boolean accountNonLocked = true;

    @Builder.Default
    private Boolean credentialsNonExpired = true;

    private Boolean enabled;

    public Boolean isEnabled() {
        return this.getEnabled();
    }

    public void setPin(String value) {
        if (value != null) {
            this.pin = new BCryptPasswordEncoder().encode(value);
        } else {
            this.pin = null;
        }
    }

}
