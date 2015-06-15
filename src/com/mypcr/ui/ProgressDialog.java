package com.mypcr.ui;

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.mypcr.constant.UIConstant;

public class ProgressDialog extends Dialog
{
	private static final long serialVersionUID = 1L;
	
	private JProgressBar m_ProgressBar;
	private JLabel m_Label;
	
	private int Maximum = 0;

	public ProgressDialog(Frame frame, String Label, int Maximum) 
	{
		super(frame);
		this.Maximum = Maximum; 
		JPanel panel = new JPanel();
		panel.setLayout(null);
		setSize(300, 150);
		setLocation(frame.getX() + (frame.getWidth() - getWidth())/2, frame.getY() + (frame.getHeight() - getHeight())/2 );
		setResizable(false);
		setUndecorated(true);
		m_Label = new JLabel(Label);
		m_Label.setFont(UIConstant.TEXT_FONT);
		m_Label.setBounds(40, 20, 260, 50);
		panel.add(m_Label);
		m_ProgressBar = new JProgressBar();
		m_ProgressBar.setBounds(50, 90, 200, 20);
		m_ProgressBar.setMinimum(0);
		m_ProgressBar.setMaximum(Maximum);
		m_ProgressBar.setValue(0);
		panel.add(m_ProgressBar);
		add( panel );
	}
	
	public void setProgressValue(int value)
	{
		if( value > Maximum )
			throw new RuntimeException("Progress value is over");
		m_ProgressBar.setValue(value);
	}
	
	public void setDialogLabel(String label)
	{
		m_Label.setText(label);
	}

}
