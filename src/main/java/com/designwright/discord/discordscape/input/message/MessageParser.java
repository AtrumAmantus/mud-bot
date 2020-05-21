package com.designwright.discord.discordscape.input.message;

import com.designwright.discord.discordscape.input.action.Actionable;
import com.designwright.discord.discordscape.input.enums.UserType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public abstract class MessageParser {

    protected final MessageEvent messageEvent;
    private final UserType userType;

    public abstract Actionable parse();

    protected String formatMessage(String message, String prefixRegEx) {
        return StringUtils.trim(
                message.replaceFirst(prefixRegEx, "")
        );
    }

}
