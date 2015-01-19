package com.iceru.classinhand;
import java.util.ArrayList;
/**
 * Created by Hongjoong on 2015-01-12.
 */
public abstract class Rule {
    private boolean isDefault;
    private int priority;

    public Rule(boolean isDefault, int priority)
    {
        this.isDefault = isDefault;
        this.priority = priority;
    }

    public abstract ArrayList<Integer> filterSeats(Student st, ArrayList<Integer> allocatable, ArrayList<Seatplan> oldPlans, ArrayList<Seat> seatArray);

    public boolean getDefaultFlag()
    {
        return this.isDefault;
    }

    public int getPriority()
    {
        return this.priority;
    }
}
