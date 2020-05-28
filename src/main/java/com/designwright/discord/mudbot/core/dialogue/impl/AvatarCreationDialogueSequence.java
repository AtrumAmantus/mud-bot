package com.designwright.discord.mudbot.core.dialogue.impl;

import com.designwright.discord.mudbot.core.dialogue.DialogueSequence;
import com.designwright.discord.mudbot.core.exception.PersistenceException;
import com.designwright.discord.mudbot.core.request.InternalApi;
import com.designwright.discord.mudbot.core.request.InternalRequest;
import com.designwright.discord.mudbot.data.domain.Avatar;
import com.designwright.discord.mudbot.data.enums.Gender;
import com.designwright.discord.mudbot.net.ConnectionInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class AvatarCreationDialogueSequence extends DialogueSequence {

    private static final String NAME_GROUP = "name";
    private static final Pattern CREATE_PATTERN = Pattern.compile("^create\\s+(?<" + NAME_GROUP + ">[a-zA-Z]{5,15})\\s*");

    public AvatarCreationDialogueSequence(InternalApi internalApi, ConnectionInfo userConnectionInfo) {
        super(internalApi, userConnectionInfo);
    }

    @Override
    public void next() {
        String message = getNextCreationLevel().prompt;

        replyToUser(message);
    }

    //TODO: Break up this massively over-complex method.
    //<3 iffy
    @Override
    public String query(String message) {
        String response = null;
        Level level = getNextCreationLevel().getLevel();

        switch (level) {
            case NAME:
                Matcher matcher = CREATE_PATTERN.matcher(message);
                if (matcher.matches()) {
                    Avatar avatar = new Avatar();
                    avatar.setName(matcher.group(NAME_GROUP));
                    avatar.setUser(userConnectionInfo.getUser());

                    InternalRequest<Avatar> avatarResponse = internalApi.sendAndReceive(
                            new InternalRequest<>(avatar, InternalRequest.Type.CREATE)
                    );

                    if (!avatarResponse.hasError()) {
                        ConnectionInfo updatedConnectionInfo = new ConnectionInfo(
                                userConnectionInfo.getDiscordUser(),
                                userConnectionInfo.getPrivateChannel(),
                                userConnectionInfo.getUser()
                        );
                        updatedConnectionInfo.setAvatar(avatarResponse.getPayloadFirst());

                        InternalRequest<ConnectionInfo> connectionInfoResponse = internalApi.sendAndReceive(
                                new InternalRequest<>(updatedConnectionInfo, InternalRequest.Type.UPDATE)
                        );
                        if (!connectionInfoResponse.hasError()) {
                            userConnectionInfo.setAvatar(connectionInfoResponse.getPayloadFirst().getAvatar());
                            replyToUser(getNextCreationLevel().prompt);
                        } else {
                            log.error(connectionInfoResponse.getMessage());
                            replyToUser("I made your avatar, but... who are you again?...");
                        }
                    } else {
                        log.error(avatarResponse.getMessage());
                        replyToUser("I can't make that avatar! I don't know why either!");
                    }
                } else if (message.startsWith("create ")) {
                    replyToUser("Invalid entry, names must be 5 to 15 letters long.");
                } else {
                    Avatar findAvatar = new Avatar();
                    findAvatar.setName(message);

                    InternalRequest<Avatar> avatarResponse = internalApi.sendAndReceive(
                            new InternalRequest<>(findAvatar, InternalRequest.Type.READ)
                    );
                    if (!avatarResponse.hasError()) {
                        if (!avatarResponse.getPayload().isEmpty()) {
                            Avatar avatar = avatarResponse.getPayloadFirst();

                            ConnectionInfo updatedConnectionInfo = new ConnectionInfo(userConnectionInfo);
                            updatedConnectionInfo.setAvatar(avatar);

                            InternalRequest<ConnectionInfo> connectionInfoResponse = internalApi.sendAndReceive(
                                    new InternalRequest<>(updatedConnectionInfo, InternalRequest.Type.UPDATE)
                            );

                            if (!connectionInfoResponse.hasError()) {
                                ConnectionInfo connectionInfo = connectionInfoResponse.getPayloadFirst();
                                replyToUser("Welcome to the world, " + avatar.getName() + "!");
                                userConnectionInfo.setAvatar(connectionInfo.getAvatar());
                            } else {
                                replyToUser("I found your avatar, but then I lost you!");
                            }
                        } else {
                            replyToUser("Sorry! I can't find an avatar named '" + message + "', want to create one?");
                        }
                    } else {
                        replyToUser("Couldn't find your avatar! Did you forget where you put them?");
                    }
                }
                break;
            case GENDER:
                List<String> genders = Arrays.asList("M", "F", "U");
                String enteredGender = message.toUpperCase();
                if (genders.contains(enteredGender)) {
                    Avatar avatar = new Avatar(userConnectionInfo.getAvatar()); // Shallow copy
                    avatar.setGender(Gender.valueOf(enteredGender));

                    InternalRequest<Avatar> avatarResponse = internalApi.sendAndReceive(
                            new InternalRequest<>(avatar, InternalRequest.Type.UPDATE)
                    );

                    if (!avatarResponse.hasError()) {
                        replyToUser("Welcome to the world, " + avatar.getName() + "!");
                        userConnectionInfo.setAvatar(avatarResponse.getPayloadFirst());
                    } else {
                        replyToUser("Hmm, couldn't do that.");
                        log.error("Failed to update avatar", new PersistenceException(avatarResponse.getMessage()));
                    }
                } else {
                    replyToUser("Sorry, I'm not familiar with that gender. Try again?");
                }
                break;
            default:
                Avatar avatar = userConnectionInfo.getAvatar();
                log.error("Avatar creation desync, user input '" + message + "'", avatar);
                replyToUser("Sorry, what were we talking about?");
                break;
        }

        return response;
    }

    AvatarCreationLevel getNextCreationLevel() {
        AvatarCreationLevel level;
        Avatar avatar = userConnectionInfo.getAvatar();

        if (avatar == null) {
            StringBuilder messageBuilder = new StringBuilder("Please select your avatar:\n");

            Avatar findAvatar = new Avatar();
            findAvatar.setUser(userConnectionInfo.getUser());

            List<Avatar> userAvatars = internalApi.sendAndReceive(
                    new InternalRequest<>(findAvatar, InternalRequest.Type.READ)
            ).getPayload();

            String userMessage = userAvatars.stream().map(Avatar::getName).collect(Collectors.joining("\n"));
            if (userMessage.isEmpty()) {
                userMessage = "No avatars exist.";
            }
            messageBuilder.append(userMessage);
            messageBuilder.append("\n" +
                            "(type 'create <avatar>' to create a new avatar, names are 5 - 15 letters)"
            );
            level = new AvatarCreationLevel(messageBuilder.toString(), Level.NAME);
        } else {
            if (avatar.getName() == null) {
                level = new AvatarCreationLevel("What's your avatar's name?", Level.NAME);
            } else if (avatar.getGender() == null) {
                level = new AvatarCreationLevel("Is " + avatar.getName() + " a (m)ale, (f)emale, or (u)nknown/unspecified?", Level.GENDER);
            } else {
                level = null;
            }
        }

        return level;
    }

    @Data
    static class AvatarCreationLevel {

        private final String prompt;
        private final Level level;

    }

    private enum Level {
        NAME,
        GENDER
    }

}
