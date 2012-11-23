package com.example.mediacontrol;

import org.teleal.cling.model.meta.Device;

public interface MediaController {

	public void deviceAdded(Device device);
	
	public void deviceRemoved(Device device);
}
