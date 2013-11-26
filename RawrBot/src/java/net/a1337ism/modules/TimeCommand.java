package net.a1337ism.modules;

import java.util.Date;

import net.a1337ism.RawrBot;
import net.a1337ism.util.ircUtil;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Using this command as a template for how commands should be made.
public class TimeCommand extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger = LoggerFactory.getLogger(RawrBot.class);

    // Check for channel messages.
    public void onMessage(MessageEvent event) throws Exception {

        // Check if message starts with !time
        if (event.getMessage().trim().toLowerCase().startsWith("!time")) {

            // If they are rate limited, then return.
            if (RateLimiter.isRateLimited(event.getUser().getNick()))
                return;

            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                // If message ends with -help or -h, then send them help information
                String timeHelp = "!time : Displays the current time.";
                ircUtil.sendMessage(event, timeHelp);

            } else {
                String newDate = "The current time is " + new Date();
                ircUtil.sendMessage(event, newDate);
            }
        }
    }

    // Check for private messages.
    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {

        // Check if message starts with !time
        if (event.getMessage().trim().toLowerCase().startsWith("!time")) {

            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                // If message ends with -help or -h, then send them help information
                String timeHelp = "!time : Displays the current time.";
                ircUtil.sendMessage(event, timeHelp);

            } else {
                String newDate = "The current time is " + new Date();
                ircUtil.sendMessage(event, newDate);
            }
        }
    }
}