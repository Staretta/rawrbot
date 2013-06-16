package net.a1337ism.util;

import java.util.Iterator;
import java.util.Set;

import net.a1337ism.RawrBot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class ircUtil {

    // Set up the logger stuff
    private static Logger logger    = LogManager.getFormatterLogger(RawrBot.class);
    private static Marker LOG_EVENT = MarkerManager.getMarker("LOG_EVENT");

    public static void sendMessage(MessageEvent event, String message) {
        sendMessageChannel(event, message);
    }

    public static void sendMessage(PrivateMessageEvent event, String message) {
        sendMessagePrivate(event, message);
    }

    public static void sendMessageChannel(MessageEvent event, String message) {
        // This function is used by all the commands. DO NOT DELETE.
        // Log the sent message
        logger.info(LOG_EVENT, "<" + event.getBot().getName() + "> " + message);

        // Send a channel message
        event.getBot().sendMessage(event.getChannel(), message);
    }

    public static void sendMessagePrivate(PrivateMessageEvent event, String message) {
        // This function is used by all the commands. DO NOT DELETE.
        // Log the sent message
        logger.info(LOG_EVENT, "(" + event.getBot().getName() + "->" + event.getUser().getNick() + ") " + message);

        // Send a private message
        event.getBot().sendMessage(event.getUser(), message);
    }

    public static void sendNotice(MessageEvent event, String message) {
        // This function is used by all the commands. DO NOT DELETE.
        // Log the sent message
        logger.info(LOG_EVENT, "->" + event.getUser().getNick() + "<- " + message);

        // Send the notice
        event.getBot().sendNotice(event.getUser().getNick(), message);
    }

    public static void sendNotice(PrivateMessageEvent event, String target, String message) {
        // This function is used by all the commands. DO NOT DELETE.
        // Log the sent message
        logger.info(LOG_EVENT, "->" + target + "<- " + message);

        // Send the notice
        event.getBot().sendNotice(target, message);
    }

    public static Set<User> channelOPs(MessageEvent event, String channel) {
        // Returns a list of nicknames of channel operators, divided by channel in the list.
        // Based on channels that the bot is currently in.
        // Something like { #channel1 { Operator1, Operator2, Operator3 }, #channel2 { Operator4, Operator5 } }
        return null;
    }

    public static Set<User> channelOPs(PrivateMessageEvent event, String channel) {
        // Returns a list of nicknames of channel operators, divided by channel in the list.
        // Based on channels that the bot is currently in.
        // Something like { #channel1 { Operator1, Operator2, Operator3 }, #channel2 { Operator4, Operator5 } }
        return null;
    }

    public static Set<User> channelVoiced(MessageEvent event, String channel) {
        // Returns a list of nicknames of channel voiced, divided by channel in the list.
        // Based on channels that the bot is currently in.
        // Something like { #channel1 { Voiced1, Voiced2, Voiced3 }, #channel2 { Voiced4, Voiced5 } }
        return null;
    }

    public static Set<User> channelVoiced(PrivateMessageEvent event, String channel) {
        // Returns a list of nicknames of channel voiced, divided by channel in the list.
        // Based on channels that the bot is currently in.
        // Something like { #channel1 { Voiced1, Voiced2, Voiced3 }, #channel2 { Voiced4, Voiced5 } }
        return null;
    }

    public static boolean isOP(MessageEvent event, String channel) {
        // See if user is an operator of the specified channel.

        // Initialize the variable.
        boolean isOP = false;

        // Get list of operators in a channel.
        Set<User> operators = event.getBot().getChannel(channel).getOps();
        Iterator<User> itr = operators.iterator();
        // Step through the set, and see if the user is an operator.
        while (itr.hasNext()) {
            if (itr.next().getNick().equalsIgnoreCase(event.getUser().getNick()))
                isOP = true;
        }

        return isOP;
    }

    public static boolean isOP(PrivateMessageEvent event, String channel) {
        // See if user is an operator of the specified channel.

        // Initialize the variable.
        boolean isOP = false;

        // Get list of operators in a channel.
        Set<User> operators = event.getBot().getChannel(channel).getOps();
        Iterator<User> itr = operators.iterator();
        // Step through the set, and see if the user is an operator.
        while (itr.hasNext()) {
            if (itr.next().getNick().equalsIgnoreCase(event.getUser().getNick()))
                isOP = true;
        }

        return isOP;
    }
}