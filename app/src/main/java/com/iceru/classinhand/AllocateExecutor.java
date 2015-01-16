package com.iceru.classinhand;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Hongjoong on 2015-01-13.
 */
public class AllocateExecutor extends Allocator {
    /* Application Class */
    private ClassInHandApplication          application;

    /* Data Structures */
    private TreeMap<Integer, Student>       mStudents;
    private TreeMap<Integer, Student>       mRemainStudents;

    private TreeMap<Integer, Rule> mRules;
    private ArrayList<Seatplan> mSeatplans;

    public AllocateExecutor()
    {

    }
    public int createRuleList()
    {
        /* Config 값을 읽어와서 순서대로 rule을 생성하고
        그에 따른 default여부와 우선순위를 함께 넘겨줘야함.
         */
        mRules.put(new Integer(1),new RuleOldSeatCheck(true, 1));
        return 1;
    }

    public ArrayList<Seat> allocateAllStudent(ArrayList<Seat> seatArray)
    {
        application = ClassInHandApplication.getInstance();
        mStudents = application.getmStudents();
        mRemainStudents = new TreeMap<>(mStudents);
        mSeatplans = application.getmSeatplans();

        ArrayList<Integer> currentAllocatable = new ArrayList<Integer>();
        TreeMap<Double, Student> pointedTreeMap = new TreeMap<>();
        // 학생 정보 얻어와서 섞어주기
        for(Map.Entry<Integer, Student> entry : mStudents.entrySet()) {
            Student s = entry.getValue();
            pointedTreeMap.put(Math.random(), s);
        }

        // 현재 좌석의 모든 아이디를 currentAllocatable에 등록한다
        for(Seat s : seatArray)
        {
            currentAllocatable.add(new Integer(s.getId()));
        }

        ArrayList<Integer> tmpAllocatable;
        for(Map.Entry<Double, Student> e : pointedTreeMap.entrySet())
        {
            for(Map.Entry<Integer, Rule> r : mRules.entrySet())
            {
                tmpAllocatable = r.getValue().filterSeats(e.getValue().getId(), currentAllocatable,mSeatplans);
                if(tmpAllocatable.size() == 0)
                    tmpAllocatable = currentAllocatable;
            }
        }
/*
        for(Seat seat : seatArray) {
            Map.Entry<Double, Student> e = pointedTreeMap.firstEntry();
            Student s = e.getValue();
            seat.setItsStudent(s);
            mRemainStudents.remove(s.getAttendNum());
            pointedTreeMap.remove(e.getKey());
        }
*/
        return seatArray;
    }
}
