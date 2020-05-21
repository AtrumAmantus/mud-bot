package com.designwright.discord.discordscape.data.persistence.respository;

import com.designwright.discord.discordscape.core.exception.ResourceNotFoundException;
import com.designwright.discord.discordscape.core.exception.PersistenceException;
import com.designwright.discord.discordscape.data.persistence.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends BaseRepository<UserEntity, Long> {

    default UserEntity getByDiscordId(String id) {
        List<UserEntity> foundEntities = findAllByDiscordId(id);

        if (foundEntities.isEmpty()) {
            throw new ResourceNotFoundException();
        } else if (foundEntities.size() > 1) {
            throw new PersistenceException("Unexpected multiple entries found matching query.");
        }

        return foundEntities.get(0);
    }

    List<UserEntity> findAllByDiscordId(String discordId);

}
