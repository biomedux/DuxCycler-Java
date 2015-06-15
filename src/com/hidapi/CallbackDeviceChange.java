package com.hidapi;

import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDManager;

public class CallbackDeviceChange extends Thread 
{
	private static CallbackDeviceChange instance = null;
	private HIDManager m_Manager = null;
	private DeviceChange m_Callback = null;
	private String serialNumber = null;
	
	private static int previous_counter = -1;

	private CallbackDeviceChange()
	{}
	private CallbackDeviceChange(HIDManager manager, DeviceChange callback)
	{
		m_Manager = manager;
		m_Callback = callback;
	}
	
	public static CallbackDeviceChange getInstance(HIDManager manager, DeviceChange callback)
	{
		if( instance == null )
			instance = new CallbackDeviceChange(manager, callback);
		return instance;
	}
	
	public void setSerialNumber(String serialNumber){
		this.serialNumber = serialNumber;
	}
	
	public void run()
	{
		String[] serials = new String[8];
		
		while(true)
		{
			try
			{
				HIDDeviceInfo[] devices = m_Manager.listDevices();
				try
				{
					Thread.sleep(100);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				
				int cnt = 0;
				
				if( devices != null )
				{
					for( HIDDeviceInfo device : devices )
					{
						serials[cnt] = device.getSerial_number();
						
						// 150507 YJ serial number check added
						if( serialNumber != null && device.getSerial_number() != null ){
							if( device.getVendor_id() == DeviceConstant.VENDOR_ID && 
									device.getProduct_id() == DeviceConstant.PRODUCT_ID && 
									device.getSerial_number().equals(serialNumber))
							{
								cnt++;
							}
						}
					}
					
					if( previous_counter != cnt ){
						if( previous_counter < cnt )
							m_Callback.OnMessage(DeviceChange.CONNECTED, cnt + "");
						else 
							m_Callback.OnMessage(DeviceChange.DISCONNECTED, cnt + "");
						
						previous_counter = cnt;
					}
				}else{
					m_Callback.OnMessage(DeviceChange.DISCONNECTED, null);
					cnt = 0;
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
