package com.iceru.classinhand;

/**
 * Created by iceru on 14. 8. 14.
 */
public class Seat implements Cloneable {
	private int id;
	private Student itsStudent;
    private int pairSeatId;
    private boolean selected = false;

	public Seat(int id, Student st) {
		this.id = id;
		this.itsStudent = st;
        this.pairSeatId = this.id % 2 == 0? this.id + 1 : this.id - 1;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean _selected) {
        this.selected = _selected;
    }
}
