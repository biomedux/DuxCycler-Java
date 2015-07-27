package com.mypcr.function;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;

import javax.swing.JOptionPane;

import com.mypcr.beans.Action;
import com.mypcr.beans.RxAction;
import com.mypcr.beans.State;
import com.mypcr.beans.State_Oper;
import com.mypcr.beans.TxAction;
import com.mypcr.handler.Handler;
import com.mypcr.timer.GoTimer;
import com.mypcr.timer.NopTimer;
import com.mypcr.ui.ButtonUI;
import com.mypcr.ui.MainUI;
import com.mypcr.ui.ProgressDialog;

public class PCR_Task 
{
	private static PCR_Task instance = null;
	
	// 다른 객체의 정보들
	private static MainUI m_MainUI = null;
	
	// 에러 메시지 종류
	private static final int ERROR_LID_OVER			=	0x01;
	private static final int ERROR_CHM_OVER 		=	0x02;
	private static final int ERROR_LID_CHM_OVER		=	0x03;
	private static final int ERROR_HEATSINK_OVER	=	0x04;
	private static final int ERROR_LID_HEATSINK_OVER=	0x05;
	private static final int ERROR_CHM_HEATSINK_OVER=	0x06;
	private static final int ERROR_ALL				=	0x07;
	private static final int ERROR_TEMP_SENSOR		=	0x08;
	private static final int ERROR_ALL_SYSTEM		=	0x0f;
	
	// Timer 를 사용하기 위한 객체
	private Timer m_NopTimer = null;
	private Timer m_GoTimer  = null;
	private boolean m_TimerFlag = false;
	
	// Rx, Tx 버퍼를 생성해준다.
	public RxAction		m_RxAction = null;
	public TxAction		m_TxAction = null;
	
	// PCR 관련 변수들
	public int LED_Counter = 0;
	public int List_Counter = 0;
	public int Timer_Counter = 0;
	public int m_nCur_ListNumber = 0;
	
	// PCR 관련 플래그들
	public boolean IsRunning = false;
	public boolean IsReadyToRun = true;
	public boolean IsFinishPCR	 = false;
	public boolean IsRefrigeratorEnd = false;
	public boolean IsProtocolEnd = false;
	public boolean IsAdmin = false;
	public boolean IsGotoStart = false;
	private boolean IsDeviceCheck = false;
	
	// Preheat
	private String m_Preheat = "104";
	
	private PCR_Task()
	{
		m_RxAction = new RxAction();
		m_TxAction = new TxAction();
	}
	
	public static PCR_Task getInstance(MainUI mainUI)
	{
		if( instance == null )
		{
			m_MainUI = mainUI;
			instance = new PCR_Task();
		}
		return instance;
	}
	
	public void setTimer(int timer)
	{
		switch( timer )
		{
			case NopTimer.TIMER_NUMBER:
				m_NopTimer = new Timer();
				m_NopTimer.schedule(new NopTimer( m_MainUI ), Calendar.getInstance().getTime(), NopTimer.TIMER_DURATION);
				m_TimerFlag = true;
				break;
			case GoTimer.TIMER_NUMBER:
				m_GoTimer = new Timer();
				m_GoTimer.schedule( new GoTimer( m_MainUI.getDevice(), m_MainUI.getActionList(), m_Preheat, m_MainUI ), Calendar.getInstance().getTime(), GoTimer.TIMER_DURATION);
				break;
		}
	}
	
	public void killTimer(int timer)
	{
		switch( timer )
		{
			case NopTimer.TIMER_NUMBER:
				m_NopTimer.cancel();
				break;
			case GoTimer.TIMER_NUMBER:
				m_GoTimer.cancel();
				break;
		}
	}
	
	public void Calc_Temp()
	{
		double Chamber_Temp, Heater_Temp;
		Chamber_Temp = (double)(m_RxAction.getChamber_TempH()) + (double)(m_RxAction.getChamber_TempL()) * 0.1;
		Heater_Temp = (double)(m_RxAction.getCover_TempH()) + (double)(m_RxAction.getCover_TempL()) * 0.1;
		String chamber = String.format("%4.1f ℃", Chamber_Temp);
		String heater = String.format("%4.1f ℃", Heater_Temp);
		m_MainUI.getStatusText().setMessage(chamber, 1);
		m_MainUI.getStatusText().setMessage(heater, 2);
	}
	
