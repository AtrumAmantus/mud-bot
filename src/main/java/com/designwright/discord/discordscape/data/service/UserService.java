package com.designwright.discord.discordscape.data.service;

import com.designwright.discord.discordscape.core.exception.InternalRequestException;
import com.designwright.discord.discordscape.core.request.InternalRequest;
import com.designwright.discord.discordscape.utility.MappingUtils;
import com.designwright.discord.discordscape.core.exception.PersistenceException;
import com.designwright.discord.discordscape.core.exception.ResourceNotFoundException;
import com.designwright.discord.discordscape.core.request.InternalRequestListener;
import com.designwright.discord.discordscape.core.request.InternalRequestService;
import com.designwright.discord.discordscape.data.domain.User;
import com.designwright.discord.discordscape.data.persistence.entity.UserEntity;
import com.designwright.discord.discordscape.data.persistence.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@InternalRequestService
@Service
public class UserService {

    private final UserRepository userRepository;

    @InternalRequestListener(InternalRequest.Type.READ)
    public InternalRequest<User> readListener(InternalRequest<User> event) {
        InternalRequest<User> response;

        User user = event.getPayloadFirst();

        try {
            if (user.getId() != null) {
                user = getUserById(user.getId());
            } else if (user.getDiscordId() != null) {
                user = getUserByDiscordId(user.getDiscordId());
            } else {
                throw new InternalRequestException("Invalid data used for lookup", user);
            }
            response = new InternalRequest<>(user, InternalRequest.Type.READ);
        } catch (ResourceNotFoundException e) {
            response = new InternalRequest<>(Collections.emptyList(), InternalRequest.Type.READ);
        }

        return response;
    }

    @InternalRequestListener(InternalRequest.Type.CREATE)
    public InternalRequest<User> createListener(InternalRequest<User> event) {
        InternalRequest<User> response;
        try {
            User savedUser = createUser(event.getPayloadFirst());
            response = new InternalRequest<>(savedUser, InternalRequest.Type.CREATE);
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
    public InternalRequest<User> updateListener(InternalRequest<User> event) {
        InternalRequest<User> response;

        try {
            User savedUser = updateUser(event.getPayloadFirst());
            response = new InternalRequest<>(savedUser, InternalRequest.Type.UPDATE);
        } catch (PersistenceException | ResourceNotFoundException e) {
            response = new InternalRequest<>(
                    Collections.emptyList(),
                    InternalRequest.Type.UPDATE,
                    InternalRequest.Status.ERROR,
                    e.getMessage()
            );
        }

        return response;
    }

    private User getUserById(Long id) {
        UserEntity userEntity = userRepository.getById(id);

        return MappingUtils.convertToType(userEntity, User.class);
    }

    private User getUserByDiscordId(String discordId) {
        UserEntity userEntity = userRepository.getByDiscordId(discordId);

        return MappingUtils.convertToType(userEntity, User.class);
    }

    private User createUser(User user) {
        UserEntity userEntity = MappingUtils.convertToType(user, UserEntity.class);

        UserEntity savedUser = userRepository.create(userEntity);

        return MappingUtils.convertToType(savedUser, User.class);
    }

    private User updateUser(User user) {
        UserEntity userEntity = MappingUtils.convertToType(user, UserEntity.class);

        UserEntity savedUser = userRepository.update(userEntity);

        return MappingUtils.convertToType(savedUser, User.class);
    }

}
