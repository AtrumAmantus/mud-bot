package com.designwright.discord.discordscape.data.service;

import com.designwright.discord.discordscape.core.exception.InternalRequestException;
import com.designwright.discord.discordscape.core.exception.PersistenceException;
import com.designwright.discord.discordscape.core.request.InternalRequest;
import com.designwright.discord.discordscape.core.request.InternalRequestListener;
import com.designwright.discord.discordscape.core.request.InternalRequestService;
import com.designwright.discord.discordscape.data.domain.Avatar;
import com.designwright.discord.discordscape.data.persistence.entity.AvatarEntity;
import com.designwright.discord.discordscape.data.persistence.respository.AvatarRepository;
import com.designwright.discord.discordscape.utility.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@InternalRequestService
@Service
public class AvatarService {

    private final AvatarRepository avatarRepository;

    @InternalRequestListener(InternalRequest.Type.READ)
    public InternalRequest<Avatar> readListener(InternalRequest<Avatar> event) {
        List<Avatar> avatars = new ArrayList<>();

        event.getPayload().forEach(avatar -> {
            if (avatar.getUser().getDiscordId() != null) {
                avatars.addAll(
                        findAllByDiscordId(avatar.getUser().getDiscordId())
                );
            } else {
                throw new InternalRequestException("Invalid data used for lookup", avatar);
            }
        });

        return new InternalRequest<>(avatars, InternalRequest.Type.READ);
    }

    @InternalRequestListener(InternalRequest.Type.CREATE)
    public InternalRequest<Avatar> createListener(InternalRequest<Avatar> event) {
        InternalRequest<Avatar> response;
        try {
            List<Avatar> savedAvatars = createAvatars(event.getPayload());
            response = new InternalRequest<>(savedAvatars, InternalRequest.Type.CREATE);
        } catch (PersistenceException e) {
            response = new InternalRequest<>(
                    Collections.emptyList(),
                    InternalRequest.Type.CREATE,
                    InternalRequest.Status.ERROR,
                    e.getMessage()
            );
        }

        return response;
    }

    @InternalRequestListener(InternalRequest.Type.UPDATE)
    public InternalRequest<Avatar> updateListener(InternalRequest<Avatar> event) {
        InternalRequest<Avatar> response;

        try {
            List<Avatar> savedAvatars = updateAvatars(event.getPayload());
            response = new InternalRequest<>(savedAvatars, InternalRequest.Type.UPDATE);
        } catch (PersistenceException e) {
            response = new InternalRequest<>(
                    Collections.emptyList(),
                    InternalRequest.Type.UPDATE,
                    InternalRequest.Status.ERROR,
                    e.getMessage()
            );
        }

        return response;
    }

    private List<Avatar> findAllByDiscordId(String id) {
        List<AvatarEntity> entities = avatarRepository.findAllByUser_DiscordId(id);

        return MappingUtils.convertToType(entities, Avatar.class);
    }

    private List<Avatar> createAvatars(List<Avatar> avatars) {
        List<AvatarEntity> avatarsToSave = MappingUtils.convertToType(avatars, AvatarEntity.class);

        avatarsToSave.forEach(avatarEntity -> {
            if (avatarRepository.existsByName(avatarEntity.getName())) {
                throw new PersistenceException("Avatar with name already exists");
            }
        });

        List<AvatarEntity> savedEntities = avatarRepository.createAll(avatarsToSave);

        return MappingUtils.convertToType(savedEntities, Avatar.class);
    }

    private List<Avatar> updateAvatars(List<Avatar> avatars) {
        List<AvatarEntity> avatarsToSave = MappingUtils.convertToType(avatars, AvatarEntity.class);

        List<AvatarEntity> savedEntities = avatarRepository.updateAll(avatarsToSave);

        return MappingUtils.convertToType(savedEntities, Avatar.class);
    }

}
