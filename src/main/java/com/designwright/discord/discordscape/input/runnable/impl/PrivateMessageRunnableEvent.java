package com.designwright.discord.discordscape.input.runnable.impl;

import com.designwright.discord.discordscape.core.request.InternalApi;
import com.designwright.discord.discordscape.core.request.InternalRequest;
import com.designwright.discord.discordscape.data.domain.Avatar;
import com.designwright.discord.discordscape.data.domain.User;
import com.designwright.discord.discordscape.input.action.Actionable;
import com.designwright.discord.discordscape.input.action.AvatarMenuAction;
import com.designwright.discord.discordscape.input.action.ConnectionAction;
import com.designwright.discord.discordscape.input.action.InvalidAction;
import com.designwright.discord.discordscape.input.action.SpeechAction;
import com.designwright.discord.discordscape.input.action.UserAction;
import com.designwright.discord.discordscape.input.action.speech.AvatarSpeechAction;
import com.designwright.discord.discordscape.input.action.speech.UserSpeechAction;
import com.designwright.discord.discordscape.input.enums.UserType;
import com.designwright.discord.discordscape.input.message.MessageEvent;
import com.designwright.discord.discordscape.input.runnable.AbstractRunnableEvent;
import com.designwright.discord.discordscape.input.message.MessageParser;
import com.designwright.discord.discordscape.input.message.MessageParserFactory;
import com.designwright.discord.discordscape.net.ConnectionInfo;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PrivateMessageRunnableEvent extends AbstractRunnableEvent {

    private final MessageParserFactory messageParserFactory;

    public PrivateMessageRunnableEvent(
            InternalApi internalApi,
            MessageParserFactory messageParserFactory,
            MessageEvent messageEvent
    ) {
        super(internalApi, messageParserFactory, messageEvent);
        this.messageParserFactory = messageParserFactory;
    }

    @Override
    public void run() {
        PrivateMessageReceivedEvent event = messageEvent.getEvent();
        Actionable actionable;
        net.dv8tion.jda.api.entities.User user = event.getAuthor();

        if (!user.isBot()) {
            UserType userType = getUserType(user);
            MessageParser parser = messageParserFactory.createParser(event, userType);
            actionable = parser.parse();

            UserAction action = actionable.getAction();

            if (action instanceof ConnectionAction) {
                handleConnectionEvent(actionable);
            } else if (action instanceof AvatarMenuAction) {
                handleAvatarMenuEvent(actionable);
            } else if (action instanceof SpeechAction) {
                handleSpeechEvent(actionable);
            } else if (action instanceof InvalidAction) {
                event.getChannel().sendMessage(action.getMessage()).queue();
            } else {
                handleNonSpeechEvent(actionable);
            }
        }
    }

    UserType getUserType(net.dv8tion.jda.api.entities.User user) {
        UserType userType;

        InternalRequest<ConnectionInfo> connectedUser = internalApi.sendAndReceive(
                new InternalRequest<>(new ConnectionInfo(user, null), InternalRequest.Type.READ)
        );

        if (!connectedUser.getPayload().isEmpty()) {
            Avatar avatar = connectedUser.getPayloadFirst().getAvatar();

            if (avatar != null) {
                if (avatar.isAdmin()) {
                    userType = UserType.ADMIN;
                } else {
                    userType = UserType.AVATAR;
                }
            } else {
                userType = UserType.UNKNOWN;
            }
        } else {
            userType = UserType.ANONYMOUS;
        }

        return userType;
    }

    void handleConnectionEvent(Actionable actionable) {
        PrivateMessageReceivedEvent event = messageEvent.getEvent();
        ConnectionAction action = (ConnectionAction) actionable.getAction();

        InternalRequest<ConnectionInfo> connectedUser = internalApi.sendAndReceive(
                new InternalRequest<>(new ConnectionInfo(event.getAuthor(), null), InternalRequest.Type.READ)
        );

        if (ConnectionAction.Action.LOGIN.equals(action.getAction())) {
            // Does user already have an active connection?
            if (connectedUser.getPayload().isEmpty()) {
                User user = getOrCreateUser(event.getAuthor().getAvatarId());
                String broadcastMessage = event.getAuthor().getName() + "(" + user.getDiscordId() + ")" + " connected.";
                log.info(broadcastMessage);
                internalApi.send(new InternalRequest<>(broadcastMessage, InternalRequest.Type.CREATE));

                ConnectionInfo userConnectionInfo = new ConnectionInfo(event.getAuthor(), event.getChannel());
                userConnectionInfo.setUser(user);

                InternalRequest<ConnectionInfo> addConnRequest = internalApi.sendAndReceive(
                        new InternalRequest<>(userConnectionInfo, InternalRequest.Type.CREATE)
                );

                if (!InternalRequest.Status.ERROR.equals(addConnRequest.getStatus())) {
                    event.getChannel().sendMessage("You have connected, please select your character (use <avatar>).").queue();

                    Avatar findAvatar = new Avatar();
                    findAvatar.setUser(user);

                    List<Avatar> userAvatars = internalApi.sendAndReceive(
                            new InternalRequest<>(findAvatar, InternalRequest.Type.READ)
                    ).getPayload();

                    String userMessage = userAvatars.stream().map(Avatar::getName).collect(Collectors.joining("\n"));
                    if (userMessage.isEmpty()) {
                        userMessage = "No avatars exist.";
                    }
                    event.getChannel().sendMessage(userMessage).queue();
                    event.getChannel().sendMessage("(type 'create <avatar>' to create a new avatar)").queue();
                } else {
                    event.getChannel().sendMessage(addConnRequest.getMessage()).queue();
                }
            } else {
                log.warn("Unexpected login de-sync for: " + event.getAuthor());
                event.getChannel().sendMessage("You are already connected.").queue();
            }
        } else {
            if (!connectedUser.getPayload().isEmpty()) {
                String broadcastMessage = event.getAuthor().getName() + " disconnected.";
                InternalRequest<ConnectionInfo> removeConnRequest = internalApi.sendAndReceive(
                        new InternalRequest<>(
                                new ConnectionInfo(event.getAuthor(), event.getChannel()),
                                InternalRequest.Type.DELETE
                        )
                );
                if (!InternalRequest.Status.ERROR.equals(removeConnRequest.getStatus())) {
                    internalApi.send(new InternalRequest<>(broadcastMessage, InternalRequest.Type.CREATE));
                    log.info(broadcastMessage);
                    event.getChannel().sendMessage("Goodbye.").queue();
                }
            } else {
                log.warn("Unexpected logout de-sync for: " + event.getAuthor());
                event.getChannel().sendMessage("You are not logged in.").queue();
            }
        }
    }

    User getOrCreateUser(String discordId) {
        User userLookup = new User();
        userLookup.setDiscordId(discordId);

        InternalRequest<User> user = internalApi.sendAndReceive(
                new InternalRequest<>(userLookup, InternalRequest.Type.READ)
        );

        if (user.getPayload().isEmpty()) {
            // Create user
            user = internalApi.sendAndReceive(
                    new InternalRequest<>(userLookup, InternalRequest.Type.CREATE)
            );
        } else {
            // Update user (login time)
            user = internalApi.sendAndReceive(
                    new InternalRequest<>(user.getPayloadFirst(), InternalRequest.Type.UPDATE)
            );
        }

        return user.getPayloadFirst();
    }

    void handleAvatarMenuEvent(Actionable actionable) {
        UserAction userAction = actionable.getAction();
    }

    void handleSpeechEvent(Actionable actionable) {
        PrivateMessageReceivedEvent event = messageEvent.getEvent();
        UserAction action = actionable.getAction();

        if (action instanceof AvatarSpeechAction) {

        } else if (action instanceof UserSpeechAction) {
            String broadcastMessage = event.getAuthor().getName() + " says: \"" + action.getMessage() + "\"";
            internalApi.send(new InternalRequest<>(broadcastMessage, InternalRequest.Type.CREATE));
        }
    }

    void handleNonSpeechEvent(Actionable actionable) {
        PrivateMessageReceivedEvent event = messageEvent.getEvent();
        UserAction action = actionable.getAction();
        InternalRequest<ConnectionInfo> connectedUser = internalApi.sendAndReceive(
                new InternalRequest<>(new ConnectionInfo(event.getAuthor(), null), InternalRequest.Type.READ)
        );
        Avatar avatar = connectedUser.getPayloadFirst().getAvatar();

        String broadcastMessage = avatar.getName() + " " + action.getMessage();
        internalApi.send(new InternalRequest<>(broadcastMessage, InternalRequest.Type.CREATE));
    }

}
