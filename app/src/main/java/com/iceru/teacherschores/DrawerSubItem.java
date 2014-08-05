package com.iceru.teacherschores;

import android.content.Context;

public class DrawerSubItem implements DrawerContent {

	public static final int SUBITEM_TYPE = 2;

	private int id;
	private String label;  
	private int icon;
	private boolean updateActionBarTitle;

	private DrawerSubItem() {
	}

	public static DrawerSubItem create( int id, String label, String icon, boolean updateActionBarTitle, Context context ) {
		DrawerSubItem item = new DrawerSubItem();
		item.setId(id);
		item.setLabel(label);
		item.setIcon(context.getResources().getIdentifier( icon, "drawable", context.getPackageName()));
		item.setUpdateActionBarTitle(updateActionBarTitle);
		return item;
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return SUBITEM_TYPE;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean updateActionBarTitle() {
		// TODO Auto-generated method stub
		return this.updateActionBarTitle;
	}

	public void setUpdateActionBarTitle(boolean updateActionBarTitle) {
		this.updateActionBarTitle = updateActionBarTitle;
	}

}
