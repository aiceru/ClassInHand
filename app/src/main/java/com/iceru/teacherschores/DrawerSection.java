package com.iceru.teacherschores;

public class DrawerSection implements DrawerContent {

	public static final int SECTION_TYPE = 0;
	private int id;
	private String label;

	private DrawerSection() {
	}

	public static DrawerSection create( int id, String label ) {
		DrawerSection section = new DrawerSection();
		section.setLabel(label);
		return section;
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
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

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return SECTION_TYPE;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateActionBarTitle() {
		// TODO Auto-generated method stub
		return false;
	}

}
