package net.staretta.businesslogic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ModuleInfo
{
	private String moduleName = "";
	private String authorName = "";
	private String authorEmail = "";
	private String moduleUrl = "";
	private String moduleVersion = "";
	private HashMap<String, List<String>> commandList = new HashMap<String, List<String>>();
	
	public ModuleInfo()
	{
		
	}
	
	public void addCommand(String command)
	{
		this.commandList.put(command, Arrays.asList());
	}
	
	public void addCommand(String command, String commandHelp)
	{
		this.commandList.put(command, Arrays.asList(commandHelp));
	}
	
	public void addCommand(String command, List<String> commandHelp)
	{
		this.commandList.put(command, commandHelp);
	}
	
	public void addCommand(String command, String... commandHelp)
	{
		this.commandList.put(command, Arrays.asList(commandHelp));
	}
	
	public HashMap<String, List<String>> getCommands()
	{
		return commandList;
	}
	
	public String getName()
	{
		return moduleName;
	}
	
	public void setName(String moduleName)
	{
		this.moduleName = moduleName;
	}
	
	public String getAuthor()
	{
		return authorName;
	}
	
	public void setAuthor(String authorName)
	{
		this.authorName = authorName;
	}
	
	public String getAuthorEmail()
	{
		return authorEmail;
	}
	
	public void setAuthorEmail(String authorEmail)
	{
		this.authorEmail = authorEmail;
	}
	
	public String getUrl()
	{
		return moduleUrl;
	}
	
	public void setUrl(String moduleUrl)
	{
		this.moduleUrl = moduleUrl;
	}
	
	public String getVersion()
	{
		return moduleVersion;
	}
	
	public void setVersion(String moduleVersion)
	{
		this.moduleVersion = moduleVersion;
	}
}