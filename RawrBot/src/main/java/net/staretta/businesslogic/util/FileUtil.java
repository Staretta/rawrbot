package net.staretta.businesslogic.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.staretta.RawrBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil
{
	private static Logger	logger	= LoggerFactory.getLogger(RawrBot.class);

	public static String[] readLines(String filename)
	{
		// Reads the lines from the text file and returns a list
		try
		{
			FileReader fileReader = new FileReader(filename);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			List<String> lines = new ArrayList<String>();
			String line = null;
			while ((line = bufferedReader.readLine()) != null)
			{
				lines.add(line);
			}
			bufferedReader.close();
			return lines.toArray(new String[lines.size()]);
		}
		catch (IOException ex)
		{
			logger.error(ex.toString());
		}
		return null;
	}
}