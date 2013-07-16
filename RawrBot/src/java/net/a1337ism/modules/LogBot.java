package net.a1337ism.modules;

import net.a1337ism.RawrBot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.ReconnectEvent;
import org.pircbotx.hooks.events.VoiceEvent;

public class LogBot extends ListenerAdapter {
    public static Logger       logger    = LogManager.getFormatterLogger(RawrBot.class);
    public static final Marker LOG_EVENT = MarkerManager.getMarker("LOG_EVENT");

    @Override
    public void onDisconnect(DisconnectEvent event) throws Exception {
        // Log the bot disconnecting from the server.
        logger.info(LOG_EVENT, "Disconnected from server");
    }

    @Override
    public void onReconnect(ReconnectEvent event) throws Exception {
        // Log the bot reconnecting to the server
        logger.info(LOG_EVENT, "Reconnecting to server");
    }

    @Override
    public void onConnect(ConnectEvent event) throws Exception {
        // Log the bot connecting to the server
        logger.info(LOG_EVENT, event.getBot().getNick() + " has connected to " + event.getBot().getServer());
    }

    @Override
    public void onMessage(final MessageEvent event) throws Exception {
        // Log the channel message
        logger.info(LOG_EVENT, "<" + event.getUser().getNick() + "> " + event.getMessage());
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        // Log the private message
        logger.info(LOG_EVENT,
                "(" + event.getUser().getNick() + "->" + event.getBot().getName() + ") " + event.getMessage());
    }

    @Override
    public void onAction(ActionEvent event) throws Exception {
        // Need to filter out the bot doing an action other people doing an action.
        if (event.getUser().getNick().compareTo(event.getBot().getNick()) != 0) {
            // Log the user who did the action.
            logger.info(LOG_EVENT, "* " + event.getUser().getNick() + " " + event.getMessage());
        }
    }

    @Override
    public void onNotice(NoticeEvent event) throws Exception {
        // Log incoming notice messages. Probably not that important, but could be useful.
        logger.info(LOG_EVENT, "-" + event.getUser().getNick() + "- " + event.getNotice());
    }

    @Override
    public void onJoin(JoinEvent event) throws Exception {
        // Need to filter out the bot joining the channel from other people joining the channel.
        if (event.getUser().getNick().compareTo(event.getBot().getNick()) != 0) {
            // Log the user who joined channel.
            logger.info(LOG_EVENT, event.getUser().getNick() + " (" + event.getUser().getLogin() + "@"
                    + event.getUser().getHostmask() + ") has joined.");
        } else {
            // Log the bot joining a channel
            logger.info(LOG_EVENT, event.getBot().getNick() + " has joined the channel " + event.getChannel().getName());
        }
    }

    @Override
    public void onPart(PartEvent event) throws Exception {
        // Need to filter out the bot leaving the channel from other people leaving the channel.
        if (event.getUser().getNick().compareTo(event.getBot().getNick()) != 0) {
            // Log the user who left channel.
            logger.info(LOG_EVENT, event.getUser().getNick() + " has left.");
        }
    }

    @Override
    public void onQuit(QuitEvent event) throws Exception {
        // Need to filter out the bot quitting the server from other people quitting the server.
        if (event.getUser().getNick().compareTo(event.getBot().getNick()) != 0) {
            // Log the user who quit the server.
            logger.info(LOG_EVENT, event.getUser().getNick() + " has quit. (" + event.getReason() + ")");
        }
    }

    @Override
    public void onNickChange(NickChangeEvent event) throws Exception {
        // Need to filter out the bot changing name from other people changing names.
        if (event.getUser().getNick().compareTo(event.getBot().getNick()) != 0) {
            // Log the user who changed their name.
            logger.info(LOG_EVENT, event.getOldNick() + " is now known as " + event.getNewNick());
        }
    }

    @Override
    public void onKick(KickEvent event) throws Exception {
        // Need to filter out the bot getting kicked from other people getting kicked.
        if (event.getRecipient().getNick().compareTo(event.getBot().getNick()) != 0) {
            // Log the user who was kicked
            logger.info(LOG_EVENT, event.getSource().getNick() + " has kicked " + event.getRecipient().getNick()
                    + " from " + event.getChannel().getName() + " (" + event.getReason() + ")");
        } else {
            // Log the bot getting kicked.
            logger.info(LOG_EVENT, "You have been kicked from " + event.getChannel().getName() + " by "
                    + event.getSource().getNick() + " (" + event.getReason() + ")");
            // TODO: Add a rejoin function, or maybe an ERROR, and send an email or text message that the bot was
            // kicked?
        }
    }

    @Override
    public void onOp(OpEvent event) throws Exception {
        // Need to filter out the bot getting Operator from other people getting Operator
        if (event.getRecipient().getNick().compareTo(event.getBot().getNick()) != 0) {
            if (event.isOp() == true) {
                // Log the user getting Operator added
                logger.info(LOG_EVENT, event.getSource().getNick() + " gives channel operator status to "
                        + event.getRecipient().getNick());
            } else {
                // Log the user getting Operator removed
                logger.info(LOG_EVENT, event.getSource().getNick() + " removes channel operator status from "
                        + event.getRecipient().getNick());
            }
        } else {
            if (event.isOp() == true) {
                // Log the bot getting Operator added
                logger.info(LOG_EVENT, event.getSource().getNick() + " gives you channel operator status");
            } else {
                // Log the bot getting Operator removed
                logger.info(LOG_EVENT, event.getSource().getNick() + " removes your channel operator status");
            }
        }
    }

    @Override
    public void onVoice(VoiceEvent event) throws Exception {
        // Need to filter out the bot getting Voice from other people getting Voice
        if (event.getRecipient().getNick().compareTo(event.getBot().getNick()) != 0) {
            if (event.hasVoice() == true) {
                // Log the user getting Voice added
                logger.info(LOG_EVENT, event.getSource().getNick() + " gives channel voice status to "
                        + event.getRecipient().getNick());
            } else {
                // Log the user getting Voice removed
                logger.info(LOG_EVENT, event.getSource().getNick() + " removes channel voice status from "
                        + event.getRecipient().getNick());
            }
        } else {
            if (event.hasVoice() == true) {
                // Log the bot getting voice added
                logger.info(LOG_EVENT, event.getSource().getNick() + " gives you channel voice status");
            } else {
                // Log the bot getting voice removed
                logger.info(LOG_EVENT, event.getSource().getNick() + " removes your channel voice status");
            }
        }
    }
}
