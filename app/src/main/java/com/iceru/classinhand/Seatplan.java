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

    public Seatplan(GregorianCalendar applyDate, ArrayList<Seat> seats) {
        this.mApplyDate = applyDate;
        mApplyDate.clear(Calendar.HOUR);
        mApplyDate.clear(Calendar.MINUTE);
        mApplyDate.clear(Calendar.SECOND);
        mApplyDate.clear(Calendar.MILLISECOND);
        this.mSeats = seats;
        for(Seat s : seats) {
            s.setBelongingPlan(this);
        }
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
}
