package com.iceru.teacherschores;

/**
 * Created by iceru on 14. 8. 13.
 */
public class Role {
	private int id;
	private String name;
	private int consume;

	public Role(int id, String name, int consume) {
		this.id = id;
		this.name = name;
		this.consume = consume;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getConsume() {
		return consume;
	}
}
