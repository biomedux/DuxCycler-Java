package com.mypcr.handler;

// 안드로이드의 Handler 역할용 인터페이스
public interface Handler 
{
	static final int MESSAGE_READ_PROTOCOL	=	0x00;
	static final int MESSAGE_START_PCR		=	0x01;
	static final int MESSAGE_STOP_PCR		=	0x02;
	static final int MESSAGE_TASK_WRITE_END	=	0x03;
	static final int MESSAGE_CONNECTED_BOOT	=	0x04;
	
	public void OnHandleMessage(int MessageType, Object data);
}
