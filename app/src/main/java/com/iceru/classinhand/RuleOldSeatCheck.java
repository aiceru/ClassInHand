package com.iceru.classinhand;

import java.util.ArrayList;

/**
 * Created by Hongjoong on 2015-01-12.
 */
public class RuleOldSeatCheck extends Rule {

    public RuleOldSeatCheck(boolean isDefault, int priority)
    {
        super(isDefault,priority);
    }
    public ArrayList<Integer> filterSeats(int studentID, ArrayList<Integer> allocatable, ArrayList<Seatplan> oldPlans)
    {
        Seatplan tmpSeatplan;

        if(allocatable.size() <= 1)
            return allocatable;

        ArrayList<Integer> newAllocatable = (ArrayList<Integer>) allocatable.clone();

        /* 실제 필터 로직 추가 */

        int maxHistory = 0;
        if(oldPlans.size() < 3)
            maxHistory = oldPlans.size();
        else
            maxHistory = 3;

        for(int numHistory = 0 ; numHistory < maxHistory ; numHistory++)
        {
            Integer removeSeatId;
            tmpSeatplan = oldPlans.get(numHistory);
            for(int numSeats = 0 ; numSeats < tmpSeatplan.getmSeats().size() ; numSeats++)
            {
                Seat curSeat = tmpSeatplan.getmSeats().get(numSeats);
                if(studentID == curSeat.getItsStudent().getId()) {
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
