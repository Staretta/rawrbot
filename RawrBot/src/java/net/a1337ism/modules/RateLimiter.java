package net.a1337ism.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.a1337ism.RawrBot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class RateLimiter extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger            logger       = LogManager.getFormatterLogger(RawrBot.class);
    private static Marker            LOG_EVENT    = MarkerManager.getMarker("LOG_EVENT");

    private static int               timeout      = 600000;                                     // Milliseconds
    private static int               maxRequests  = 5;
    private static Map<String, List> userRequests = new HashMap<String, List>();

    // userRequests is our hashmap of users and how many times they do a command.
    // Basically something like { username : [ millisec1, millisec2 ] } Where username is the key, and the list is the
    // list that contains the millisecond when they entered the command.

    public void onMessage(MessageEvent event) throws Exception {
        if (event.getMessage().startsWith("!")) {
            addRequest(event.getUser().getNick());
        }
    }

    private void addRequest(String nickname) {
        // If the hashmap contains the username as a key
        if (userRequests.containsKey(nickname)) {
            // Make a list, and put that user's requests into the list.
            List<Long> timeList = userRequests.get(nickname);

            // if the list is less than or equal to maxRequests, add the current time to the list, and put it in the
            // hashmap.
            if (timeList.size() <= maxRequests) {
                timeList.add(System.currentTimeMillis());
                userRequests.put(nickname, timeList);
            } else {
                // We need to try and see if any of the times listed in the list can be removed, and then
                // add the current time to it if it's less than the max requests.
                Iterator itr = timeList.listIterator();
                while (itr.hasNext()) {
                    Object timeLast = itr.next();
                    if ((long) timeLast < (System.currentTimeMillis() - timeout))
                        timeList.remove(timeLast);
                }
                if (timeList.isEmpty())
                    userRequests.remove(nickname);
                else if (timeList.size() < maxRequests) {
                    timeList.add(System.currentTimeMillis());
                    userRequests.put(nickname, timeList);
                } else
                    userRequests.put(nickname, timeList);
            }
        } else {
            // If the user is not in the hashmap, then add them to the hashmap along with the current time
            List<Long> timeList = new ArrayList<Long>();
            timeList.add(System.currentTimeMillis());
            userRequests.put(nickname, timeList);
        }
    }

    public static boolean isRateLimited(String nickname) {
        // Cleanup the request queue for the user, if it can be cleaned up.
        cleanupRequest(nickname);

        // Make a list, and put that user's requests into the list.
        List<Long> timeList = userRequests.get(nickname);
        // If the list is greater than or equal to max requests, then return true. Otherwise return false.
        if (timeList.size() >= maxRequests)
            return true;
        else
            return false;
    }

    private static void cleanupRequest(String nickname) {
        // Make a list, and put that user's requests into the list.
        List<Long> timeList = userRequests.get(nickname);

        // We need to try and see if any of the times listed in the list can be removed, and then
        // add the current time to it if it's less than the max requests.
        Iterator itr = timeList.listIterator();
        while (itr.hasNext()) {
            Object timeLast = itr.next();
            if ((long) timeLast < (System.currentTimeMillis() - timeout))
                timeList.remove(timeLast);
        }
        if (timeList.isEmpty())
            userRequests.remove(nickname);
        else
            userRequests.put(nickname, timeList);
    }
}