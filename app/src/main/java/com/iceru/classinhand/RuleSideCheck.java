package com.iceru.classinhand;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Hongjoong on 2015-01-12.
 */
public class RuleSideCheck extends Rule {

    private int isBoyRight;
    private boolean genderflag;
    public RuleSideCheck(boolean isDefault, int priority)
    {
        super(isDefault, priority);
    }
    public void setIsBoyFlag(boolean gf)
    {
        genderflag = gf;
    }
    public ArrayList<Integer> filterSeats(Student st, ArrayList<Integer> allocatable, ArrayList<Seatplan> oldPlans, ArrayList<Seat> seatArray)
    {
        if(allocatable.size() <= 1)
            return allocatable;

        ArrayList<Integer> newAllocatable = (ArrayList<Integer>) allocatable.clone();

        if(st.isBoy() == genderflag)
            isBoyRight = 0; // 남자일 경우 왼쪽, 여자일 경우 오른쪽에 앉을수 있도록 지정
        else
            isBoyRight = 1;
        /* 실제 필터 로직 추가 */
        for(int allocEntry:allocatable)
        {
            if((allocEntry % 2) == isBoyRight)
                //newAllocatable.remove(allocEntry);
                newAllocatable.remove(new Integer(allocEntry));
        }

        return newAllocatable;
    }
}
