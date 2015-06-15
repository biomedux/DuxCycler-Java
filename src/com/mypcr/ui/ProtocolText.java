package com.mypcr.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mypcr.constant.UIConstant;

public class ProtocolText extends JComponent 
{
	private String Protocol_Text = UIConstant.PROTOCOL_DEFAULT_NAME;
	private String RemainingTime_Text = UIConstant.REMAINING_TIME;
	
	public ProtocolText()
	{
		setBounds(0, 10, UIConstant.MYPCR_WIDTH, 40);
		setLayout(null);
		setVisible(true);
	}
	
	public ProtocolText(String Text)
	{
		setBounds(0, 10, UIConstant.MYPCR_WIDTH, 40);
		setLayout(null);
		setProtocolText(Text);
		setVisible(true);
	}
	
	public void setProtocolText(String Protocol_Text)
	{
		this.Protocol_Text = Protocol_Text;
		repaint();
	}
	
	public void setRemainingTimeText(String RemainingTime_Text)
	{
		this.RemainingTime_Text = RemainingTime_Text;
		repaint();
	}

	@Override
	public void paint(Graphics g) 
	{
		g.setColor(UIConstant.TEXT_COLOR);
		g.setFont(UIConstant.TEXT_FONT);
		g.drawString(Protocol_Text, UIConstant.PROTOCOL_X, UIConstant.PROTOCOL_Y);
		g.drawString(RemainingTime_Text, UIConstant.REMAINING_TIME_X, UIConstant.REMAINING_TIME_Y);	
		super.paint(g);
	}
	
	
}
