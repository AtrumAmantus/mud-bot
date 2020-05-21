package com.designwright.discord.discordscape.data.persistence.entity;

public interface IdentifiableEntity<I> {

    I getId();

    void setId(I id);

}
