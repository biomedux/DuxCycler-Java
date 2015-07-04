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
		while(true)
		{
			try
			{
				int firmwareVersion = 0;
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
						// 150507 YJ serial number check added
						if( serialNumber != null && device.getSerial_number() != null ){
							if( device.getVendor_id() == DeviceConstant.VENDOR_ID && 
									device.getProduct_id() == DeviceConstant.PRODUCT_ID && 
									device.getSerial_number().equals(serialNumber))
							{
								cnt++;
								firmwareVersion = device.getRelease_number();
							}
						}
					}
					
					if( previous_counter != cnt ){
						if( previous_counter < cnt )
							m_Callback.OnMessage(DeviceChange.CONNECTED, cnt + "", firmwareVersion);
						else 
							m_Callback.OnMessage(DeviceChange.DISCONNECTED, cnt + "", firmwareVersion);
						
						previous_counter = cnt;
					}
				}else{
					m_Callback.OnMessage(DeviceChange.DISCONNECTED, null, firmwareVersion);
					cnt = 0;
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
