package com.mypcr.beans;

public class Command 
{
	public static final int NOP			=	0x00;
	public static final int TASK_WRITE	=	0x01;
	public static final int TASK_END	=	0x02;
	public static final int GO			=	0x03;
	public static final int STOP		=	0x04;
	public static final int PARAM_WRITE	=	0x05;
	public static final int PARAM_END	=	0x06;
	public static final int BOOTLOADER	=	0x55;
}
