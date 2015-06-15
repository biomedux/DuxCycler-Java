package com.mypcr.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDManager;
import com.hidapi.CallbackDeviceChange;
import com.hidapi.DeviceChange;
import com.hidapi.DeviceConstant;
import com.hidapi.HidClassLoader;
import com.mypcr.Main;
import com.mypcr.beans.Action;
import com.mypcr.bootloader.BootLoader;
import com.mypcr.constant.UIConstant;
import com.mypcr.function.PCR_Task;
import com.mypcr.handler.Handler;
import com.mypcr.server.parser.ServerParser;
import com.mypcr.timer.GoTimer;
import com.mypcr.timer.NopTimer;
import com.mypcr.tools.Resolution;

/**
 * 처음에 나타나는 UI에 대한 클래스이다.
 * JFrame을 상속받고 {@link Main} 클래스에서 호출하여 실행된다.
 * @author YJ
 *
 */
public class MainUI extends JFrame implements Handler, DeviceChange, KeyListener
{
	/**
	 * JFrame의 기본 serialVersionUID 값 설정.(설정은 생략가능하다.)
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Device 리스트, Device 연결 등의 역할을 담당하는 객체
	 */
	private HIDManager m_Manager = null;
	/**
	 * {@link HIDManager} 의 OpenById 함수를 통해 장치와 연결되면, 연결된 장치를 저장한다.
	 * 이 객체는 장치와 Write, Read 통신이 가능하고 통신에서 Blocking, Non-Blocking 처리를 제어한다.
	 * @see HIDManager#openById(int, int, String)
	 * @see HIDManager#openByPath(String)
	 */
	private HIDDevice m_Device = null;
	/**
	 * 통신을 하고자 하는 장치의 상태의 변화를 실시간으로 체크하여 알려주는 콜백함수 역할을 하는 클래스이다.
	 * 상태가 바뀔 경우에 통지를 받기 위해서는 {@link DeviceChange} 인터페이스 인스턴스를 매개변수로 넘겨줘야 한다.
	 * Connected, Disconnect 상태를 받아올 수 있다.
	 * @see DeviceChange#OnMessage(int, Object)
	 */
	private CallbackDeviceChange m_Callback_DeviceChange = null;
	/**
	 * Frame에 구성요소들을 담고 있는 Swing Panel 이다.
	 * 이 Panel 에 add 함수를 이용하여 Swing 컴포넌트들을 더하면 Frame에 표시된다.
	 */
	private JPanel m_Panel = null;
	/**
	 * 읽어온 프로토콜 파일 이름, 남은 시간 표시하는 부분을 포함하는 UI이다.
	 * @see ProtocolText
	 */
	private ProtocolText m_ProtocolText = null;
	/**
	 * 현재 연결상태(시리얼번호), Chamber 온도, LID Heater 온도을 표시하고, Preheat 값을 설정 하는 UI 이다.
	 * @see StatusText
	 */
	private StatusText m_PCRStatusText = null;
	/**
	 * 프로토콜 파일로부터 읽어온 프로토콜을 List Control을 포함하는 UI이다.
	 * @see ProtocolList
	 */
	private ProtocolList m_ProtocolList = null;
	/**
	 * Start, Stop, Read Protocol 버튼에 대한 UI이다.
	 * @see ButtonUI
	 */
	private ButtonUI m_ButtonUI = null;

