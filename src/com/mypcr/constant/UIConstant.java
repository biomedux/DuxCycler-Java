package com.mypcr.constant;

import java.awt.Color;
import java.awt.Font;

public class UIConstant 
{
	public static final String PROTOCOL_DEFAULT_NAME 	=	"No Protocol";
	public static final String REMAINING_TIME		 	=	"0:00:00";
	public static final String FONT_NAME				=	"±¼¸²";
	public static final String DEFAULT_STATUS_MESSAGE0	=	"Not Connected";
	public static final String DEFAULT_STATUS_MESSAGE1	=	"0.0 ¡É";
	public static final String DEFAULT_STATUS_MESSAGE2	=	"0.0 ¡É";
	public static final String BUTTON_START_TEXT		=	"Start";
	public static final String BUTTON_STOP_TEXT			=	"Stop";
	public static final String BUTTON_READPROTOCOL_TEXT	=	"Read Protocol";
	public static final String BUTTON_EXIT_TEXT			=	"Exit";
	
	// UI X, Y, Width, Hegiht values
	public static final int MYPCR_WIDTH					=	390;
	public static final int MYPCR_HEIGHT				=	460;
	public static final int PROTOCOL_X					=	15;
	public static final int PROTOCOL_Y					=	15;
	public static final int REMAINING_TIME_X			=	245;
	public static final int REMAINING_TIME_Y			=	15;
	public static final int FONT_SIZE					=	12;
	public static final int GROUP_SIZE					=	3;
	
	public static final String[] TABLE_HEADER			=	{ "No", "Temp.", "Time", "Remain" };

	public static final Color BACKGROUND_COLOR			=	new Color(208, 228, 163);
	public static final Color TEXT_COLOR				=	new Color(112, 48, 160);
	public static final Font TEXT_FONT					=	new Font(FONT_NAME, Font.BOLD, FONT_SIZE);
}
