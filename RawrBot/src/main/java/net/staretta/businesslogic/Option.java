package net.staretta.businesslogic;

import java.util.ArrayList;

public class Option
{
	private String option;
	private ArrayList<String> optionHelp;
	
	public Option()
	{
	}
	
	public Option(String option)
	{
		this.option = option;
	}
	
	public Option(String option, ArrayList<String> optionHelp)
	{
		this.option = option;
		this.optionHelp = optionHelp;
	}
	
	public String getOption()
	{
		return option;
	}
	
	public void setOption(String option)
	{
		this.option = option;
	}
	
	public ArrayList<String> getOptionHelp()
	{
		return optionHelp;
	}
	
	public void setOptionHelp(ArrayList<String> optionHelp)
	{
		this.optionHelp = optionHelp;
	}
}
