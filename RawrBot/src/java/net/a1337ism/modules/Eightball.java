package net.a1337ism.modules;

import net.a1337ism.RawrBot;
import net.a1337ism.util.Config;
import net.a1337ism.util.FileUtil;
import net.a1337ism.util.MiscUtil;
import net.a1337ism.util.ircUtil;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Eightball extends ListenerAdapter {
    private static Logger logger     = LoggerFactory.getLogger(RawrBot.class);
    private Config        cfg        = new Config("././config.properties");
    private String        bot_owner  = cfg.getProperty("bot_owner");
    private String        filename   = "data/eightball_answers.txt";
    private String[]      answerList = FileUtil.readLines(filename);

    public void onMessage(MessageEvent event) throws Exception {
        if ((event.getMessage().trim().toLowerCase().startsWith("!8ball")
                || event.getMessage().trim().toLowerCase().startsWith("!eightball") || event.getMessage().trim()
                .toLowerCase().startsWith("!8-ball"))
                && event.getMessage().trim().toLowerCase().endsWith("?")
                && !RateLimiter.isRateLimited(event.getUser().getNick())) {
            // Check if message starts with !8ball or !eightball, and ends with "?", and if they are not rate limited

            String answer = MiscUtil.randomSelection(answerList);
            ircUtil.sendMessage(event, answer);
        }
    }

    public void onPrivateMessage(PrivateMessageEvent event) {
        if ((event.getMessage().trim().toLowerCase().startsWith("!8ball")
                || event.getMessage().trim().toLowerCase().startsWith("!eightball") || event.getMessage().trim()
                .toLowerCase().startsWith("!8-ball"))
                && event.getMessage().trim().toLowerCase().endsWith("?")) {
            // Check if message starts with !8ball or !eightball and ends with "?"

            String answer = MiscUtil.randomSelection(answerList);
            ircUtil.sendMessage(event, answer);

        } else if (event.getMessage().trim().toLowerCase().equalsIgnoreCase("!8ball -reload")
                && event.getUser().getNick().equalsIgnoreCase(bot_owner)) {
            // If message equals !8ball reload
            ircUtil.sendMessage(event, "Reloading 8ball reply list");
            answerList = FileUtil.readLines(filename);
        }
    }
}