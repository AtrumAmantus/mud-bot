//package dev.codesupport.discord.discordscape.input.message.impl;
//
//import dev.codesupport.discord.discordscape.input.action.Actionable;
//import dev.codesupport.discord.discordscape.input.message.MessageParser;
//import dev.codesupport.discord.discordscape.net.UserConnections;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
//import org.slf4j.MarkerFactory;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class AdminMessageParser implements MessageParser {
//
//    private final UserConnections userConnections;
//
//    public Actionable parse(PrivateMessageReceivedEvent event) {
//        String message = event.getMessage().getContentRaw();
//
//        if (userConnections.isConnected(event.getAuthor())) {
//            if (message.equalsIgnoreCase("logout")) {
//                log.info(MarkerFactory.getMarker("CONN"), event.getAuthor() + " logged out.");
//                event.getChannel().sendMessage("Goodbye.").queue();
//                userConnections.removeConnection(event.getAuthor());
//            } else {
//                log.info(MarkerFactory.getMarker("CHAT"), event.getAuthor() + " said: " + event.getMessage().getContentRaw());
//                userConnections.getConnections().forEach(connectionInfo -> {
//                    if (connectionInfo.getUser() != event.getAuthor()) {
//                        connectionInfo.getPrivateChannel().sendMessage(event.getAuthor().getName() + " said: " + message).queue();
//                    }
//                });
//            }
//        } else {
//            if (message.equalsIgnoreCase("login")) {
//                log.info(MarkerFactory.getMarker("CONN"), event.getAuthor() + " logged in.");
//                event.getChannel().sendMessage("You are logged in, type `logout` to stop getting messages").queue();
//                userConnections.addConnection(event.getAuthor(), event.getChannel());
//                userConnections.getConnections().forEach(
//                        connectionInfo -> connectionInfo.getPrivateChannel().sendMessage(event.getAuthor().getName() + " logged in.")
//                                .queue());
//            } else {
//                event.getChannel().sendMessage("You must login first to interact with me.  Type `login`").queue();
//            }
//        }
//    }
//
//}
