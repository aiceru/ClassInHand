package com.iceru.classinhand;

/**
 * Created by iceru on 14. 8. 14.
 */
public class Seat {
	private int id;
	private Student itsStudent;
    private int pairSeatId;
    private boolean recentSeatedFlag;
    private boolean selectedFlag;
    private boolean fixed;

	public Seat(int id, Student st, boolean fixed) {
		this.id = id;
		this.itsStudent = st;
        this.pairSeatId = this.id % 2 == 0? this.id + 1 : this.id - 1;
        this.recentSeatedFlag = false;
        this.selectedFlag = false;
        this.fixed = fixed;
	}

	public Seat(int id) {
		this(id, null);
	}

    public Seat(int id, Student st) {
        this(id, st, false);
    }

    public Seat(int id, boolean fixed) {
        this(id, null, fixed);
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

    public boolean getRecentSeatedFlag() {
        return recentSeatedFlag;
    }

    public boolean getSelectedFlag() { return selectedFlag; }

    public boolean isFixed() { return fixed; }

    /*public Seatplan getBelongingPlan() {
        return belongingPlan;
    }*/

    public void setItsStudent(Student itsStudent) {
		this.itsStudent = itsStudent;
	}

    public void setRecentSeatedFlag(boolean flag) {
        this.recentSeatedFlag = flag;
    }

    public void setSelectedFlag(boolean flag) {
        this.selectedFlag = flag;
    }

    public void setFixed(boolean fix) {
        this.fixed = fix;
    }

    /*public void setBelongingPlan(Seatplan p) {
        this.belongingPlan = p;
    }*/
}
