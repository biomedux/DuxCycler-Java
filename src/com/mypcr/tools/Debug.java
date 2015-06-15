package com.mypcr.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Debug 
{
	private PrintWriter out = null;
	
	public Debug()
	{
		try
		{
			out = new PrintWriter(new File("C:\\Users\\YJ\\Desktop\\logs\\log_java.txt"));
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void close()
	{
		out.close();
	}
	
	public void Print(String string)
	{		
		out.println(string);
		out.flush();
	}
}
