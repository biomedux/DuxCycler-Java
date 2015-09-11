package com.mypcr.function;

import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.mypcr.beans.Action;
import com.mypcr.constant.ProtocolConstants;

public class Functions 
{
	private static boolean isMac = false;
	private static String pcrPath = null;
	private static String logpath = null;
	private static String logFilePath = null;
	private static String tempLogPath = null;
	private static String tempLogFilePath = null;
	private static String protocolPath = null;
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	private static boolean isLogging = false;
	private static String serialNumber = null;
	
	private static long tempLogCounter = 0;
	private static long tempLogStartTime = 0; 

	static{
		String os = System.getProperty("os.name", "win").toLowerCase();
		
		if( os.indexOf("win") != -1 ){
			isMac = false;
		}else if( os.indexOf("mac") != -1 ){
			isMac = true;
		}
		
		if( !isMac)
			pcrPath = "C:\\mPCR";
		else{
			String classPath = System.getProperty("java.class.path");
			String[] tempPath = classPath.split("/");
			for(int i=0; i<tempPath.length-1; ++i){
				pcrPath += tempPath[i] + "/";
			}
			
			pcrPath += "mPCR";
		}
		
		logpath = pcrPath + (isMac ? "/log" : "\\log");
		tempLogPath = pcrPath + (isMac ? "/temperature" : "\\temperature");
		protocolPath = pcrPath + (isMac ? "/protocols" : "\\protocols");
		
		String dateFormat = df.format(new Date());
		logFilePath = logpath + (isMac ? ("/log_" + dateFormat + ".txt") : ("\\log_" + dateFormat + ".txt"));
	}
	
	public static void setLogSerialNumber(String serialNumber){
		Functions.serialNumber = serialNumber;
	}
	
	public static void log(String message)
	{
		if( isLogging ){
			File logFile = new File(logpath);	logFile.mkdirs();
			logFile = new File(logFilePath);
			
			String dateFormat = "[" + df.format(new Date()) + "," + serialNumber + "] "; 
			
			try {
				PrintWriter out = new PrintWriter(new FileWriter(logFile, true));
				out.println(dateFormat + message);
				out.close();
			} catch (IOException e) {
				// 	TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void logTemperature(double temperature, boolean isFirst){
		if( isLogging ){
			File logFile = new File(tempLogPath);	logFile.mkdirs();
			
			String header = null;
			
			if( isFirst ){
				tempLogCounter = 0;
				tempLogStartTime = System.currentTimeMillis();
				String dateFormat = df.format(new Date());
				tempLogFilePath = tempLogPath + (isMac ? ("/temp_" + dateFormat + ".txt") : ("\\temp_" + dateFormat + ".txt"));
				header = String.format("%8s\t%8s\t%8s", "Number", "Time", "Temp");
			}
			else
				tempLogCounter++;
			
			logFile = new File(tempLogFilePath);
			
			long endTime = System.currentTimeMillis();
			
			String data = String.format("%8d\t%8.0f\t%8.1f", tempLogCounter, (endTime-tempLogStartTime)/1000.0, temperature); 
			
			try {
				PrintWriter out = new PrintWriter(new FileWriter(logFile, true));
				if( header != null ){
					out.println(serialNumber);
					out.println(header);
				}
				else
					out.println(data);
				out.close();
			} catch (IOException e) {
				// 	TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static Action[] loadProtocol(String protocolName) throws Exception {
		File protocolDir = new File(protocolPath);	protocolDir.mkdirs();
		protocolDir = new File(protocolPath + (isMac ? "/" : "\\") + protocolName);
		Action[] action = null;
		
		FileInputStream fis = new FileInputStream(protocolDir);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		action = (Action[])ois.readObject();
		ois.close();
		
		return action;
	}
	
	public static void saveProtocol(Action[] action, String protocolName){
		File protocolDir = new File(protocolPath);	protocolDir.mkdirs();
		protocolDir = new File(protocolPath + (isMac ? "/" : "\\") + protocolName);
		
		try{
			FileOutputStream fos = new FileOutputStream(protocolDir);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(action);
			oos.close();
		}catch(IOException e){
			JOptionPane.showMessageDialog(null, "Protocol 을 저장하는데 문제가 발생하였습니다.");
			e.printStackTrace();
		}
	}
	
	public static void removeProtocol(String protocolName){
		File protocolDir = new File(protocolPath);	protocolDir.mkdirs();
		protocolDir = new File(protocolPath + (isMac ? "/" : "\\") + protocolName);
		protocolDir.delete();
	}
	
	public static ArrayList<Action[]> enumProtocols(){
		File protocolDir = new File(protocolPath);	protocolDir.mkdirs();
		File[] files = protocolDir.listFiles();
		
		ArrayList<Action[]> returnList = new ArrayList<Action[]>();
		
		for(int i=0; i<files.length; ++i){
			File file = files[i];
			Action[] temp = null;
			
			// ext 확인 필요
			if( !file.getName().endsWith(ProtocolConstants.ext) )
				continue;
			
			// Checking for validation
			try{
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				
				temp = (Action[])ois.readObject();
				ois.close();
			}catch(Exception e){
				// this file is not protocol file.
				e.printStackTrace();
				continue;
			}
			
			returnList.add(temp);
		}
		
		return returnList;
	}
	
	public static ArrayList<String> enumProtocolNames(){
		File protocolDir = new File(protocolPath);	protocolDir.mkdirs();
		File[] files = protocolDir.listFiles();
		
		ArrayList<String> returnList = new ArrayList<String>();
		
		for(int i=0; i<files.length; ++i){
			File file = files[i];
			
			if( !file.getName().endsWith(ProtocolConstants.ext) )
				continue;
			
			// Checking for validation
			try{
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				
				ois.readObject();
				ois.close();
			}catch(Exception e){
				// this file is not protocol file.
				e.printStackTrace();
				continue;
			}
			
			returnList.add(file.getName());
		}
		
		return returnList;
	}
	
	public static String calcTotalTime(Action[] actions){
		if( actions == null )
			return "00:00:00";
		
		Action[] tempActions = new Action[actions.length];
		// deep copy
		for(int i=0; i<tempActions.length; ++i)
			tempActions[i] = new Action(actions[i].getLabel(), actions[i].getTemp(), actions[i].getTime());
		
		// 전체 남은 시간 계산
		int totalSec = 0;
		for(int i=0; i<tempActions.length; ++i){
			Action action = tempActions[i];
			
			if( action.getLabel().equalsIgnoreCase("GOTO") ){
				String gotoLabel = action.getTemp();
				int remain = Integer.parseInt(action.getTime()) - 1;
				tempActions[i].setTime(remain+"");
				
				if( remain != -1 ){
					// 	해당 label 의 index 구하기
					int gotoIndex = -1;
					for(int j=0; j<tempActions.length; ++j){
						if( tempActions[j].getLabel().equals(gotoLabel+"") ){
							gotoIndex = j;
						}
					}
				
					i = gotoIndex-1;
				}
			}else{
				totalSec += Integer.parseInt(action.getTime());
			}
		}
		
		int second = totalSec % 60;
		int minute = totalSec / 60;
		int hour = minute / 60;
		minute = minute - hour * 60;
		
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}
	
	public static void setLogging(boolean isLogging){
		Functions.isLogging = isLogging;
		
		if( isLogging ){
			logTemperature(0, true);
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
		in = new BufferedReader(new InputStreamReader(new FileInputStream( path )));
		
		while( (tempData = in.readLine()) != null )
		{
			inData.add( tempData );
		}
			
		in.close();
		
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
