package com.designwright.discord.mudbot.input.message;

import com.designwright.discord.mudbot.core.user.CommandDictionary;
import com.designwright.discord.mudbot.input.action.Actionable;
import lombok.Data;

@Data
public abstract class MessageParser {

    protected final MessageEvent messageEvent;
    protected final CommandDictionary commandDictionary;

    public abstract Actionable parse();

}
