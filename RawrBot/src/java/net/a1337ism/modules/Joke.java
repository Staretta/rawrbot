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

import com.fasterxml.jackson.databind.JsonNode;

public class Joke extends ListenerAdapter {
    private static Logger logger = LoggerFactory.getLogger(RawrBot.class);

    public void onMessage(MessageEvent event) throws Exception {
        if (event.getMessage().trim().toLowerCase().startsWith("!joke")) {
            if (RateLimiter.isRateLimited(event.getUser().getNick()))
                return;

            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                // If message ends with -help or -h, then send them the help information
                String jokeHelp = "!joke : Says a random joke from the Internet Chuck Norris Database.";
                ircUtil.sendMessage(event, jokeHelp);

            } else {
                // It's currently in an array because I wanted to know what ID number the joke was using in the event
                // that I needed to debug escape characters.
                Object[] joke = null;
                joke = getJoke();

                // If joke isn't null, meaning it didn't crash, then do the good stuff!
                if (joke != null) {
                    ircUtil.sendMessage(event, joke[1].toString());
                }
            }
        }
    }

    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        if (event.getMessage().trim().toLowerCase().startsWith("!joke")) {
            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                // If message ends with -help or -h, then send them the help information
                String jokeHelp = "!joke : Says a random joke from the Internet Chuck Norris Database.";
                ircUtil.sendMessage(event, jokeHelp);
            } else {
                // It's currently in an array because I wanted to know what ID number the joke was using in the event
                // that I needed to debug escape characters.
                Object[] joke = null;
                joke = getJoke();

                // If joke isn't null, meaning it didn't crash, then do the good stuff!
                if (joke != null) {
                    ircUtil.sendMessage(event, joke[1].toString());
                }
            }
        }
    }

    private Object[] getJoke() throws IOException, JSONException {
        // grabs JSONobject and stores it into json for us to read from
        JsonNode json = Json
                .readJsonFromUrl("http://api.icndb.com/jokes/random?escape=javascript&firstName=Rawr&lastName=Bot");
        // stores the specific values I want into an array to be used later.
        Object[] array = { (json.get("value")).get("id").asText(), (json.get("value")).get("joke").asText() };
        return array;
    }
}