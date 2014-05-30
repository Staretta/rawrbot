package net.staretta.businesslogic.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.staretta.RawrBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Json
{
	private static Logger	logger	= LoggerFactory.getLogger(RawrBot.class);

	/**
	 * Gets the HTTP Page URL, and returns as a string.
	 * 
	 * @param url
	 * @return page(string)
	 * @throws IOException
	 */
	public static JsonNode getHttpJson(String url)
	{
		ObjectMapper m = new ObjectMapper();
		JsonNode node = m.createObjectNode();
		InputStream is = null;
		try
		{
			is = new URL(url).openStream();
			node = m.readTree(is);
			is.close();
		}
		catch (Exception e)
		{
			logger.info("Exception: Could not get or parse " + url);
		}
		return node;
	}

	private static JsonNode getFileJson(String location)
	{
		ObjectMapper m = new ObjectMapper();
		JsonNode node = m.createObjectNode();
		try
		{
			node = m.readTree(new FileReader(location));
		}
		catch (Exception e)
		{
			logger.info("Exception: Could not get or parse " + location);
			e.printStackTrace();
		}
		return node;
	}

	/**
	 * Converts URL Connection response to a JSON Object
	 * 
	 * @return JSONObject json
	 * @throws IOException
	 */
	public static JsonNode readJsonFromUrl(String url)
	{
		JsonNode json = new ObjectMapper().createObjectNode();
		json = getHttpJson(url);
		return json;
	}

	public static JsonNode readJsonFromFile(String location)
	{
		JsonNode json = new ObjectMapper().createObjectNode();
		json = getFileJson(location);
		return json;
	}
}