package com.slotify.backend.spring.user.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class UserEntity {

    @Id
    @Column(name = "user_id")
    @EqualsAndHashCode.Include
    private UUID userId;

    @Column(name = "username", nullable = false, length = 70)
    private String username;

    @Column(name = "s3_image_key", length = 200)
    private String s3ImageKey;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "email_address", length = 320)
    private String emailAddress;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by_company_id")
    private UUID createdByCompany;
}
