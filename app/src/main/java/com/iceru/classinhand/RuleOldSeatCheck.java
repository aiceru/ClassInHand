package com.iceru.classinhand;

import java.util.ArrayList;

/**
 * Created by Hongjoong on 2015-01-12.
 */
public class RuleOldSeatCheck extends Rule {
    private ClassInHandApplication          application;
    //private static final int MaxHistoryLookup = 3;
    public RuleOldSeatCheck(boolean isDefault, int priority)
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
        // 학생 별로 앉았던 자리의 히스토리를 들고 있으므로 해당 값을 할당 가능한 자리에서 제거함
        int historyCount = 1;
        for(PersonalHistory p : st.getHistories()) {
            if(newAllocatable.size() <= 1)
                break;
            newAllocatable.remove(new Integer(p.seatId));
            if(++historyCount > maxHistory)
                break;
        }
        return newAllocatable;
    }
}
