package net.a1337ism.modules;

import net.a1337ism.RawrBot;
import net.a1337ism.util.ircUtil;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Help extends ListenerAdapter {
    private static Logger logger = LoggerFactory.getLogger(RawrBot.class);

    public void onMessage(MessageEvent event) throws Exception {
        if (event.getMessage().trim().toLowerCase().startsWith("!commands")
                || event.getMessage().trim().toLowerCase().startsWith("!help")) {
            if (RateLimiter.isRateLimited(event.getUser().getNick()))
                return;

            String[] commandHelp = { "Commands: !joke !quote !rawr !8ball !lastseen !report !uptime !time !commands",
                    "For command specific help, type \"-help\" after a command." };
            for (String line : commandHelp) {
                ircUtil.sendMessage(event, line);
            }
        }
    }

    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        if (event.getMessage().trim().toLowerCase().startsWith("!commands")
                || event.getMessage().trim().toLowerCase().startsWith("!help")) {
            String[] commandHelp = { "Commands: !joke !quote !rawr !8ball !lastseen !report !uptime !time !commands",
                    "For command specific help, type \"-help\" after a command." };
            for (String line : commandHelp) {
                ircUtil.sendMessage(event, line);
            }
        }
    }
}