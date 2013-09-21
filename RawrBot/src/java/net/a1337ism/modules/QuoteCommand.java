package net.a1337ism.modules;

import java.io.IOException;

import net.a1337ism.RawrBot;
import net.a1337ism.util.Json;
import net.a1337ism.util.ircUtil;

import org.json.JSONException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteCommand extends ListenerAdapter {
    // Log4j setup stuffs
    private static Logger logger = LoggerFactory.getLogger(RawrBot.class);

    // Check for channel messages
    public void onMessage(MessageEvent event) throws Exception {

        // Check if message starts with !quote
        if (event.getMessage().trim().toLowerCase().startsWith("!quote")) {

            // If they are rate limited, then return.
            if (RateLimiter.isRateLimited(event.getUser().getNick()))
                return;

            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                // If message ends with -help or -h, then send them help information
                String quoteHelp = "!quote : Says a random quote from the iheartquotes.com Database.";
                ircUtil.sendMessage(event, quoteHelp);

            } else {
                // Throw it into a variable
                Object[] quote = null;
                quote = getQuote();

                // If it's not null, continue
                if (quote != null) {
                    ircUtil.sendMessage(event, quote[0].toString());
                }
            }
        }
    }

    // Check for private messages.
    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {

        // Check if message starts with !quote
        if (event.getMessage().trim().toLowerCase().startsWith("!quote")) {

            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                // If message ends with -help or -h, then send them help information
                String quoteHelp = "!quote : Says a random quote from the iheartquotes.com Database.";
                ircUtil.sendMessage(event, quoteHelp);

            } else {
                // Throw it into a variable
                Object[] quote = null;
                quote = getQuote();

                // If it's not null, continue
                if (quote != null) {
                    ircUtil.sendMessage(event, quote[0].toString());
                }
            }
        }
    }

    private Object[] getQuote() throws IOException, JSONException {
        // grabs JSONobject and stores it into json for us to read from
        org.json.JSONObject json = Json.readJsonFromUrl("http://www.iheartquotes.com/api/v1/random?"
                + "format=json&max_lines=1&max_characters=510&source=oneliners");
        // stores the specific values I want into an array to be used later.
        Object[] array = { json.get("quote"), json.get("link") };
        return array;
    }
}