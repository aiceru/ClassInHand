package com.iceru.classinhand;

import java.util.GregorianCalendar;

/**
 * Created by iceru on 15. 1. 28..
 */
public class PersonalHistory {
    public GregorianCalendar   applyDate;
    public int                 seatId;
    public int                 pairId;

    public PersonalHistory(GregorianCalendar cal, int seatId, int pairId) {
        this.applyDate = cal;
        this.seatId = seatId;
        this.pairId = pairId;
    }
}
