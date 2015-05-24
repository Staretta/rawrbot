package net.staretta.businesslogic.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AdminInfo
{
	private String adminVersion = "";
	private HashMap<String, List<String>> adminCommandList = new HashMap<String, List<String>>();
	
	public AdminInfo()
	{
		
	}
	
	public HashMap<String, List<String>> getCommands()
	{
		return adminCommandList;
	}
	
	public void addCommand(String command)
	{
		this.adminCommandList.put(command, Arrays.asList());
	}
	
	public void addCommand(String command, String commandHelp)
	{
		this.adminCommandList.put(command, Arrays.asList(commandHelp));
	}
	
	public void addCommand(String command, List<String> commandHelp)
	{
		this.adminCommandList.put(command, commandHelp);
	}
	
	public String getAdminVersion()
	{
		return adminVersion;
	}
	
	public void setAdminVersion(String adminVersion)
	{
		this.adminVersion = adminVersion;
	}
}