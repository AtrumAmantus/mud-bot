package com.designwright.discord.mudbot.data.persistence.respository;

import com.designwright.discord.mudbot.data.persistence.entity.AvatarEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvatarRepository extends BaseRepository<AvatarEntity, Long> {

    List<AvatarEntity> findAllByUser_DiscordId(String discordId);

    boolean existsByName(String name);

}
