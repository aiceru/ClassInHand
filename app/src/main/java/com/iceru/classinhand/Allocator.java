package com.iceru.classinhand;

import java.util.LinkedList;

/**
 * Created by Hongjoong on 2015-01-13.
 */
public abstract class Allocator {

    protected LinkedList<Rule> mRuleList;

    public abstract int createRuleList();
}

