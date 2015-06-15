package com.mypcr.timer;

import java.io.IOException;
import java.util.TimerTask;

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

	@Override
	public void run()
	{
		try
		{
			if( m_MainUI.getDevice() != null )
			{
				m_MainUI.getDevice().write( m_MainUI.getPCR_Task().m_TxAction.Tx_NOP() );

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
					m_MainUI.getPCR_Task().m_RxAction.set_Info(readBuffer);

					m_MainUI.getPCR_Task().Calc_Temp();

					m_MainUI.getPCR_Task().Check_Status();

					m_MainUI.getPCR_Task().Line_Task();

					m_MainUI.getPCR_Task().Get_DeviceProtocol();

					m_MainUI.getPCR_Task().Error_Check();

					m_MainUI.getPCR_Task().Calc_Time();
				}
			}
		}catch(IOException e)
		{
			// e.printStackTrace();
		}
	}
}
