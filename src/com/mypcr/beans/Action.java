package com.mypcr.beans;

public class Action 
{
	private static final int LABEL = 0;
	private static final int TEMP = 1;
	private static final int TIME = 2;
	private static final int REMAININGTIME = 3;
	
	private String ProtocolName;
	private String Label;
	private String Temp;
	private String Time;
	private String RemainingTime;
	
	public Action()
	{
		Label = null; Temp = null; Time = null; RemainingTime = "";
	}
	
	public Action(String ProtocolName)
	{
		this.ProtocolName = ProtocolName; Label = null; Temp = null; Time = null; RemainingTime = "";
	}
	
	public Action(String Label, String Temp, String Time)
	{
		this.Label = Label;
		this.Temp = Temp;
		this.Time = Time;
		RemainingTime = "";
	}
	
	public String get(int index)
	{
		switch( index )
		{
			case LABEL:
				return Label;
			case TEMP:
				return Temp;
			case TIME:
				return Time;
			case REMAININGTIME:
				return RemainingTime;
			default:
				throw new IllegalArgumentException();
		}
	}
	
	public void set(int index, String data)
	{
		switch( index )
		{
			case LABEL:
				this.Label = data;
				break;
			case TEMP:
				this.Temp = data;
				break;
			case TIME:
				this.Time = data;
				break;
			case REMAININGTIME:
				this.RemainingTime = data;
				break;
		}
	}
	
	public String getProtocolName()
	{
		return ProtocolName;
	}

	public String getLabel() 
	{
		return Label;
	}

	public void setLabel(String label) 
	{
		Label = label;
	}

	public String getTemp() 
	{
		return Temp;
	}

	public void setTemp(String temp) 
	{
		Temp = temp;
	}

	public String getTime() 
	{
		return Time;
	}

	public void setTime(String time)
	{
		Time = time;
	}

	public String getRemainingTime() 
	{
		return RemainingTime;
	}

	public void setRemainingTime(String remainingTime) 
	{
		RemainingTime = remainingTime;
	}
	
	
	
}
