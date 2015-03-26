package com.iceru.classinhand;

import java.util.ArrayList;

/**
 * Created by iceru on 14. 8. 13.
 */
public class Student {

    private int     id;
	private int		attendNum;
	private String	name;
	private boolean isBoy;		// true -> boy, false -> girl... T/F has no meaning. :)
    private ArrayList<PersonalHistory> histories;

    private long    inDate;
    private long    outDate;

	public Student(int id, int attendNum, String name, boolean isBoy, long inDate, long outDate) {
        this.id = id;
		this.attendNum = attendNum;
		this.name = name;
		this.isBoy = isBoy;
        this.histories = new ArrayList<>();

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

    public long getInDate() {
        return inDate;
    }

    public long getOutDate() {
        return outDate;
    }

    public ArrayList<PersonalHistory> getHistories() {
        return histories;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAttendNum(int attendNum) {
        this.attendNum = attendNum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBoy(boolean isBoy) {
        this.isBoy = isBoy;
    }

    public void setInDate(long inDate) {
        this.inDate = inDate;
    }

    public void setOutDate(long outDate) {
        this.outDate = outDate;
    }
}
