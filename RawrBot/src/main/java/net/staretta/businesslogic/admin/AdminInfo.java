package net.staretta.businesslogic.admin;

import java.util.HashMap;

public class AdminInfo
{
	private String adminVersion = "";
	private HashMap<String, String[]> adminCommandList = new HashMap<String, String[]>();
	
	public AdminInfo()
	{
		
	}
	
	public HashMap<String, String[]> getCommands()
	{
		return adminCommandList;
	}
	
	public void addCommand(String command)
	{
		this.adminCommandList.put(command, new String[] {});
	}
	
	public void addCommand(String command, String commandHelp)
	{
		this.adminCommandList.put(command, new String[] { commandHelp });
	}
	
	public void addCommand(String command, String[] commandHelp)
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