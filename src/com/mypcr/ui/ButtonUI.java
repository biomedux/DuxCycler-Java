package com.mypcr.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.mypcr.beans.Action;
import com.mypcr.constant.UIConstant;
import com.mypcr.function.Functions;
import com.mypcr.handler.Handler;
import com.mypcr.timer.NopTimer;

public class ButtonUI implements ActionListener
{
	private static ButtonUI instance = null;
	public static final int BUTTON_START	=	0x00;
	public static final int BUTTON_STOP		=	0x01;
	public static final int BUTTON_PROTOCOL	=	0x02;
	
	private static JFrame m_Parent = null;
	public JPanel m_Panel = null;
	private JButton m_Button_Start = null;
	private JButton m_Button_Stop = null;
	private JButton m_Button_ReadProtocol = null;
	//private JButton m_Button_Exit = null;
	
	private ButtonUI()
	{
		// 판넬의 색상과 크기 지정
		m_Panel = new JPanel();
		m_Panel.setBackground(UIConstant.BACKGROUND_COLOR);
		m_Panel.setBounds(20, 340, UIConstant.MYPCR_WIDTH - 50, 40);
		
		// 버튼 생성 및 캡션 달아주기
		m_Button_Start = new JButton(UIConstant.BUTTON_START_TEXT);
		m_Button_Stop = new JButton(UIConstant.BUTTON_STOP_TEXT);
		m_Button_ReadProtocol = new JButton(UIConstant.BUTTON_READPROTOCOL_TEXT);
	//	m_Button_Exit = new JButton(UIConstant.BUTTON_EXIT_TEXT);
		
		// 버튼 이벤트 리스너 등록
		m_Button_Start.addActionListener(this);
		m_Button_Stop.addActionListener(this);
		m_Button_ReadProtocol.addActionListener(this);
	//	m_Button_Exit.addActionListener(this);
		
		// 판넬에 버튼을 넣어준다. 
		m_Panel.add(m_Button_Start);
		m_Panel.add(m_Button_Stop);
		m_Panel.add(m_Button_ReadProtocol);
	//	m_Panel.add(m_Button_Exit);
		
		// 버튼 비활성화 초기화 처리
		setEnable(BUTTON_START, false);
		setEnable(BUTTON_STOP, false);
		setEnable(BUTTON_PROTOCOL, false);
	}
	
	public static ButtonUI getInstance( JFrame parent )
	{
		m_Parent = parent;
		if( instance == null )
			instance = new ButtonUI();
		return instance;
	}
	
	public JPanel getPanel()
	{
		return m_Panel;
	}
	
	public void setEnable(int button, boolean bool)
	{
		switch( button )
		{
			case BUTTON_START:
				m_Button_Start.setEnabled(bool);
				break;
			case BUTTON_STOP:
				m_Button_Stop.setEnabled(bool);
				break;
			case BUTTON_PROTOCOL:
				m_Button_ReadProtocol.setEnabled(bool);
				break;
		}
	}
	
	public boolean isEnable(int button)
	{
		switch( button )
		{
			case BUTTON_START:
				return m_Button_Start.isEnabled();
			case BUTTON_STOP:
				return m_Button_Stop.isEnabled();
			case BUTTON_PROTOCOL:
				return m_Button_ReadProtocol.isEnabled();
		}
		
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object event = e.getSource();
		
		if( event == m_Button_Start )
		{
			((Handler)m_Parent).OnHandleMessage(Handler.MESSAGE_START_PCR, null);
		}
		else if( event == m_Button_Stop )
		{
			((Handler)m_Parent).OnHandleMessage(Handler.MESSAGE_STOP_PCR, null);
		}
		else if( event == m_Button_ReadProtocol )
		{
			Action[] actions = null;
			actions = Functions.ReadProtocolbyDialog( m_Parent );
			// Parent 에 메시지를 날리고, 메시지의 유효성 여부는 Parent에 맡긴다.
			((Handler)m_Parent).OnHandleMessage(Handler.MESSAGE_READ_PROTOCOL, actions);
		}
		//else if( event == m_Button_Exit )
		//{
			
		//}
	}
}
