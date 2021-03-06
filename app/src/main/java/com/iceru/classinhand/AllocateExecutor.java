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
    private Seatplan mNewSeatplan;

    public AllocateExecutor(Seatplan s, TreeMap<Integer, Student> students)
    {
        mStudents = students;
        mNewSeatplan = s;
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
        mRules.put(new Integer(4),new RuleOldRowCheck(true, 4));
        // mNewSeatplan의 isboyRight를 전달해주기 위해서 별도의 함수를 선언하고 캐스팅으로 해결
        // siceman 2015-03-15 TODO:다형성을 적용한 생성자로 후처리가 필요함
        ((RuleSideCheck)mRules.get(3)).setIsBoyFlag(mNewSeatplan.isBoyRight());
        return 1;
    }

    public Seatplan allocateAllStudent(ArrayList<Seat> seatArray)
    {
        application = ClassInHandApplication.getInstance();
        //mStudents = application.getmStudents();
        //mRemainStudents = new TreeMap<>(mStudents);
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
            // 2015.04.15 siceman : 고정되지 않은 자리만 이용해서 배치에 적용함
            if(!s.isFixed())
                currentAllocatable.add(new Integer(s.getId()));
        }

        ArrayList<Integer> newAllocatable;
        for(Map.Entry<Double, Student> e : pointedTreeMap.entrySet())
        {
            newAllocatable = allocateStudent(e.getValue(), currentAllocatable, seatArray);
            // 현재 할당 가능한 자리 기준으로 랜덤하게 하나를 골라서 seatArray에 반영해 주고
            // currentAllocatable에서 좌석을 삭제한다.
            // newAllocatable은 한 학생의 자리 배치에만 관여한다.
            int seatIndex = (int) (Math.random()*19717)% newAllocatable.size();
            int selectedSeatID = newAllocatable.get(seatIndex);

            Student s = e.getValue();
            seatArray.get(selectedSeatID).setItsStudent(s);
            // wooseok. 2015 03 31
            // Fix issue : 자리 하나에 학생 앉히고 나면, mRemainStudents (AE 내부에서는 mStudents) 에서 remove 해줘야 함~
            mStudents.remove(s.getAttendNum());

            currentAllocatable.remove(new Integer(selectedSeatID));
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
        return mNewSeatplan;
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

