package com.designwright.discord.mudbot.data.service;

import com.designwright.discord.mudbot.core.exception.InternalRequestException;
import com.designwright.discord.mudbot.core.exception.PersistenceException;
import com.designwright.discord.mudbot.core.request.InternalRequest;
import com.designwright.discord.mudbot.core.request.InternalRequestListener;
import com.designwright.discord.mudbot.core.request.InternalRequestService;
import com.designwright.discord.mudbot.data.domain.Avatar;
import com.designwright.discord.mudbot.data.persistence.entity.AvatarEntity;
import com.designwright.discord.mudbot.data.persistence.respository.AvatarRepository;
import com.designwright.discord.mudbot.utility.MappingUtils;
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
            if (avatar.getUser() != null && avatar.getUser().getDiscordId() != null) {
                avatars.addAll(
                        findAllByDiscordId(avatar.getUser().getDiscordId())
                );
            } else if (avatar.getName() != null) {
                avatars.addAll(
                        getAvatarByName(avatar.getName())
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

    private List<Avatar> getAvatarByName(String name) {
        List<AvatarEntity> entities = avatarRepository.getByName(name);

        return MappingUtils.convertToType(entities, Avatar.class);
    }

    private List<Avatar> findAllByDiscordId(String id) {
        List<AvatarEntity> entities = avatarRepository.findAllByUser_DiscordId(id);

        return MappingUtils.convertToType(entities, Avatar.class);
    }

    private List<Avatar> createAvatars(List<Avatar> avatars) {
        List<AvatarEntity> avatarsToSave = MappingUtils.convertToType(avatars, AvatarEntity.class);

        avatarsToSave.forEach(avatarEntity -> {
            if (avatarRepository.existsByName(avatarEntity.getName().toLowerCase())) {
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
