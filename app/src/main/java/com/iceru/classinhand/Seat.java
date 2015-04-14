package com.iceru.classinhand;

/**
 * Created by iceru on 14. 8. 14.
 */
public class Seat {
	private int id;
	private Student itsStudent;
    private int pairSeatId;
    private byte recentSeatedFlag;
    private byte selectedFlag;
    private boolean fixed;

	public Seat(int id, Student st) {
		this.id = id;
		this.itsStudent = st;
        this.pairSeatId = this.id % 2 == 0? this.id + 1 : this.id - 1;
        this.recentSeatedFlag = 0x00;
        this.selectedFlag = 0x00;
        this.fixed = false;
	}

	public Seat(int id) {
		this(id, null);
	}

	public int getId() {
		return id;
	}

	public Student getItsStudent() {
		return itsStudent;
	}

    public int getPairSeatId() {
        return pairSeatId;
    }

    public byte getRecentSeatedFlag() {
        return recentSeatedFlag;
    }

    public byte getSelectedFlag() { return selectedFlag; }

    public boolean isFixed() { return fixed; }

    /*public Seatplan getBelongingPlan() {
        return belongingPlan;
    }*/

    public void setItsStudent(Student itsStudent) {
		this.itsStudent = itsStudent;
	}

    public void setRecentSeatedFlag(byte flag) {
        this.recentSeatedFlag |= flag;
    }

    public void clrRecentSeatedFlag(byte flag) {
        this.recentSeatedFlag &= (~flag);
    }

    public void setSelectedFlag(byte flag) {
        this.selectedFlag |= flag;
    }

    public void clrSelectedFlag(byte flag) {
        this.selectedFlag &= (~flag);
    }

    public void setFixed(boolean fix) {
        this.fixed = fix;
    }

    /*public void setBelongingPlan(Seatplan p) {
        this.belongingPlan = p;
    }*/
}
