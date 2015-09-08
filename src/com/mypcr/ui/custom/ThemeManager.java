package com.mypcr.ui.custom;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertGreen;

public class ThemeManager
{
	public static void setPlasticTheme()
	{
		try
		{
			PlasticLookAndFeel.setPlasticTheme(new DesertGreen());
			
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticLookAndFeel");
        } catch (Exception e)
        {
        }
	}

	public static void updateTheme(JFrame frame)
	{
		SwingUtilities.updateComponentTreeUI(frame);
	}
}
