package com.mypcr.tools;

import java.awt.Toolkit;

public class Resolution 
{
	public static final int X;
	public static final int Y;
	
	static
	{
		X = Toolkit.getDefaultToolkit().getScreenSize().width; 
		Y = Toolkit.getDefaultToolkit().getScreenSize().height;
	}
}
