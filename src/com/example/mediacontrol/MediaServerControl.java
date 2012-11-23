package com.example.mediacontrol;

import java.util.ArrayList;
import java.util.List;

import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;

public class MediaServerControl implements MediaController {

	private List<DeviceDisplay> servers = new ArrayList<DeviceDisplay>();
	private MediaServerListener listener;
	private Service currentService = null;
	
	public interface MediaServerListener {
		public void onSetCurrentService(Service service);
	}
	
	@Override
	public void deviceAdded(Device device) {
		for (Service<Device, Service> serv : device.getServices()) {
			if (serv.getServiceType().getType().equals(DeviceType.ContentDirectory.name())) {
				DeviceDisplay dd = new DeviceDisplay(device);
				
				if (servers.contains(dd)) {
					servers.set(servers.indexOf(dd), dd);
				} else {
					servers.add(dd);
				}
				
				if (currentService == null) {
					currentService = serv;
					listener.onSetCurrentService(currentService);
				}
				
			}
		}
		
	}

	@Override
	public void deviceRemoved(Device device) {
		DeviceDisplay dd = new DeviceDisplay(device);
		if (servers.contains(dd)) {
			servers.remove(dd);
			
			//notify listener
			//listener.onServerDeleted(device);
		}
		
	}
	
	
}
