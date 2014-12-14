package com.iceru.classinhand;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Created by iceru on 14. 8. 13.
 */
public class Student implements Cloneable {

    private int     id;
	private int		attendNum;
	private String	name;
	private boolean isBoy;		// true -> boy, false -> girl... T/F has no meaning. :)
	private Seat    itsCurrentSeat;

    private long    inDate;
    private long    outDate;

    private double     seatPoint = 0;  // 자리 배치할 때 사용
    private double     pairPoint = 0;  // 짝 배치할 때 사용

	public Student(int id, int attendNum, String name, boolean isBoy, long inDate, long outDate) {
        this.id = id;
		this.attendNum = attendNum;
		this.name = name;
		this.isBoy = isBoy;
		this.itsCurrentSeat = null;

        this.inDate = inDate;
        this.outDate = outDate;
	}

	public int getAttendNum() {
		return attendNum;
	}

	public String getName() {
		return name;
	}

    public int getId() {
        return id;
    }

	public boolean isBoy() {
		return isBoy;
	}

	public Seat getItsCurrentSeat() {
		return itsCurrentSeat;
	}

	public void setItsCurrentSeat(Seat itsCurrentSeat) {
		this.itsCurrentSeat = itsCurrentSeat;
	}

    public long getInDate() {
        return inDate;
    }

    public long getOutDate() {
        return outDate;
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
