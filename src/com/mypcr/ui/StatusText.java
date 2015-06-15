package com.mypcr.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import com.mypcr.constant.UIConstant;

public class StatusText extends JComponent
{
	private static StatusText instance = null; 
	
	private GroupBox m_GroupBox = null;
	private String[] m_Messages = null;
	private String[] m_Titles = null;
	
	// Not Using Default
	private StatusText(){}
	private StatusText(int GroupCount, String[] titles)
	{
		this.m_Titles = titles;
		setBounds(0, 10 + UIConstant.FONT_SIZE, UIConstant.MYPCR_WIDTH, 80);
		setLayout(null);
		m_GroupBox = new GroupBox.Builder(GroupCount)
		.addGroup(18, 15, 100, 45, 10, 10)
		.addGroup(128, 15, 100, 45, 10, 10)
		.addGroup(238, 15, 120, 45, 10, 10)
		.build();
		m_Messages = new String[GroupCount];
		m_Messages[0] = UIConstant.DEFAULT_STATUS_MESSAGE0;
		m_Messages[1] = UIConstant.DEFAULT_STATUS_MESSAGE1;
		m_Messages[2] = UIConstant.DEFAULT_STATUS_MESSAGE2;
		setVisible(true);
	}
	
	public static StatusText getInstance(int GroupCount, String[] titles)
	{
		if( instance == null )
			instance = new StatusText(GroupCount, titles);
		return instance;
	}
	
	public void setMessage(String[] messages)
	{
		if( messages.length != m_Messages.length )
			throw new IllegalArgumentException();
		
		for(int i=0; i<messages.length; i++)
			m_Messages[i] = messages[i];
		
		repaint();
	}
	
	public void setMessage(String message, int index)
	{
		if( m_Messages.length <= index || index < 0 )
			throw new IllegalArgumentException();
		
		m_Messages[index] = message;
		
		repaint();
	}
	
	public void DrawGroupBox(Graphics2D g)
	{
		for(int i=0; i<m_GroupBox.Length(); i++)
			g.draw( m_GroupBox.Groups(i) );
	}
	
	public void DrawGroupTitle(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		for(int i=0; i<m_Titles.length; i++)
			g.drawString(m_Titles[i], 10 + (int)(m_GroupBox.Groups(i).getX()), 5 + (int)(m_GroupBox.Groups(i).getY()));
	}
	
	public void DrawGroupMessage(Graphics2D g)
	{
		g.setColor(UIConstant.TEXT_COLOR);
		g.drawString(m_Messages[0], 28, 45);
		for(int i=1; i<m_Messages.length; i++)
			g.drawString(m_Messages[i], 28 + (int)(m_GroupBox.Groups(i).getX()), 45);
	}

	@Override
	public void paint(Graphics g) 
	{
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(2));
		DrawGroupBox(g2);
		DrawGroupMessage(g2);
		DrawGroupTitle(g2);
		super.paint(g);
	}
}

class GroupBox
{
	private RoundRectangle2D[] Group;
	private final int GroupCount;
	
	private GroupBox( Builder builder )
	{
		Group = builder.Group;
		GroupCount = builder.GroupCount;
	}
	
	public int Length()
	{
		return GroupCount;
	}
	
	public RoundRectangle2D Groups(int index)
	{
		return Group[index];
	}
	
	public static class Builder
	{
		private RoundRectangle2D[] Group;
		private int CurrentCount = 0;
		private final int GroupCount;
		
		public Builder()
		{
			throw new IllegalArgumentException();
		}
		
		public Builder(int GroupCount)
		{
			if( GroupCount <= 0 )
				throw new IllegalArgumentException();
			this.GroupCount = GroupCount;
			Group = new RoundRectangle2D[GroupCount];
		}
		
		public Builder addGroup(int x, int y, int w, int h, int arch1, int arch2)
		{
			if( (CurrentCount+1) > GroupCount )
				throw new IllegalArgumentException();
			Group[CurrentCount++] = new RoundRectangle2D.Float(x, y, w, h, arch1, arch2);
			return this;
		}
		
		public GroupBox build()
		{
			if( CurrentCount != GroupCount )
				throw new IllegalArgumentException();
			return new GroupBox( this );
		}
	}
}