package com.mypcr.timer;

import java.io.IOException;
import java.util.TimerTask;

import com.codeminders.hidapi.HIDDevice;
import com.mypcr.beans.Action;
import com.mypcr.beans.RxAction;
import com.mypcr.beans.State;
import com.mypcr.beans.TxAction;
import com.mypcr.function.Functions;
import com.mypcr.handler.Handler;
import com.mypcr.ui.MainUI;
import com.mypcr.ui.ProgressDialog;

public class GoTimer extends TimerTask
{
	public static final int TIMER_DURATION	=	100;
	public static final int TIMER_NUMBER	=	0x01;
	
	private static final int TIMEOUT_MS = 15000;
	
	private HIDDevice 		m_Device = null;
	private MainUI 	  		m_Handler	= null;
	private TxAction		m_TxAction	= null;
	private Action[]		m_Actions = null;
	private String			m_preheat = null;
	private int				m_index = 0;
	private int				m_protocol_length = 0;
	private ProgressDialog 	m_dialog = null;
	private boolean			isTaskEnd = false;
	
	private int 			timerCounter = 0;
	private boolean			gotoEnded = false;
	
	public GoTimer(HIDDevice device, Action[] actions, String preheat, MainUI handler)
	{
		m_Device = device;
		m_TxAction = new TxAction();
		m_Handler = handler;
		m_preheat = preheat;
		m_Actions = actions; 
		m_protocol_length = m_Actions.length;
		m_dialog = new ProgressDialog(m_Handler, "PCR Protocol Transmitting...", m_protocol_length+2);
		Thread TempThread = new Thread("Go timer tempThread1")
		{
			public void run()
			{
				m_dialog.setModal(true);
				m_dialog.setVisible(true);
			}
		};
		TempThread.start();
	}

	@Override
	public void run() 
	{
		timerCounter++;
		
		if( timerCounter >= TIMEOUT_MS/TIMER_DURATION ){
			m_Handler.OnHandleMessage(Handler.MESSAGE_TASK_WRITE_TIMEOUT, null);
			m_dialog.setVisible(false);
		}
		
		if( m_index < m_protocol_length )
		{
			m_dialog.setProgressValue(m_index);
			
			try
			{
				m_Device.write( m_TxAction.Tx_TaskWrite(m_Actions[m_index].getLabel(), m_Actions[m_index].getTemp(), m_Actions[m_index].getTime(), m_preheat, m_index) );
				
				Functions.log(String.format("데이터 전송(ProtocolWrite[%d/%d])[%s,%s,%s,%s]", m_index+1, m_protocol_length, m_Actions[m_index].getLabel(), m_Actions[m_index].getTemp(), m_Actions[m_index].getTime(), m_preheat));
				
				Thread.sleep(20);
				
				byte[] readBuffer = new byte[65];
				if( m_Device.read(readBuffer) != 0 ){
					RxAction rx = new RxAction();
					rx.set_Info(readBuffer);
					
					int label = 0, temp = 0, time_h = 0, time_l = 0, time = 0;
					
					if( m_Actions[m_index].getLabel().equals("GOTO"))
						label = RxAction.AF_GOTO;
					else
						label = Integer.parseInt(m_Actions[m_index].getLabel());
					temp = Integer.parseInt(m_Actions[m_index].getTemp());
					time = Integer.parseInt(m_Actions[m_index].getTime());
					time_h = (time/256)&0xff;
					time_l = (time&0xff);
					
					if( rx.getLabel() == label && 
						rx.getTemp() == temp &&
						rx.getTime_H() == time_h &&
						rx.getTime_L() == time_l && 
						rx.getReqLine() == m_index){
						Functions.log(String.format("데이터 전송(ProtocolWrite[%d/%d]) 확인 완료",  m_index+1, m_protocol_length));
						m_index++;
					}else{
						Functions.log(String.format("데이터 전송(ProtocolWrite[%d/%d]) 확인 실패, 재전송",  m_index+1, m_protocol_length));
					}
					
					rx = null;
				}
				
				readBuffer  = null;
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if( !gotoEnded )
		{
			try
			{
				if( !isTaskEnd )
				{
					m_dialog.setDialogLabel("PCR Protocol Checking...");
					
					Functions.log("데이터 전송(ProtocolWriteEnd) 시도");
					
					m_Device.write( m_TxAction.Tx_TaskEnd() );
					try
					{
						Thread.sleep(20);
					}catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					
					// Checking the device protocol length
					byte[] readBuffer = new byte[65];
					if( m_Device.read(readBuffer) != 0 ){
						RxAction rx = new RxAction();
						rx.set_Info(readBuffer);
						
						if( rx.getTotal_Action() != m_Actions.length ){
							Functions.log("데이터 전송(ProtocolWriteEnd) 확인 실패");
							rx = null;
							readBuffer = null;
							return;
						}
						rx = null;
					}
					
					Functions.log("데이터 전송(ProtocolWriteEnd) 확인 완료");
					
					m_dialog.setProgressValue(m_index+1);
					isTaskEnd = true;
				}
				
				m_dialog.setDialogLabel("Waiting for running!");
				Functions.log("데이터 전송(PCR Start) 시도");
				
				// Checking the device status
				m_Device.write( m_TxAction.Tx_Go() );
				try
				{
					Thread.sleep(20);
				}catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				
				byte[] readBuffer = new byte[65];
				if( m_Device.read(readBuffer) != 0 ){
					RxAction rx = new RxAction();
					rx.set_Info(readBuffer);
					
					if( rx.getState() != State.RUN ){
						Functions.log("데이터 전송(PCR Start) 확인 실패");
						readBuffer = null;
						rx = null;
						return;
					}
					rx = null;
				}
				readBuffer = null;
				
				Functions.log("데이터 전송(PCR Start) 확인 완료");
				
				m_dialog.setProgressValue(m_index+2);
				
				m_Handler.OnHandleMessage(Handler.MESSAGE_TASK_WRITE_END, null);
				
				Thread TempThread = new Thread("Go timer tempThread2")
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
						m_dialog.dispose();
						gotoEnded = true;
					}
				};
				TempThread.start();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
