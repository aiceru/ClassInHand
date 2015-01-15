package com.iceru.classinhand;

/**
 * Created by iceru on 14. 8. 14.
 */
public class Seat implements Cloneable {
	private int id;
	private Student itsStudent;
    private int pairSeatId;
    private int recentSeated;
    private int selected;

	public Seat(int id, Student st) {
		this.id = id;
		this.itsStudent = st;
        this.pairSeatId = this.id % 2 == 0? this.id + 1 : this.id - 1;
        this.recentSeated = ClassInHandApplication.SEATED_NOT;
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

	public void setItsStudent(Student itsStudent) {
		this.itsStudent = itsStudent;
	}

    public int getPairSeatId() {
        return pairSeatId;
    }

    @Override
    public Seat clone() throws CloneNotSupportedException {
        return (Seat) super.clone();
    }

    public int getRecentSeatedLev() {
        return recentSeated;
    }

    public void setRecentSeatedLev(int lev) {
        this.recentSeated = lev;
    }

    public int getSelected() { return selected; }

    public void setSelected(int where) {
        this.selected = where;
    }
}
