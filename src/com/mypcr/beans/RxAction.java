package com.mypcr.beans;

public class RxAction
{
	private byte State;
	private byte Cover_TempH;
	private byte Cover_TempL;
	private byte Chamber_TempH;
	private byte Chamber_TempL;
	private byte Heatsink_TempH;
	private byte Heatsink_TempL;
	private byte Current_Operation;
	private byte Current_Action;
	private byte Current_Loop;
	private byte Total_Action;
	private byte Error;
	private byte Serial_H;
	private byte Serial_L;
	private int Total_TimeLeft;
	private double Sec_TimeLeft;
	private byte Firmware_Version;

	private boolean IsReceiveOnce = false;

	public static final int RX_BUFSIZE		=	64;

	public static final int RX_STATE 		= 	0;
	public static final int RX_RES			=	1;
	public static final int RX_CURRENTACTNO	=	2;
	public static final int RX_CURRENTLOOP	=	3;
	public static final int RX_TOTALACTNO	=	4;
	public static final int RX_KP			=	5;
	public static final int RX_KI			=	6;
	public static final int RX_KD			=	7;
	public static final int RX_LEFTTIMEH	=	8;
	public static final int RX_LEFTTIMEL	=	9;
	public static final int RX_LEFTSECTIMEH	=	10;
	public static final int RX_LEFTSECTIMEL	=	11;
	public static final int RX_LIDTEMPH		=	12;
	public static final int RX_LIDTEMPL		=	13;
	public static final int RX_CHMTEMPH		=	14;
	public static final int RX_CHMTEMPL		=	15;
	public static final int RX_PWMH			=	16;
	public static final int RX_PWML			=	17;
	public static final int RX_PWMDIR		=	18;
	public static final int RX_LABEL		=	19;
	public static final int RX_TEMP			=	20;
	public static final int RX_TIMEH		=	21;
	public static final int RX_TIMEL		=	22;
	public static final int RX_LIDTEMP		=	23;
	public static final int RX_REQLINE		=	24;
	public static final int RX_ERROR		=	25;
	public static final int RX_CUR_OPR		=	26;
	public static final int RX_SINKTEMPH	=	27;
	public static final int RX_SINKTEMPL	=	28;
	public static final int RX_KP_1			=	39;
	public static final int RX_KI_1			=	33;
	public static final int RX_KD_1			=	37;
	public static final int RX_SERIALH		=	41;	// not using this version.
	public static final int RX_SERIALL		=	42;	// only bluetooth version
	public static final int RX_SERIALRESERV	=	43;
	public static final int RX_VERSION		=	44;

	public static final int AF_GOTO			=	250;

	public RxAction()
	{
		State = 0;	Cover_TempH = 0; 	Cover_TempL = 0;
		Chamber_TempH = 0; 	Chamber_TempL = 0;
		Heatsink_TempH = 0;	Heatsink_TempL = 0;
		Current_Operation = 0;	Current_Action = 0;	Current_Loop = -1;
		Total_Action = 0;	Error = 0;	Total_TimeLeft = 0;
		Sec_TimeLeft = 0;	Serial_H = 0;	Serial_L = 0;
	}

	public void set_Info(byte[] buffer)
	{
		State 				= (buffer[RX_STATE]);
		Current_Action 		= (buffer[RX_CURRENTACTNO]);
		Current_Loop		= (buffer[RX_CURRENTLOOP]);
		Total_Action		= (buffer[RX_TOTALACTNO]);
		Total_TimeLeft		= (int)((buffer[RX_LEFTTIMEH]) * 256 + (buffer[RX_LEFTTIMEL]));
		Sec_TimeLeft		= (double)(buffer[RX_LEFTSECTIMEH]) * 256 + (double)(buffer[RX_LEFTSECTIMEL]);
		Cover_TempH			= (buffer[RX_LIDTEMPH]);
		Cover_TempL			= (buffer[RX_LIDTEMPL]);
		Chamber_TempH		= (buffer[RX_CHMTEMPH]);
		Chamber_TempL		= (buffer[RX_CHMTEMPL]);
		Heatsink_TempH		= (buffer[RX_SINKTEMPH]);
		Heatsink_TempL		= (buffer[RX_SINKTEMPL]);
		Current_Operation	= (buffer[RX_CUR_OPR]);
		Error				= (buffer[RX_ERROR]);
		Serial_H			= (buffer[RX_SERIALH]);
		Serial_L			= (buffer[RX_SERIALL]);
		Firmware_Version	= (buffer[RX_VERSION]);
		IsReceiveOnce = true;
	}

	public boolean IsValidBuffer()
	{
		return IsReceiveOnce;
	}

	public void setTotal_Action(int Total_Action)
	{
		this.Total_Action = (byte)Total_Action;
	}

	public int getState()
	{
		return State;
	}

	public int getCover_TempH()
	{
		return Cover_TempH;
	}

	public int getCover_TempL()
	{
		return Cover_TempL;
	}

	public int getChamber_TempH()
	{
		return Chamber_TempH;
	}

	public int getChamber_TempL()
	{
		return Chamber_TempL;
	}

	public int getHeatsink_TempH()
	{
		return Heatsink_TempH;
	}

	public int getHeatsink_TempL()
	{
		return Heatsink_TempL;
	}

	public int getCurrent_Operation()
	{
		return Current_Operation;
	}

	public int getCurrent_Action()
	{
		return Current_Action;
	}

	public int getCurrent_Loop()
	{
		return Current_Loop;
	}

	public int getTotal_Action()
	{
		return Total_Action;
	}

	public int getError()
	{
		return Error;
	}

	public int getTotal_TimeLeft()
	{
		return Total_TimeLeft;
	}

	public double getSec_TimeLeft()
	{
		return Sec_TimeLeft;
	}

	public int getSerial_H()
	{
		return Serial_H;
	}

	public int getSerial_L()
	{
		return Serial_L;
	}

	public byte getFirmware_Version()
	{
		return Firmware_Version;
	}
}
