package net.a1337ism.modules;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.a1337ism.RawrBot;
import net.a1337ism.util.ircUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class RawrCommand extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger logger    = LogManager.getFormatterLogger(RawrBot.class);
    private static Marker LOG_EVENT = MarkerManager.getMarker("LOG_EVENT");
    public String         filename  = "data/rawr.txt";                            // Set the rawr.txt location
    public String[]       rawrList  = readLines(filename);                        // Throw the lines into a list

    // Check for channel messages
    public void onMessage(MessageEvent event) throws Exception {
        // Check if message starts with !RAWR
        if (event.getMessage().trim().toLowerCase().startsWith("!rawr")) {
            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                String rawrHelp = "!rawr: Rawr. :3";
                ircUtil.sendMessage(event, rawrHelp);
            } else if (event.getMessage().trim().startsWith("!RAWR")) {
                String rawr = getRawr().toUpperCase();
                ircUtil.sendMessage(event, rawr);
            } else {
                String rawr = getRawr();
                ircUtil.sendMessage(event, rawr);
            }
        }
    }

    // Check for private messages
    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        // Check if message starts with !RAWR
        if (event.getMessage().trim().toLowerCase().startsWith("!rawr")) {
            if (event.getMessage().trim().toLowerCase().endsWith("-help")
                    || event.getMessage().trim().toLowerCase().endsWith("-h")) {
                String rawrHelp = "!rawr: Rawr. :3";
                ircUtil.sendMessage(event, rawrHelp);
            } else if (event.getMessage().trim().startsWith("!RAWR")) {
                String rawr = getRawr().toUpperCase();
                ircUtil.sendMessage(event, rawr);
            } else {
                String rawr = getRawr();
                ircUtil.sendMessage(event, rawr);
            }
        }
    }

    // Reads the lines from the text file and returns a list
    private String[] readLines(String filename) {
        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<String> lines = new ArrayList<String>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
            return lines.toArray(new String[lines.size()]);
        } catch (IOException ex) {
            logger.error(ex);
        }
        return null;
    }

    private int randomSelection() throws Exception {
        // Randomly select a number based on the length of the list.
        // Do not want out of index errors.
        int randomNumber = (int) (Math.random() * rawrList.length);
        return randomNumber;
    }

    private String getRawr() throws Exception {
        // Choose a random rawr based on the random number selected.
        String randomRawr = rawrList[randomSelection()];
        return randomRawr;
    }
}