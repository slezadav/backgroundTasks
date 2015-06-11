package com.github.slezadav.backgroundTasks;

import java.util.ArrayList;

/**
 * Class containing info about a chain of tasks
 * Created by david.slezak on 9.6.2015.
 */
public class TaskChain {
    ArrayList<BaseTask> tasks = new ArrayList<>();
    ArrayList<Object[]> params = new ArrayList<>();
    ArrayList<Boolean> useHistoryParam = new ArrayList<>();
    Object finalTag;

    public TaskChain(Object tag) {
        this.finalTag = tag;
    }

    public TaskChain addTask(BaseTask task, boolean usePreviousResult, Object... params) {
        this.tasks.add(task);
        this.params.add(params);
        this.useHistoryParam.add(usePreviousResult);
        return this;
    }

    public TaskChain addTask(BaseTask task, Object... params) {
        this.tasks.add(task);
        this.params.add(params);
        this.useHistoryParam.add(false);
        return this;
    }
}