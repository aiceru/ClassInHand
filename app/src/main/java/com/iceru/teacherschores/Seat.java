package com.iceru.teacherschores;

/**
 * Created by iceru on 14. 8. 14.
 */
public class Seat {
	private int id;
	private Student itsStudent;

	public Seat(int id, Student st) {
		this.id = id;
		this.itsStudent = st;
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
}
