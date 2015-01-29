package com.iceru.classinhand;

/**
 * Created by iceru on 14. 8. 14.
 */
public class Seat {
	private int id;
	private Student itsStudent;
    private int pairSeatId;
    private int recentSeatedFlag;
    private int selectedFlag;
    private Seatplan belongingPlan;

	public Seat(int id, Student st) {
		this.id = id;
		this.itsStudent = st;
        this.pairSeatId = this.id % 2 == 0? this.id + 1 : this.id - 1;
        this.recentSeatedFlag = ClassInHandApplication.SEATED_NOT;
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

    public int getRecentSeatedFlag() {
        return recentSeatedFlag;
    }

    public int getSelectedFlag() { return selectedFlag; }

    public Seatplan getBelongingPlan() {
        return belongingPlan;
    }

    public void setItsStudent(Student itsStudent) {
		this.itsStudent = itsStudent;
	}

    public void setRecentSeatedFlag(int lev) {
        this.recentSeatedFlag = lev;
    }

    public void setSelectedFlag(int where) {
        this.selectedFlag = where;
    }

    public void setBelongingPlan(Seatplan p) {
        this.belongingPlan = p;
    }
}
