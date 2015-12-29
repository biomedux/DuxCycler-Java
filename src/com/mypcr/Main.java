package com.mypcr;

import javax.swing.JOptionPane;
import com.hidapi.HidClassLoader;
import com.mypcr.function.Functions;
import com.mypcr.ui.MainUI;

/**
 * Main 함수를 포함하는 클래스
 * @author YJ
 */
public class Main 
{
	static
	{
		// HID 관련 Native 라이브러리를 운영체제별로 부를 수 있는 함수이다. 프로그램이 동작시 1번만 실행된다.
		if( !HidClassLoader.LoadLibrary() )
		{
			// 만약 로드에 실패하면 오류 메시지창을 띄우고, 프로그램을 종료시킨다.
			JOptionPane.showMessageDialog(null, "Not Supported OS.. Exit the Program.");
			System.exit(-1);
		}
	}
	/**
	 * MainUI 클래스의 Run() 함수를 호출하여 MainUI를 띄워주는 역할.
	 * @param args 여기서는 사용하지 않음.
	 * @see MainUI#Run()
	 */
	public static void main(String[] args)
	{
		MainUI ui = new MainUI();
		
		
		if( args.length != 1 ){
			JOptionPane.showMessageDialog(null, "mPCR 은 반드시 multiple mPCR 을 통해 실행하셔야 합니다.");
			return;
		}
		
		if( args[0].length() != 11 ){
			JOptionPane.showMessageDialog(null, "Serial Number 가 잘못되었습니다.");
			return;
		}
		

		ui.setSerialNumber(args[0]);	// MyPCR333333
		Functions.logFileCreate( );
		ui.Run();
	}
}
