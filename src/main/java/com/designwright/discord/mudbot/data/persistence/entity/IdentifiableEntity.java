package com.designwright.discord.mudbot.data.persistence.entity;

public interface IdentifiableEntity<I> {

    I getId();

    void setId(I id);

}