	private JTextField m_LidText = null;
	/**
	 * Protocol List에 저장된 Action들을 저장하는 배열이다.
	 * Action은 Label, Temp, Time, Remaining Time 값을 가지고 있다.
	 * @see Action
	 */
	private Action[] m_ActionList = null;
	/**
	 * PCR 을 제어하기 위한 기능들을 가지고 있는 객체이다.
	 * 전체 클래스에서 가장 중요한 역할을 한다.
	 * @see PCR_Task
	 */
	private PCR_Task m_PCRTask = null;
	/**
	 * PCR과 연결 상태를 나타내는 플래그.
	 * 연결되면 true, 연결이 해제되면 false.
	 */
	private boolean IsConnected = false;
	/**
	 * Protocol 파일을 읽었는지 확인하는 플래그.
	 * 읽었으면 true, 읽은 적이 없거나 오류가 생기면 false.
	 */
	private boolean IsProtocolRead = false;
	/**
	 * 정지가 된적이 있는지 확인하는 플래그.
	 * 정지된 적이 없으면 true, 있으면 false.
	 * 정지된 적이 없으면 처음 실행한 것이 되기 때문에, 처음 실행한 것이 되기 때문에
	 * true가 되는 경우는 프로그램이 처음 켜졌을 경우만 해당한다.
	 * PCR과 연결 상태에서 정지된 적이 없는 경우에 PCR이 동작중이라면 PCR의 프로토콜을 읽어오는
	 */
	public boolean IsNoStop = true;
	
	
	// LED Control 
	private JLabel ledBlue, ledRed, ledGreen;
	private URL url_blueOff = getClass().getClassLoader().getResource("ledBLow.png");
	private URL url_blueOn = getClass().getClassLoader().getResource("ledBHigh.png");
	private URL url_greenOff = getClass().getClassLoader().getResource("ledGLow.png");
	private URL url_greenOn = getClass().getClassLoader().getResource("ledGHigh.png");
	private URL url_redOff = getClass().getClassLoader().getResource("ledRLow.png");
	private URL url_redOn = getClass().getClassLoader().getResource("ledRHigh.png");
	private ImageIcon icon_blueOff, icon_blueOn, icon_greenOff, icon_greenOn, icon_redOn, icon_redOff;
	
	public void bLEDOn(){
		ledBlue.setIcon(icon_blueOn);
	}
	
	public void rLEDOn(){
		ledRed.setIcon(icon_redOn);
	}

	public void gLEDOn(){
		ledGreen.setIcon(icon_greenOn);
	}
	
	public void bLEDOff(){
		ledBlue.setIcon(icon_blueOff);
	}
	
	public void rLEDOff(){
		ledRed.setIcon(icon_redOff);
	}

	public void gLEDOff(){
		ledGreen.setIcon(icon_greenOff);
	}
	
	/**
	 * MainUI 기본 생성자이다.
	 * private 설정으로 생성자를 호출할 수 없도록 되어 있고, getInstance() 함수를 통하여 인스턴스를 생성할 수 있다.(Singleton 기법).
	 * 내부적으로 init() 함수를 호출하여 UI를 초기화 시킨다.
	 */
	private String serialNumber = null;
	
