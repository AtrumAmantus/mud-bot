package com.designwright.discord.mudbot.data.persistence.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Getter
@Setter
@Entity
public class UserEntity implements IdentifiableEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, updatable = false)
    private String discordId;
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
