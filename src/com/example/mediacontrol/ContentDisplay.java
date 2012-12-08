package com.example.mediacontrol;

import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

public class ContentDisplay {
	private String parentId;
	private String id;
	private String title;
	private String location;
	
	public ContentDisplay(Container content) {
		this.parentId = content.getParentID();
		this.id = content.getId();
		this.title = content.getTitle();
	}
	
	public ContentDisplay(Item item) {
		this.parentId = item.getParentID();
		this.id = item.getId();
		this.title = item.getTitle();
		this.location = item.getResources().get(0).getImportUri().toString();
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	@Override
	public String toString() {
		return title;
	
	}
}
