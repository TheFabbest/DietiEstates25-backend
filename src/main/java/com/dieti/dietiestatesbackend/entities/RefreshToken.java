package com.dieti.dietiestatesbackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "refresh_token", indexes = {
    @Index(name = "idx_refresh_token_value", columnList = "token_value", unique = true),
    @Index(name = "idx_refresh_token_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class RefreshToken extends BaseEntity {

    @NotBlank
    @Column(name = "token_value", nullable = false, unique = true)
    private String tokenValue;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_refresh_token_user"))
    private User user;

    @NotNull
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
}