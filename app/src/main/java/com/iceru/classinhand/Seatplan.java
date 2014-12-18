package com.iceru.classinhand;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by iceru on 14. 12. 15..
 */
public class Seatplan {
    private GregorianCalendar       mApplyDate;
    private ArrayList<Seat>         mSeats;

    public Seatplan (GregorianCalendar applyDate, ArrayList<Seat> seats) {
        this.mApplyDate = applyDate;
        this.mSeats = seats;
    }

    public GregorianCalendar getmApplyDate() {
        return mApplyDate;
    }

    public ArrayList<Seat> getmSeats() {
        return mSeats;
    }
}
