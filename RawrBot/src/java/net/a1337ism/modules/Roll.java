package net.a1337ism.modules;

import java.util.ArrayList;
import java.util.Random;

public class Roll
{
	public enum Behavior
	{
		H, L, N;
	}

	private ArrayList<Integer>	modifier;
	private Behavior			behavior	= Behavior.N;
	private int					dieSize;
	private int					numberDie;
	private ArrayList<Integer>	results;
	private int					lowest		= 1001;
	private int					highest		= 0;
	private int					total;

	public Roll(int dieSize)
	{
		this.dieSize = dieSize;
		this.numberDie = 1;
	}

	public Roll(int dieSize, int numberDie)
	{
		this.dieSize = dieSize;
		this.numberDie = numberDie;
	}

	public int randomNumber(int dieSize)
	{
		Random random = new Random();
		int n = random.nextInt(dieSize) + 1;
		return n;
	}

	private void setBehavior(Behavior behavior)
	{
		this.behavior = behavior;
	}

	private void addModifier(int modifier)
	{
		this.modifier.add(modifier);
	}

	private void setResults()
	{
		for (int i = 1; i <= this.numberDie; i++)
		{
			int x = randomNumber(dieSize);
			if (highest < x)
				highest = x;
			else if (lowest > x)
				lowest = x;

			this.results.add(x);
		}
	}

	@Override
	public String toString()
	{
		/* 4d6 - L + 10
		 * 
		 * 4d6(3,4,4,2) dropped (2) + 10
		 * 
		 * 4d6 - L + 10 + 2d4 + 20
		 * 
		 * 4d6(3,4,4,2) dropped (2) + (10) */

		setResults();

		String message = this.numberDie + "d" + this.dieSize + "(";
		for (Integer result : results)
		{
			message += result + ",";
			total += result;
		}
		message += ") ";
		if (behavior.equals(Behavior.H))
		{
			message += "dropped (" + highest + ") ";
			total -= highest;
		}
		else if (behavior.equals(Behavior.L))
		{
			message += "dropped (" + lowest + ") ";
			total -= lowest;
		}

		for (Integer number : modifier)
		{
			if (number < 0)
				message += "- (" + (number * -1) + ") ";
			else if (number > 0)
				message += "+ (" + number + ") ";
		}

		return message;
	}

	public int getTotal()
	{
		return this.total;
	}
}
