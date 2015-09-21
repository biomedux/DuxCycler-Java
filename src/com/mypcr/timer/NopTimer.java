package com.mypcr.timer;

import java.io.IOException;
import java.util.TimerTask;

import com.mypcr.beans.RxAction;
import com.mypcr.function.Functions;
import com.mypcr.ui.MainUI;

public class NopTimer extends TimerTask
{
	public static final int TIMER_DURATION = 100;
	public static final int TIMER_NUMBER   = 0x00;

	private MainUI 	m_MainUI = null;

	public NopTimer(MainUI mainUI)
	{
		m_MainUI = mainUI;
	}
	
	private void logProcess(RxAction rx){
		double chamber_temp = (double)(rx.getChamber_TempH()) + (double)(rx.getChamber_TempL()) * 0.1;
		double lid_temp = (double)(rx.getCover_TempH()) + (double)(rx.getCover_TempL()) * 0.1;
		double heatsink_temp = (double)(rx.getHeatsink_TempH()) + (double)(rx.getHeatsink_TempH()) * 0.1;
		
		String message = String.format("State: %d, CurrentAction: %d, TotalAction: %d, TotalLeftTime: %d, LeftTime: %.0f,"
									+  "LeftGoto: %d, ChamTemp: %.1f, LidTemp: %.1f, SinkTemp: %.1f, Error: %d", 
									rx.getState(), rx.getCurrent_Action(), rx.getTotal_Action(), rx.getTotal_TimeLeft(), 
									rx.getSec_TimeLeft(), rx.getCurrent_Loop(), chamber_temp, lid_temp, heatsink_temp, rx.getError());
		Functions.log(message);
		Functions.logTemperature(chamber_temp, lid_temp, heatsink_temp, false);
	}

	@Override
	public void run()
	{
		try
		{
			if( m_MainUI.getDevice() != null )
			{
				byte[] readBuffer = new byte[65];

				if( m_MainUI.getDevice().read(readBuffer) != 0 )
				{
					m_MainUI.getPCR_Task().m_RxAction.set_Info(readBuffer);

					m_MainUI.getPCR_Task().Calc_Temp();

					m_MainUI.getPCR_Task().Check_Status();

					m_MainUI.getPCR_Task().Line_Task();

					m_MainUI.getPCR_Task().Get_DeviceProtocol();

					m_MainUI.getPCR_Task().Error_Check();

					m_MainUI.getPCR_Task().Calc_Time();
					
					logProcess(m_MainUI.getPCR_Task().m_RxAction);
				}
				
				m_MainUI.getDevice().write( m_MainUI.getPCR_Task().m_TxAction.Tx_NOP() );
			}
		}catch(IOException e)
		{
			// e.printStackTrace();
		}
	}
}
