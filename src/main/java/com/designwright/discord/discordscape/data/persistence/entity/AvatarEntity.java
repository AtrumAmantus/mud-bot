package com.designwright.discord.discordscape.data.persistence.entity;

import com.designwright.discord.discordscape.data.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Getter
@Setter
@Entity
public class AvatarEntity implements IdentifiableEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private UserEntity user;
    @Column(nullable = false, updatable = false, unique = true)
    private String name;
    private boolean admin;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(nullable = false, updatable = false)
    private Long createDate;
    @Column(nullable = false)
    private Long lastLoginDate;

    @PrePersist
    public void onCreate() {
        this.createDate = System.currentTimeMillis();
        this.lastLoginDate = System.currentTimeMillis();
    }

    @PreUpdate
    public void onUpdate() {
        this.lastLoginDate = System.currentTimeMillis();
    }

}
