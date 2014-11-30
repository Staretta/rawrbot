package net.staretta.businesslogic;

import java.util.HashMap;

public class ModuleInfo
{
	private String moduleName = "";
	private String moduleAuthor = "";
	private String moduleUrl = "";
	private String moduleVersion = "";
	private HashMap<String, String> commandList = new HashMap<String, String>();
	
	public ModuleInfo()
	{
		
	}
	
	public void addCommand(String command)
	{
		this.commandList.put(command, "");
	}
	
	public void addCommand(String command, String commandHelp)
	{
		this.commandList.put(command, commandHelp);
	}
	
	public HashMap<String, String> getCommands()
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
		return moduleAuthor;
	}
	
	public void setAuthor(String moduleAuthor)
	{
		this.moduleAuthor = moduleAuthor;
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