	public void Check_Status()
	{
		switch( m_RxAction.getState() )
		{
			case State.READY:
				switch( m_RxAction.getCurrent_Operation() )
				{
					case State_Oper.INIT:
						m_MainUI.bLEDOff();
						break;
					case State_Oper.COMPLETE:
						m_MainUI.bLEDOn();
						if( !IsReadyToRun )
						{
							IsFinishPCR = true;
							IsReadyToRun = true;
							PCR_End();
						}
						break;
					case State_Oper.INCOMPLETE:
						m_MainUI.bLEDOff();
						m_MainUI.rLEDOn();
						break;
				}
				break;
			case State.RUN:
				m_MainUI.rLEDOff();
				if( m_RxAction.getCurrent_Operation() == State_Oper.RUN_REFRIGERATOR )
				{
					m_MainUI.bLEDOn();
					IsRefrigeratorEnd = true;
					IsProtocolEnd = true;
					IsFinishPCR = true;
				}
				else
				{
					if( LED_Counter > 8 )
						m_MainUI.bLEDOn();
					else if( LED_Counter == 0 )
						m_MainUI.bLEDOff();
				}
				LED_Counter++;
				
				if( LED_Counter == 14 )
					LED_Counter = 0;
				IsReadyToRun = false;
				break;
			case State.PCREND:
				m_MainUI.bLEDOn();
				break;
		}
	}
	
	public void Line_Task()
	{
		int taskLabel, Action_Point = 0;
		String tempString;
		Action[] actions = m_MainUI.getActionList();
		if( actions == null )
			return;
		int lines = actions.length;
		
		m_nCur_ListNumber = (int)m_RxAction.getCurrent_Action() - 1;
		
		if( List_Counter > 5 )
		{
			for(int i=0; i<lines; i++)
			{
				tempString = actions[i].getLabel();
				if( !tempString.equals("GOTO") )
				{
					taskLabel = Integer.parseInt(tempString);
					if( taskLabel == m_nCur_ListNumber + 1)
					{
						Action_Point = i;
						break;
					}
				}
			}
			
			m_nCur_ListNumber = Action_Point;
			
			if( IsRunning )
			{
				Display_LineTime();
				
				// Select 처리
				m_MainUI.getProtocolList().setSelection(m_nCur_ListNumber);
			}
			List_Counter = 0;
		}
		else
			List_Counter++;
	}
	
	public void Display_LineTime()
	{
		int durs, durm;
		String tempString;
		Action[] actions = m_MainUI.getActionList();
		
		tempString = actions[m_nCur_ListNumber].getLabel();
		
		if( tempString.equals("GOTO") )
			m_MainUI.getProtocolList().ChangeRemainTime("", m_nCur_ListNumber-1);
		else
			m_MainUI.getProtocolList().ChangeRemainTime("", m_nCur_ListNumber);
		
		durs = (int)m_RxAction.getSec_TimeLeft();
		durm = durs/60;
		durs = durs%60;
		
		if( durs == 0 )
		{
			if( durm == 0 ) tempString = "";
			else tempString = durm + "m";
		}
		else
		{
			if( durm == 0 ) tempString = durs + "s";
			else tempString = durm + "m " + durs + "s";
		}
		
		// 모든 라인의 남은 시간 초기화(GOTO 부분 제외)
		for(int i=0; i<m_RxAction.getTotal_Action(); i++)
		{
			if( !actions[i].getLabel().equals("GOTO"))
				m_MainUI.getProtocolList().ChangeRemainTime("", i);
		}
		
		// 남은 시간 변경
		m_MainUI.getProtocolList().ChangeRemainTime(tempString, m_nCur_ListNumber);
				
		if( m_RxAction.getCurrent_Loop() != 0 )
		{
			if( m_RxAction.getCurrent_Loop() == 255 )
				IsGotoStart = true;
		}
		
		if( m_RxAction.getCurrent_Loop() != 255 )
		{
			if( IsGotoStart )
			{
				boolean flag = true;
				for( int i=m_nCur_ListNumber; i<m_RxAction.getTotal_Action(); i++ )
				{
					tempString = actions[i].getLabel();
					if( tempString.equals("GOTO") )
					{
						if( flag )
						{
							flag = false;
							tempString = m_RxAction.getCurrent_Loop() + "";
							m_MainUI.getProtocolList().ChangeRemainTime(tempString, i);
						}
					}
				}
			}
		}
	}
	
