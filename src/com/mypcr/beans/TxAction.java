package com.mypcr.beans;


public class TxAction
{	
	private byte[] Tx_Buffer;
	
	public static final int TX_BUFSIZE			=	65;
	
	private static final int TX_CMD				=	1;
	private static final int TX_ACTNO			=	2;
	private static final int TX_TEMP			=	3;
	private static final int TX_TIMEH			=	4;
	private static final int TX_TIMEL			=	5;
	private static final int TX_LIDTEMP			=	6;
	private static final int TX_REQLINE			=	7;
	private static final int TX_CURRENT_ACT_NO 	= 	8;
	private static final int TX_BOOTLOADER		=	10;
	
	public static final int AF_GOTO				=	250;
	
	public TxAction()
	{
		Tx_Buffer = new byte[TX_BUFSIZE];
	}
	
	private void Tx_Clear()
	{
		Tx_Buffer = new byte[TX_BUFSIZE];
	}
	
	public byte[] Tx_NOP()
	{
		Tx_Clear();
		return Tx_Buffer;
	}
	
	public byte[] Tx_TaskWrite(String label, String temp, String time, String preheat, int currentActNo)
	{
		Tx_Clear();
		int nlabel, ntemp, ntime, npreheat;
		if( label.equals("GOTO"))
			nlabel = AF_GOTO;
		else
			nlabel = Integer.parseInt(label);
		ntemp = Integer.parseInt(temp);
		ntime = Integer.parseInt(time);
		npreheat = Integer.parseInt(preheat);
		Tx_Buffer[TX_CMD] 		= Command.TASK_WRITE;
		Tx_Buffer[TX_ACTNO] 	= (byte)nlabel;
		Tx_Buffer[TX_TEMP] 		= (byte)ntemp; 
		Tx_Buffer[TX_TIMEH] 	= (byte)(ntime/256.0);
		Tx_Buffer[TX_TIMEL] 	= (byte)ntime;
		Tx_Buffer[TX_LIDTEMP] 	= (byte)npreheat;
		Tx_Buffer[TX_CURRENT_ACT_NO] = (byte)currentActNo;
		Tx_Buffer[TX_REQLINE] = (byte)currentActNo;
		
		return Tx_Buffer;
	}
	
	public byte[] Tx_TaskEnd()
	{
		Tx_Clear();
		Tx_Buffer[TX_CMD] = Command.TASK_END;
		return Tx_Buffer;
	}
	
	public byte[] Tx_Go()
	{
		Tx_Clear();
		Tx_Buffer[TX_CMD] = Command.GO;
		return Tx_Buffer;
	}
	
	public byte[] Tx_Stop()
	{
		Tx_Clear();
		Tx_Buffer[TX_CMD] = Command.STOP;
		return Tx_Buffer;
	}
	
	public byte[] Tx_BootLoader()
	{
		Tx_Clear();
		Tx_Buffer[TX_BOOTLOADER] = Command.BOOTLOADER;
		return Tx_Buffer;
	}
	
	public byte[] Tx_RequestLine(byte reqline)
	{
		Tx_Clear();
		Tx_Buffer[TX_REQLINE] = reqline;
		return Tx_Buffer;
	}
}
