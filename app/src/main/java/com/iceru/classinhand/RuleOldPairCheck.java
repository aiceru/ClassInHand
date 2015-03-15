package com.iceru.classinhand;

import java.util.ArrayList;

/**
 * Created by Hongjoong on 2015-01-12.
 */
public class RuleOldPairCheck extends Rule {
    private ClassInHandApplication          application;
   // private static final int MaxHistoryLookup = 3;
    public RuleOldPairCheck(boolean isDefault, int priority)
    {
        super(isDefault,priority);
    }
    public ArrayList<Integer> filterSeats(Student st, ArrayList<Integer> allocatable, ArrayList<Seatplan> oldPlans, ArrayList<Seat> seatArray)
    {
        Seatplan tmpSeatplan;
        application = ClassInHandApplication.getInstance();

        if(allocatable.size() <= 1)
            return allocatable;

        ArrayList<Integer> newAllocatable = (ArrayList<Integer>) allocatable.clone();

        /* 실제 필터 로직 추가 */

        int maxHistory = 0;
        if(st.getHistories().size() < application.globalProperties.num_histories)
            maxHistory = st.getHistories().size();
        else
            maxHistory = application.globalProperties.num_histories;

        if(maxHistory == 0)
            return newAllocatable;
        // 과거에 짝이었던 학생이 현재 할당된 자리의 옆자리를 제거함
        int historyCount = 1;
        for(PersonalHistory p : st.getHistories()) {
            if(p == null)
                break;

            for(Seat seat:seatArray)
            {
                if(seat.getItsStudent() == null)
                    continue;
                if(seat.getItsStudent().getId() == p.pairId)
                {
                    newAllocatable.remove(new Integer(seat.getPairSeatId()));
                    break;
                }
            }
            if(++historyCount > maxHistory)
                break;
            if(newAllocatable.size() <= 0)
                break;
        }
        return newAllocatable;
    }
}