	public void Get_DeviceProtocol()
	{
		if( !IsDeviceCheck && m_MainUI.IsNoStop )
		{
			IsDeviceCheck = true;
			
			// 액션이 없을 경우에, 즉 처음 켰을 때
			if( m_RxAction.getTotal_Action() == 0 )
			{
				// Recent Protocol 파일 경로를 받아온다.
				String path = Functions.Get_RecentProtocolPath();
				
				// 최근 불러온 파일이 있을 경우
				if( path != null )
				{
					Action[] actions = null;
					try
					{
						actions = Functions.ReadProtocolbyPath(path);
					}catch(Exception e)
					{
						JOptionPane.showMessageDialog(null, "No Recent Protocol File! Please Read Protocol!");
						return;
					}
					m_MainUI.OnHandleMessage(Handler.MESSAGE_READ_PROTOCOL, actions);
				}
				else
					JOptionPane.showMessageDialog(null, "No Recent Protocol File! Please Read Protocol!");
				return;
			}

			byte readLine = 0;
			int reqline = 0;
			ArrayList<Action> actions = new ArrayList<Action>();
			final ProgressDialog dialog = new ProgressDialog(m_MainUI, "Checking the state of the equipment", (int)m_RxAction.getTotal_Action());
			Thread tempThread = new Thread()
			{
				public void run()
				{
					dialog.setModal(true);
					dialog.setVisible(true);
				}
			};
			tempThread.start();

			while( readLine < (int)m_RxAction.getTotal_Action() )
			{
				dialog.setProgressValue(readLine);
				try
				{
					m_MainUI.getDevice().write( m_TxAction.Tx_RequestLine(readLine) );
					
					try
					{
						Thread.sleep(10);
					}catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					
					byte[] readBuffer = new byte[65];
					
					if( m_MainUI.getDevice().read(readBuffer) != 0 )
					{
						RxAction tempAction = new RxAction();
						tempAction.set_Info(readBuffer);
						
						reqline = readBuffer[RxAction.RX_REQLINE];
						m_RxAction.setTotal_Action( tempAction.getTotal_Action() );
						
						if( reqline == readLine )
						{
							Action action = new Action("Device Protocol");
							if( (readBuffer[RxAction.RX_LABEL] & 0xff) != RxAction.AF_GOTO )
							{
								action.setLabel("" + readBuffer[RxAction.RX_LABEL]);
								action.setTemp("" + readBuffer[RxAction.RX_TEMP]);
								int time = ((int)(readBuffer[RxAction.RX_TIMEH]*256.) + (int)(readBuffer[RxAction.RX_TIMEL]));
								action.setTime("" + time);
							}
							else
							{
								action.setLabel("GOTO");
								action.setTemp("" + readBuffer[RxAction.RX_TEMP]);
								int time = ((int)(readBuffer[RxAction.RX_TIMEH]*256.) + (int)(readBuffer[RxAction.RX_TIMEL]));
								action.setTime("" + time);
							}
							actions.add(action);
							readLine++;
						}
					}
					
					if( readLine == m_RxAction.getTotal_Action() )
					{
						IsRunning = true;
						IsFinishPCR = false;
						
						m_MainUI.getButtonUI().setEnable(ButtonUI.BUTTON_START, false);
						m_MainUI.getButtonUI().setEnable(ButtonUI.BUTTON_STOP, true);
						m_MainUI.getButtonUI().setEnable(ButtonUI.BUTTON_PROTOCOL, false);
					}
					
				}catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			
			// 리스트에 action 을 뿌려주기 위해..
			Action[] tempAction = new Action[actions.size()];
			for(int i=0; i<tempAction.length; i++)
			{
				tempAction[i] = new Action("Device Protocol");
				tempAction[i].setLabel(actions.get(i).getLabel());
				tempAction[i].setTemp(actions.get(i).getTemp());
				tempAction[i].setTime(actions.get(i).getTime());
			}
			m_MainUI.OnHandleMessage(Handler.MESSAGE_READ_PROTOCOL, tempAction);
			
			Thread tempThread2 = new Thread()
			{
				public void run()
				{
					try
					{
						Thread.sleep(1000);
					}catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					dialog.setVisible(false);
				}
			};
			tempThread2.start();
		}
	}
	
	public void Error_Check()
	{
		// 에러가 있다면
		if( m_RxAction.getError() != 0 )
		{
			Print_ErrorMsg( m_RxAction.getError() );
		}
	}
	
	public void Print_ErrorMsg(int error)
	{
		String message = "";
		
		switch( error )
		{
			case ERROR_LID_OVER:
				message = "LID overheating error! Please power-off and check MyPCR machine!";
				break;
			case ERROR_CHM_OVER:
				message = "Chamber overheating error! Please power-off and check MyPCR machine!";
				break;
			case ERROR_LID_CHM_OVER:
				message = "LID Heater and Chamber overheating error! Please power-off and check MyPCR machine!";
				break;
			case ERROR_HEATSINK_OVER:
				message = "Heat Sink overheating error! Please power-off and check MyPCR machine!";
				break;
			case ERROR_LID_HEATSINK_OVER:
				message = "LID Heater and Heat Sink overheating error! Please power-off and check MyPCR machine!";
				break;
			case ERROR_CHM_HEATSINK_OVER:
				message = "Chamber and Heat Sink overheating error! Please power-off and check MyPCR machine!";
				break;
			case ERROR_ALL:
				message = "LID Heater and Chamber, Heat Sink overheating error! Please power-off and check MyPCR machine!";
				break;
			case ERROR_TEMP_SENSOR:
				message = "Temperature Sensor error! Please power-off and check MyPCR machine!";
				break;
			case ERROR_ALL_SYSTEM:
				message = "All system is not working! Please power-off and check MyPCR machine!";
				break;
		}
		
		JOptionPane.showMessageDialog(null, message);
	}
	
	public void Calc_Time()
	{
		int hour, minute, second;
		int totalTime = m_RxAction.getTotal_TimeLeft();
		
		switch( m_RxAction.getState() )
		{
			case State.READY:
				if( IsRunning )
				{
					IsRunning = false;
					m_MainUI.getButtonUI().setEnable(ButtonUI.BUTTON_START, true);
					m_MainUI.getButtonUI().setEnable(ButtonUI.BUTTON_STOP, false);
					m_MainUI.getButtonUI().setEnable(ButtonUI.BUTTON_PROTOCOL, true);
				}
				break;
			case State.RUN:
				IsRunning = true;
				m_MainUI.getButtonUI().setEnable(ButtonUI.BUTTON_START, false);
				m_MainUI.getButtonUI().setEnable(ButtonUI.BUTTON_STOP, true);
				m_MainUI.getButtonUI().setEnable(ButtonUI.BUTTON_PROTOCOL, false);
				break;
		}
		
		Timer_Counter++;
		
		if( Timer_Counter % 5 == 0 )
		{
			second = totalTime % 60;
			minute = totalTime / 60;
			hour = minute / 60;
			minute = minute - hour * 60;
			
			if( IsRunning );
				m_MainUI.getProtocolText().setRemainingTimeText((hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second));
		}
		
		if( Timer_Counter == 10 )
			Timer_Counter = 0;
	}
	
	public void PCR_Start(String preheat)
	{
		killTimer(NopTimer.TIMER_NUMBER);
		IsRunning = true;
		IsFinishPCR = false;
		IsGotoStart = false;
		m_Preheat = preheat;
		int lines = m_MainUI.getActionList().length;
		for(int i=0; i<lines; i++)
			m_MainUI.getProtocolList().ChangeRemainTime("", i);
	}
	
	public void Stop_PCR()
	{
		if( IsRunning )
		{
			m_NopTimer.cancel();
			try
			{
				Thread.sleep(300);
			}catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			try
			{
				m_MainUI.getDevice().write( m_TxAction.Tx_Stop() );
			}catch(IOException e)
			{
				System.err.println( e );
			}
			try
			{
				Thread.sleep(300);
			}catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			
			if( !IsProtocolEnd )
			{
				m_MainUI.rLEDOn();
				IsRunning = false;
				IsFinishPCR = false;
			}
			else if( IsRefrigeratorEnd )
			{
				m_MainUI.rLEDOff();
				IsFinishPCR = true;
				IsRunning = false;
				setTimer(NopTimer.TIMER_NUMBER);
				return;
			}
			
			setTimer(NopTimer.TIMER_NUMBER);
		}
	}
	
	public void PCR_End()
	{
		m_nCur_ListNumber = 0;
		IsRunning = false;
		
		m_MainUI.getButtonUI().setEnable(ButtonUI.BUTTON_START, true);
		m_MainUI.getButtonUI().setEnable(ButtonUI.BUTTON_STOP, false);
		m_MainUI.getButtonUI().setEnable(ButtonUI.BUTTON_PROTOCOL, true);
		
		if( IsFinishPCR )
		{
			int lines = m_MainUI.getActionList().length;
			for(int i=0; i<lines; i++)
				m_MainUI.getProtocolList().ChangeRemainTime("", i);
			m_MainUI.getProtocolList().clearSelection();
			JOptionPane.showMessageDialog(null, "PCR Ended!!", m_MainUI.getSerialNumber(), JOptionPane.OK_OPTION);
		}
		else
			JOptionPane.showMessageDialog(null, "PCR Incomplete!!", m_MainUI.getSerialNumber(), JOptionPane.OK_OPTION);
	}
}
