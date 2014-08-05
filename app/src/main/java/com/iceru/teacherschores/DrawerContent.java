package com.iceru.teacherschores;

public interface DrawerContent {
	public int getId();
	public String getLabel();
	public int getType();
	public boolean isEnabled();
	public boolean updateActionBarTitle();
}
