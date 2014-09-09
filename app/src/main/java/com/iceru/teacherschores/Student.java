package com.iceru.teacherschores;

/**
 * Created by iceru on 14. 8. 13.
 */
public class Student implements Cloneable {
	private int		num;
	private String	name;
	private boolean boy;		// true -> boy, false -> girl... T/F has no meaning. :)
	private Seat    itsCurrentSeat;

	public Student(int num, String name, boolean boy) {
		this.num = num;
		this.name = name;
		this.boy = boy;
		this.itsCurrentSeat = null;
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

	public Seat getItsCurrentSeat() {
		return itsCurrentSeat;
	}

	public void setItsCurrentSeat(Seat itsCurrentSeat) {
		this.itsCurrentSeat = itsCurrentSeat;
	}

    @Override
    public Student clone() throws CloneNotSupportedException {
        return (Student) super.clone();
    }
}
