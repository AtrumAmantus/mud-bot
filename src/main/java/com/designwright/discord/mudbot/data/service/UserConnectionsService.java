package com.designwright.discord.mudbot.data.service;

import com.designwright.discord.mudbot.core.exception.InternalRequestException;
import com.designwright.discord.mudbot.core.request.InternalRequest;
import com.designwright.discord.mudbot.net.ConnectionInfo;
import com.designwright.discord.mudbot.net.UserConnections;
import com.designwright.discord.mudbot.core.request.InternalRequestListener;
import com.designwright.discord.mudbot.core.request.InternalRequestService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@InternalRequestService
@Service
public class UserConnectionsService {

    private final UserConnections userConnections;

    @InternalRequestListener(InternalRequest.Type.CREATE)
    public void broadcastListener(InternalRequest<String> request) {
        request.getPayload().forEach(userConnections::broadcast);
    }

    @InternalRequestListener(InternalRequest.Type.READ)
    public InternalRequest<ConnectionInfo> readListener(InternalRequest<ConnectionInfo> request) {
        InternalRequest<ConnectionInfo> response;

        try {
            ConnectionInfo connectionInfo = getUserConnection(request.getPayloadFirst().getDiscordUser());
            response = new InternalRequest<>(connectionInfo, InternalRequest.Type.READ);
        } catch (InternalRequestException e) {
            response = new InternalRequest<>(
                    Collections.emptyList(),
                    InternalRequest.Type.READ,
                    InternalRequest.Status.ERROR,
                    e.getMessage()
            );
        }

        return response;
    }

    @InternalRequestListener(InternalRequest.Type.CREATE)
    public InternalRequest<ConnectionInfo> createListener(InternalRequest<ConnectionInfo> request) {
        return new InternalRequest<>(addUserConnection(request.getPayloadFirst()), InternalRequest.Type.CREATE);
    }

    @InternalRequestListener(InternalRequest.Type.UPDATE)
    public InternalRequest<ConnectionInfo> updateListener(InternalRequest<ConnectionInfo> request) {
        return new InternalRequest<>(updateUserConnection(request.getPayloadFirst()), InternalRequest.Type.UPDATE);
    }

    @InternalRequestListener(InternalRequest.Type.DELETE)
    public InternalRequest<ConnectionInfo> deleteListener(InternalRequest<ConnectionInfo> request) {
        return new InternalRequest<>(removeUserConnection(request.getPayloadFirst()), InternalRequest.Type.DELETE);
    }

    ConnectionInfo getUserConnection(User user) {
        return userConnections.getConnection(user);
    }

    ConnectionInfo addUserConnection(ConnectionInfo connectionInfo) {
        userConnections.addConnection(connectionInfo);

        return userConnections.getConnection(connectionInfo.getDiscordUser());
    }

    ConnectionInfo updateUserConnection(ConnectionInfo connectionInfo) {
        userConnections.removeConnection(connectionInfo.getDiscordUser());
        userConnections.addConnection(connectionInfo);

        return userConnections.getConnection(connectionInfo.getDiscordUser());
    }

    ConnectionInfo removeUserConnection(ConnectionInfo connectionInfo) {
        userConnections.removeConnection(connectionInfo.getDiscordUser());

        return connectionInfo;
    }

}
