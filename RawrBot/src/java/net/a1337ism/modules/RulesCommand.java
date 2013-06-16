package net.a1337ism.modules;

import net.a1337ism.RawrBot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class RulesCommand extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger    = LogManager.getFormatterLogger(RawrBot.class);
    private static Marker LOG_EVENT = MarkerManager.getMarker("LOG_EVENT");

    public void onMessage(MessageEvent event) throws Exception {
        if (event.getMessage().toLowerCase().trim().startsWith("!rules")) {
        	// If they are rate limited, then return. 
            if (RateLimiter.isRateLimited(event.getUser().getNick()))
                return;
            // ircUtil.sendNotice(event, "someMessage");
        }
    }

    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {

    }

}