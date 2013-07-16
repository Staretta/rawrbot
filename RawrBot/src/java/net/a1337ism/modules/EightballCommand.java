package net.a1337ism.modules;

import net.a1337ism.RawrBot;
import net.a1337ism.util.FileUtil;
import net.a1337ism.util.MiscUtil;
import net.a1337ism.util.ircUtil;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EightballCommand extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger     = LoggerFactory.getLogger(RawrBot.class);
    private String        filename   = "data/eightball_answers.txt";
    private String[]      answerList = FileUtil.readLines(filename);

    public void onMessage(MessageEvent event) throws Exception {
        // Check if message starts with !8ball or !eightball
        if ((event.getMessage().trim().toLowerCase().startsWith("!8ball") || event.getMessage().trim().toLowerCase()
                .startsWith("!eightball"))
                && event.getMessage().trim().toLowerCase().endsWith("?")) {

            // If they are rate limited, then return.
            if (RateLimiter.isRateLimited(event.getUser().getNick())) {
                return;
            }

            String answer = MiscUtil.randomSelection(answerList);
            ircUtil.sendMessage(event, answer);
        }
    }

    public void onPrivateMessage(PrivateMessageEvent event) {
        // Check if message starts with !8ball or !eightball
        if ((event.getMessage().trim().toLowerCase().startsWith("!8ball") || event.getMessage().trim().toLowerCase()
                .startsWith("!eightball"))
                && event.getMessage().trim().toLowerCase().endsWith("?")) {

            String answer = MiscUtil.randomSelection(answerList);
            ircUtil.sendMessage(event, answer);
        }
    }
}