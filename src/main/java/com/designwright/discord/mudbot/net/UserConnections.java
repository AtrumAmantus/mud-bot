package com.designwright.discord.mudbot.net;

import com.designwright.discord.mudbot.core.exception.InternalRequestException;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserConnections {

    private final Map<User, ConnectionInfo> connectionMap;
    private List<ConnectionInfo> connectionList;

    public enum ConnectionStatus {
        EXISTING,
        CONNECTED,
        DISCONNECTED,
        FAIL
    }

    public UserConnections() {
        connectionMap = new HashMap<>();
        connectionList = new ArrayList<>();
    }

    public List<ConnectionInfo> getConnections() {
        return connectionList;
    }

    public boolean isConnected(User user) {
        return connectionMap.containsKey(user);
    }

    public ConnectionStatus addConnection(User user, PrivateChannel privateChannel) {
        ConnectionStatus status;

        if(connectionMap.containsKey(user)) {
            status = ConnectionStatus.EXISTING;
        } else {
            connectionMap.put(user, new ConnectionInfo(user, privateChannel));
            updateConnectList();
            status = ConnectionStatus.CONNECTED;
        }

        return status;
    }

    public ConnectionStatus removeConnection(User user) {
        ConnectionStatus status;

        if (connectionMap.containsKey(user)) {
            connectionMap.remove(user);
            updateConnectList();
            status = ConnectionStatus.DISCONNECTED;
        } else {
            status = ConnectionStatus.FAIL;
        }

        return status;
    }

    public ConnectionInfo getConnection(User user) {
        if (!isConnected(user)) {
            throw new InternalRequestException("User is not connected.");
        }
        return connectionMap.get(user);
    }

    public void broadcast(String message) {
        broadcast(message, null);
    }

    public void broadcast(String message, User excludeUser) {
        connectionList.forEach(connectionInfo -> {
            if (!connectionInfo.getUser().equals(excludeUser)) {
                connectionInfo.getPrivateChannel().sendMessage(message).queue();
            }
        });
    }

    void updateConnectList() {
        connectionList = new ArrayList<>(connectionMap.values());
    }

}
