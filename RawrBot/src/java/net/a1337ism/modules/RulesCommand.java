package net.a1337ism.modules;

import net.a1337ism.RawrBot;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RulesCommand extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger        = LoggerFactory.getLogger(RawrBot.class);

    // Set up the database stuff
    String                sUrlString    = "jdbc:sqlite:data/rules.db";
    String                sDriverString = "org.sqlite.JDBC";

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