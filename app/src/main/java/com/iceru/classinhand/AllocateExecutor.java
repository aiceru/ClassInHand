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
        mRules = new TreeMap<>();
    }
    public int createRuleList()
    {
        /* Config 값을 읽어와서 순서대로 rule을 생성하고
        그에 따른 default여부와 우선순위를 함께 넘겨줘야함.
         */
        mRules.put(new Integer(1),new RuleOldSeatCheck(true, 1));
        mRules.put(new Integer(2),new RuleOldPairCheck(true, 2));
        mRules.put(new Integer(3),new RuleSideCheck(true, 3));
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

        ArrayList<Integer> newAllocatable;
        //  임시 좌석과 필터 결과, 이미 할당된 학생들의 할당 현황 세가지 변수간에 관계를
        //  좀 더 확실하게 잡은 뒤에 구현할 것
        for(Map.Entry<Double, Student> e : pointedTreeMap.entrySet())
        {
            newAllocatable = allocateStudent(e.getValue(), currentAllocatable, seatArray);
            // 현재 할당 가능한 자리 기준으로 랜덤하게 하나를 골라서 seatArray에 반영해 주고
            // currentAllocatable에서 좌석을 삭제한다.
            // newAllocatable은 한 학생의 자리 배치에만 관여한다.
            int seatIndex = (int) (Math.random()*19717)% newAllocatable.size();
            Student s = e.getValue();
            seatArray.get(seatIndex).setItsStudent(s);
            currentAllocatable.remove(new Integer(seatArray.get(seatIndex).getId()));
            pointedTreeMap.remove(e.getKey());

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
    private ArrayList<Integer> allocateStudent(Student st, ArrayList<Integer> currentAllocatable, ArrayList<Seat> seatArray)
    {
        ArrayList<Integer> oldAllocatable = currentAllocatable;
        ArrayList<Integer> newAllocatable = null;
        for(Map.Entry<Integer, Rule> r : mRules.entrySet())
        {
            newAllocatable = r.getValue().filterSeats(st , oldAllocatable,mSeatplans, seatArray);
            if(newAllocatable.size() == 0) {
                newAllocatable = oldAllocatable;
                break;
            }
            else
                oldAllocatable = newAllocatable;
        }
        return newAllocatable;
    }
}