	public MainUI()
	{
		init();
	}
	/**
	 * UI를 초기화 하거나, 객체들을 초기화 하는 역할을 한다.
	 * MainUI() 생성자에서만 호출할 수 있다. 단 한번만 호출된다.
	 */
	private void init()
	{
		// 프레임의 크기 지정
		setBounds((Resolution.X * 2/5), Resolution.Y/4 ,UIConstant.MYPCR_WIDTH ,UIConstant.MYPCR_HEIGHT);
		// 타이틀 설정
		setTitle("MyPCR version 3.2");

		// 종료시 프로그램 종료
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 최대화 막기
		setResizable(false);
		
		// title icon 변경
		setIconImage(new ImageIcon(getClass().getClassLoader().getResource("icon.png")).getImage());

		// 안의 컴포넌트를 담기위한 Panel
		// 레이아웃을 절대좌표로 사용하기 위해 null로 설정
		m_Panel = new JPanel();
		m_Panel.setLayout(null);
		m_Panel.setBackground(UIConstant.BACKGROUND_COLOR);

		// 3개의 GroupBox의 title
		String[] titles = { "Serial Number", "CHAMBER", "LID HEATER" };

		/** 컴포넌트 설정 **/
		m_ProtocolText = new ProtocolText();
		m_PCRStatusText = StatusText.getInstance(UIConstant.GROUP_SIZE, titles);
		m_ProtocolList = ProtocolList.getInstance();
		m_ButtonUI = ButtonUI.getInstance( this );
		m_LidText = new JTextField();
		m_LidText.setLayout(null);
		m_LidText.setBounds(310, 55, 40, 20);
		m_LidText.setText("104");
		m_LidText.addKeyListener(this);
		
		// 로고 추가
		JLabel labelLogo = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("logo.jpg")));
		labelLogo.setBounds(100, 385, 182, 37);

		// LED added
		icon_blueOff = new ImageIcon(url_blueOff); 
		icon_blueOn  = new ImageIcon(url_blueOn);
		icon_greenOff = new ImageIcon(url_greenOff);
		icon_greenOn = new ImageIcon(url_greenOn);
		icon_redOff = new ImageIcon(url_redOff);
		icon_redOn = new ImageIcon(url_redOn);
		
		ledBlue = new JLabel(icon_blueOff);
		ledBlue.setBounds(310, 1, 22, 29);
		ledRed = new JLabel(icon_redOff);
		ledRed.setBounds(332, 1, 22, 29);
		ledGreen = new JLabel(icon_greenOff);
		ledGreen.setBounds(354, 1, 22, 29);

		m_Panel.add(m_ProtocolText);
		m_Panel.add(m_PCRStatusText);
		m_Panel.add(m_ProtocolList.getPane());
		m_Panel.add(m_ButtonUI.getPanel());
		m_Panel.add(m_LidText);

		// 150509 logo and led added
		m_Panel.add(labelLogo);
		m_Panel.add(ledBlue);
		m_Panel.add(ledGreen);
		m_Panel.add(ledRed);
		/** 컴포넌트 설정 **/

		// 판넬을 현재 프레임에 더함
		add(m_Panel);

		// 150507 화면에 UI 를 띄우기 전에 장치 확인을 먼저 하기 위한 처리
		// 화면에 보이도록
		// setVisible(true);

		// Device 연결 체크용 콜백 함수 설정
		try
		{
			// DeviceManager 인스턴스 생성
			m_Manager = HIDManager.getInstance();
			// Device 연결 상태를 표시해주는 콜백함수 등록
			m_Callback_DeviceChange = CallbackDeviceChange.getInstance(m_Manager, this);
			m_Callback_DeviceChange.setDaemon(true);
			m_Callback_DeviceChange.start();
		}catch(IOException e)
		{
			e.printStackTrace();
		}

		// MyPCR 관련 기능을 담고 있는 객체의 인스턴스를 생성
		m_PCRTask = PCR_Task.getInstance(this);
	}

	/**
	 * main 함수에서 UI를 띄우기 위해 맨 처음 호출하는 함수.
	 * Singleton 기법의 getInstance와 같은 함수이다.
	 * 어법상 말이 Run이 더 어울리기 때문에 Run으로 정하였다.
	 * @return MainUI의 instance 값을 리턴한다.
	 */
	
	// 150507 yj 기존의 singleton 기법 제거
	public void Run()
	{
		setVisible(true);
	}
	
	/**
	 * ProtocolList 객체를 리턴한다.
	 * @see ProtocolList
	 */
	public ProtocolList getProtocolList()
	{
		return m_ProtocolList;
	}
	/**
	 * ProtocolText 객체를 리턴한다.
	 * @see ProtocolText
	 */
	public ProtocolText getProtocolText()
	{
		return m_ProtocolText;
	}
	/**
	 * StatusText 객체를 리턴한다.
	 * @see StatusText
	 */
	public StatusText getStatusText()
	{
		return m_PCRStatusText;
	}
	/**
	 * ButtonUI 객체를 리턴한다.
	 * @see ButtonUI
	 */
	public ButtonUI getButtonUI()
	{
		return m_ButtonUI;
	}
	/**
	 * 프로토콜 리스트에 표시된 프로토콜을 담은 Action 배열을 리턴한다.
	 */
	public Action[] getActionList()
	{
		return m_ActionList;
	}
	/**
	 * HIDManager 를 통하여 연결한 HIDDevice 객체를 리턴한다.
	 * 연결되어 있을 떄(Not null), 다른 클래스에서 이 함수를 통하여 PCR 장치와 통신을 할 수 있다.
	 * @see HIDManager
	 */
	public HIDDevice getDevice()
	{
		return m_Device;
	}
	/**
	 * PCR_Task 객체를 리턴한다.
	 * @see PCR_Task
	 */
	public PCR_Task getPCR_Task()
	{
		return m_PCRTask;
	}
	/**
	 * 웹 서버로부터 펌웨어 버전을 체크하고, 펌웨어 업데이트를 할 수 있도록 하는 함수.
	 * PCR과 연결이 된 상태에서 맨 처음에 한번만 체크합니다.
	 * 펌웨어 버전을 서버에서 받아온 후, 그 버전과 PCR 버전과 같은지 확인 후 다르면 웹서버로부터 새로운 펌웨어를 받아서
	 * PCR에 업로드 시킵니다. 업로드가 완료되면 PCR을 Reset 시킵니다.
	 * 웹 서버가 꺼져있으면 연결할 수 없다는 메시지가 나옵니다.
	 * 이 기능이 제대로 동작하려면 다른 BootLoader 프로그램을 종료하십시오.
	 * @see BootLoader
	 */
	
	public void setSerialNumber(String serialNumber){
		this.serialNumber = serialNumber;
		m_Callback_DeviceChange.setSerialNumber(serialNumber);
	}
	
	private void connectToDevice(){
		try
		{
			if( m_Device != null )
			{
				m_Device.close();
				m_Device = null;
			}
			
			m_Device = m_Manager.openById(DeviceConstant.VENDOR_ID, DeviceConstant.PRODUCT_ID, serialNumber);
			if( m_Device != null )
			{
				IsConnected = true;
				m_Device.disableBlocking();
				m_ButtonUI.setEnable(ButtonUI.BUTTON_START, true);
				m_ButtonUI.setEnable(ButtonUI.BUTTON_STOP, false);
				m_ButtonUI.setEnable(ButtonUI.BUTTON_PROTOCOL, true);
				m_PCRStatusText.setMessage(m_Device.getSerialNumberString(), 0);
				m_PCRTask.setTimer(NopTimer.TIMER_NUMBER);
				
				setTitle(m_Device.getSerialNumberString());
				setSerialNumber(m_Device.getSerialNumberString());
				
				gLEDOn();
				// 150506 YJ Firmware check disable
				// UpdateFromServer();
			}
			else
			{
				// 연결 에러 처리
				System.out.println("Fatal Error!");
				gLEDOff();
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Handler를 implements 한 메소드. 다른 클래스로부터 오는 Message를 받는다.
	 * @param MessageType int 메시지 종류
	 * @param data Object 메시지와 같이 오는 데이터
	 */
	@Override
	public void OnHandleMessage(int MessageType, Object data)
	{
		switch( MessageType )
		{
			// Protocol 을 읽었다는 메시지 일 경우
			case MESSAGE_READ_PROTOCOL:
				// Protocol 리스트가 담겨있다.
				Action[] actions = (Action[])data;
				// null 인 경우는 프로토콜 파일을 잘못 불러온 경우
				if( actions == null )
					JOptionPane.showMessageDialog(this, "올바르지 않은 Protocol 파일입니다.");
				else
				{
					// 플래그 초기화
					IsProtocolRead = false;
					// action의 0번째 배열의 레이블이 null인 경우는 잘못된 파일인 경우.
					if( actions[0].getLabel() == null )
						return;
					// List를 비워준다.
					m_ProtocolList.ResetContent();
					// 얻어온 프로토콜 만큼 insert 해준다.
					for( Action action : actions )
						m_ProtocolList.InsertData(action);
					// 받아온 프로토콜들을 멤버변수에 저장해준다.
					m_ActionList = actions;
					// 읽어온 프로토콜 파일의 이름을 상단에 표시한다.
					m_ProtocolText.setProtocolText(actions[0].getProtocolName());
					// 읽었으니 플래그 true
					IsProtocolRead = true;
				}
				break;
				// 스타트 버튼을 눌렀을 때.
			case MESSAGE_START_PCR:
				if( IsConnected )
				{
					// 불러온 프로토콜 파일이 있을 경우에만 동작
					if( IsProtocolRead )
					{
						rLEDOff();
						
						m_PCRTask.PCR_Start(m_LidText.getText());
						m_PCRTask.setTimer(GoTimer.TIMER_NUMBER);
						m_ButtonUI.setEnable(ButtonUI.BUTTON_START, false);
						m_ButtonUI.setEnable(ButtonUI.BUTTON_STOP, true);
						m_ButtonUI.setEnable(ButtonUI.BUTTON_PROTOCOL, false);
					}
					else
					{
						JOptionPane.showMessageDialog(this, "불러온 프로토콜 파일이 없습니다.");
					}
				}
				break;
				// 스탑 버튼을 눌렀을 때
			case MESSAGE_STOP_PCR:
				if( IsConnected )
				{
					// 종료 처리
					m_PCRTask.Stop_PCR();
					m_ButtonUI.setEnable(ButtonUI.BUTTON_START, true);
					m_ButtonUI.setEnable(ButtonUI.BUTTON_STOP, false);
					m_ButtonUI.setEnable(ButtonUI.BUTTON_PROTOCOL, true);
					// 플래그 해제
					IsNoStop = false;
					// Stop 중임을 알리는 프로그래스 바
					final ProgressDialog dialog = new ProgressDialog(this, "Stoping this device...", 10);
					// 모달리스 기능을 가진 모달 대화상자를 띄우기 위해 스레드 적용
					Thread tempThread = new Thread()
					{
						public void run()
						{
							dialog.setModal(true);
							dialog.setVisible(true);
						}
					};
					tempThread.start();

					// 0.2초 마다 프로그래스바가 1칸씩 동작하도록 2초의 종료 시간을 둔다. ( 안정적인 종료를 위해 )
					Thread tempThread2 = new Thread()
					{
						public void run()
						{
							for(int i=1; i<=10; i++)
							{
								dialog.setProgressValue(i);
								try
								{
									Thread.sleep(200);
								}catch(InterruptedException e)
								{
									e.printStackTrace();
								}
							}

							dialog.setVisible(false);

							m_PCRTask.PCR_End();
						}
					};
					tempThread2.start();
				}
				break;
				// Start 이후, 프로토콜들을 전부 전송 했을 경우에 NOP 타이머를 동작 시키기 위한 메시지
			case MESSAGE_TASK_WRITE_END:
				try
				{
					Thread.sleep(300);
				}catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				// NopTimer 동작시킨다.
				m_PCRTask.setTimer(NopTimer.TIMER_NUMBER);
				break;
		}
	}

	@Override
	public void OnMessage(int MessageType, Object data)
	{
		switch( MessageType )
		{
			case CONNECTED:
				String count = (String)data;
				if( count.equals("1") )
					connectToDevice();
				break;
			case DISCONNECTED:
				gLEDOff();
				IsConnected = false;
				m_ButtonUI.setEnable(ButtonUI.BUTTON_START, false);
				m_ButtonUI.setEnable(ButtonUI.BUTTON_STOP, false);
				m_ButtonUI.setEnable(ButtonUI.BUTTON_PROTOCOL, false);
				m_PCRTask.killTimer(NopTimer.TIMER_NUMBER);
				m_PCRStatusText.setMessage(UIConstant.DEFAULT_STATUS_MESSAGE0, 0);
				m_PCRStatusText.setMessage(UIConstant.DEFAULT_STATUS_MESSAGE1, 1);
				m_PCRStatusText.setMessage(UIConstant.DEFAULT_STATUS_MESSAGE2, 2);
				break;
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// 숫자만 받도록
		char c = e.getKeyChar();

		if( !Character.isDigit(c) )
		{
			e.consume();
			return;
		}

		if( m_LidText.getText().length() == 3 )
			e.consume();
	}
}
