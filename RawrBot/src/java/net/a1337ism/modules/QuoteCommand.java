package net.a1337ism.modules;

import java.io.IOException;

import net.a1337ism.RawrBot;
import net.a1337ism.util.Json;
import net.a1337ism.util.ircUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.json.JSONException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class QuoteCommand extends ListenerAdapter {
    // Log4j setup stuffs
    private static Logger logger    = LogManager.getFormatterLogger(RawrBot.class);
    private static Marker LOG_EVENT = MarkerManager.getMarker("LOG_EVENT");

    // Check for channel messages
    public void onMessage(MessageEvent event) throws Exception {
        // Check if message starts with !quote
        if (event.getMessage().trim().toLowerCase().startsWith("!quote")) {
        	
        	// If they are rate limited, then return. 
            if (RateLimiter.isRateLimited(event.getUser().getNick()))
                return;
        	
            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                String quoteHelp = "!quote : Says a random quote from the iheartquotes.com Database.";
                ircUtil.sendMessage(event, quoteHelp);
            } else {
                // Throw it into a variable
                Object[] quote = getQuote();
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
                String quoteHelp = "!quote : Says a random quote from the iheartquotes.com Database.";
                ircUtil.sendMessage(event, quoteHelp);
            } else {
                // Throw it into a variable
                Object[] quote = getQuote();
                // If it's not null, continue
                if (quote != null) {
                    ircUtil.sendMessage(event, quote[0].toString());
                }
            }
        }
    }

    private Object[] getQuote() throws IOException, JSONException {
        try {
            // grabs JSONobject and stores it into json for us to read from
            org.json.JSONObject json = Json.readJsonFromUrl("http://www.iheartquotes.com/api/v1/random?"
                    + "format=json&max_lines=1&max_characters=510&source=oneliners");
            // stores the specific values I want into an array to be used later.
            Object[] array = { json.get("quote"), json.get("link") };
            return array;
        } catch (JSONException ex) {
            logger.error("ERROR " + ex);
        } catch (IOException ex) {
            logger.error("ERROR " + ex);
        }
        return null;
    }
}