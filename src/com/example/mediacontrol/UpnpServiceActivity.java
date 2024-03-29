package com.example.mediacontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.android.AndroidUpnpServiceImpl;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.support.contentdirectory.callback.Browse;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mediacontrol.MediaFragment.IMediaListener;
import com.example.mediacontrol.MediaServerControl.MediaServerListener;

public class UpnpServiceActivity extends FragmentActivity implements MediaServerListener, IMediaListener {

	//ProgressBar progressBar;
	private String[] categories = new String[]{"Artists", "Albums", "Playlists", "Videos"};
	private UpnpTabListener tabListener = new UpnpTabListener();
	private Service currentServer;
	private List<DeviceDisplay> renderers = new ArrayList<DeviceDisplay>();
	private List<MediaController> controllers = new ArrayList<MediaController>();
	private Map<String, String> menu = new HashMap<String, String>();
	private MediaFragment currentFragment;
	//private TextView label;
	
	AndroidUpnpService upnpService;
	BrowseRegistryListener listener = new BrowseRegistryListener();
	
	ServiceConnection serviceConnection = new ServiceConnection(){

		public void onServiceConnected(ComponentName name, IBinder service) {
			upnpService = (AndroidUpnpService) service;
						
			for (Device dev : upnpService.getRegistry().getDevices()) {
				listener.deviceAdded(dev);
			}
			
			upnpService.getRegistry().addListener(listener);			
			upnpService.getControlPoint().search(new STAllHeader());

		}

		public void onServiceDisconnected(ComponentName name) {
			upnpService = null;				
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upnp_service);
		
		controllers.add(new MediaServerControl(this));
		
		
		//bind to the service		       
		getApplicationContext().bindService(new Intent(this, AndroidUpnpServiceImpl.class), serviceConnection, Context.BIND_AUTO_CREATE);
        		
		//progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
		
		//label = (TextView) findViewById(R.id.label);
		
		//setup tabs
		ActionBar actionBar = this.getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		for (String name : categories) {
			Tab tab = actionBar.newTab();
			tab.setText(name);
			tab.setTabListener(tabListener);
			actionBar.addTab(tab);			
		}	
		
	}
	
	private void findMenuOptions() {
		if (!currentServer.hasActions()) return;
		
		/**
		upnpService.getControlPoint().execute(new Browse(currentServer, "0", BrowseFlag.DIRECT_CHILDREN){

			@Override
			public void received(ActionInvocation arg0, DIDLContent content) {
				// TODO Auto-generated method stub
				for (Container container : content.getContainers()) {
					for (String menuTitle : categories) {
						if (container.getTitle().equalsIgnoreCase(menuTitle)) {
							menu.put(menuTitle, container.getId());
						}
					}					
				}
				
				//search default category
				String id = menu.get("Artists");
				lookupContent(id);
			}

			@Override
			public void updateStatus(Status arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0.name());
			}

			@Override
			public void failure(ActionInvocation arg0, UpnpResponse arg1,
					String arg2) {
				// TODO Auto-generated method stub
				
			}
			
		});
		*/
		
		menu.put("Artists", "6");
		menu.put("Albums", "7");
		menu.put("Videos", "2");
		menu.put("Playlists", "12");
		
		//search default category
		String id = menu.get("Artists");
		lookupContent(id);
		
		
	}
	private void lookupContent(final String id) {
		//label.setText("lookupContent "+id);
		if (currentServer != null) {
			//runOnUiThread(new Runnable() {
	          //  public void run() {
					upnpService.getControlPoint().execute(new Browse(currentServer, id, BrowseFlag.DIRECT_CHILDREN){
			
						@Override
						public void received(ActionInvocation arg0, final DIDLContent content) {
							
							/**for (Container c : content.getContainers()) {
								list.add(new ContentDisplay(c));
							}
							for (Item i : content.getItems()) {
								list.add(new ContentDisplay(i));
							}*/
							runOnUiThread(new Runnable(){

								@Override
								public void run() {
									List<ContentDisplay> list = new ArrayList<ContentDisplay>();
									for (Container c : content.getContainers()) {
										list.add(new ContentDisplay(c));
									}
									for (Item i : content.getItems()) {
										list.add(new ContentDisplay(i));
									}
									//List<ContentDisplay> smallList = list.subList(0, 10);
									//smallList.add(new ContentDisplay(content.getContainers().get(0)));
									
									//Container c = new Container();
									//c.setTitle("test");
									//smallList.add(new ContentDisplay(c));
									//send list to fragment
									//((IMediaFragment)currentFragment).setContent(smallList);
									//currentFragment = (MediaFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
									System.out.println("setting content");
									((IMediaFragment)currentFragment).setContent(list);
									//getSupportFragmentManager().beginTransaction().replace(R.id.container, currentFragment).addToBackStack(null).commit();
									System.out.println("Added: "+currentFragment.isAdded());
									System.out.println("Detached: "+currentFragment.isDetached());
									System.out.println("Hidden"+ currentFragment.isHidden());
									System.out.println("Resumed: "+currentFragment.isResumed());
									System.out.println("Visible: "+currentFragment.isVisible());
									//getFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentById(R.id.fragment)).commit();
								//	progressBar.setVisibility(View.INVISIBLE);
			//						label.setText("Recieved: "+list.size());
									
								}
								
							});
							
						}
			
						@Override
						public void updateStatus(Status arg0) {
							// TODO Auto-generated method stub
			//				System.out.println(arg0.name());
							//label.setText("Update: "+arg0.name());
						}
			
						@Override
						public void failure(ActionInvocation arg0, UpnpResponse arg1,
								String arg2) {
							// TODO Auto-generated method stub
				//			System.out.println(arg2);
//							label.setText("Error: "+arg2);
							
						}
						
					});
	            //}
            //});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_upnp_service, menu);
		return true;
	}
	
	@Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	if (upnpService != null) {
            upnpService.getRegistry().removeListener(listener);
        }
        getApplicationContext().unbindService(serviceConnection);

    }

