package net.staretta.businesslogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimiter extends ListenerAdapter
{
	private static Logger logger = LoggerFactory.getLogger(RateLimiter.class);
	private static int timeout = 600000; // Milliseconds
	private static volatile Map<User, List> userRequests = new HashMap<User, List>();

	// userRequests is our hashmap of users and how many times they do a command.
	// Basically something like { username : [ millisec1, millisec2 ] } Where username is the key, and the list is the
	// list that contains the millisecond when they entered the command.

	public static void addRequest(User user)
	{
		addRequest(user, 1);
	}

	public static void addRequest(User user, int request)
	{
		// If the hashmap contains the username as a key
		if (userRequests.containsKey(user))
		{
			synchronized (RateLimiter.class)
			{
				// Make a list, and put that user's requests into the list.
				List<Long> timeList = userRequests.get(user);

				for (int x = 0; x < request; x++)
				{
					timeList.add(System.currentTimeMillis());
				}

				userRequests.put(user, timeList);
			}
		}
		else
		{
			// If the user is not in the hashmap, then add them to the hashmap along with the current time
			List<Long> timeList = new ArrayList<Long>();

			for (int x = 0; x < request; x++)
			{
				timeList.add(System.currentTimeMillis());
				// logger.info("for loop " + x);
			}

			userRequests.put(user, timeList);
		}
	}

	public static boolean isRateLimited(User user)
	{
		return isRateLimited(user, 5);
	}

	public static boolean isRateLimited(User user, int maxRequests)
	{
		// Cleanup the request queue for the user, if it can be cleaned up.
		cleanupRequest(user);

		// If the hashmap contains the nickname
		if (userRequests.containsKey(user))
		{
			synchronized (RateLimiter.class)
			{
				// Make a list, and put that user's requests into the list.
				List<Long> timeList = userRequests.get(user);

				if (timeList.size() < maxRequests)
				{
					addRequest(user);
					return false;
				}
				else
				{
					return true;
				}
			}
		}
		else
		{
			// If the user is not in the hashmap, then we know cleanupRequest deleted their old requests.
			// Then we return false, because they aren't limited at the moment.
			addRequest(user);
			return false;
		}
	}

	private static void cleanupRequest(User user)
	{
		if (userRequests.containsKey(user))
		{

			synchronized (RateLimiter.class)
			{
				// Make a list, and put that user's requests into the list.
				List<Long> timeList = userRequests.get(user);
				int listSize = timeList.size();

				// We need to try and see if any of the times listed in the list can be removed, and then put it into
				// the hashmap if we removed anything from the list.
				for (int i = timeList.size() - 1; i >= 0; i--)
				{
					// If the time is less than the current system time minus timeout, then remove that time from the
					// list
					if (timeList.get(i) < (System.currentTimeMillis() - timeout))
					{
						timeList.remove(i);
					}
				}
				if (timeList.isEmpty())
				{
					// If the list is empty, then we remove the name from the hashmap.
					userRequests.remove(user);
				}
				else if (timeList.size() < listSize)
				{
					// If the list is less than it's original size, then put it back into the hashmap.
					userRequests.put(user, timeList);
				}
			}
		}
	}
}
