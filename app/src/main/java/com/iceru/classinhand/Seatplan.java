package com.iceru.classinhand;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by iceru on 14. 12. 15..
 */
public class Seatplan {
    private GregorianCalendar       mApplyDate;
    private ArrayList<Seat>         mSeats;
    private int                     mColumns;
    private int                     mRows;
    private boolean                 mBoySitsRight;

    public Seatplan(GregorianCalendar applyDate, ArrayList<Seat> seats, int columns, int rows, boolean isBoyRight) {
        this.mApplyDate = applyDate;
        mApplyDate.clear(Calendar.HOUR);
        mApplyDate.clear(Calendar.MINUTE);
        mApplyDate.clear(Calendar.SECOND);
        mApplyDate.clear(Calendar.MILLISECOND);
        this.mColumns = columns;
        this.mRows = rows;
        this.mBoySitsRight = isBoyRight;
        this.mSeats = seats;
        /*for(Seat s : seats) {
            s.setBelongingPlan(this);
        }*/
    }

    public void setmApplyDate(GregorianCalendar cal) {
        this.mApplyDate = cal;
    }

    public GregorianCalendar getmApplyDate() {
        return mApplyDate;
    }

    public ArrayList<Seat> getmSeats() {
        return mSeats;
    }

    public int getmColumns() {
        return mColumns;
    }

    public int getmRows() { return mRows; }

    public boolean isBoyRight() { return mBoySitsRight; }
}
