package com.iceru.teacherschores;

/**
 * Created by iceru on 14. 8. 13.
 */
public class Student {
	private int		num;
	private String	name;
	private boolean boy;		// true -> boy, false -> girl... T/F has no meaning. :)

	public Student(int num, String name, boolean boy) {
		this.num = num;
		this.name = name;
		this.boy = boy;
	}
	/**
	 * @return the num
	 */
	public int getNum() {
		return num;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the gender
	 */
	public boolean isBoy() {
		return boy;
	}
}
