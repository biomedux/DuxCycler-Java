package com.mypcr.function;

import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.hidapi.HidClassLoader;
import com.mypcr.beans.Action;

public class Functions 
{
	private static boolean isMac = false;
	private static String pcrPath = null;
	
	static{
		String os = System.getProperty("os.name", "win").toLowerCase();
		
		if( os.indexOf("win") != -1 ){
			isMac = false;
		}else if( os.indexOf("mac") != -1 ){
			isMac = true;
		}
		
		File[] files = File.listRoots();
		
		if( !isMac)
			pcrPath = files[0].getAbsolutePath() + "\\mPCR";
		else{
			String classPath = System.getProperty("java.class.path");
			String[] tempPath = classPath.split("/");
			for(int i=0; i<tempPath.length-1; ++i){
				pcrPath += tempPath[i] + "/";
			}
			
			pcrPath += "mPCR";
		}
	}
	
	public static String Get_RecentProtocolPath()
	{
		String save_path = pcrPath;
		File file = new File(save_path);	file.mkdir();
		
		if( !isMac )
			save_path += "\\recent_protocol.txt";
		else
			save_path += "/recent_protocol.txt";
		File RecentFile = new File(save_path);
		BufferedReader in = null;
		
		try
		{	
			in = new BufferedReader(new FileReader(RecentFile));
			String input = in.readLine();
			in.close();
			
			return input;
		} catch (IOException e)
		{
			return null;
		}
	}
	
	public static void Save_RecentProtocol(String protocol_path)
	{
		String save_path = pcrPath;
		File file = new File(save_path);	file.mkdir();
		if( !isMac )
			save_path += "\\recent_protocol.txt";
		else
			save_path += "/recent_protocol.txt";
		File RecentFile = new File(save_path);
		
		BufferedReader in = null;
		PrintWriter out = null;
		try
		{	
			in = new BufferedReader(new FileReader(RecentFile));
			in.close();
			out = new PrintWriter(new FileWriter(RecentFile));
			out.println(protocol_path);
			out.close();
		} catch (IOException e)
		{
			try
			{
				in = null;
				RecentFile.createNewFile();
				Save_RecentProtocol(protocol_path);
			}catch(IOException e2){}
		}
	}
	
	public static Action[] ReadProtocolbyPath( String path ) throws Exception
	{
		BufferedReader in = null;
		ArrayList<String> inData = new ArrayList<String>();
		String tempData = null;
		
		// 파일로부터 데이터를 읽어봄
		
		// 130326 YJ
//		try
//		{
			in = new BufferedReader(new InputStreamReader(new FileInputStream( path )));
			
			while( (tempData = in.readLine()) != null )
			{
				inData.add( tempData );
			}
//		}catch(Exception e)
//		{
//			e.printStackTrace();
//		}finally
//		{
//			try
//			{
				in.close();
//			}catch(IOException e)
//			{
//				e.printStackTrace();
//			}
//		}
		
		// PCR Protocol 파일인지 확인
		// 맨 윗줄과 맨 끝줄에 %PCR%, %END% 가 있는지 확인
		String start = inData.get(0);
		String end   = inData.get(inData.size()-1);
		int actionCount = inData.size()-2;
		Action[] actions = new Action[actionCount];
		
		if( !start.contains("%PCR%") )
			return null;
		if( !end.contains("%END") )
			return null;
		
		String token = "\\\\";
		
		if( isMac )
			token = "/";
		
		String[] tokens = path.split(token);
		
		for(int i=1; i<inData.size()-1; i++)
		{
			String[] datas = inData.get(i).split("\t{1,}| {1,}");
			actions[i-1] = new Action(tokens[tokens.length-1]);
			int j=0;
			for( String temp : datas )
			{
				actions[i-1].set(j++, temp);
			}
		}
		
		return actions;
	}

	public static Action[] ReadProtocolbyDialog( JFrame parent )
	{
		// 파일 선택 다이얼로그 생성
		FileDialog fileDialog = new FileDialog(parent, "Select protocol file", FileDialog.LOAD);
		fileDialog.setFile("*.txt");
		fileDialog.setVisible(true);
		
		String dir = fileDialog.getDirectory();
		String file = fileDialog.getFile();
		String path = dir + file;
		
		if( dir == null )
		{
			Action[] action = new Action[1];
			action[0] = new Action();
			return action;
		}
		
		BufferedReader in = null;
		ArrayList<String> inData = new ArrayList<String>();
		String tempData = null;
		
		// 파일로부터 데이터를 읽어봄
		try
		{
			in = new BufferedReader(new InputStreamReader(new FileInputStream( path )));
			
			while( (tempData = in.readLine()) != null )
			{
				inData.add( tempData );
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			try
			{
				in.close();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		// PCR Protocol 파일인지 확인
		// 맨 윗줄과 맨 끝줄에 %PCR%, %END% 가 있는지 확인
		String start = inData.get(0);
		String end   = inData.get(inData.size()-1);
		int actionCount = inData.size()-2;
		Action[] actions = new Action[actionCount];
		
		if( !start.contains("%PCR%") )
			return null;
		if( !end.contains("%END") )
			return null;
		
		for(int i=1; i<inData.size()-1; i++)
		{
			String[] datas = inData.get(i).split("\t{1,}| {1,}");
			actions[i-1] = new Action(file);
			int j=0;
			for( String temp : datas )
			{
				actions[i-1].set(j++, temp);
			}
		}
		
		if( actions[0].getLabel() != null )
			Save_RecentProtocol(path);
		
		return actions;
	}
}
