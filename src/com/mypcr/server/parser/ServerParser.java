package com.mypcr.server.parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import com.mypcr.server.constant.ServerConstant;

public class ServerParser 
{	
	public static String getFirmwareVer() throws Exception
	{
		String version = null;
		URL url = new URL(ServerConstant.VERSION_URL);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		version = in.readLine();
		return version;
	}
	
	public static ArrayList<String> getHexData() throws Exception
	{
		ArrayList<String> hexfile = new ArrayList<String>();
		URL url = new URL(ServerConstant.HEX_URL);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String data = null;
		
		while( (data = in.readLine()) != null )
		{
			hexfile.add(data);
		}
		return hexfile;
	}
}
