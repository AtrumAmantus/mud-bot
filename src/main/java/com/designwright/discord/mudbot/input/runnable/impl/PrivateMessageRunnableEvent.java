package com.designwright.discord.mudbot.input.runnable.impl;

import com.designwright.discord.mudbot.core.dialogue.impl.AvatarCreationDialogueSequence;
import com.designwright.discord.mudbot.core.exception.PersistenceException;
import com.designwright.discord.mudbot.core.request.InternalApi;
import com.designwright.discord.mudbot.core.request.InternalRequest;
import com.designwright.discord.mudbot.data.domain.User;
import com.designwright.discord.mudbot.input.action.Actionable;
import com.designwright.discord.mudbot.input.action.AnimateAction;
import com.designwright.discord.mudbot.input.action.CommandAction;
import com.designwright.discord.mudbot.input.action.InvalidAction;
import com.designwright.discord.mudbot.input.action.UserAction;
import com.designwright.discord.mudbot.input.message.MessageEvent;
import com.designwright.discord.mudbot.input.message.MessageParser;
import com.designwright.discord.mudbot.input.message.MessageParserFactory;
import com.designwright.discord.mudbot.input.runnable.AbstractRunnableEvent;
import com.designwright.discord.mudbot.net.ConnectionInfo;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrivateMessageRunnableEvent extends AbstractRunnableEvent {

    private final MessageParserFactory messageParserFactory;
    private ConnectionInfo userConnectionInfo;

    public PrivateMessageRunnableEvent(
            InternalApi internalApi,
            MessageParserFactory messageParserFactory,
            MessageEvent messageEvent
    ) {
        super(internalApi, messageParserFactory, messageEvent);
        this.messageParserFactory = messageParserFactory;
    }

    protected void runEvent() {
        PrivateMessageReceivedEvent event = messageEvent.getEvent();
        Actionable actionable;

        loadUserConnectionInformation(event.getAuthor());

        if (!event.getAuthor().isBot()) {
            MessageParser parser = messageParserFactory.createParser(event);
            actionable = parser.parse();

            if (userConnectionInfo.getUser() == null) {
                handleAction(actionable, false);
            } else if (userConnectionInfo.getAvatar() == null || !userConnectionInfo.getAvatar().isValid()) {
                handleAvatarMenuDialogue(actionable);
            } else {
                handleAction(actionable, true);
            }
        }
    }

    void loadUserConnectionInformation(net.dv8tion.jda.api.entities.User discordUser) {
        InternalRequest<ConnectionInfo> userConnectionResponse = internalApi.sendAndReceive(
                new InternalRequest<>(new ConnectionInfo(discordUser, null, null), InternalRequest.Type.READ)
        );

        if (userConnectionResponse.getPayload().isEmpty()) {
            userConnectionInfo = new ConnectionInfo(null, null, null);
        } else {
            userConnectionInfo = userConnectionResponse.getPayloadFirst();
        }
    }

    void handleAction(Actionable actionable, boolean isLoggedIn) {
        UserAction action = actionable.getAction();

        if (action instanceof AnimateAction) {
            if (isLoggedIn) {
                handleAnimateAction(actionable);
            } else {
                replyToSender("Hi =), if you'd like to interact, please '/login'!");
            }
        } else if (action instanceof CommandAction) {
            handleCommandAction(actionable);
        } else {
            replyToSender(((InvalidAction) action).getMessage());
        }
    }

    void handleAnimateAction(Actionable actionable) {
        AnimateAction action = (AnimateAction) actionable.getAction();

        if (userConnectionInfo.getDiscordUser() != null) {
            replyToSender("Sorry, I don't do anything else yet.");
        } else {
            replyToSender("You must login before we can interact! Type '/login'");
        }
    }

    void handleCommandAction(Actionable actionable) {
        CommandAction action = (CommandAction) actionable.getAction();

        if ("logout".equalsIgnoreCase(action.getCommandName())) {
            handleLogoutAction();
        } else if ("login".equalsIgnoreCase(action.getCommandName())) {
            handleLoginAction();
        } else {
            if (userConnectionInfo.getDiscordUser() != null) {
                replyToSender("'/" + action.getCommandName() + "' is not a valid command. Are you hacking?");
            } else {
                replyToSender("I can't interact with you yet, you need to '/login'!");
            }
        }
    }

    void handleLogoutAction() {
        if (userConnectionInfo.getDiscordUser() != null) {
            InternalRequest<ConnectionInfo> connectionInfoResponse = internalApi.sendAndReceive(
                    new InternalRequest<>(
                            new ConnectionInfo(messageEvent.getEvent().getAuthor(), null, null),
                            InternalRequest.Type.DELETE
                    )
            );
            if (!connectionInfoResponse.hasError()) {
                log.info(userConnectionInfo.getDiscordUser().getName() + "(" + userConnectionInfo.getDiscordUser().getId() + ") logged out.");
                replyToSender("Goodbye!");
            } else {
                log.error("Issue disconnecting user", new PersistenceException(connectionInfoResponse.getMessage()));
                replyToSender("Can't log you out, you're stuck here!");
            }
        } else {
            replyToSender("You're not logged in! How can I log you out?!");
        }
    }

    void handleLoginAction() {
        if (userConnectionInfo.getDiscordUser() == null) {
            try {
                createUserConnectionInformation();

                log.info(userConnectionInfo.getDiscordUser().getName() + "(" + userConnectionInfo.getDiscordUser().getId() + ") logged in.");
                replyToSender("Welcome, " + userConnectionInfo.getDiscordUser().getName() + "!");

                AvatarCreationDialogueSequence sequence = new AvatarCreationDialogueSequence(internalApi, userConnectionInfo);
                sequence.next();
            } catch (PersistenceException e) {
                log.error("Failed to persist user", e);
                replyToSender("Couldn't log you in! =(");
            }
        } else {
            replyToSender("Uh... we already did that...");
        }
    }

    void handleAvatarMenuDialogue(Actionable actionable) {
        UserAction action = actionable.getAction();

        if (action instanceof CommandAction) {
            CommandAction commandAction = (CommandAction) action;
            if (commandAction.getCommandName().equalsIgnoreCase("logout")) {
                handleLogoutAction();
            } else {
                replyToSender("You can't execute commands while picking an avatar (except logout), naughty!");
            }
        } else if (action instanceof AnimateAction) {
            String message = ((AnimateAction)action).getPhrase();

            AvatarCreationDialogueSequence sequence = new AvatarCreationDialogueSequence(internalApi, userConnectionInfo);
            sequence.query(message);
        } else {
            log.error("Invalid action type in avatar menu");
            log.error(action.toString());
            replyToSender("I don't know what you are trying to do.");
        }
    }

    void createUserConnectionInformation() {
        User user = getOrCreateUser(messageEvent.getEvent().getAuthor());

        InternalRequest<ConnectionInfo> connectionInfoResponse = internalApi.sendAndReceive(
                new InternalRequest<>(
                        new ConnectionInfo(
                                messageEvent.getEvent().getAuthor(),
                                messageEvent.getEvent().getChannel(),
                                user
                        ),
                        InternalRequest.Type.CREATE
                )
        );

        if (connectionInfoResponse.hasError()) {
            throw new PersistenceException(connectionInfoResponse.getMessage());
        }

        userConnectionInfo = connectionInfoResponse.getPayloadFirst();
    }

    User getOrCreateUser(net.dv8tion.jda.api.entities.User discordUser) {
        User userLookup = new User();
        userLookup.setDiscordId(discordUser.getId());

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

    void replyToSender(String message) {
        messageEvent.getEvent().getChannel().sendMessage(message).queue();
    }

}
