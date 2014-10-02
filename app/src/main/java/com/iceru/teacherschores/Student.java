package com.iceru.teacherschores;

/**
 * Created by iceru on 14. 8. 13.
 */
public class Student implements Cloneable {
	private int		num;
	private String	name;
	private boolean boy;		// true -> boy, false -> girl... T/F has no meaning. :)
	private Seat    itsCurrentSeat;

    private double     seatPoint = 0;  // 자리 배치할 때 사용
    private double     pairPoint = 0;  // 짝 배치할 때 사용

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

    public double getSeatPoint() {
        return seatPoint;
    }

    public double getPairPoint() {
        return pairPoint;
    }

    public void setSeatPoint(double seatPoint) {
        this.seatPoint = seatPoint;
    }

    public void setPairPoint(double pairPoint) {
        this.pairPoint = pairPoint;
    }
}
