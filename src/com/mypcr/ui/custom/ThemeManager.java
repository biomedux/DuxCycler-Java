package com.mypcr.ui.custom;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
	
	public static void setLocaleByEnglish(){
		JOptionPane.setDefaultLocale(new Locale("en"));
	}

	public static void updateTheme(JFrame frame)
	{
		SwingUtilities.updateComponentTreeUI(frame);
	}
}
