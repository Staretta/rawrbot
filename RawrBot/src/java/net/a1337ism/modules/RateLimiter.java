package net.a1337ism.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.a1337ism.RawrBot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.pircbotx.hooks.ListenerAdapter;

public class RateLimiter extends ListenerAdapter {
    // Set up the logger stuff
    private static Logger                     logger       = LogManager.getFormatterLogger(RawrBot.class);
    private static Marker                     LOG_EVENT    = MarkerManager.getMarker("LOG_EVENT");

    private static int                        timeout      = 600000;                                      // Milliseconds
    private static int                        maxRequests  = 5;
    private static volatile Map<String, List> userRequests = new HashMap<String, List>();

    // userRequests is our hashmap of users and how many times they do a command.
    // Basically something like { username : [ millisec1, millisec2 ] } Where username is the key, and the list is the
    // list that contains the millisecond when they entered the command.

    private static void addRequest(String nickname) {
        // If the hashmap contains the username as a key
        if (userRequests.containsKey(nickname)) {
            synchronized (RateLimiter.class) {
                // Make a list, and put that user's requests into the list.
                List<Long> timeList = userRequests.get(nickname);

                timeList.add(System.currentTimeMillis());

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

        // If the hashmap contains the nickname
        if (userRequests.containsKey(nickname)) {
            synchronized (RateLimiter.class) {
                // Make a list, and put that user's requests into the list.
                List<Long> timeList = userRequests.get(nickname);

                if (timeList.size() < maxRequests) {
                    addRequest(nickname);
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            // If the user is not in the hashmap, then we know cleanupRequest deleted their old requests.
            addRequest(nickname);
            // Then we return false, because they aren't limited at the moment.
            return false;
        }
    }

    private static void cleanupRequest(String nickname) {
        // If the hashmap contains the nickname
        if (userRequests.containsKey(nickname)) {

            synchronized (RateLimiter.class) {
                // Make a list, and put that user's requests into the list.
                List<Long> timeList = userRequests.get(nickname);
                int listSize = timeList.size();

                // We need to try and see if any of the times listed in the list can be removed, and then put it into
                // the
                // hashmap if we removed anything from the list.
                for (int i = timeList.size() - 1; i >= 0; i--) {
                    // If the time is less than the current system time minus timeout, then remove that time from the
                    // list
                    if (timeList.get(i) < (System.currentTimeMillis() - timeout)) {
                        timeList.remove(i);
                    }
                }
                if (timeList.isEmpty()) {
                    // If the list is empty, then we remove the name from the hashmap.
                    userRequests.remove(nickname);
                } else if (timeList.size() < listSize) {
                    // If the list is less than it's original size, then put it back into the hashmap.
                    userRequests.put(nickname, timeList);
                }
            }
        }
    }
}
