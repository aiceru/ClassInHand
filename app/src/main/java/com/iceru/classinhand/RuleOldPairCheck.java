package com.iceru.classinhand;

import java.util.ArrayList;

/**
 * Created by Hongjoong on 2015-01-12.
 */
public class RuleOldPairCheck extends Rule {

    private static final int MaxHistoryLookup = 3;
    public RuleOldPairCheck(boolean isDefault, int priority)
    {
        super(isDefault,priority);
    }
    public ArrayList<Integer> filterSeats(Student st, ArrayList<Integer> allocatable, ArrayList<Seatplan> oldPlans, ArrayList<Seat> seatArray)
    {
        Seatplan tmpSeatplan;

        if(allocatable.size() <= 1)
            return allocatable;

        ArrayList<Integer> newAllocatable = (ArrayList<Integer>) allocatable.clone();

        /* 실제 필터 로직 추가 */

        int maxHistory = 0;
        if(oldPlans.size() < MaxHistoryLookup)
            maxHistory = oldPlans.size();
        else
            maxHistory = MaxHistoryLookup;

        for(int numHistory = 0 ; numHistory < maxHistory ; numHistory++)
        {
            Integer removeSeatId;
            tmpSeatplan = oldPlans.get(numHistory);
            for(int numSeats = 0 ; numSeats < tmpSeatplan.getmSeats().size() ; numSeats++)
            {
                Seat curSeat = tmpSeatplan.getmSeats().get(numSeats);
                if(st.getId() == curSeat.getItsStudent().getId()) {
                    newAllocatable.remove(new Integer(curSeat.getId()));
                    break;
                }
            }
            if(newAllocatable.size() <= 0)
                break;
        }

        return newAllocatable;
    }
}