	public class UpnpTabListener implements TabListener {		

		@Override
		public void onTabReselected(Tab arg0,
				android.app.FragmentTransaction arg1) {
			
			//lookupContent(menu.get(arg0.getText().toString()));
		}

		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			
			//currentFragment = (MediaFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
			currentFragment = new MediaFragment();			
			getSupportFragmentManager().beginTransaction().replace(R.id.container, currentFragment).addToBackStack(null).commit();
			
			
			if (menu.containsKey(tab.getText().toString())) {
				lookupContent(menu.get(tab.getText().toString()));
			}
			
			//List<ContentDisplay> list = new ArrayList<ContentDisplay>();
			//Container c = new Container();
			//c.setTitle("title");
			//c.setId("id");
			//list.add(new ContentDisplay(c));
			//((IMediaFragment)currentFragment).setContent(list);
		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}	
		
	}
	
	class BrowseRegistryListener extends DefaultRegistryListener {
    	@Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(
                            UpnpServiceActivity.this,
                            "Discovery failed of '" + device.getDisplayString() + "': " +
                                    (ex != null ? ex.toString() : "Couldn't retrieve device/service descriptors"),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
            deviceRemoved(device);
        }

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            deviceRemoved(device);
        }

        @Override
        public void deviceAdded(Registry registry, Device device) {
        	deviceAdded(device);
        }
        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            deviceAdded(device);
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            deviceRemoved(device);
        }

        
        public void deviceAdded(final Device device) {
            runOnUiThread(new Runnable() {
                public void run() {
                    for (MediaController controller : controllers) {
                    	controller.deviceAdded(device);
                    }                   
                }
            });
        }

        public void deviceRemoved(final Device device) {
            runOnUiThread(new Runnable() {
                public void run() {
                    for (MediaController controller : controllers) {
                    	controller.deviceRemoved(device);
                    }
                }
            });
        }

    }

	@Override
	public void onSetCurrentService(Service service) {
		this.currentServer = service;
		//label.setText("service found");
		this.findMenuOptions();		
	}

	@Override
	public void onContentSelected(ContentDisplay content) {
		//lookup or play
		System.out.println(content.getId());
		currentFragment = new MediaFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.container, currentFragment).addToBackStack(null).commit();
		
		lookupContent(content.getId());		
	}
}
