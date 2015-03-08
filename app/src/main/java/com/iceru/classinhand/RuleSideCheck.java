package com.iceru.classinhand;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Hongjoong on 2015-01-12.
 */
public class RuleSideCheck extends Rule {

    public RuleSideCheck(boolean isDefault, int priority)
    {
        super(isDefault,priority);
    }
    public ArrayList<Integer> filterSeats(Student st, ArrayList<Integer> allocatable, ArrayList<Seatplan> oldPlans, ArrayList<Seat> seatArray)
    {
        int genderflag = 0;
        if(allocatable.size() <= 1)
            return allocatable;

        ArrayList<Integer> newAllocatable = (ArrayList<Integer>) allocatable.clone();

        if(st.isBoy() == true)
            genderflag = 0; // 남자일 경우 왼쪽, 여자일 경우 오른쪽에 앉을수 있도록 지정
        else
            genderflag = 1;
        /* 실제 필터 로직 추가 */
        for(Integer allocEntry:newAllocatable)
        {
            if((allocEntry % 2) == genderflag)
                newAllocatable.remove(allocEntry);
        }

        return newAllocatable;
    }
}
